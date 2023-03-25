package com.gifu.coreservice.service.paymentscheme;

import com.gifu.coreservice.entity.OrderCheckout;
import com.gifu.coreservice.entity.OrderCheckoutPayment;

import java.util.List;

public abstract class AbstractCreatePaymentScheme {
    abstract public List<OrderCheckoutPayment> createPaymentScheme(OrderCheckout orderCheckout);
}
