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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductCategoryService {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Value("${picture.path}")
    private String pictureBasePath;

    public List<ProductCategoryDto> getAllProductCategory() {
        List<ProductCategory> productCategories = productCategoryRepository.findAll();
        return productCategories.stream().map(productCategory ->
                ProductCategoryDto.builder()
                        .id(productCategory.getId())
                        .name(productCategory.getName())
                        .picture(productCategory.getPicture())
                        .workflowCode(productCategory.getWorkflowCode())
                        .productType(productCategory.getProductType())
                        .designEstimation(productCategory.getDesignEstimation())
                        .productionEstimation(productCategory.getProductionEstimation())
                        .build()).collect(Collectors.toList());
    }

    public ProductCategoryDto createProductCategory(SaveProductCategoryRequest request, MultipartFile pictureFile) throws IOException {
        FileUtils fileUtils = new FileUtils();
        String filePath = fileUtils.storeFile(pictureFile, pictureBasePath);
        ProductCategory productCategory = new ProductCategory();
        productCategory.setName(request.getName());
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
