package com.gifu.coreservice.service;

import com.gifu.coreservice.entity.Content;
import com.gifu.coreservice.entity.ProductVariant;
import com.gifu.coreservice.entity.Variant;
import com.gifu.coreservice.enumeration.CodePrefix;
import com.gifu.coreservice.enumeration.SearchOperation;
import com.gifu.coreservice.enumeration.VariantTypeEnum;
import com.gifu.coreservice.exception.InvalidRequestException;
import com.gifu.coreservice.model.dto.SearchProductVariantDto;
import com.gifu.coreservice.model.dto.ValueTextDto;
import com.gifu.coreservice.model.request.SaveVariantContentRequest;
import com.gifu.coreservice.model.request.SaveVariantRequest;
import com.gifu.coreservice.model.request.SearchProductVariantRequest;
import com.gifu.coreservice.repository.*;
import com.gifu.coreservice.repository.spec.BasicSpec;
import com.gifu.coreservice.repository.spec.SearchCriteria;
import com.gifu.coreservice.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
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
public class ProductVariantService {
    //TODO: check is_deleted = false before selecting

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private VariantRepository variantRepository;
    @Autowired
    private ContentRepository contentRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Autowired
    private ProductVariantRepository productVariantRepository;
    @Autowired
    private VariantTypeRepository variantTypeRepository;

    @Value("${picture.path}")
    private String pictureBasePath;

    public List<ValueTextDto> getVariantTypeReference() {
        List<ValueTextDto> types = new ArrayList<>();
        for(VariantTypeEnum item : VariantTypeEnum.values()){
            types.add(new ValueTextDto(item.name(), item.getText(), item.getMeta()));
        }
        return types;
    }

    public Page<SaveVariantContentRequest> findVariantContentsByVariantId(Long variantId, Pageable pageable) {
        Page<Content> contents = contentRepository.findPageByVariantId(variantId, pageable);
        return contents.map(it -> SaveVariantContentRequest.builder()
                .id(it.getId())
                .variantId(it.getVariantId())
                .name(it.getName())
                .existingPicturePath(it.getPicture())
                .build());
    }

    public SaveVariantRequest findProductVariantsById(Long variantId) throws InvalidRequestException {
        Optional<Variant> existing = variantRepository.findById(variantId);
        if (existing.isEmpty()) {
            throw new InvalidRequestException("Variant is not existed");
        }
        Variant variant = existing.get();
        return SaveVariantRequest.builder()
                .id(variant.getId())
                .name(variant.getName())
                .variantTypeCode(VariantTypeEnum.valueOf(variant.getVariantTypeCode()))
                .allowedToBeSecondary(variant.getAllowedBeSecondary())
                .existingPicturePath(variant.getPicture())
                .build();
    }

    public Page<SearchProductVariantDto> searchProductVariants(SearchProductVariantRequest request, Pageable pageable) {
        BasicSpec<Variant> nameContains = new BasicSpec<>(new SearchCriteria("name", SearchOperation.LIKE, request.getQuery()));
        BasicSpec<Variant> codeContains = new BasicSpec<>(new SearchCriteria("variantCode", SearchOperation.LIKE, request.getQuery()));
        BasicSpec<Variant> typeContains = new BasicSpec<>(new SearchCriteria("variantTypeCode", SearchOperation.LIKE, request.getQuery()));
        Specification<Variant> where = Specification.where(nameContains).or(codeContains).or(typeContains);
        if (StringUtils.hasText(request.getVariantTypeCode())) {
            BasicSpec<Variant> typeEquals = new BasicSpec<>(new SearchCriteria("variantTypeCode", SearchOperation.EQUALS, request.getVariantTypeCode()));
            where.and(typeEquals);
        }
        Page<Variant> pages = variantRepository.findAll(where, pageable);
        return pages.map(it -> {
            List<Content> contents = contentRepository.findByVariantId(it.getId());
            long numberOfUsage = productVariantRepository.countByVariations(it.getId());
            return SearchProductVariantDto.builder()
                    .id(it.getId())
                    .name(it.getName())
                    .variantTypeCode(it.getVariantTypeCode())
                    .numberOfContent(contents.size())
                    .numberOfUsage((int) numberOfUsage)
                    .contentPics(contents.stream().map(Content::getPicture).collect(Collectors.toList()))
                    .build();
        });
    }

    public String generateVariantCode() {
        long count = variantRepository.count();
        return CodePrefix.VARIANT.getPrefix().concat(com.gifu.coreservice.utils.StringUtils.toDigits(count, 5));
    }

    public boolean deleteVariantContent(Long contentId) {
        contentRepository.deleteById(contentId);
        return true;
    }

    @Transactional
    public boolean deleteVariant(Long variantId) {
        List<ProductVariant> productVariants = productVariantRepository.findByVariations(variantId);
        for (ProductVariant it : productVariants) {
            if (variantId.equals(it.getVariantId())) {
                productVariantRepository.delete(it);
                continue;
            }
            if (variantId.equals(it.getFirstSubvariantId())) {
                it.setFirstSubvariantId(null);
                productVariantRepository.save(it);
                continue;
            }
            if (variantId.equals(it.getSecondSubvariantId())) {
                it.setSecondSubvariantId(null);
                productVariantRepository.save(it);
            }
        }
        contentRepository.deleteByVariantId(variantId);
        variantRepository.deleteById(variantId);
        return true;
    }

    public Content saveVariantContent(SaveVariantContentRequest request, MultipartFile pictureFile, String saveBy) throws InvalidRequestException, IOException {
        Content content = new Content();
        if (request.getId() != null) {
            Optional<Content> existing = contentRepository.findById(request.getId());
            if (existing.isEmpty()) {
                throw new InvalidRequestException("Variant Content is not existed");
            }
            content = existing.get();
        } else {
            content.setCreatedDate(ZonedDateTime.now());
        }

        content.setVariantId(request.getVariantId());
        content.setName(request.getName());
        FileUtils fileUtils = new FileUtils();
        if (pictureFile != null) {
            String filePath = fileUtils.storeFile(pictureFile, pictureBasePath);
            content.setPicture(filePath);
        }
        content.setUpdatedDate(ZonedDateTime.now());
        content.setUpdatedBy(saveBy);
        return contentRepository.save(content);
    }

    public Variant saveVariant(SaveVariantRequest request, MultipartFile pictureFile, String saveBy) throws InvalidRequestException, IOException {
        Variant variant = new Variant();
        if (request.getId() != null) {
            Optional<Variant> existing = variantRepository.findById(request.getId());
            if (existing.isEmpty()) {
                throw new InvalidRequestException("Variant is not existed");
            }
            variant = existing.get();
        } else {
            variant.setVariantCode(generateVariantCode());
            variant.setCreatedDate(ZonedDateTime.now());
        }
        variant.setName(request.getName());
        FileUtils fileUtils = new FileUtils();
        if (pictureFile != null) {
            String filePath = fileUtils.storeFile(pictureFile, pictureBasePath);
            variant.setPicture(filePath);
        }
        variant.setVariantTypeCode(request.getVariantTypeCode().name());
        variant.setAllowedBeSecondary(request.isAllowedToBeSecondary());
        variant.setUpdatedDate(ZonedDateTime.now());
        variant.setUpdatedBy(saveBy);
        return variantRepository.save(variant);
    }
}
