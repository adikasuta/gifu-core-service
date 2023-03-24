package com.gifu.coreservice.service;

import com.gifu.coreservice.entity.PricingRange;
import com.gifu.coreservice.entity.Product;
import com.gifu.coreservice.entity.ProductCategory;
import com.gifu.coreservice.entity.ProductVariant;
import com.gifu.coreservice.enumeration.CodePrefix;
import com.gifu.coreservice.enumeration.SearchOperation;
import com.gifu.coreservice.exception.InvalidRequestException;
import com.gifu.coreservice.model.dto.PricingRangeDto;
import com.gifu.coreservice.model.dto.ProductSearchDto;
import com.gifu.coreservice.model.dto.ProductVariantDto;
import com.gifu.coreservice.model.request.SaveProductRequest;
import com.gifu.coreservice.model.request.SearchProductRequest;
import com.gifu.coreservice.repository.PricingRangeRepository;
import com.gifu.coreservice.repository.ProductCategoryRepository;
import com.gifu.coreservice.repository.ProductRepository;
import com.gifu.coreservice.repository.ProductVariantRepository;
import com.gifu.coreservice.repository.spec.BasicSpec;
import com.gifu.coreservice.repository.spec.SearchCriteria;
import com.gifu.coreservice.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductVariantRepository productVariantRepository;
    @Autowired
    private PricingRangeRepository pricingRangeRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Value("${picture.path}")
    private String pictureBasePath;

    public String generateCode() {
        long count = productRepository.count() + 1;
        return CodePrefix.PRODUCT.getPrefix().concat(com.gifu.coreservice.utils.StringUtils.toDigits(count, 5));
    }

    @Transactional
    public Product saveProduct(SaveProductRequest request, MultipartFile pictureFile, String updatedBy) throws InvalidRequestException, IOException {
        Product product = new Product();
        product.setDeleted(false);
        product.setIsNotAvailable(false);
        product.setCreatedBy(updatedBy);
        product.setCreatedDate(ZonedDateTime.now());
        if(request.getId()!=null){
            Optional<Product> prodOpt = productRepository.findById(request.getId());
            if(prodOpt.isEmpty()){
                throw new InvalidRequestException("Product is not existed");
            }
            product = prodOpt.get();
        }
        FileUtils fileUtils = new FileUtils();
        if(pictureFile!=null){
            String filePath = fileUtils.storeFile(pictureFile, pictureBasePath);
            product.setPicture(filePath);
        }
        product.setProductCode(generateCode());
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

        cleanUpProductVariant(product.getId());
        for(ProductVariantDto variants : request.getProductVariants()){
            ProductVariant productVariant = new ProductVariant();
            productVariant.setProductId(product.getId());
            productVariant.setVariantId(variants.getMainVariantId());
            productVariant.setFirstSubvariantId(variants.getFirstSubVariantId());
            productVariant.setSecondSubvariantId(variants.getSecondSubVariantId());
            productVariant.setPrice(variants.getPrice());
            productVariantRepository.save(productVariant);
        }

        cleanUpPricingRange(product.getId());
        for(PricingRangeDto item : request.getPricingRanges()){
            PricingRange pricingRange = new PricingRange();
            pricingRange.setProductId(product.getId());
            pricingRange.setQtyMax(item.getMaxQuantity());
            pricingRange.setQtyMin(item.getMinQuantity());
            pricingRange.setPrice(item.getPrice());
            pricingRangeRepository.save(pricingRange);
        }

        return product;
    }

    private void cleanUpPricingRange(Long productId){
        List<PricingRange> pricingRanges = pricingRangeRepository.findByProductId(productId);
        for(PricingRange it : pricingRanges){
            pricingRangeRepository.deleteById(it.getId());
        }
    }

    private void cleanUpProductVariant(Long productId){
        List<ProductVariant> productVariants = productVariantRepository.findByProductId(productId);
        for(ProductVariant it : productVariants){
            productVariantRepository.deleteById(it.getId());
        }
    }

    private Page<Product> searchProduct(SearchProductRequest request, Pageable pageable){
        Specification<Product> specAnd = Specification.where(null);
        Specification<Product> specOr = Specification.where(null);
        if (request.getProductCategoryId() != null) {
            BasicSpec<Product> equalCategory = new BasicSpec<>(new SearchCriteria(
                    "productCategoryId", SearchOperation.EQUALS, request.getProductCategoryId()
            ));
            specAnd.and(equalCategory);
        }
        if (request.getProductType() != null) {
            BasicSpec<Product> equalProductType = new BasicSpec<>(new SearchCriteria(
                    "productType", SearchOperation.EQUALS, request.getProductType()
            ));
            specAnd.and(equalProductType);
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

            specOr.or(likeName).or(productCategoriesIn);
        }

        return productRepository.findAll(
                Specification.where(specAnd).and(specOr),
                pageable
        );
    }

    public Page<ProductSearchDto> searchProductThenMap(SearchProductRequest request, Pageable pageable) throws InvalidRequestException {
        Page<Product> products = searchProduct(request, pageable);

        List<ProductSearchDto> dtoList = new ArrayList<>();
        for(Product it : products.getContent()){
            String size = it.getLength() + " X " + it.getWidth() + " X " + it.getHeight();
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
        Optional<PricingRange> pricingOpt = pricingRangeRepository.findByProductIdAndQtyMaxIsNull(productId);
        if(pricingOpt.isPresent()){
            return pricingOpt.get();
        }
        pricingOpt = pricingRangeRepository.findByProductIdAndHighestQty(productId);
        if(pricingOpt.isEmpty()){
            throw new InvalidRequestException("Pricing Range is not set");
        }
        return pricingOpt.get();
    }
}
