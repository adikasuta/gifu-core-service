package com.gifu.coreservice.service;

import com.gifu.coreservice.entity.*;
import com.gifu.coreservice.enumeration.*;
import com.gifu.coreservice.exception.InvalidRequestException;
import com.gifu.coreservice.exception.ObjectToJsonStringException;
import com.gifu.coreservice.model.dto.*;
import com.gifu.coreservice.model.request.ChangeStatusRequest;
import com.gifu.coreservice.model.request.CreateVaBillPaymentRequest;
import com.gifu.coreservice.model.request.OrderCheckoutRequest;
import com.gifu.coreservice.model.request.SearchCheckoutOrderRequest;
import com.gifu.coreservice.repository.*;
import com.gifu.coreservice.repository.spec.BasicSpec;
import com.gifu.coreservice.repository.spec.SearchCriteria;
import com.gifu.coreservice.service.paymentscheme.AbstractCreatePaymentScheme;
import com.gifu.coreservice.service.paymentscheme.CashPaymentSchemeService;
import com.gifu.coreservice.service.paymentscheme.DownPaymentSchemeService;
import com.gifu.coreservice.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
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
    @Autowired
    private CustomerRepository customerRepository;

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

    public List<CartItemDto> getCartItems(String clientIp) {
        List<Order> orders = orderRepository.findByClientIpAddressAndStatus(clientIp, OrderStatus.IN_CART.name());
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
    public OrderCheckout orderCheckout(OrderCheckoutRequest request, String clientIp) throws InvalidRequestException {
        OrderCheckout orderCheckout = new OrderCheckout();
        orderCheckout.setGrandTotal(BigDecimal.ZERO);
        orderCheckout.setPaymentTerm(request.getPaymentTermCode());
        orderCheckout.setCreatedDate(ZonedDateTime.now());
        orderCheckout.setCustomerName(request.getCustomerName());
        orderCheckout.setCustomerPhoneNo(request.getPhoneNumber());
        orderCheckout.setCustomerEmail(request.getCustomerEmail());
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
            if(!clientIp.equals(order.getClientIpAddress())){
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
                    .updaterEmail(clientIp)
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

    void expireBillByOrderCheckoutPaymentId(Long orderCheckoutPaymentId) throws ObjectToJsonStringException {
        List<Bill> bills = billRepository.findByOrderCheckoutPaymentIdAndStatusIn(orderCheckoutPaymentId, List.of(BillStatus.READY_TO_PAY.name(), BillStatus.PENDING.name()));
        for (Bill bill : bills) {
            bill.setExpiryDate(ZonedDateTime.now());
            billRepository.save(bill);
            xenditService.expireBill(bill);
        }
    }

    public PaymentInstructionDto getPaymentInstructionByOrderCheckoutPaymentId(Long orderCheckoutPaymentId) throws InvalidRequestException {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("id").descending());
        List<Bill> bills = billRepository.findByOrderCheckoutPaymentId(orderCheckoutPaymentId, pageable);
        if (bills.isEmpty()) {
            throw new InvalidRequestException("Checkout Payment is not billed yet");
        }
        Bill lastBill = bills.get(0);
        List<XenditClosedVa> virtualAccounts = xenditService.findByBillId(lastBill.getId());
        List<PaymentInstructionVADto> dto = virtualAccounts.stream().map(it -> PaymentInstructionVADto.builder()
                .accountName(it.getName())
                .bankCode(it.getBankCode())
                .fullVaNumber(it.getFullVaNumber())
                .build()).collect(Collectors.toList());
        return PaymentInstructionDto.builder()
                .orderCheckoutPaymentId(orderCheckoutPaymentId)
                .billId(lastBill.getId())
                .expiryDate(lastBill.getExpiryDate())
                .amount(lastBill.getAmount())
                .paymentDate(lastBill.getPaymentDate())
                .virtualAccounts(dto)
                .build();
    }

    public List<CheckoutPaymentDto> findCheckoutPaymentByOrderCheckoutId(Long orderCheckoutId) {
        Pageable paymentPageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("sequenceNo").ascending());
        List<OrderCheckoutPayment> orderCheckoutPayments = orderCheckoutPaymentRepository.findByOrderCheckoutId(orderCheckoutId, paymentPageable);
        return orderCheckoutPayments.stream().map(ocp -> {
            CheckoutPaymentDto dto = CheckoutPaymentDto.builder()
                    .orderCheckoutPaymentId(ocp.getId())
                    .sequenceNo(ocp.getSequenceNo())
                    .amount(ocp.getAmount())
                    .build();
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("id").descending());
            List<Bill> bills = billRepository.findByOrderCheckoutPaymentId(ocp.getId(), pageable);
            dto.setVirtualAccounts(new ArrayList<>());
            if(!bills.isEmpty()){
                Bill bill = bills.get(0);
                dto.setBillId(bill.getId());
                dto.setPaymentDate(bill.getPaymentDate());
                dto.setExpiryDate(bill.getExpiryDate());

                List<XenditClosedVa> virtualAccounts = xenditService.findByBillId(bill.getId());
                List<PaymentInstructionVADto> vaDto = virtualAccounts.stream().map(it -> PaymentInstructionVADto.builder()
                        .accountName(it.getName())
                        .bankCode(it.getBankCode())
                        .fullVaNumber(it.getFullVaNumber())
                        .build()).collect(Collectors.toList());
                dto.setVirtualAccounts(vaDto);
            }
            return dto;
        }).collect(Collectors.toList());
    }

    public Page<CheckoutOrderDto> findCheckoutOrderList(SearchCheckoutOrderRequest request, Pageable pageable) {
        BasicSpec<Order> orderCodeLike = new BasicSpec<>(new SearchCriteria("orderCode", SearchOperation.LIKE, request.getQuery()));
        BasicSpec<Order> productNameLike = new BasicSpec<>(new SearchCriteria("productName", SearchOperation.LIKE, request.getQuery()));
        BasicSpec<Order> customerNameLike = new BasicSpec<>(new SearchCriteria("customerName", SearchOperation.LIKE, request.getQuery()));
        BasicSpec<Order> customerEmailLike = new BasicSpec<>(new SearchCriteria("customerEmail", SearchOperation.LIKE, request.getQuery()));

        Specification<Order> orderSpec = Specification.where(orderCodeLike).or(productNameLike).or(customerNameLike).or(customerEmailLike);
        Specification<Product> productSpec = Specification.where(null);
        boolean skipProduct = true;
        if (request.getProductCategoryId() != null) {
            BasicSpec<Product> categoryIdEquals = new BasicSpec<>(new SearchCriteria("productCategoryId", SearchOperation.EQUALS, request.getProductCategoryId()));
            productSpec = productSpec.and(categoryIdEquals);
            skipProduct = false;
        }
        if (StringUtils.hasText(request.getProductType())) {
            BasicSpec<Product> productTypeEquals = new BasicSpec<>(new SearchCriteria("productType", SearchOperation.EQUALS, request.getProductType()));
            productSpec = productSpec.and(productTypeEquals);
            skipProduct = false;
        }
        if (!skipProduct) {
            List<Product> products = productRepository.findAll(productSpec);
            List<Long> productIds = products.stream().map(Product::getId).collect(Collectors.toList());
            orderSpec = orderSpec.and(new BasicSpec<>(new SearchCriteria("productId", SearchOperation.IN, productIds)));
        }

        if (request.getPeriodFrom() != null) {
            ZonedDateTime start = DateUtils.toZoneDateTime(request.getPeriodFrom(), true);
            orderSpec = orderSpec.and(new BasicSpec<>(new SearchCriteria("checkoutDate", SearchOperation.GREATER_THAN_EQUALS, start)));
        }

        if (request.getPeriodUntil() != null) {
            ZonedDateTime end = DateUtils.toZoneDateTime(request.getPeriodUntil(), true);
            orderSpec = orderSpec.and(new BasicSpec<>(new SearchCriteria("checkoutDate", SearchOperation.LESSER_THAN_EQUALS, end)));
        }

        orderSpec = orderSpec.and(new BasicSpec<>(new SearchCriteria("status", SearchOperation.IN, List.of(OrderStatus.WAITING_TO_CREATE_BILL.name(), OrderStatus.WAITING_FOR_PAYMENT.name(), OrderStatus.IN_PROGRESS_PRODUCTION.name()))));
        List<Order> orders = orderRepository.findAll(orderSpec);
        List<Long> orderCheckoutIds = orders.stream().map(Order::getOrderCheckoutId).collect(Collectors.toList());
        Page<OrderCheckout> orderCheckouts = orderCheckoutRepository.findAll(Specification.where(new BasicSpec<>(new SearchCriteria("id", SearchOperation.IN, orderCheckoutIds))), pageable);
        return orderCheckouts.map(it -> {
            Pageable paymentPageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("sequenceNo").ascending());
            List<OrderCheckoutPayment> checkoutPayments = orderCheckoutPaymentRepository.findByOrderCheckoutId(it.getId(), paymentPageable);
            List<SimpleCheckoutPaymentDto> payments = checkoutPayments.stream().map(pay -> SimpleCheckoutPaymentDto.builder().id(pay.getId())
                    .isPaid(pay.isPaid())
                    .amount(pay.getAmount())
                    .sequenceNo(pay.getSequenceNo())
                    .paymentDate(pay.getPaymentDate())
                    .build()).collect(Collectors.toList());
            List<CheckoutOrderDetailDto> orderDto = orders.stream().map(order -> CheckoutOrderDetailDto.builder()
                    .orderId(order.getId())
                    .orderCode(order.getOrderCode())
                    .orderStatus(OrderStatus.valueOf(order.getStatus()).getLabel())
                    .productName(order.getProductName())
                    .grandTotal(order.getGrandTotal())
                    .build()).collect(Collectors.toList());
            return CheckoutOrderDto.builder()
                    .orderCheckoutId(it.getId())
                    .customerPhoneNo(it.getCustomerPhoneNo())
                    .customerName(it.getCustomerName())
                    .customerEmail(it.getCustomerEmail())
                    .orders(orderDto)
                    .grandTotalCheckout(it.getGrandTotal())
                    .paymentTerm(it.getPaymentTerm())
                    .createdDate(it.getCreatedDate())
                    .payments(payments)
                    .build();
        });
    }

    @Transactional
    public Bill createVaBillPayment(CreateVaBillPaymentRequest request) throws InvalidRequestException, ObjectToJsonStringException {
        Optional<OrderCheckoutPayment> orderCheckoutPaymentOpt = orderCheckoutPaymentRepository.findById(request.getOrderCheckoutPaymentId());
        if (orderCheckoutPaymentOpt.isEmpty()) {
            throw new InvalidRequestException("Order Checkout is not existed", null);
        }
        OrderCheckoutPayment orderCheckoutPayment = orderCheckoutPaymentOpt.get();
        Optional<OrderCheckout> orderCheckoutOpt = orderCheckoutRepository.findById(orderCheckoutPayment.getOrderCheckoutId());
        if (orderCheckoutOpt.isEmpty()) {
            throw new InvalidRequestException("Order Checkout is not existed", null);
        }
        OrderCheckout orderCheckout = orderCheckoutOpt.get();
        Optional<Customer> customerOpt = customerRepository.findByEmail(orderCheckout.getCustomerEmail());
        if (customerOpt.isEmpty()) {
            throw new InvalidRequestException("Order Checkout is not existed", null);
        }

        expireBillByOrderCheckoutPaymentId(orderCheckoutPayment.getId());
        Bill bill = new Bill();
        bill.setCustomerEmail(orderCheckout.getCustomerEmail());
        bill.setCustomerName(orderCheckout.getCustomerName());
        bill.setCustomerId(customerOpt.get().getId());
        bill.setOrderCheckoutPaymentId(orderCheckoutPayment.getId());
        bill.setAmount(orderCheckoutPayment.getAmount());
        bill.setCreatedDate(ZonedDateTime.now());
        bill.setExpiryDate(ZonedDateTime.now().plusDays(1));
        bill.setRemarks(DEFAULT_REMARKS + orderCheckoutPayment.getSequenceNo());
        bill.setStatus(BillStatus.PENDING.name());
        bill.setCreatedBy(request.getCreatedBy());
        bill.setPaymentPartner(PaymentPartner.XENDIT.name());//possible to change/add another payment partner in future
        billRepository.save(bill);
        xenditService.createVaClose(bill);//possible to change/add another payment partner in future
        if (orderCheckoutPayment.getSequenceNo() == 1) {
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
