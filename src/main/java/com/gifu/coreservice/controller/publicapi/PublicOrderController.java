package com.gifu.coreservice.controller.publicapi;

import com.gifu.coreservice.service.OrderPaymentService;
import com.gifu.coreservice.service.OrderService;
import io.netty.util.internal.UnstableApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/public/order")
public class PublicOrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderPaymentService orderPaymentService;
}
