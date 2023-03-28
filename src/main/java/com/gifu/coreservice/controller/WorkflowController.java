package com.gifu.coreservice.controller;

import com.gifu.coreservice.entity.User;
import com.gifu.coreservice.exception.InvalidRequestException;
import com.gifu.coreservice.model.dto.WorkflowDto;
import com.gifu.coreservice.model.request.ChangeWorkflowNameRequest;
import com.gifu.coreservice.model.request.SaveWorkflowRequest;
import com.gifu.coreservice.model.request.SearchWorkflowInput;
import com.gifu.coreservice.model.response.SingleResourceResponse;
import com.gifu.coreservice.service.WorkflowService;
import com.gifu.coreservice.utils.SessionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping(path = "api/workflow")
public class WorkflowController {

    @Autowired
    private WorkflowService workflowService;

    @GetMapping
    public ResponseEntity<SingleResourceResponse<Page<WorkflowDto>>> searchWorkflow
            (@RequestParam(required = false) Long productCategoryId, @RequestParam(required = false) String query, Pageable page) {
        try {
            SearchWorkflowInput input = new SearchWorkflowInput();
            input.setProductCategoryId(productCategoryId);
            input.setQuery(query);
            Page<WorkflowDto> result = workflowService.searchWorkflow(input, page);
            return ResponseEntity.ok(new SingleResourceResponse<>(result));
        } catch (Exception ex) {
            log.error("ERROR GET WORKFLOW: " + ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    @PostMapping
    public ResponseEntity<SingleResourceResponse<WorkflowDto>> postWorkflow(
            @Valid @RequestBody SaveWorkflowRequest request
    ) {
        try {
            String code = workflowService.generateWorkflowCode();
            User user = SessionUtils.getUserContext();
            WorkflowDto result = workflowService.createWorkflow(request, code, user.getEmail());
            return ResponseEntity.ok(new SingleResourceResponse<>(result));
        } catch (Exception ex) {
            log.error("ERROR POST WORKFLOW: " + ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    @PutMapping("/name")
    public ResponseEntity<SingleResourceResponse<WorkflowDto>> changeWorkflowName(
            @Valid @RequestBody ChangeWorkflowNameRequest request
    ) {
        try {
            User user = SessionUtils.getUserContext();
            WorkflowDto result = workflowService.changeName(request, user.getEmail());
            return ResponseEntity.ok(new SingleResourceResponse<>(result));
        } catch (InvalidRequestException ex) {
            log.error("ERROR PUT WORKFLOW NAME: " + ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new SingleResourceResponse<>(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception ex) {
            log.error("ERROR PUT WORKFLOW NAME: " + ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PutMapping
    public ResponseEntity<SingleResourceResponse<WorkflowDto>> putWorkflow(
            @Valid @RequestBody SaveWorkflowRequest request
    ) {
        try {
            if (request.getId() == null) {
                throw new InvalidRequestException("Workflow id is required", null);
            }
            User user = SessionUtils.getUserContext();
            WorkflowDto result;
            if (workflowService.isMinorChanges(request)) {
                result = workflowService.minorUpdate(request, user.getEmail());
            } else {
                result = workflowService.replaceWorkflow(request, user.getEmail());
            }
            return ResponseEntity.ok(new SingleResourceResponse<>(result));
        } catch (InvalidRequestException ex) {
            log.error("ERROR PUT WORKFLOW: " + ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new SingleResourceResponse<>(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception ex) {
            log.error("ERROR PUT WORKFLOW: " + ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @DeleteMapping("/{workflowId}")
    public ResponseEntity<SingleResourceResponse<WorkflowDto>> deleteWorkflow(
            @PathVariable("workflowId") Long workflowId
    ) {
        try {
            User user = SessionUtils.getUserContext();
            WorkflowDto result = workflowService.deleteWorkflow(workflowId, user.getEmail());
            return ResponseEntity.ok(new SingleResourceResponse<>(result));
        } catch (InvalidRequestException ex) {
            log.error("ERROR DELETE WORKFLOW: " + ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new SingleResourceResponse<>(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception ex) {
            log.error("ERROR DELETE WORKFLOW: " + ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}
