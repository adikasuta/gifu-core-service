package com.gifu.coreservice.service;

import com.gifu.coreservice.entity.*;
import com.gifu.coreservice.enumeration.CodePrefix;
import com.gifu.coreservice.enumeration.PricingRangeFilter;
import com.gifu.coreservice.enumeration.SearchOperation;
import com.gifu.coreservice.enumeration.VariantTypeEnum;
import com.gifu.coreservice.exception.InvalidRequestException;
import com.gifu.coreservice.model.dto.*;
import com.gifu.coreservice.model.dto.dashboard.product.ProductOrderDto;
import com.gifu.coreservice.model.dto.dashboard.product.ProductVariantViewDto;
import com.gifu.coreservice.model.request.SaveProductRequest;
import com.gifu.coreservice.model.request.SearchProductRequest;
import com.gifu.coreservice.repository.*;
import com.gifu.coreservice.repository.spec.BasicSpec;
import com.gifu.coreservice.repository.spec.SearchCriteria;
import com.gifu.coreservice.utils.FileUtils;
import com.gifu.coreservice.utils.SpecUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductVariantPriceRepository productVariantPriceRepository;
    @Autowired
    private PricingRangeRepository pricingRangeRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Autowired
    private VariantRepository variantRepository;
    @Autowired
    private ContentRepository contentRepository;
    @Autowired
    private ProductVariantViewRepository productVariantViewRepository;
    @Autowired
    private ProductVariantVisibilityRuleRepository productVariantVisibilityRuleRepository;

    @Value("${picture.path}")
    private String pictureBasePath;

    public String generateCode() {
        String token = com.gifu.coreservice.utils.StringUtils.generateRandomNumericString(5);
        token = CodePrefix.PRODUCT.getPrefix().concat(token);
        if (productRepository.countByProductCode(token) > 0) {
            return generateCode();
        }
        return token;
    }

    public ProductOrderDto getProductById(Long productId) throws InvalidRequestException {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty() || productOpt.get().isDeleted()) {
            throw new InvalidRequestException("Product (id=" + productId + ") is not existed");
        }
        List<ProductVariantView> productVariantViews = productVariantViewRepository.findByProductId(productId);
        List<ProductVariantViewDto> viewDtos = productVariantViews.stream().map(it -> {
            List<ProductVariantVisibilityRule> rules = productVariantVisibilityRuleRepository.findByProductVariantViewId(it.getId());
            return ProductVariantViewDto.builder()
                    .variantTypeCode(VariantTypeEnum.valueOf(it.getVariantTypeCode()))
                    .variantIds(it.getVariantIds())
                    .rules(rules)
                    .build();
        }).collect(Collectors.toList());

        List<ProductVariantPrice> productVariantPrices = productVariantPriceRepository.findByProductId(productId);
        List<PricingRange> pricingRanges = pricingRangeRepository.findByProductId(productId);

        Product product = productOpt.get();
        return ProductOrderDto.builder()
                .id(product.getId())
                .productCode(product.getProductCode())
                .productCategoryId(product.getProductCategoryId())
                .productType(product.getProductType())
                .name(product.getName())
                .existingPicture(product.getPicture())
                .length(product.getLength())
                .width(product.getWidth())
                .height(product.getHeight())
                .weight(product.getWeight())
                .minOrder(product.getMinOrder())
                .description(product.getDescription())
                .productVariantViews(viewDtos)
                .productVariantPrices(productVariantPrices)
                .pricingRanges(pricingRanges)
                .build();
    }

    @Transactional
    public Product saveProduct(SaveProductRequest request, MultipartFile pictureFile, String updatedBy) throws InvalidRequestException, IOException {
        Product product = new Product();
        product.setDeleted(false);
        product.setIsNotAvailable(false);
        product.setCreatedBy(updatedBy);
        product.setCreatedDate(ZonedDateTime.now());
        product.setProductCode(generateCode());
        if (request.getId() != null) {
            Optional<Product> prodOpt = productRepository.findById(request.getId());
            if (prodOpt.isEmpty()) {
                throw new InvalidRequestException("Product is not existed");
            }
            product = prodOpt.get();
        }
        FileUtils fileUtils = new FileUtils();
        if (pictureFile != null) {
            String filePath = fileUtils.storeFile(pictureFile, pictureBasePath);
            product.setPicture(filePath);
        }
        product.setProductType(request.getProductType().name());
        product.setName(request.getName());
        product.setProductCategoryId(request.getCategoryId());
        product.setLength(request.getLength());
        product.setWidth(request.getWidth());
        product.setHeight(request.getHeight());
        product.setWeight(request.getWeight());
        product.setMinOrder(request.getMinOrder());
        product.setDescription(request.getDescription());
        product.setUpdatedBy(updatedBy);
        product.setUpdatedDate(ZonedDateTime.now());
        productRepository.save(product);

        cleanUpProductVariantView(product.getId());
        for (ProductVariantViewInputRequestDto variantView : request.getProductVariantViews()) {
            ProductVariantView productVariantView = new ProductVariantView();
            productVariantView.setProductId(product.getId());

            productVariantView.setVariantIds(com.gifu.coreservice.utils.StringUtils.toStringSeparatedWith(variantView.getVariantIds(), ","));
            productVariantView.setVariantTypeCode(variantView.getVariantTypeCode().name());
            productVariantViewRepository.save(productVariantView);

            for (ProductVariantViewRuleInputRequestDto rule : variantView.getRules()) {
                ProductVariantVisibilityRule visibilityRule = new ProductVariantVisibilityRule();
                visibilityRule.setProductVariantViewId(productVariantView.getId());
                visibilityRule.setVariantTypeCode(rule.getVariantTypeCode());
                visibilityRule.setVariantIds(com.gifu.coreservice.utils.StringUtils.toStringSeparatedWith(rule.getVariantIds(), ","));
                productVariantVisibilityRuleRepository.save(visibilityRule);
            }
        }

        cleanUpProductVariant(product.getId());
        for (ProductVariantPriceDto variant : request.getProductVariantPrices()) {
            ProductVariantPrice productVariantPrice = new ProductVariantPrice();
            productVariantPrice.setProductId(product.getId());
            productVariantPrice.setVariantIds(com.gifu.coreservice.utils.StringUtils.toStringSeparatedWith(variant.getVariantIds(), ","));
            productVariantPrice.setPrice(variant.getPrice());
            productVariantPriceRepository.save(productVariantPrice);
        }

        cleanUpPricingRange(product.getId());
        for (PricingRangeDto item : request.getPricingRanges()) {
            PricingRange pricingRange = new PricingRange();
            pricingRange.setProductId(product.getId());
            pricingRange.setQtyMax(item.getQtyMax());
            pricingRange.setQtyMin(item.getQtyMin());
            pricingRange.setPrice(item.getPrice());
            pricingRangeRepository.save(pricingRange);
        }

        return product;
    }

    private void cleanUpPricingRange(Long productId) {
        List<PricingRange> pricingRanges = pricingRangeRepository.findByProductId(productId);
        for (PricingRange it : pricingRanges) {
            pricingRangeRepository.deleteById(it.getId());
        }
    }

    private void cleanUpProductVariant(Long productId) {
        List<ProductVariantPrice> productVariantPrices = productVariantPriceRepository.findByProductId(productId);
        for (ProductVariantPrice it : productVariantPrices) {
            productVariantPriceRepository.deleteById(it.getId());
        }
    }

    private void cleanUpProductVariantView(Long productId) {
        List<ProductVariantView> productVariantViews = productVariantViewRepository.findByProductId(productId);
        for (ProductVariantView it : productVariantViews) {
            List<ProductVariantVisibilityRule> visibilityRules = productVariantVisibilityRuleRepository.findByProductVariantViewId(it.getId());
            for (ProductVariantVisibilityRule rule : visibilityRules) {
                productVariantVisibilityRuleRepository.delete(rule);
            }
            productVariantViewRepository.delete(it);
        }
    }

    private Page<Product> searchProduct(SearchProductRequest request, Pageable pageable) {
        Specification<Product> specAnd = Specification.where(null);
        Specification<Product> specOr = Specification.where(null);
        if (request.getProductCategoryId() != null) {
            BasicSpec<Product> equalCategory = new BasicSpec<>(new SearchCriteria(
                    "productCategoryId", SearchOperation.EQUALS, request.getProductCategoryId()
            ));
            specAnd = specAnd.and(equalCategory);
        }
        if (request.getProductType() != null) {
            BasicSpec<Product> equalProductType = new BasicSpec<>(new SearchCriteria(
                    "productType", SearchOperation.EQUALS, request.getProductType()
            ));
            specAnd = specAnd.and(equalProductType);
        }

        if (StringUtils.hasText(request.getSearchQuery())) {
            BasicSpec<Product> likeName = new BasicSpec<>(new SearchCriteria(
                    "name", SearchOperation.LIKE, request.getSearchQuery()
            ));
            BasicSpec<ProductCategory> likeProductCategoryName = new BasicSpec<>(new SearchCriteria(
                    "name", SearchOperation.LIKE, request.getSearchQuery()
            ));
            List<ProductCategory> productCategories = productCategoryRepository.findAll(Specification.where(likeProductCategoryName));
            List<Long> productCategoryIds = productCategories.stream().map(ProductCategory::getId).collect(Collectors.toList());
            BasicSpec<Product> productCategoriesIn = new BasicSpec<>(new SearchCriteria(
                    "productCategoryId", SearchOperation.IN, productCategoryIds
            ));

            specOr = specOr.or(likeName).or(productCategoriesIn);
        }

        if (request.getPricingRangeFilter() != null) {
            Map<Long, BigDecimal> mapProductLowestPrice = new HashMap<>();
            List<PricingRange> pricingRanges = pricingRangeRepository.findAll();
            for (PricingRange it : pricingRanges) {
                BigDecimal lowestPrice = mapProductLowestPrice.get(it.getProductId());
                if(lowestPrice==null){
                    mapProductLowestPrice.put(it.getProductId(), it.getPrice());
                } else if(it.getPrice().compareTo(lowestPrice)<0) {
                    mapProductLowestPrice.put(it.getProductId(), it.getPrice());
                }
            }
            List<Long> productIds = new ArrayList<>();
            for(Map.Entry<Long, BigDecimal> entry : mapProductLowestPrice.entrySet()){
                if(request.getPricingRangeFilter().getLow().compareTo(entry.getValue())<=0){
                    if(request.getPricingRangeFilter().getHigh()==null){
                        productIds.add(entry.getKey());
                        continue;
                    }
                    if(request.getPricingRangeFilter().getHigh().compareTo(entry.getValue())>=0){
                        productIds.add(entry.getKey());
                    }
                }
            }

            if(productIds.isEmpty()){
                return Page.empty(pageable);
            }

            BasicSpec<Product> productIdsIn = new BasicSpec<>(new SearchCriteria(
                    "id", SearchOperation.IN, productIds
            ));
            specAnd = specAnd.and(productIdsIn);
        }

        return productRepository.findAll(
                Specification.where(specAnd).and(specOr).and(new SpecUtils<Product>().isNotTrue("isDeleted")),
                pageable
        );
    }

    public Page<ProductSearchDto> searchProductThenMap(SearchProductRequest request, Pageable pageable) throws InvalidRequestException {
        Page<Product> products = searchProduct(request, pageable);

        List<ProductSearchDto> dtoList = new ArrayList<>();
        for (Product it : products.getContent()) {
            String size = it.getLength() + " x " + it.getWidth() + " x " + it.getHeight();
            ProductSearchDto dto = ProductSearchDto.builder()
                    .id(it.getId())
                    .name(it.getName())
                    .picture(it.getPicture())
                    .displayPricing(getDisplayPrice(it.getId()))
                    .pricingRanges(pricingRangeRepository.findByProductId(it.getId()))
                    .size(size)
                    .build();
            dtoList.add(dto);
        }

        return new PageImpl<>(dtoList, products.getPageable(), products.getTotalElements());
    }

    private PricingRange getDisplayPrice(Long productId) throws InvalidRequestException {
        List<PricingRange> pricingOpt = pricingRangeRepository.findByProductIdAndQtyMaxIsNull(productId);
        if (!pricingOpt.isEmpty()) {
            return pricingOpt.get(0);
        }
        Pageable pageable = PageRequest.of(0, 1, Sort.by("qtyMax").descending());
        pricingOpt = pricingRangeRepository.findByProductIdWithPageable(productId, pageable);
        if (pricingOpt.isEmpty()) {
            throw new InvalidRequestException("Pricing Range is not set");
        }
        return pricingOpt.get(0);
    }
}
