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
import com.gifu.coreservice.model.request.ChangeWorkflowNameRequest;
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

    public WorkflowDto deleteWorkflow(Long workflowId, String deleterEmail) throws InvalidRequestException {
        Optional<Workflow> existing = workflowRepository.findById(workflowId);
        if (existing.isEmpty()) {
            throw new InvalidRequestException("No workflow to remove", null);
        }
        resetProductCategoryWorkflow(existing.get().getWorkflowCode());
        existing.get().setIsDeleted(true);
        existing.get().setUpdatedDate(ZonedDateTime.now());
        existing.get().setUpdatedBy(deleterEmail);
        workflowRepository.save(existing.get());

        return getDto(existing.get());
    }

    public boolean isMinorChanges(SaveWorkflowRequest request) throws InvalidRequestException {
        Optional<Workflow> existing = workflowRepository.findById(request.getId());
        if (existing.isEmpty()) {//new workflow
            return false;
        }
        List<Step> existingSteps = stepRepository.findByWorkflowId(request.getId());
        if (existingSteps.size() != request.getSteps().size()) {//different step length
            return false;
        }

        existingSteps.sort(SortUtils.orderFlowFromTop());
        int i = 0;
        for (StepDto stepDto : request.getSteps()) {
            if (stepDto.getId() == null) {//new step
                return false;
            }
            if (stepDto.getId() != existingSteps.get(i).getId()) {//changing sequence
                return false;
            }
            if (!Objects.equals(stepDto.getNeedApproval(), existingSteps.get(i).getNeedApproval())) {
                return false;
            }
            if (!Objects.equals(stepDto.getAssignedToUserId(), existingSteps.get(i).getDefaultAssignedUserId())) {
                return false;
            }
            i++;
        }
        return true;
    }

    @Transactional
    public WorkflowDto changeName(ChangeWorkflowNameRequest request, String updaterEmail) throws InvalidRequestException {
        Optional<Workflow> existing = workflowRepository.findById(request.getWorkflowId());
        if (existing.isEmpty()) {
            throw new InvalidRequestException("No workflow to update", null);
        }
        existing.get().setName(request.getWorkflowName());
        existing.get().setUpdatedBy(updaterEmail);
        existing.get().setUpdatedDate(ZonedDateTime.now());
        workflowRepository.save(existing.get());

        return getDto(existing.get());
    }

    @Transactional
    public WorkflowDto minorUpdate(SaveWorkflowRequest request, String updaterEmail) throws InvalidRequestException {
        ChangeWorkflowNameRequest changeNameRequest = new ChangeWorkflowNameRequest();
        changeNameRequest.setWorkflowName(request.getName());
        changeNameRequest.setWorkflowId(request.getId());
        WorkflowDto result = changeName(changeNameRequest, updaterEmail);
        updateProductCategoryWorkflow(request, result.getWorkflowCode());
        updateStepNames(request,updaterEmail);
        return result;
    }

    private void updateStepNames(SaveWorkflowRequest request, String updaterEmail){
        for(StepDto item : request.getSteps()){
            Optional<Step> step = stepRepository.findById(item.getId());
            if(step.isPresent()){
                step.get().setName(item.getName());
                step.get().setUpdatedDate(ZonedDateTime.now());
                step.get().setUpdatedBy(updaterEmail);
                stepRepository.save(step.get());
            }
        }
    }


    @Transactional
    public WorkflowDto replaceWorkflow(SaveWorkflowRequest request, String updaterEmail) throws InvalidRequestException {
        Optional<Workflow> existing = workflowRepository.findById(request.getId());
        if (existing.isEmpty()) {
            throw new InvalidRequestException("No workflow to update", null);
        }
        existing.get().setIsDeleted(true);
        existing.get().setUpdatedBy(updaterEmail);
        existing.get().setUpdatedDate(ZonedDateTime.now());
        workflowRepository.save(existing.get());

        return this.createWorkflow(request, existing.get().getWorkflowCode(), updaterEmail);
    }

    @Transactional
    public WorkflowDto createWorkflow(SaveWorkflowRequest request, String workflowCode, String creatorEmail) {
        Workflow workflow = new Workflow();
        workflow.setIsDeleted(false);
        workflow.setWorkflowCode(workflowCode);
        workflow.setName(request.getName());
        workflow.setCreatedDate(ZonedDateTime.now());
        workflow.setUpdatedDate(ZonedDateTime.now());
        workflow.setCreatedBy(creatorEmail);
        workflow.setUpdatedBy(creatorEmail);
        workflow = workflowRepository.save(workflow);
        Long nextStepId = null;
        for (int i = request.getSteps().size() - 1; i >= 0; i--) {
            StepDto stepDto = request.getSteps().get(i);
            Step step = new Step();
            step.setName(stepDto.getName());
            step.setWorkflowId(workflow.getId());
            step.setCreatedDate(ZonedDateTime.now());
            step.setUpdatedDate(ZonedDateTime.now());
            step.setCreatedBy(creatorEmail);
            step.setUpdatedBy(creatorEmail);
            step.setNeedApproval(stepDto.getNeedApproval());
            step.setDefaultAssignedUserId(stepDto.getAssignedToUserId());
            if (stepDto.getAssignedToUserId() != null) {
                Optional<User> user = userRepository.findById(stepDto.getAssignedToUserId());
                if(user.isPresent() && user.get().getRoleId()!=null){
                    Optional<Role> role = roleRepository.findById(user.get().getRoleId());
                    role.ifPresent(value -> step.setUserRoleCode(value.getCode()));
                }
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
                    status.setCreatedBy(creatorEmail);
                    status.setCreatedDate(ZonedDateTime.now());
                    statusRepository.save(status);
                    nextStatusId = status.getId();
                }
            });
        }
        updateProductCategoryWorkflow(request, workflowCode);
        return getDto(workflow);
    }

    private void updateProductCategoryWorkflow(SaveWorkflowRequest request, String workflowCode){
        resetProductCategoryWorkflow(workflowCode);
        List<ProductCategory> categories = productCategoryRepository.findAllById(request.getProductCategoryIds());
        for (ProductCategory category : categories) {
            category.setWorkflowCode(workflowCode);
            productCategoryRepository.save(category);
        }
    }

    private void resetProductCategoryWorkflow(String workflowCode){
        List<ProductCategory> categories = productCategoryRepository.findByWorkflowCode(workflowCode);
        for (ProductCategory category : categories) {
            category.setWorkflowCode(null);
            productCategoryRepository.save(category);
        }
    }

    private WorkflowDto getDto(Workflow workflow) {
        BasicSpec<Step> workflowId = new BasicSpec<>(new SearchCriteria(
                "workflowId", SearchOperation.EQUALS, workflow.getId()
        ));
        List<Step> steps = stepRepository.findAll(Specification.where(workflowId));
        steps.sort(SortUtils.orderFlowFromTop());
        List<StepDto> stepDtos = steps.stream().map(
                (itStep) -> StepDto.builder()
                        .id(itStep.getId())
                        .assignedToUserId(itStep.getDefaultAssignedUserId())
                        .needApproval(itStep.getNeedApproval())
                        .name(itStep.getName())
                        .build()
        ).collect(Collectors.toList());
        WorkflowDto dto = WorkflowDto.builder()
                .id(workflow.getId())
                .workflowCode(workflow.getWorkflowCode())
                .name(workflow.getName())
                .steps(stepDtos)
                .build();

        List<ProductCategory> productCategories = productCategoryRepository.findByWorkflowCode(workflow.getWorkflowCode());
        dto.setProductCategories(productCategories.stream().map(cat -> ProductCategoryDto.builder()
                .id(cat.getId())
                .name(cat.getName())
                .workflowCode(workflow.getWorkflowCode())
                .productionEstimation(cat.getProductionEstimation())
                .designEstimation(cat.getDesignEstimation())
                .picture(cat.getPicture())
                .productType(cat.getProductType())
                .build()).collect(Collectors.toList()));
        for (ProductCategory cat : productCategories) {
            List<Product> products = productRepository.findByProductCategoryId(cat.getId());
            List<ProductDto> productDtos = products.stream().map(
                    (prod) -> ProductDto.builder()
                            .id(prod.getId())
                            .name(prod.getName())
                            .build()
            ).collect(Collectors.toList());
            dto.setProducts(productDtos);
        }

        return dto;
    }

    public Page<WorkflowDto> searchWorkflow(SearchWorkflowInput input, Pageable pageable) {
        String fieldName = input.getFieldName();
        if(!org.springframework.util.StringUtils.hasText(fieldName)){
            fieldName = "name";
        }
        BasicSpec<ProductCategory> like = new BasicSpec<>(new SearchCriteria(
                fieldName, SearchOperation.LIKE, input.getQuery()
        ));
        List<ProductCategory> productCategories = productCategoryRepository.findAll(Specification.where(like));

        List<String> workflowCodes = productCategories.stream().map(ProductCategory::getWorkflowCode).collect(Collectors.toList());
        Page<Workflow> workflows = Page.empty(pageable);
        if(!workflowCodes.isEmpty()){
            BasicSpec<Workflow> inWorkflowCodes = new BasicSpec<>(new SearchCriteria(
                    "workflowCode", SearchOperation.IN, workflowCodes
            ));
            BasicSpec<Workflow> isNotDeletedWorkflow = new BasicSpec<>(new SearchCriteria(
                    "isDeleted", SearchOperation.EQUALS, false
            ));
            workflows = workflowRepository.findAll(Specification.where(inWorkflowCodes).and(isNotDeletedWorkflow), pageable);
        }

        return workflows.map(this::getDto);
    }


}
