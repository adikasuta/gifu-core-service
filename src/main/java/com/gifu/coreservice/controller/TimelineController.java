package com.gifu.coreservice.controller;

import com.gifu.coreservice.entity.User;
import com.gifu.coreservice.model.dto.StepTodoDto;
import com.gifu.coreservice.model.request.CreateVaBillPaymentRequest;
import com.gifu.coreservice.model.request.UpdateStepStatusRequest;
import com.gifu.coreservice.model.response.SingleResourceResponse;
import com.gifu.coreservice.service.TimelineService;
import com.gifu.coreservice.utils.SessionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "api/timeline")
public class TimelineController {

    @Autowired
    private TimelineService timelineService;

    @GetMapping("/tasks")
    public ResponseEntity<SingleResourceResponse<List<StepTodoDto>>> getTasks() {
        try {
            User context = SessionUtils.getUserContext();
            List<StepTodoDto> res = timelineService.findStepTodoByUserId(context.getId());
            return ResponseEntity.ok(new SingleResourceResponse<>(res));
        } catch (Exception ex) {
            log.error("ERROR GET TASKS: " + ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    @PostMapping("/update-status")
    public ResponseEntity<SingleResourceResponse<String>> updateStatus(
            @RequestBody UpdateStepStatusRequest request
    ) {
        try {
            User context = SessionUtils.getUserContext();
            timelineService.handleChangeStepStatus(request, context);
            return ResponseEntity.ok(new SingleResourceResponse<>("Success"));
        } catch (Exception ex) {
            log.error("ERROR POST UPDATE TIMELINE STATUS: " + ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }
}
