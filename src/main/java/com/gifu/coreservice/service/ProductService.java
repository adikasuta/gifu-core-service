package com.gifu.coreservice.service;

import com.gifu.coreservice.entity.Product;
import com.gifu.coreservice.entity.ProductCategory;
import com.gifu.coreservice.enumeration.SearchOperation;
import com.gifu.coreservice.model.dto.ProductSearchDto;
import com.gifu.coreservice.model.request.SearchProductRequest;
import com.gifu.coreservice.repository.ProductCategoryRepository;
import com.gifu.coreservice.repository.ProductRepository;
import com.gifu.coreservice.repository.spec.BasicSpec;
import com.gifu.coreservice.repository.spec.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Value("${picture.path}")
    private String pictureBasePath;

    public Page<ProductSearchDto> searchProduct(SearchProductRequest request, Pageable pageable) {
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


        Page<Product> products = productRepository.findAll(
                Specification.where(specAnd).and(specOr),
                pageable
        );

        return products.map((it) -> {
            String size = it.getLength() + " X " + it.getWidth() + " X " + it.getHeight();
            return ProductSearchDto.builder()
                    .id(it.getId())
                    .name(it.getName())
                    .picture(it.getPicture())
                    .price(it.getPrice())
                    .size(size)
                    .build();
        });
    }
}
