package com.gifu.coreservice.service;

import com.gifu.coreservice.entity.ProductCategory;
import com.gifu.coreservice.exception.InvalidRequestException;
import com.gifu.coreservice.model.dto.ProductCategoryDto;
import com.gifu.coreservice.model.request.SaveProductCategoryRequest;
import com.gifu.coreservice.repository.ProductCategoryRepository;
import com.gifu.coreservice.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
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

    public boolean deleteProductCategory(Long id){
        productCategoryRepository.deleteById(id);
        return true;
    }

    public ProductCategoryDto updateProductCategory(SaveProductCategoryRequest request, MultipartFile pictureFile) throws InvalidRequestException, IOException {
        Optional<ProductCategory> existing = productCategoryRepository.findById(request.getId());
        if(existing.isEmpty()){
            throw new InvalidRequestException("Category is not existed");
        }
        FileUtils fileUtils = new FileUtils();
        ProductCategory productCategory = existing.get();
        if(pictureFile!=null){
            String filePath = fileUtils.storeFile(pictureFile, pictureBasePath);
            productCategory.setPicture(filePath);
        }
        productCategory.setName(request.getName());
        productCategory.setProductType(request.getProductType().name());
        productCategory.setDesignEstimation(request.getDesignEstimation());
        productCategory.setProductionEstimation(request.getProductionEstimation());
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

    public ProductCategoryDto createProductCategory(SaveProductCategoryRequest request, MultipartFile pictureFile) throws IOException {
        FileUtils fileUtils = new FileUtils();
        ProductCategory productCategory = new ProductCategory();
        if(pictureFile!=null){
            String filePath = fileUtils.storeFile(pictureFile, pictureBasePath);
            productCategory.setPicture(filePath);
        }
        productCategory.setName(request.getName());
        productCategory.setProductType(request.getProductType().name());
        productCategory.setDesignEstimation(request.getDesignEstimation());
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
