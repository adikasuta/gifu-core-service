package com.gifu.coreservice.service.paymentscheme;

import com.gifu.coreservice.entity.OrderCheckout;
import com.gifu.coreservice.entity.OrderCheckoutPayment;
import com.gifu.coreservice.repository.OrderCheckoutPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CashPaymentSchemeService extends AbstractCreatePaymentScheme {
    @Autowired
    private OrderCheckoutPaymentRepository orderCheckoutPaymentRepository;

    @Override
    public List<OrderCheckoutPayment> createPaymentScheme(OrderCheckout orderCheckout) {
        OrderCheckoutPayment payment = new OrderCheckoutPayment();
        payment.setAmount(orderCheckout.getGrandTotal());
        payment.setOrderCheckoutId(orderCheckout.getId());
        payment.setSequenceNo(1);
        payment = orderCheckoutPaymentRepository.save(payment);
        return List.of(payment);
    }
}
