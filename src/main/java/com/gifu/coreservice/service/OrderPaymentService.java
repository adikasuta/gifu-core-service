package com.gifu.coreservice.service;

import com.gifu.coreservice.entity.Order;
import com.gifu.coreservice.entity.OrderCheckout;
import com.gifu.coreservice.entity.OrderCheckoutPayment;
import com.gifu.coreservice.entity.Bill;
import com.gifu.coreservice.enumeration.*;
import com.gifu.coreservice.exception.InvalidRequestException;
import com.gifu.coreservice.exception.ObjectToJsonStringException;
import com.gifu.coreservice.model.request.ChangeStatusRequest;
import com.gifu.coreservice.model.request.CreateVaBillPaymentRequest;
import com.gifu.coreservice.model.request.OrderCheckoutRequest;
import com.gifu.coreservice.repository.OrderCheckoutPaymentRepository;
import com.gifu.coreservice.repository.OrderCheckoutRepository;
import com.gifu.coreservice.repository.OrderRepository;
import com.gifu.coreservice.repository.BillRepository;
import com.gifu.coreservice.service.paymentscheme.AbstractCreatePaymentScheme;
import com.gifu.coreservice.service.paymentscheme.CashPaymentSchemeService;
import com.gifu.coreservice.service.paymentscheme.DownPaymentSchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderPaymentService {

    @Autowired
    private OrderCheckoutRepository orderCheckoutRepository;
    @Autowired
    private OrderCheckoutPaymentRepository orderCheckoutPaymentRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private HistoricalOrderStatusService historicalOrderStatusService;
    @Autowired
    private CashPaymentSchemeService cashPaymentSchemeService;
    @Autowired
    private DownPaymentSchemeService downPaymentSchemeService;
    @Autowired
    private BillRepository billRepository;
    @Autowired
    private XenditService xenditService;

    private static final String DEFAULT_REMARKS = "PEMBAYARAN KE-";

    private AbstractCreatePaymentScheme getPaymentSchemeCreator(PaymentTerm paymentTerm) {
        Map<PaymentTerm, AbstractCreatePaymentScheme> mapper = new HashMap<>();
        mapper.put(PaymentTerm.CASH, cashPaymentSchemeService);
        mapper.put(PaymentTerm.DOWN_PAYMENT, downPaymentSchemeService);

        return mapper.get(paymentTerm);
    }

    private List<OrderCheckoutPayment> createPayments(OrderCheckout orderCheckout) {
        AbstractCreatePaymentScheme paymentSchemeCreator = getPaymentSchemeCreator(PaymentTerm.valueOf(orderCheckout.getPaymentTerm()));
        return paymentSchemeCreator.createPaymentScheme(orderCheckout);
    }

    //TODO: create get cart item service
    public OrderCheckout orderCheckout(OrderCheckoutRequest request) throws InvalidRequestException {
        OrderCheckout orderCheckout = new OrderCheckout();
        orderCheckout.setGrandTotal(BigDecimal.ZERO);
        orderCheckout.setPaymentTerm(request.getPaymentTermCode());
        orderCheckout.setCreatedDate(ZonedDateTime.now());
        orderCheckoutRepository.save(orderCheckout);
        BigDecimal grandTotal = BigDecimal.ZERO;
        for (Long id : request.getOrderIds()) {
            Optional<Order> optOrder = orderRepository.findById(id);
            if (optOrder.isPresent()) {
                Order order = optOrder.get();
                order.setOrderCheckoutId(orderCheckout.getId());
                order.setStatus(OrderStatus.WAITING_FOR_CONFIRMATION.name());
                order.setUpdatedDate(ZonedDateTime.now());
                order.setUpdatedBy(SystemConst.SYSTEM.name());
                orderRepository.save(order);
                historicalOrderStatusService.changeStatus(ChangeStatusRequest.builder()
                        .orderId(order.getId())
                        .status(OrderStatus.WAITING_FOR_CONFIRMATION.name())
                        .updaterEmail(SystemConst.SYSTEM.name())
                        .build());
                grandTotal = grandTotal.add(order.getGrandTotal());
            }
        }
        orderCheckout.setGrandTotal(grandTotal);
        orderCheckout.setCreatedDate(ZonedDateTime.now());
        orderCheckout.setUpdatedDate(ZonedDateTime.now());
        orderCheckout.setUpdatedBy(SystemConst.SYSTEM.name());
        orderCheckout = orderCheckoutRepository.save(orderCheckout);
        createPayments(orderCheckout);
        return orderCheckout;
    }

    //TODO: write code to show to be created bill list
    public Bill createVaBillPayment(CreateVaBillPaymentRequest request) throws InvalidRequestException, ObjectToJsonStringException {
        Optional<OrderCheckoutPayment> orderCheckoutPaymentOpt = orderCheckoutPaymentRepository.findByOrderCheckoutIdAndSequenceNo(request.getOrderCheckoutId(), request.getSequenceNo());
        if(orderCheckoutPaymentOpt.isEmpty()){
            throw new InvalidRequestException("Order Checkout is not existed", null);
        }
        OrderCheckoutPayment orderCheckoutPayment = orderCheckoutPaymentOpt.get();
        Bill bill = new Bill();
        bill.setOrderCheckoutPaymentId(orderCheckoutPayment.getId());
        bill.setAmount(orderCheckoutPayment.getAmount());
        bill.setCreatedDate(ZonedDateTime.now());
        bill.setExpiryDate(ZonedDateTime.now().plusDays(1));
        bill.setRemarks(DEFAULT_REMARKS+request.getSequenceNo());
        bill.setStatus(BillStatus.PENDING.name());
        bill.setCreatedBy(request.getCreatedBy());
        bill.setPaymentPartner(PaymentPartner.XENDIT.name());//possible to change/add another payment partner in future
        billRepository.save(bill);
        xenditService.createVaClose(bill);//possible to change/add another payment partner in future
        if(request.getSequenceNo() == 1){
            List<Order> orders = orderRepository.findByOrderCheckoutId(orderCheckoutPayment.getOrderCheckoutId());
            for(Order order : orders){
                historicalOrderStatusService.changeStatus(ChangeStatusRequest.builder()
                        .orderId(order.getId())
                        .status(OrderStatus.WAITING_FOR_PAYMENT.name())
                        .updaterEmail(request.getCreatedBy())
                        .build());
            }
        }
        return bill;
    }

}
