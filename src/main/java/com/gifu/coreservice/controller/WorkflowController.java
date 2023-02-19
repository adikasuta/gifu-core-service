package com.gifu.coreservice.controller;

import com.gifu.coreservice.exception.InvalidRequestException;
import com.gifu.coreservice.model.dto.WorkflowDto;
import com.gifu.coreservice.model.request.SaveWorkflowRequest;
import com.gifu.coreservice.model.request.SearchWorkflowInput;
import com.gifu.coreservice.model.response.SingleResourceResponse;
import com.gifu.coreservice.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "api/workflow")
public class WorkflowController {

    @Autowired
    private WorkflowService workflowService;

    @GetMapping
    public ResponseEntity<SingleResourceResponse<Page<WorkflowDto>>> searchWorkflow
            (@RequestParam String categoryName, Pageable page) {
        try {
            SearchWorkflowInput input = new SearchWorkflowInput();
            input.setCategoryName(categoryName);
            Page<WorkflowDto> result = workflowService.searchWorkflow(input, page);
            return ResponseEntity.ok(new SingleResourceResponse<>(result));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<SingleResourceResponse<WorkflowDto>> postWorkflow(
            @Valid @RequestBody SaveWorkflowRequest request
    ) {
        try {
            String code = workflowService.generateWorkflowCode();
            WorkflowDto result = workflowService.createWorkflow(request, code);
            return ResponseEntity.ok(new SingleResourceResponse<>(result));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping
    public ResponseEntity<SingleResourceResponse<WorkflowDto>> putWorkflow(
            @Valid @RequestBody SaveWorkflowRequest request
    ) {
        try {
            if (request.getWorkflowId() == null) {
                throw new InvalidRequestException("Workflow id is required", null);
            }
            WorkflowDto result = workflowService.replaceWorkflow(request);
            return ResponseEntity.ok(new SingleResourceResponse<>(result));
        } catch (InvalidRequestException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new SingleResourceResponse<>(ex.getMessage(), String.valueOf(HttpStatus.BAD_REQUEST.value())));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
