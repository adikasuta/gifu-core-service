package com.gifu.coreservice.controller;

import com.gifu.coreservice.entity.User;
import com.gifu.coreservice.model.dto.DashboardOrderDto;
import com.gifu.coreservice.model.dto.ProductCategoryDto;
import com.gifu.coreservice.model.request.ConfirmOrderRequest;
import com.gifu.coreservice.model.request.CreateVaBillPaymentRequest;
import com.gifu.coreservice.model.request.SearchDashboardOrderRequest;
import com.gifu.coreservice.model.response.SingleResourceResponse;
import com.gifu.coreservice.service.OrderPaymentService;
import com.gifu.coreservice.service.OrderService;
import com.gifu.coreservice.utils.SessionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderPaymentService orderPaymentService;

    @GetMapping("/dashboard")
    public ResponseEntity<SingleResourceResponse<Page<DashboardOrderDto>>> getDashboardOrder(
            @RequestParam String productType, @RequestParam ZonedDateTime periodFrom,
            @RequestParam ZonedDateTime periodUntil, @RequestParam String query,
            Pageable pageable
    ) {
        try {
            SearchDashboardOrderRequest request = SearchDashboardOrderRequest.builder()
                    .productType(productType)
                    .periodFrom(periodFrom)
                    .periodUntil(periodUntil)
                    .query(query)
                    .build();
            Page<DashboardOrderDto> result = orderService.getDashboardOrderPage(request, pageable);
            return ResponseEntity.ok(new SingleResourceResponse<>(result));
        } catch (Exception ex) {
            log.error("ERROR GET ORDER DASHBOARD: " + ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    @PostMapping("/confirmation")
    public ResponseEntity<SingleResourceResponse<String>> postConfirmation(
            @RequestBody ConfirmOrderRequest request
    ) {
        try {
            User context =  SessionUtils.getUserContext();
            request.setUpdaterEmail(context.getEmail());
            orderService.confirmOrder(request);
            return ResponseEntity.ok(new SingleResourceResponse<>("Success"));
        } catch (Exception ex) {
            log.error("ERROR POST ORDER CONFIRMATION: " + ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    @PostMapping("/va-bill")
    public ResponseEntity<SingleResourceResponse<String>> postVaBill(
            @RequestBody CreateVaBillPaymentRequest request
    ) {
        try {
            User context =  SessionUtils.getUserContext();
            request.setCreatedBy(context.getEmail());
            orderPaymentService.createVaBillPayment(request);
            return ResponseEntity.ok(new SingleResourceResponse<>("Success"));
        } catch (Exception ex) {
            log.error("ERROR POST VA BILL: " + ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }


}
