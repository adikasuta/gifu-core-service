package com.gifu.coreservice.service;

import com.gifu.coreservice.entity.*;
import com.gifu.coreservice.enumeration.OrderStatus;
import com.gifu.coreservice.enumeration.PermissionEnum;
import com.gifu.coreservice.exception.InvalidRequestException;
import com.gifu.coreservice.model.dto.ProgressTrackerDto;
import com.gifu.coreservice.model.dto.StepStatusTodoDto;
import com.gifu.coreservice.model.dto.StepTodoDto;
import com.gifu.coreservice.model.dto.TimelineTrackerDto;
import com.gifu.coreservice.model.request.ChangeStatusRequest;
import com.gifu.coreservice.model.request.UpdateStepStatusRequest;
import com.gifu.coreservice.repository.*;
import com.gifu.coreservice.utils.SortUtils;
import liquibase.pro.packaged.P;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import javax.transaction.Transactional;
import java.io.OptionalDataException;
import java.sql.Time;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TimelineService {
    @Autowired
    private TimelineRepository timelineRepository;
    @Autowired
    private OrderCheckoutPaymentRepository orderCheckoutPaymentRepository;
    @Autowired
    private OrderCheckoutRepository orderCheckoutRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private WorkflowRepository workflowRepository;
    @Autowired
    private StepRepository stepRepository;
    @Autowired
    private TimelineStepRepository timelineStepRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private TimelineStepStatusRepository timelineStepStatusRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private HistoricalOrderStatusService historicalOrderStatusService;
    @Autowired
    private UserPermissionService userPermissionService;

    private static final String SYSTEM = "SYSTEM";

    public ProgressTrackerDto trackOrder(String orderCode, String phoneNumber) throws InvalidRequestException {
        Optional<Order> orderOpt = orderRepository.findByOrderCode(orderCode);
        if (orderOpt.isEmpty()) {
            throw new InvalidRequestException("Order code=" + orderCode + " is not valid");
        }
        Order order = orderOpt.get();
        if (!phoneNumber.equals(order.getCustomerPhoneNo())) {
            throw new InvalidRequestException("Phone number=" + phoneNumber + " is not valid");
        }
        List<TimelineTrackerDto> steps = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("id").ascending());

        List<HistoricalOrderStatus> historicalStatus = historicalOrderStatusService.findByOrderIdAndStatusIn(
                order.getId(),
                List.of(OrderStatus.WAITING_FOR_CONFIRMATION.name(), OrderStatus.IN_PROGRESS_PRODUCTION.name()),
                pageable);
        for (HistoricalOrderStatus it : historicalStatus) {
            steps.add(TimelineTrackerDto.builder()
                    .stepName("Preparation")
                    .lastStatus(it.getStatus())
                    .createdDate(it.getCreatedDate())
                    .build());
        }

        List<Timeline> timelineList = timelineRepository.findByOrderId(order.getId());//it should be only one
        if (!timelineList.isEmpty()) {

            List<TimelineStep> timelineSteps = timelineStepRepository.findTimelineStepByTimelineIdWithPageable(timelineList.get(0).getId(), pageable);
            for (TimelineStep it : timelineSteps) {
                TimelineTrackerDto step = TimelineTrackerDto.builder()
                        .stepName(it.getStepName())
                        .createdDate(it.getCreatedDate())
                        .build();
                TimelineStepStatus lastStatus = this.getCurrentTimelineStepStatus(it.getId());
                if (lastStatus != null) {
                    step.setLastStatus(lastStatus.getStatusName());
                }
                steps.add(step);
            }
        }

        return ProgressTrackerDto.builder()
                .productName(order.getProductName())
                .quantity(order.getQuantity())
                .orderCode(orderCode)
                .lastOrderStatus(order.getStatus())
                .timeline(steps)
                .build();
    }

    private TimelineStep getCurrentTimelineStep(Long timelineId) {
        Pageable pageable = PageRequest.of(0, 1, Sort.by("id").descending());
        List<TimelineStep> steps = timelineStepRepository.findTimelineStepByTimelineIdWithPageable(timelineId, pageable);
        if (steps.isEmpty()) {
            return null;
        }
        return steps.get(0);
    }

    private TimelineStepStatus getCurrentTimelineStepStatus(Long timelineStepId) {
        Pageable pageable = PageRequest.of(0, 1, Sort.by("id").descending());
        List<TimelineStepStatus> timelineStepStatuses = timelineStepStatusRepository.findByTimelineStepIdWithPageable(timelineStepId, pageable);
        if (timelineStepStatuses.isEmpty()) {
            return null;
        }
        return timelineStepStatuses.get(0);
    }

    @Transactional
    public void handleChangeStepStatus(UpdateStepStatusRequest request, User requester) throws InvalidRequestException {
        boolean isApprover = userPermissionService.hasPermission(PermissionEnum.APPROVE_STEP_STATUS, requester.getId());
        Optional<Timeline> timelineOpt = timelineRepository.findById(request.getTimelineId());
        if (timelineOpt.isEmpty()) {
            throw new InvalidRequestException("Timeline (id=" + request.getTimelineId() + ") is not found");
        }
        Timeline timeline = timelineOpt.get();
        TimelineStep currentStep = getCurrentTimelineStep(timeline.getId());
        if (currentStep == null) {
            throw new InvalidRequestException("Timeline (id=" + request.getTimelineId() + ") has no started yet");
        }
        if (!currentStep.getStepId().equals(request.getCurrentStepId())) {
            throw new InvalidRequestException("Timeline (id=" + request.getTimelineId() + ") step is not synced with request");
        }
        Optional<Step> masterStepOpt = stepRepository.findById(currentStep.getStepId());
        if (masterStepOpt.isEmpty()) {
            throw new InvalidRequestException("Timeline (id=" + request.getTimelineId() + ") step is not existed");
        }
        Step masterStep = masterStepOpt.get();
        if (!masterStep.getDefaultAssignedUserId().equals(requester.getId())) {
            if (masterStep.getNeedApproval() && !isApprover) {
                throw new InvalidRequestException("Update request for Timeline (id=" + request.getTimelineId() + ") is initialized by unauthorized user");
            }
        }
        TimelineStepStatus currentStepStatus = getCurrentTimelineStepStatus(currentStep.getId());
        if (currentStepStatus == null) {
            throw new InvalidRequestException("Timeline (id=" + request.getTimelineId() + ") step status is not existed");
        }
        Optional<Status> statusOpt = statusRepository.findById(currentStepStatus.getStatusId());
        if (statusOpt.isEmpty()) {
            throw new InvalidRequestException("Timeline (id=" + request.getTimelineId() + ") step status is not existed");
        }
        Status status = statusOpt.get();
        if (status.getNextStatusId() == null) {
            throw new InvalidRequestException("Timeline (id=" + request.getTimelineId() + ") step already finished");
        }

        Optional<Status> newStatusOpt = statusRepository.findById(request.getNewStatusId());
        if (newStatusOpt.isEmpty()) {
            throw new InvalidRequestException("Timeline (id=" + request.getTimelineId() + ") new step status is not existed");
        }
        Status newStatus = newStatusOpt.get();
        TimelineStepStatus newTimelineStepStatus = new TimelineStepStatus();
        newTimelineStepStatus.setStatusId(request.getNewStatusId());
        newTimelineStepStatus.setTimelineStepId(currentStep.getId());
        newTimelineStepStatus.setCreatedDate(ZonedDateTime.now());
        newTimelineStepStatus.setStatusName(newStatus.getName());
        newTimelineStepStatus.setCreatedBy(requester.getEmail());
        timelineStepStatusRepository.save(newTimelineStepStatus);
        if (newStatus.getNextStatusId() == null) {
            initiateNextStep(timeline, requester);
        }
    }

    private void generateNewTimelineStep(Long timelineId, Step stepMaster, String createdBy) {
        TimelineStep timelineStep = new TimelineStep();
        timelineStep.setTimelineId(timelineId);
        timelineStep.setStepId(stepMaster.getId());
        timelineStep.setStepName(stepMaster.getName());
        timelineStep.setAssigneeUserId(stepMaster.getDefaultAssignedUserId());
        timelineStep.setCreatedBy(createdBy);
        timelineStep.setCreatedDate(ZonedDateTime.now());
        timelineStepRepository.save(timelineStep);
        List<Status> statuses = statusRepository.findByStepId(stepMaster.getId());
        statuses.sort(SortUtils.orderFlowFromTop());
        Status firstStatus = statuses.get(0);

        TimelineStepStatus timelineStepStatus = new TimelineStepStatus();
        timelineStepStatus.setStatusId(firstStatus.getId());
        timelineStepStatus.setTimelineStepId(timelineStep.getId());
        timelineStepStatus.setCreatedBy(createdBy);
        timelineStepStatus.setCreatedDate(ZonedDateTime.now());
        timelineStepStatus.setStatusName(firstStatus.getName());
        timelineStepStatusRepository.save(timelineStepStatus);
    }

    private void initiateNextStep(Timeline timeline, User requester) throws InvalidRequestException {
        TimelineStep currentStep = getCurrentTimelineStep(timeline.getId());
        if (currentStep == null) {
            throw new InvalidRequestException("Timeline (id=" + timeline.getId() + ") has no started yet");
        }
        Optional<Step> stepMasterOpt = stepRepository.findById(currentStep.getStepId());
        if (stepMasterOpt.isEmpty()) {
            throw new InvalidRequestException("Timeline (id=" + timeline.getId() + ") step is invalid");
        }
        Step currentStepMaster = stepMasterOpt.get();
        if (currentStepMaster.getNextStepId() == null) {
            timeline.setDone(true);
            timelineRepository.save(timeline);
            historicalOrderStatusService.changeStatus(ChangeStatusRequest.builder()
                    .orderId(timeline.getOrderId())
                    .status(OrderStatus.DONE.name())
                    .updaterEmail(SYSTEM)
                    .build());
            return;
        }
        Optional<Step> newStepMasterOpt = stepRepository.findById(currentStepMaster.getNextStepId());
        if (newStepMasterOpt.isEmpty()) {
            throw new InvalidRequestException("Timeline (id=" + timeline.getId() + ") step is invalid");
        }
        Step newStepMaster = newStepMasterOpt.get();
        generateNewTimelineStep(timeline.getId(), newStepMaster, requester.getEmail());
    }

    //Step RULE:
    //show all current userId is assigned to
    //show steps that its current status needs permission to update, and this userId has that access
    //###############################################################################################
    //Step Status RULE:
    //if current userId can access current step status, show all status before current and one step ahead current
    //if current userId cannot access current step status, only show current step status
    //if current step status is the last one, only show current step status
    public List<StepTodoDto> findStepTodoByUserId(Long userId) {
        List<Timeline> activeTimelines = timelineRepository.findAllActiveTimeline();
        boolean isApprover = userPermissionService.hasPermission(PermissionEnum.APPROVE_STEP_STATUS, userId);
        List<StepTodoDto> stepTodoList = new ArrayList<>();
        for (Timeline timeline : activeTimelines) {
            TimelineStep currentStep = getCurrentTimelineStep(timeline.getId());
            if (currentStep == null) {
                continue;
            }
            TimelineStepStatus currentStatus = getCurrentTimelineStepStatus(currentStep.getId());
            if (currentStatus == null) {
                continue;
            }
            Optional<Status> statusOpt = statusRepository.findById(currentStatus.getStatusId());
            if (statusOpt.isEmpty()) {
                continue;
            }
            Status status = statusOpt.get();
            StepTodoDto stepToDo = StepTodoDto.builder()
                    .id(currentStep.getStepId())
                    .timelineId(timeline.getId())
                    .assigneeUserId(currentStep.getAssigneeUserId())
                    .name(currentStep.getStepName())
                    .currentStatusId(currentStatus.getStatusId())
                    .currentStatusName(currentStatus.getStatusName())
                    .build();
            List<Status> statuses = new ArrayList<>();
            statuses.add(status);
            if (PermissionEnum.APPROVE_STEP_STATUS.name().equals(status.getPermissionCode())) {
                if(isApprover){
                    statuses = new ArrayList<>(getModifiableStatus(status, currentStep));
                }
            } else if (currentStep.getAssigneeUserId().equals(userId)) {
                statuses = new ArrayList<>(getModifiableStatus(status, currentStep));
            }

            Optional<User> userOpt = userRepository.findById(currentStep.getAssigneeUserId());
            userOpt.ifPresent(user -> stepToDo.setAssigneeName(user.getName()));
            stepToDo.setStatuses(statuses);
            stepToDo.setOrderDto(orderService.getOrderDtoById(timeline.getOrderId()));
            stepTodoList.add(stepToDo);
        }
        return stepTodoList;
    }

    private List<Status> getModifiableStatus(Status currentStatus, TimelineStep currentStep) {
        List<Status> statuses = new ArrayList<>();
        List<Status> allStatus = statusRepository.findByStepId(currentStep.getStepId());
        allStatus.sort(SortUtils.orderFlowFromTop());
        boolean hasPassedCurrentStatus = false;
        for (Status it : allStatus) {
            if (it.getId() == currentStatus.getId()) {
                hasPassedCurrentStatus = true;
            }
            if (!hasPassedCurrentStatus) {
                statuses.add(it);
            } else if (it.getId() == currentStatus.getId()) {
                statuses.add(it);
            } else if (it.getId() == currentStatus.getNextStatusId()) {
                statuses.add(it);
            }
        }
        return statuses;
    }

    @Transactional
    public void startProductionTimeline(Bill bill) {
        Optional<OrderCheckoutPayment> orderCheckoutPaymentOpt = orderCheckoutPaymentRepository.findById(bill.getOrderCheckoutPaymentId());
        if (orderCheckoutPaymentOpt.isEmpty()) {
            log.error("Bill (id=" + bill.getId() + ") is not affiliated with any orderCheckoutPayment");
            return;
        }
        OrderCheckoutPayment orderCheckoutPayment = orderCheckoutPaymentOpt.get();
        orderCheckoutPayment.setPaid(true);
        orderCheckoutPayment.setPaymentDate(ZonedDateTime.now());
        orderCheckoutPaymentRepository.save(orderCheckoutPayment);
        Optional<OrderCheckout> orderCheckoutOpt = orderCheckoutRepository.findById(orderCheckoutPayment.getOrderCheckoutId());
        if (orderCheckoutOpt.isEmpty()) {
            log.error("Bill (id=" + bill.getId() + ") is not affiliated with any orderCheckoutPayment");
            return;
        }
        OrderCheckout orderCheckout = orderCheckoutOpt.get();
        List<Order> orders = orderRepository.findByOrderCheckoutId(orderCheckout.getId());
        for (Order item : orders) {
            List<Timeline> timelines = timelineRepository.findByOrderId(item.getId());
            if (!timelines.isEmpty()) {
                log.warn("Order (id=" + item.getId() + ") timeline already started");
                continue;
            }
            Optional<Workflow> workflowOpt = workflowRepository.findById(item.getWorkflowId());
            if (workflowOpt.isEmpty()) {
                log.warn("Order (id=" + item.getId() + ") is not affiliated with any workflow");
                continue;
            }
            Workflow workflow = workflowOpt.get();
            Timeline timeline = new Timeline();
            timeline.setCreatedBy(SYSTEM);
            timeline.setCreatedDate(ZonedDateTime.now());
            timeline.setOrderId(item.getId());
            timeline.setDone(false);
            timeline.setWorkflowId(workflow.getId());
            timelineRepository.save(timeline);

            List<Step> steps = stepRepository.findByWorkflowId(workflow.getId());
            steps.sort(SortUtils.orderFlowFromTop());
            Step firstStep = steps.get(0);

            generateNewTimelineStep(timeline.getId(), firstStep, SYSTEM);

            try {
                item.setFirstPaymentDate(ZonedDateTime.now());
                orderRepository.save(item);
                historicalOrderStatusService.changeStatus(ChangeStatusRequest.builder()
                        .orderId(item.getId())
                        .status(OrderStatus.IN_PROGRESS_PRODUCTION.name())
                        .updaterEmail(SYSTEM)
                        .build());
            } catch (InvalidRequestException e) {
                log.error("ERROR when update order status:" + e.getMessage(), e);
            }
        }

    }
}
