package com.gifu.coreservice.service;

import com.gifu.coreservice.entity.*;
import com.gifu.coreservice.enumeration.OrderStatus;
import com.gifu.coreservice.enumeration.SearchOperation;
import com.gifu.coreservice.enumeration.SystemConst;
import com.gifu.coreservice.exception.InvalidRequestException;
import com.gifu.coreservice.model.dto.CustomerDetailsDto;
import com.gifu.coreservice.model.dto.DashboardOrderDto;
import com.gifu.coreservice.model.dto.InvoiceSouvenirDto;
import com.gifu.coreservice.model.dto.ShippingDetailsDto;
import com.gifu.coreservice.model.request.ChangeStatusRequest;
import com.gifu.coreservice.model.request.ConfirmOrderRequest;
import com.gifu.coreservice.model.request.OrderSouvenirRequest;
import com.gifu.coreservice.model.request.SearchDashboardOrderRequest;
import com.gifu.coreservice.repository.*;
import com.gifu.coreservice.repository.spec.BasicSpec;
import com.gifu.coreservice.repository.spec.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderVariantInfoRepository orderVariantInfoRepository;
    @Autowired
    private OrderVariantRepository orderVariantRepository;
    @Autowired
    private OrderAdditionalInfoRepository orderAdditionalInfoRepository;
    @Autowired
    private OrderBrideGroomRepository orderBrideGroomRepository;
    @Autowired
    private OrderEventDetailRepository orderEventDetailRepository;
    @Autowired
    private OrderShippingRepository orderShippingRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProvinceRepository provinceRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private DistrictRepository districtRepository;
    @Autowired
    private KelurahanRepository kelurahanRepository;

    @Autowired
    private VariantRepository variantRepository;
    @Autowired
    private ProductVariantRepository productVariantRepository;
    @Autowired
    private ContentRepository contentRepository;
    @Autowired
    private OrderCheckoutRepository orderCheckoutRepository;
    @Autowired
    private HistoricalOrderStatusService historicalOrderStatusService;

    public Page<DashboardOrderDto> getDashboardOrderPage(SearchDashboardOrderRequest request, Pageable pageable){
        BasicSpec<Order> productType = new BasicSpec<>(
                new SearchCriteria("productType", SearchOperation.EQUALS,request.getProductType())
        );
        BasicSpec<Order> periodFrom = new BasicSpec<>(
                new SearchCriteria("checkoutDate", SearchOperation.GREATER_THAN_EQUALS, request.getPeriodFrom())
        );
        BasicSpec<Order> periodUntil = new BasicSpec<>(
                new SearchCriteria("checkoutDate", SearchOperation.LESSER_THAN_EQUALS, request.getPeriodFrom())
        );

        Page<Order> orders = orderRepository.findAll(Specification.where(productType).and(periodFrom).and(periodUntil), pageable);
        return orders.map(it -> {
            DashboardOrderDto result = DashboardOrderDto.builder()
                    .id(it.getId())
                    .orderCode(it.getOrderCode())
                    .deadline(it.getDeadline())
                    .grandTotal(it.getGrandTotal())
                    .customerName(it.getCustomerName())
                    .quantity(it.getQuantity())
                    .paymentDate(null)//TODO
                    .eventDate(null)//TODO
                    .build();
            Optional<Product> product = productRepository.findById(it.getProductId());
            product.ifPresent(value -> result.setProductName(value.getName()));

            return result;
        });
    }

    private OrderCheckout syncOrderCheckout(Long orderCheckoutId, String updaterEmail) throws InvalidRequestException {
        Optional<OrderCheckout> checkoutOpt = orderCheckoutRepository.findById(orderCheckoutId);
        if(checkoutOpt.isEmpty()){
            throw new InvalidRequestException("Order checkout is not existed", null);
        }
        List<Order> orders = orderRepository.findByOrderCheckoutId(orderCheckoutId);
        BigDecimal grandTotal = BigDecimal.ZERO;
        for(Order it : orders){
            grandTotal = grandTotal.add(it.getGrandTotal());
        }
        OrderCheckout orderCheckout = checkoutOpt.get();
        orderCheckout.setGrandTotal(grandTotal);
        orderCheckout.setUpdatedDate(ZonedDateTime.now());
        orderCheckout.setUpdatedBy(updaterEmail);
        return orderCheckoutRepository.save(orderCheckout);
    }

    public Order confirmOrder(ConfirmOrderRequest request) throws InvalidRequestException {
        Optional<Order> orderOpt = orderRepository.findById(request.getOrderId());
        if(orderOpt.isEmpty()){
            throw new InvalidRequestException("Order is not existed", null);
        }
        Order order = orderOpt.get();
        if(OrderStatus.WAITING_FOR_CONFIRMATION.name().equals(order.getStatus())){
            throw new InvalidRequestException("Order cannot be confirmed", null);
        }
        order.setShippingFee(request.getShippingFee());
        order.setDeadline(request.getDeadline());
        order.setUpdatedDate(ZonedDateTime.now());
        order.setUpdatedBy(request.getUpdaterEmail());

        BigDecimal grandTotal = order.getSubTotal().add(order.getShippingFee()).add(order.getChargeFee()).subtract(order.getDiscount());
        order.setGrandTotal(grandTotal);
        orderRepository.save(order);
        historicalOrderStatusService.changeStatus(ChangeStatusRequest.builder()
                .orderId(order.getId())
                .status(OrderStatus.WAITING_TO_CREATE_BILL.name())
                .updaterEmail(request.getUpdaterEmail())
                .build());
        syncOrderCheckout(order.getOrderCheckoutId(), request.getUpdaterEmail());
        return order;
    }

    private String generateOrderCode(OrderSouvenirRequest request) {
        return "";
    }

    private Customer saveCustomer(OrderSouvenirRequest request) {
        CustomerDetailsDto customerDetails = request.getCustomerDetails();
        ShippingDetailsDto shippingDetailsDto = request.getShippingDetails();
        Optional<Customer> existing = customerRepository.findByEmail(customerDetails.getEmail());
        Customer customer = new Customer();
        if (existing.isPresent()) {
            customer = existing.get();
        } else {
            customer.setCreatedDate(ZonedDateTime.now());
        }
        customer.setName(customerDetails.getName());
        customer.setAddress(shippingDetailsDto.getAddress());
        customer.setPhoneNumber(customer.getPhoneNumber());
        return customerRepository.save(customer);
    }

    private OrderShipping saveOrderShipping(OrderSouvenirRequest request) {
        ShippingDetailsDto shippingDetails = request.getShippingDetails();
        OrderShipping orderShipping = new OrderShipping();
        orderShipping.setAddress(shippingDetails.getAddress());
        orderShipping.setPostalCode(shippingDetails.getPostalCode());
        orderShipping.setPreferredShippingVendor(shippingDetails.getPreferredShippingVendor());
        orderShipping.setUseWoodenCrate(shippingDetails.getUseWoodenCrate());
        orderShipping.setProvinceCode(shippingDetails.getProvinceId());
        orderShipping.setCityCode(shippingDetails.getCityId());
        orderShipping.setDistrictCode(shippingDetails.getDistrctId());
        orderShipping.setKelurahanCode(shippingDetails.getKelurahanId());

        orderShipping.setCreatedDate(ZonedDateTime.now());
        orderShipping.setUpdatedDate(ZonedDateTime.now());
        return orderShippingRepository.save(orderShipping);
    }

    private OrderBrideGroom saveOrderBrideGroom(OrderSouvenirRequest request) {
        OrderBrideGroom orderBrideGroom = new OrderBrideGroom();
        orderBrideGroom.setBrideName(request.getCustomerDetails().getBrideName());
        orderBrideGroom.setGroomName(request.getCustomerDetails().getGroomName());

        orderBrideGroom.setCreatedDate(ZonedDateTime.now());
        orderBrideGroom.setUpdatedDate(ZonedDateTime.now());
        return orderBrideGroomRepository.save(orderBrideGroom);
    }

