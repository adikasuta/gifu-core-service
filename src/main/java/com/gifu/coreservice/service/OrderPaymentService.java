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
    public OrderCheckout orderCheckout(OrderCheckoutRequest request, CustomerSessionDto sessionDto) throws InvalidRequestException {
        OrderCheckout orderCheckout = new OrderCheckout();
        orderCheckout.setGrandTotal(BigDecimal.ZERO);
        orderCheckout.setPaymentTerm(request.getPaymentTermCode());
        orderCheckout.setCreatedDate(ZonedDateTime.now());
        orderCheckout.setCustomerName(sessionDto.getCustomerName());
        orderCheckout.setCustomerPhoneNo(sessionDto.getPhoneNumber());
        orderCheckout.setCustomerEmail(sessionDto.getCustomerEmail());
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
                    .updaterEmail(sessionDto.getCustomerEmail())
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
            xenditService.expireBill(bill);
        }
    }

    public List<CheckoutPaymentDto> findCheckoutPaymentByOrderCheckoutId(Long orderCheckoutId) {
        List<OrderCheckoutPayment> orderCheckoutPayments = orderCheckoutPaymentRepository.findByOrderCheckoutId(orderCheckoutId);
        return orderCheckoutPayments.stream().map(ocp -> {
            CheckoutPaymentDto dto = CheckoutPaymentDto.builder()
                    .orderCheckoutPaymentId(ocp.getId())
                    .sequenceNo(ocp.getSequenceNo())
                    .amount(ocp.getAmount())
                    .build();
            BasicSpec<Bill> orderCheckoutPaymentIdEquals = new BasicSpec<>(new SearchCriteria("orderCheckoutPaymentId", SearchOperation.EQUALS, ocp.getId()));
            BasicSpec<Bill> statusIn = new BasicSpec<>(new SearchCriteria("status", SearchOperation.IN, List.of(BillStatus.READY_TO_PAY.name(), BillStatus.PENDING.name())));
            BasicSpec<Bill> inactiveDate = new BasicSpec<>(new SearchCriteria("expiryDate",SearchOperation.GREATER_THAN, ZonedDateTime.now()));
            Pageable pageable = PageRequest.of(0, 1, Sort.by("id").descending());

            Page<Bill> bills = billRepository.findAll(Specification.where(orderCheckoutPaymentIdEquals).and(statusIn).and(inactiveDate), pageable);
            if (bills.getContent().size()>0){//assert that only one active bill per checkout payment
                Bill bill = bills.getContent().get(0);
                dto.setActiveBillId(bill.getId());
                dto.setExpiryDate(bill.getExpiryDate());
                dto.setVirtualAccounts(xenditService.findByBillId(bill.getId()));
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

        if(request.getPeriodFrom()!=null){
            ZonedDateTime start = DateUtils.toZoneDateTime(request.getPeriodFrom(), true);
            orderSpec = orderSpec.and(new BasicSpec<>(new SearchCriteria("checkoutDate", SearchOperation.GREATER_THAN_EQUALS, start)));
        }

        if(request.getPeriodUntil()!=null){
            ZonedDateTime end = DateUtils.toZoneDateTime(request.getPeriodUntil(), true);
            orderSpec = orderSpec.and(new BasicSpec<>(new SearchCriteria("checkoutDate", SearchOperation.LESSER_THAN_EQUALS, end)));
        }

        orderSpec = orderSpec.and(new BasicSpec<>(new SearchCriteria("status", SearchOperation.IN, List.of(OrderStatus.WAITING_TO_CREATE_BILL.name(), OrderStatus.WAITING_FOR_PAYMENT.name(), OrderStatus.IN_PROGRESS_PRODUCTION))));
        List<Order> orders = orderRepository.findAll(orderSpec);
        List<Long> orderCheckoutIds = orders.stream().map(Order::getOrderCheckoutId).collect(Collectors.toList());
        Page<OrderCheckout> orderCheckouts = orderCheckoutRepository.findAll(Specification.where(new BasicSpec<>(new SearchCriteria("id", SearchOperation.IN, orderCheckoutIds))), pageable);
        return orderCheckouts.map(it -> {
            List<CheckoutOrderDetailDto> orderDto = orders.stream().map(order -> CheckoutOrderDetailDto.builder()
                    .orderId(order.getId())
                    .orderCode(order.getOrderCode())
                    .orderStatus(order.getStatus())
                    .productName(order.getProductName())
                    .grandTotal(order.getGrandTotal())
                    .build()).collect(Collectors.toList());
            return CheckoutOrderDto.builder()
                    .orderCheckoutId(it.getId())
                    .customerPhoneNo(it.getCustomerPhoneNo())
                    .customerName(it.getCustomerName())
                    .customerEmail(it.getCustomerEmail())
                    .orders(orderDto)
                    .build();
        });
    }

    @Transactional
    public Bill createVaBillPayment(CreateVaBillPaymentRequest request) throws InvalidRequestException, ObjectToJsonStringException {
        Optional<OrderCheckoutPayment> orderCheckoutPaymentOpt = orderCheckoutPaymentRepository.findByOrderCheckoutIdAndSequenceNo(request.getOrderCheckoutId(), request.getSequenceNo());
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
        if(customerOpt.isEmpty()){
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
