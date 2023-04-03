package com.gifu.coreservice.controller;

import com.gifu.coreservice.entity.User;
import com.gifu.coreservice.model.dto.CheckoutOrderDto;
import com.gifu.coreservice.model.dto.DashboardOrderDto;
import com.gifu.coreservice.model.dto.ProductCategoryDto;
import com.gifu.coreservice.model.request.ConfirmOrderRequest;
import com.gifu.coreservice.model.request.CreateVaBillPaymentRequest;
import com.gifu.coreservice.model.request.SearchCheckoutOrderRequest;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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
            @RequestParam(required = false) String productType, @RequestParam(required = false) String periodFrom,
            @RequestParam(required = false) String periodUntil, @RequestParam String query,
            @RequestParam(required = false) List<String> statuses,
            Pageable pageable
    ) {
        try {

            SearchDashboardOrderRequest request = SearchDashboardOrderRequest.builder()
                    .productType(productType)
                    .query(query)
                    .statuses(statuses)
                    .build();
            if(StringUtils.hasText(periodFrom)){
                request.setPeriodFrom(LocalDate.parse(periodFrom, DateTimeFormatter.ISO_LOCAL_DATE));
            }
            if(StringUtils.hasText(periodUntil)){
                request.setPeriodUntil(LocalDate.parse(periodUntil, DateTimeFormatter.ISO_LOCAL_DATE));
            }
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

    @GetMapping("/order-checkout")
    public ResponseEntity<SingleResourceResponse<Page<CheckoutOrderDto>>> searchOrderCheckout(
            @RequestParam(required = false) String productType, @RequestParam(required = false) String periodFrom,
            @RequestParam(required = false) String periodUntil, @RequestParam String query,
            @RequestParam(required = false) List<String> statuses,
            Pageable pageable
    ) {
        try {

            return ResponseEntity.ok(new SingleResourceResponse<>("Success"));
        } catch (Exception ex) {
            log.error("ERROR POST VA BILL: " + ex.getMessage(), ex);
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