//    private Pair<Integer, BigDecimal> calculateQtyAndSubTotal(OrderSouvenirRequest request) throws InvalidRequestException {
//        Optional<Product> productOpt = productRepository.findById(request.getProductId());
//        if (productOpt.isEmpty()) {
//            throw new InvalidRequestException("Product is not existed", null);
//        }
//        Product product = productOpt.get();
//        BigDecimal price = product.getPrice();
//
//        List<OrderVariantDto> allVariants = new ArrayList<>(request.getColors());
//        allVariants.addAll(request.getPackagingColors());
//        allVariants.add(request.getPackaging());
//        allVariants.add(request.getAdditionalVariant());
//        allVariants.add(request.getEmboss());
//        allVariants.add(request.getSize());
//        allVariants.add(request.getPosition());
//        allVariants.add(request.getGreetings());
//        List<ProductVariant> productVariants = productVariantRepository.findByProductId(product.getId());
//        for (ProductVariant master : productVariants) {
//            if(master.getVariantId()!=null){
//                List<OrderVariantDto> variant = allVariants.stream().filter(var -> var.getVariantId().equals(master.getVariantId())).collect(Collectors.toList());
//                if(variant.isEmpty()){
//                    continue;
//                }
//            }
//            if(master.getPairVariantId()!=null){
//                List<OrderVariantDto> variant = allVariants.stream().filter(var -> var.getVariantId().equals(master.getVariantId())).collect(Collectors.toList());
//                if(variant.isEmpty()){
//                    continue;
//                }
//            }
//
//        }
//
//
//        int qty = 0;
//
//
//        return qty;
//    }

//    @Transactional
//    public InvoiceSouvenirDto addToCartSouvenir(OrderSouvenirRequest request) {
//        Order order = new Order();
//        order.setOrderCode(generateOrderCode(request));
//
//        Customer customer = saveCustomer(request);
//        order.setCustomerId(customer.getId());
//        order.setCustomerEmail(customer.getEmail());
//        order.setCustomerPhoneNo(customer.getPhoneNumber());
//
//        Optional<Product> productOpt = productRepository.findById(request.getProductId());
//        if (productOpt.isPresent()) {
//            order.setProductCode(productOpt.get().getProductCode());
//            order.setProductType(productOpt.get().getProductType());
//        }
//
//        order.setStatus(OrderStatus.IN_CART.name());
//        OrderShipping orderShipping = saveOrderShipping(request);
//        order.setOrderShippingId(orderShipping.getId());
//        OrderBrideGroom orderBrideGroom = saveOrderBrideGroom(request);
//        order.setOrderBrideGroomId(orderBrideGroom.getId());
//
//        order.setQuantity(countQty(request));
//        return null;
//    }
}
