package com.gifu.coreservice.controller.publicapi;

import com.gifu.coreservice.model.request.ConfirmOrderRequest;
import com.gifu.coreservice.model.request.OrderRequest;
import com.gifu.coreservice.model.response.SingleResourceResponse;
import com.gifu.coreservice.service.OrderPaymentService;
import com.gifu.coreservice.service.OrderService;
import io.netty.util.internal.UnstableApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "public/api/order")
public class PublicOrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderPaymentService orderPaymentService;

    @PostMapping
    public ResponseEntity<SingleResourceResponse<String>> postOrder(
            @RequestBody OrderRequest request
    ) {
        try {
            orderService.addToCartSouvenir(request);
            return ResponseEntity.ok(new SingleResourceResponse<>("Success"));
        } catch (Exception ex) {
            log.error("ERROR POST ORDER: " + ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }
}
