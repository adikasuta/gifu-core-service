package com.gifu.coreservice.service;

import com.gifu.coreservice.entity.*;
import com.gifu.coreservice.enumeration.*;
import com.gifu.coreservice.exception.InvalidRequestException;
import com.gifu.coreservice.exception.ObjectToJsonStringException;
import com.gifu.coreservice.model.dto.*;
import com.gifu.coreservice.model.request.ChangeStatusRequest;
import com.gifu.coreservice.model.request.CreateVaBillPaymentRequest;
import com.gifu.coreservice.model.request.OrderCheckoutRequest;
import com.gifu.coreservice.repository.*;
import com.gifu.coreservice.service.paymentscheme.AbstractCreatePaymentScheme;
import com.gifu.coreservice.service.paymentscheme.CashPaymentSchemeService;
import com.gifu.coreservice.service.paymentscheme.DownPaymentSchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Autowired
    private OrderVariantRepository orderVariantRepository;
    @Autowired
    private OrderVariantInfoRepository orderVariantInfoRepository;
    @Autowired
    private ProductRepository productRepository;

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

    public List<CartItemDto> getCartItems(String customerEmail) {
        List<Order> orders = orderRepository.findByCustomerEmailAndStatus(customerEmail, OrderStatus.IN_CART.name());
        return orders.stream().map(it -> {
            Optional<Product> productOpt = productRepository.findById(it.getProductId());
            CartItemDto dto = CartItemDto.builder()
                    .orderCode(it.getOrderCode())
                    .productName(it.getProductName())
                    .productType(it.getProductType())
                    .quantity(it.getQuantity())
                    .productPrice(it.getProductPrice())
                    .variantPrice(it.getVariantPrice())
                    .subTotal(it.getSubTotal())
                    .shippingFee(it.getShippingFee())
                    .chargeFee(it.getChargeFee())
                    .cashback(it.getCashback())
                    .discount(it.getDiscount())
                    .grandTotal(it.getGrandTotal())
                    .build();
            productOpt.ifPresent(prod -> dto.setProductImage(prod.getPicture()));
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public OrderCheckout orderCheckout(OrderCheckoutRequest request) throws InvalidRequestException {
        OrderCheckout orderCheckout = new OrderCheckout();
        orderCheckout.setGrandTotal(BigDecimal.ZERO);
        orderCheckout.setPaymentTerm(request.getPaymentTermCode());
        orderCheckout.setCreatedDate(ZonedDateTime.now());
        orderCheckoutRepository.save(orderCheckout);
        BigDecimal grandTotal = BigDecimal.ZERO;
        for (String code : request.getOrderCodes()) {
            Optional<Order> optOrder = orderRepository.findByOrderCode(code);
            if (optOrder.isEmpty()) {
                throw new InvalidRequestException("Invalid order");
            }
            Order order = optOrder.get();
            if (!OrderStatus.IN_CART.name().equals(order.getStatus())) {
                throw new InvalidRequestException("Invalid order");
            }
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
        orderCheckout.setGrandTotal(grandTotal);
        orderCheckout.setCreatedDate(ZonedDateTime.now());
        orderCheckout.setUpdatedDate(ZonedDateTime.now());
        orderCheckout.setUpdatedBy(SystemConst.SYSTEM.name());
        orderCheckout = orderCheckoutRepository.save(orderCheckout);
        createPayments(orderCheckout);
        return orderCheckout;
    }

    //TODO: write code to show to be created bill list

    void expireBillByOrderCheckoutPaymentId(Long orderCheckoutPaymentId) throws ObjectToJsonStringException {
        List<Bill> bills = billRepository.findByOrderCheckoutPaymentIdAndStatusIn(orderCheckoutPaymentId, List.of(BillStatus.READY_TO_PAY.name(), BillStatus.PENDING.name()));
        for (Bill bill : bills) {
            bill.setExpiryDate(ZonedDateTime.now());
            xenditService.expireBill(bill);
        }
    }


    @Transactional
    public Bill createVaBillPayment(CreateVaBillPaymentRequest request) throws InvalidRequestException, ObjectToJsonStringException {
        Optional<OrderCheckoutPayment> orderCheckoutPaymentOpt = orderCheckoutPaymentRepository.findByOrderCheckoutIdAndSequenceNo(request.getOrderCheckoutId(), request.getSequenceNo());
        if (orderCheckoutPaymentOpt.isEmpty()) {
            throw new InvalidRequestException("Order Checkout is not existed", null);
        }
        OrderCheckoutPayment orderCheckoutPayment = orderCheckoutPaymentOpt.get();
        expireBillByOrderCheckoutPaymentId(orderCheckoutPayment.getId());
        Bill bill = new Bill();
        bill.setOrderCheckoutPaymentId(orderCheckoutPayment.getId());
        bill.setAmount(orderCheckoutPayment.getAmount());
        bill.setCreatedDate(ZonedDateTime.now());
        bill.setExpiryDate(ZonedDateTime.now().plusDays(1));
        bill.setRemarks(DEFAULT_REMARKS + request.getSequenceNo());
        bill.setStatus(BillStatus.PENDING.name());
        bill.setCreatedBy(request.getCreatedBy());
        bill.setPaymentPartner(PaymentPartner.XENDIT.name());//possible to change/add another payment partner in future
        billRepository.save(bill);
        xenditService.createVaClose(bill);//possible to change/add another payment partner in future
        if (request.getSequenceNo() == 1) {
            List<Order> orders = orderRepository.findByOrderCheckoutId(orderCheckoutPayment.getOrderCheckoutId());
            for (Order order : orders) {
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
