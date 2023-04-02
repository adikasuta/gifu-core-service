package com.gifu.coreservice.controller.publicapi;

import com.gifu.coreservice.exception.InvalidRequestException;
import com.gifu.coreservice.model.dto.ProgressTrackerDto;
import com.gifu.coreservice.model.response.SingleResourceResponse;
import com.gifu.coreservice.service.TimelineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "public/api/timeline")
public class PublicTimelineController {

    @Autowired
    private TimelineService timelineService;

    @GetMapping("/order-tracker")
    public ResponseEntity<SingleResourceResponse<List<ProgressTrackerDto>>> getOrderTracker(
            @RequestParam List<String> orderCodes,
            @RequestParam String phoneNumber
    ) {
        try {
            List<ProgressTrackerDto> res = new ArrayList<>();
            for (String orderCode : orderCodes) {
                res.add(timelineService.trackOrder(orderCode, phoneNumber));
            }
            return ResponseEntity.ok(new SingleResourceResponse<>(res));
        } catch (InvalidRequestException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new SingleResourceResponse<>(ex.getMessage(), HttpStatus.BAD_REQUEST.value())
            );
        } catch (Exception ex) {
            log.error("ERROR ORDER TRACKER: " + ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }
}
