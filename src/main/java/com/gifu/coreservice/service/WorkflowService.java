package com.gifu.coreservice.service;

import com.gifu.coreservice.entity.*;
import com.gifu.coreservice.enumeration.CodePrefix;
import com.gifu.coreservice.enumeration.RefStatusPatternCode;
import com.gifu.coreservice.enumeration.SearchOperation;
import com.gifu.coreservice.exception.InvalidRequestException;
import com.gifu.coreservice.model.dto.ProductCategoryDto;
import com.gifu.coreservice.model.dto.ProductDto;
import com.gifu.coreservice.model.dto.StepDto;
import com.gifu.coreservice.model.dto.WorkflowDto;
import com.gifu.coreservice.model.request.SaveWorkflowRequest;
import com.gifu.coreservice.model.request.SearchWorkflowInput;
import com.gifu.coreservice.repository.*;
import com.gifu.coreservice.repository.spec.BasicSpec;
import com.gifu.coreservice.repository.spec.SearchCriteria;
import com.gifu.coreservice.utils.SortUtils;
import com.gifu.coreservice.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkflowService {

    @Autowired
    private WorkflowRepository workflowRepository;
    @Autowired
    private StepRepository stepRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RefStatusPatternRepository refStatusPatternRepository;

    public String generateWorkflowCode() {
        long count = workflowRepository.count();
        return CodePrefix.WORKFLOW.getPrefix().concat(StringUtils.toDigits(count, 5));
    }

    @Transactional
    public WorkflowDto replaceWorkflow(SaveWorkflowRequest request) throws InvalidRequestException {
        Optional<Workflow> existing = workflowRepository.findById(request.getWorkflowId());
        if (existing.isEmpty()) {
            throw new InvalidRequestException("No workflow to update", null);
        }
        existing.get().setIsDeleted(true);
        existing.get().setUpdatedDate(ZonedDateTime.now());
        workflowRepository.save(existing.get());

        return this.createWorkflow(request, existing.get().getWorkflowCode());
    }

    @Transactional
    public WorkflowDto createWorkflow(SaveWorkflowRequest request, String workflowCode) {
        Workflow workflow = new Workflow();
        workflow.setIsDeleted(false);
        workflow.setWorkflowCode(workflowCode);
        workflow.setName(request.getWorkflowName());
        workflow.setCreatedDate(ZonedDateTime.now());
        workflow.setUpdatedDate(ZonedDateTime.now());
        workflow = workflowRepository.save(workflow);
        Long nextStepId = null;
        List<Step> steps = new ArrayList<>();
        for (int i = request.getSteps().size() - 1; i >= 0; i--) {
            StepDto stepDto = request.getSteps().get(i);
            Step step = new Step();
            step.setName(stepDto.getName());
            step.setWorkflowId(workflow.getId());
            step.setCreatedDate(ZonedDateTime.now());
            step.setUpdatedDate(ZonedDateTime.now());
            step.setDefaultAssignedUserId(stepDto.getAssignedToUserId());
            if (stepDto.getAssignedToUserId() != null) {
                Optional<User> user = userRepository.findById(stepDto.getAssignedToUserId());
                user.ifPresent(userVal -> {
                    Optional<Role> role = roleRepository.findById(userVal.getRoleId());
                    role.ifPresent(value -> step.setUserRoleCode(value.getCode()));
                });
            }
            step.setNextStepId(nextStepId);
            stepRepository.save(step);
            nextStepId = step.getId();
            Optional<RefStatusPattern> refStatusPattern;
            if (stepDto.getNeedApproval()) {
                refStatusPattern = refStatusPatternRepository.findByPatternCode(RefStatusPatternCode.NEED_APPROVAL.name());
            } else {
                refStatusPattern = refStatusPatternRepository.findByPatternCode(RefStatusPatternCode.STRAIGHT_FORWARD.name());
            }
            refStatusPattern.ifPresent((refStatusPatternVal) -> {
                List<RefStatus> refStatuses = new ArrayList<>(refStatusPatternVal.getRefStatuses());
                refStatuses.sort(SortUtils.orderFlowFromBottom());
                Long nextStatusId = null;
                for (RefStatus refStatus : refStatuses) {
                    Status status = new Status();
                    status.setName(refStatus.getName());
                    status.setPermissionCode(refStatus.getPermissionCode());
                    status.setStepId(step.getId());
                    status.setNextStatusId(nextStatusId);
                    statusRepository.save(status);
                    nextStatusId = status.getId();
                }
            });
            steps.add(step);
        }
        List<ProductCategory> categories = productCategoryRepository.findAllById(request.getCategoryProductIds());
        for(ProductCategory productCategory: categories){
            productCategory.setWorkflowCode(workflow.getWorkflowCode());
            productCategoryRepository.save(productCategory);
        }
        List<ProductCategoryDto> productCategoryDtos = categories.stream().map(
                (it) -> ProductCategoryDto.builder()
                        .id(it.getId())
                        .name(it.getName())
                        .build()
        ).collect(Collectors.toList());
        List<Product> products = productRepository.findByProductCategoryIdIn(request.getCategoryProductIds());
        List<ProductDto> productDtos = products.stream().map(
                (it) -> ProductDto.builder()
                        .id(it.getId())
                        .name(it.getName())
                        .build()
        ).collect(Collectors.toList());
        steps.sort(SortUtils.orderFlowFromTop());
        List<StepDto> stepDtos = steps.stream().map(
                (itStep) -> StepDto.builder()
                        .id(itStep.getId())
                        .name(itStep.getName())
                        .build()
        ).collect(Collectors.toList());
        return WorkflowDto.builder()
                .id(workflow.getId())
                .name(workflow.getName())
                .productCategories(productCategoryDtos)
                .products(productDtos)
                .steps(stepDtos)
                .build();
    }

    public Page<WorkflowDto> searchWorkflow(SearchWorkflowInput input, Pageable pageable) {
        BasicSpec<ProductCategory> likeCategoryName = new BasicSpec<>(new SearchCriteria(
                "name", SearchOperation.LIKE, input.getCategoryName()
        ));
        BasicSpec<ProductCategory> likeCodeName = new BasicSpec<>(new SearchCriteria(
                "productCategoryCode", SearchOperation.LIKE, input.getCategoryName()
        ));
        List<ProductCategory> productCategories = productCategoryRepository.findAll(Specification.where(likeCategoryName).or(likeCodeName));
        List<ProductCategoryDto> productCategoryDtos = productCategories.stream().map(
                (it) -> ProductCategoryDto.builder()
                        .id(it.getId())
                        .name(it.getName())
                        .build()
        ).collect(Collectors.toList());

        List<Long> categoryIds = productCategories.stream().map(ProductCategory::getId).collect(Collectors.toList());
        List<Product> products = productRepository.findByProductCategoryIdIn(categoryIds);
        List<ProductDto> productDtos = products.stream().map(
                (it) -> ProductDto.builder()
                        .id(it.getId())
                        .name(it.getName())
                        .build()
        ).collect(Collectors.toList());

        List<String> workflowCodes = productCategories.stream().map(ProductCategory::getWorkflowCode).collect(Collectors.toList());
        BasicSpec<Workflow> inWorkflowCodes = new BasicSpec<>(new SearchCriteria(
                "productCategoryCode", SearchOperation.IN, workflowCodes
        ));
        BasicSpec<Workflow> isNotDeletedWorkflow = new BasicSpec<>(new SearchCriteria(
                "isDeleted", SearchOperation.EQUALS, false
        ));
        Page<Workflow> workflows = workflowRepository.findAll(Specification.where(inWorkflowCodes).and(isNotDeletedWorkflow), pageable);
        return workflows.map((it) -> {
            BasicSpec<Step> workflowId = new BasicSpec<>(new SearchCriteria(
                    "workflowId", SearchOperation.EQUALS, it.getId()
            ));
            List<Step> steps = stepRepository.findAll(Specification.where(workflowId));
            List<StepDto> stepDtos = steps.stream().map(
                    (itStep) -> StepDto.builder()
                            .id(itStep.getId())
                            .name(itStep.getName())
                            .build()
            ).collect(Collectors.toList());
            return WorkflowDto.builder()
                    .id(it.getId())
                    .name(it.getName())
                    .products(productDtos)
                    .productCategories(productCategoryDtos)
                    .steps(stepDtos)
                    .build();
        });
    }


}
