package com.gifu.coreservice.service.paymentscheme;

import com.gifu.coreservice.entity.OrderCheckout;
import com.gifu.coreservice.entity.OrderCheckoutPayment;
import com.gifu.coreservice.repository.OrderCheckoutPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class DownPaymentSchemeService extends AbstractCreatePaymentScheme {
    @Autowired
    private OrderCheckoutPaymentRepository orderCheckoutPaymentRepository;

    private static final List<Float> PAYMENT_PROPORTIONS = List.of(0.50F,0.30F,0.20F);

    @Override
    public List<OrderCheckoutPayment> createPaymentScheme(OrderCheckout orderCheckout) {
        List<OrderCheckoutPayment> orderCheckoutPayments = new ArrayList<>();
        int sequence = 1;
        for(Float proportion : PAYMENT_PROPORTIONS){
            OrderCheckoutPayment orderCheckoutPayment = new OrderCheckoutPayment();
            orderCheckoutPayment.setOrderCheckoutId(orderCheckout.getId());
            orderCheckoutPayment.setSequenceNo(sequence);
            orderCheckoutPayment.setAmount(orderCheckout.getGrandTotal().multiply(BigDecimal.valueOf(proportion)));
            orderCheckoutPaymentRepository.save(orderCheckoutPayment);
            orderCheckoutPayments.add(orderCheckoutPayment);
            sequence++;
        }
        return orderCheckoutPayments;
    }
}
