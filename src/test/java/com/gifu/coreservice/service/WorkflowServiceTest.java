package com.gifu.coreservice.service;

import com.gifu.coreservice.entity.*;
import com.gifu.coreservice.enumeration.CodePrefix;
import com.gifu.coreservice.model.dto.StepDto;
import com.gifu.coreservice.model.dto.WorkflowDto;
import com.gifu.coreservice.model.request.SaveWorkflowRequest;
import com.gifu.coreservice.repository.*;
import com.gifu.coreservice.utils.SortUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WorkflowServiceTest {
    @Autowired
    private RefStatusPatternRepository refStatusPatternRepository;
    @Autowired
    private RefStatusRepository refStatusRepository;
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Autowired
    private WorkflowRepository workflowRepository;
    @Autowired
    private StepRepository stepRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    private Role staff;
    private User designer;
    private User produksi;
    private User packing;

    @BeforeAll
    public void setUp() {
        staff = createRole();
        designer = createUser("designer", staff.getId());
        produksi = createUser("produksi", staff.getId());
        packing = createUser("packing", staff.getId());
    }

    @AfterEach
    public void cleanUpEach() {
        productCategoryRepository.deleteAll();
        statusRepository.deleteAll();
        stepRepository.deleteAll();
        workflowRepository.deleteAll();
    }

    @AfterAll
    public void cleanUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    private Role createRole() {
        Role staff = new Role();
        staff.setCode("STAFF");
        staff.setName("staff");
        return roleRepository.save(staff);
    }

    private User createUser(String name, Long roleId) {
        User user = new User();
        user.setName(name);
        user.setEmail("some@user.com");
        user.setRoleId(roleId);
        return userRepository.save(user);
    }

    private List<ProductCategory> createCategories() {
        ProductCategory category1 = new ProductCategory();
        category1.setName("category1");
        category1.setProductCategoryCode("category1");
        productCategoryRepository.save(category1);
        ProductCategory category2 = new ProductCategory();
        category2.setName("category2");
        category2.setProductCategoryCode("category2");
        productCategoryRepository.save(category2);
        return Arrays.asList(category1, category2);
    }

    private List<StepDto> mockSteps() {
        StepDto design = StepDto.builder()
                .name("Design")
                .assignedToUserId(designer.getId())
                .needApproval(true)
                .build();
        StepDto production = StepDto.builder()
                .name("Production")
                .assignedToUserId(produksi.getId())
                .needApproval(false)
                .build();
        StepDto stepPacking = StepDto.builder()
                .name("Packing")
                .assignedToUserId(packing.getId())
                .needApproval(false)
                .build();
        return Arrays.asList(design, production, stepPacking);
    }

    @Test
    public void test_create_shouldCreateWorkflow() {
        List<ProductCategory> categories = createCategories();
        SaveWorkflowRequest request = new SaveWorkflowRequest();
        request.setWorkflowName("Workflow Test");
        request.setCategoryProductIds(categories.stream().map(ProductCategory::getId).collect(Collectors.toList()));
        request.setSteps(mockSteps());

        String workflowCode = workflowService.generateWorkflowCode();
        WorkflowDto result = workflowService.createWorkflow(request, workflowCode);

        assertThat(workflowCode).startsWith(CodePrefix.WORKFLOW.getPrefix());
        assertThat(result.getName()).isEqualTo("Workflow Test");
        assertThat(result.getSteps().size()).isEqualTo(3);

        assertThat(workflowRepository.findById(result.getId()).get().getName()).isEqualTo("Workflow Test");
        Step design = stepRepository.findById(result.getSteps().get(0).getId()).get();
        Step production = stepRepository.findById(result.getSteps().get(1).getId()).get();
        Step packing = stepRepository.findById(result.getSteps().get(2).getId()).get();

        assertThat(design.getName()).isEqualTo("Design");
        assertThat(design.getNextStepId()).isEqualTo(production.getId());
        assertThat(production.getName()).isEqualTo("Production");
        assertThat(production.getNextStepId()).isEqualTo(packing.getId());
        assertThat(packing.getName()).isEqualTo("Packing");
        assertThat(packing.getNextStepId()).isNull();

        assertThat(design.getStatuses().size()).isEqualTo(4);
        List<Status> designStatus = new ArrayList<>(design.getStatuses());
        designStatus.sort(SortUtils.orderFlowFromTop());
        assertThat(designStatus.get(0).getName()).isEqualTo("To do");
        assertThat(designStatus.get(0).getNextStatusId()).isEqualTo(designStatus.get(1).getId());
        assertThat(designStatus.get(1).getName()).isEqualTo("In progress");
        assertThat(designStatus.get(1).getNextStatusId()).isEqualTo(designStatus.get(2).getId());
        assertThat(designStatus.get(2).getName()).isEqualTo("Waiting for approval");
        assertThat(designStatus.get(2).getNextStatusId()).isEqualTo(designStatus.get(3).getId());
        assertThat(designStatus.get(3).getName()).isEqualTo("Done");
        assertThat(designStatus.get(3).getNextStatusId()).isNull();

        assertThat(production.getStatuses().size()).isEqualTo(3);
        List<Status> productStatuses = new ArrayList<>(production.getStatuses());
        productStatuses.sort(SortUtils.orderFlowFromTop());
        assertThat(productStatuses.get(0).getName()).isEqualTo("To do");
        assertThat(productStatuses.get(0).getNextStatusId()).isEqualTo(productStatuses.get(1).getId());
        assertThat(productStatuses.get(1).getName()).isEqualTo("In progress");
        assertThat(productStatuses.get(1).getNextStatusId()).isEqualTo(productStatuses.get(2).getId());
        assertThat(productStatuses.get(2).getName()).isEqualTo("Done");
        assertThat(productStatuses.get(2).getNextStatusId()).isNull();
    }
}