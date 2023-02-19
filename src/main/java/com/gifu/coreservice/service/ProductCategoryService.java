package com.gifu.coreservice.service;

import com.gifu.coreservice.entity.ProductCategory;
import com.gifu.coreservice.enumeration.CodePrefix;
import com.gifu.coreservice.model.dto.ProductCategoryDto;
import com.gifu.coreservice.model.request.SaveProductCategoryRequest;
import com.gifu.coreservice.repository.ProductCategoryRepository;
import com.gifu.coreservice.utils.FileUtils;
import com.gifu.coreservice.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZonedDateTime;

@Service
public class ProductCategoryService {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Value("${picture.path}")
    private String pictureBasePath;

    public String generateWorkflowCode() {
        long count = productCategoryRepository.count();
        return CodePrefix.PRODUCT_CATEGORY.getPrefix().concat(StringUtils.toDigits(count, 5));
    }

    public ProductCategoryDto createProductCategory(SaveProductCategoryRequest request, MultipartFile pictureFile) throws IOException {
        FileUtils fileUtils = new FileUtils();
        String filePath = fileUtils.storeFile(pictureFile, pictureBasePath);
        ProductCategory productCategory = new ProductCategory();
        productCategory.setName(request.getName());
        productCategory.setProductCategoryCode(generateWorkflowCode());
        productCategory.setProductType(request.getProductTypeCode().name());
        productCategory.setDesignEstimation(request.getDesignEstimation());
        productCategory.setPicture(filePath);
        productCategory.setProductionEstimation(request.getProductionEstimation());
        productCategory.setCreatedDate(ZonedDateTime.now());
        productCategory.setUpdatedDate(ZonedDateTime.now());
        productCategory = productCategoryRepository.save(productCategory);

        return ProductCategoryDto.builder()
                .id(productCategory.getId())
                .name(productCategory.getName())
                .productType(productCategory.getProductType())
                .designEstimation(productCategory.getDesignEstimation())
                .productionEstimation(productCategory.getProductionEstimation())
                .build();
    }
}
