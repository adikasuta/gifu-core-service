package com.gifu.coreservice.service;

import com.gifu.coreservice.entity.*;
import com.gifu.coreservice.enumeration.OrderStatus;
import com.gifu.coreservice.enumeration.SearchOperation;
import com.gifu.coreservice.enumeration.ShippingVendor;
import com.gifu.coreservice.enumeration.SystemConst;
import com.gifu.coreservice.exception.InvalidRequestException;
import com.gifu.coreservice.model.dto.*;
import com.gifu.coreservice.model.request.ChangeStatusRequest;
import com.gifu.coreservice.model.request.ConfirmOrderRequest;
import com.gifu.coreservice.model.request.OrderRequest;
import com.gifu.coreservice.model.request.SearchDashboardOrderRequest;
import com.gifu.coreservice.repository.*;
import com.gifu.coreservice.repository.spec.BasicSpec;
import com.gifu.coreservice.repository.spec.SearchCriteria;
import com.gifu.coreservice.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    private ProductVariantPriceRepository productVariantPriceRepository;
    @Autowired
    private ContentRepository contentRepository;
    @Autowired
    private OrderCheckoutRepository orderCheckoutRepository;
    @Autowired
    private HistoricalOrderStatusService historicalOrderStatusService;
    @Autowired
    private PricingRangeRepository pricingRangeRepository;
    @Autowired
    private OrderVariantPriceRepository orderVariantPriceRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Autowired
    private WorkflowRepository workflowRepository;

    public OrderDto getOrderDtoById(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            List<OrderVariant> orderVariants = orderVariantRepository.findByOrderId(orderId);
            List<OrderVariantInfoDto> orderVariantDtos = orderVariants.stream().map(it -> {
                List<OrderVariantInfo> additionalInfos = orderVariantInfoRepository.findByOrderVariantId(it.getId());
                List<KeyValueDto> mappedAdditionalInfo = additionalInfos.stream().map(variantInfo ->
                        KeyValueDto.builder().key(variantInfo.getKey())
                                .value(variantInfo.getValue()).build()
                ).collect(Collectors.toList());
                return OrderVariantInfoDto.builder()
                        .id(it.getId())
                        .variantTypeCode(it.getVariantTypeCode())
                        .variantName(it.getVariantName())
                        .variantContentName(it.getVariantContentName())
                        .variantContentPicture(it.getVariantContentPicture())
                        .additionalInfo(mappedAdditionalInfo)
                        .build();
            }).collect(Collectors.toList());
            return OrderDto.builder()
                    .id(orderId)
                    .customerEmail(order.getCustomerEmail())
                    .customerName(order.getCustomerName())
                    .customerPhoneNo(order.getCustomerPhoneNo())
                    .productName(order.getProductName())
                    .deadline(order.getDeadline())
                    .quantity(order.getQuantity())
                    .notes(order.getNotes())
                    .variantInfo(orderVariantDtos)
                    .build();
        }
        return null;
    }

    public Page<DashboardOrderDto> getDashboardOrderPage(SearchDashboardOrderRequest request, Pageable pageable) {
        BasicSpec<Order> likeProductName = new BasicSpec<>(new SearchCriteria("productName", SearchOperation.LIKE, request.getQuery()));
        BasicSpec<Order> likeCustomerName = new BasicSpec<>(new SearchCriteria("customerName", SearchOperation.LIKE, request.getQuery()));
        BasicSpec<Order> likeCustomerEmail = new BasicSpec<>(new SearchCriteria("customerEmail", SearchOperation.LIKE, request.getQuery()));
        BasicSpec<Order> likeOrderCode = new BasicSpec<>(new SearchCriteria("orderCode", SearchOperation.LIKE, request.getQuery()));
        Specification<Order> spec = Specification.where(likeProductName).or(likeCustomerName).or(likeCustomerEmail).or(likeOrderCode);
        if (StringUtils.hasText(request.getProductType())) {
            BasicSpec<Order> productType = new BasicSpec<>(
                    new SearchCriteria("productType", SearchOperation.EQUALS, request.getProductType())
            );
            spec = spec.and(productType);
        }
        if (request.getPeriodFrom() != null) {
            ZonedDateTime start = DateUtils.toZoneDateTime(request.getPeriodFrom(), true);
            BasicSpec<Order> periodFrom = new BasicSpec<>(
                    new SearchCriteria("createdDate", SearchOperation.GREATER_THAN_EQUALS, start)
            );
            spec = spec.and(periodFrom);
        }
        if (request.getPeriodUntil() != null) {
            ZonedDateTime end = DateUtils.toZoneDateTime(request.getPeriodUntil(), false);
            BasicSpec<Order> periodUntil = new BasicSpec<>(
                    new SearchCriteria("createdDate", SearchOperation.LESSER_THAN_EQUALS, end)
            );
            spec = spec.and(periodUntil);
        }
        if (request.getStatuses().size() > 0) {
            BasicSpec<Order> statusesIn = new BasicSpec<>(
                    new SearchCriteria("status", SearchOperation.IN, request.getStatuses())
            );
            spec = spec.and(statusesIn);
        }

        BasicSpec<Order> notDraft = new BasicSpec<>(
                new SearchCriteria("status", SearchOperation.NOT_EQUALS, OrderStatus.DRAFT.name())
        );
        spec = spec.and(notDraft);

        BasicSpec<Order> notCart = new BasicSpec<>(
                new SearchCriteria("status", SearchOperation.NOT_EQUALS, OrderStatus.IN_CART.name())
        );
        spec = spec.and(notCart);

        Page<Order> orders = orderRepository.findAll(spec, pageable);
        return orders.map(it -> {
            Optional<OrderShipping> orderShippingOpt = orderShippingRepository.findById(it.getOrderShippingId());

            DashboardOrderDto dto = DashboardOrderDto.builder()
                    .id(it.getId())
                    .createdDate(it.getCreatedDate())
                    .checkoutDate(it.getCheckoutDate())
                    .orderCode(it.getOrderCode())
                    .productType(it.getProductType())
                    .customerName(it.getCustomerName())
                    .productName(it.getProductName())
                    .quantity(it.getQuantity())
                    .deadline(it.getDeadline())
                    .grandTotal(it.getGrandTotal())
                    .status(it.getStatus())
                    .statusText(OrderStatus.valueOf(it.getStatus()).getLabel())
                    .paymentDate(it.getFirstPaymentDate())
                    .build();

            if(orderShippingOpt.isPresent()){
                OrderShipping orderShipping = orderShippingOpt.get();
                Optional<Province> provinceOpt = provinceRepository.findById(orderShipping.getProvinceCode());
                provinceOpt.ifPresent(addr -> dto.setProvinceName(addr.getName()));
                Optional<City> cityOptional = cityRepository.findById(orderShipping.getCityCode());
                cityOptional.ifPresent(addr -> dto.setCityName(addr.getName()));
                Optional<District> district = districtRepository.findById(orderShipping.getDistrictCode());
                district.ifPresent(addr -> dto.setDistrictName(addr.getName()));
                Optional<Kelurahan> kelurahan = kelurahanRepository.findById(orderShipping.getKelurahanCode());
                kelurahan.ifPresent(addr -> dto.setKelurahanName(addr.getName()));
                dto.setPostalCode(orderShipping.getPostalCode());
                dto.setAddress(orderShipping.getAddress());
                dto.setShippingVendorCode(orderShipping.getPreferredShippingVendor());
                dto.setShippingVendorName(ShippingVendor.valueOf(orderShipping.getPreferredShippingVendor()).getText());
            }
            return dto;
        });
    }

    private OrderCheckout syncOrderCheckout(Long orderCheckoutId, String updaterEmail) throws InvalidRequestException {
        Optional<OrderCheckout> checkoutOpt = orderCheckoutRepository.findById(orderCheckoutId);
        if (checkoutOpt.isEmpty()) {
            throw new InvalidRequestException("Order checkout is not existed", null);
        }
        List<Order> orders = orderRepository.findByOrderCheckoutId(orderCheckoutId);
        BigDecimal grandTotal = BigDecimal.ZERO;
        for (Order it : orders) {
            grandTotal = grandTotal.add(it.getGrandTotal());
        }
        OrderCheckout orderCheckout = checkoutOpt.get();
        orderCheckout.setGrandTotal(grandTotal);
        orderCheckout.setUpdatedDate(ZonedDateTime.now());
        orderCheckout.setUpdatedBy(updaterEmail);
        return orderCheckoutRepository.save(orderCheckout);
    }

    @Transactional
    public Order confirmOrder(ConfirmOrderRequest request) throws InvalidRequestException {
        Optional<Order> orderOpt = orderRepository.findById(request.getOrderId());
        if (orderOpt.isEmpty()) {
            throw new InvalidRequestException("Order is not existed", null);
        }
        Order order = orderOpt.get();
        if (!OrderStatus.WAITING_FOR_CONFIRMATION.name().equals(order.getStatus())) {
            throw new InvalidRequestException("Order cannot be confirmed", null);
        }
        order.setShippingFee(request.getShippingFee());
        order.setDeadline(DateUtils.toZoneDateTime(request.getDeadline(), true));
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

    //TODO: follow gifu requirements
    private String generateOrderCode(Order order) {
        String token = com.gifu.coreservice.utils.StringUtils.generateReferralCode();
        long existingCount = orderRepository.countByOrderCode(token);
        if (existingCount > 0) {
            generateOrderCode(order);
        }
        order.setOrderCode(token);
        orderRepository.save(order);
        return token;
    }

    private Customer saveCustomer(OrderRequest request) {
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
        customer.setEmail(customerDetails.getEmail());
        customer.setAddress(shippingDetailsDto.getAddress());
        customer.setPhoneNumber(customerDetails.getPhoneNumber());
        customerRepository.save(customer);
        return customer;
    }

    private OrderShipping saveOrderShipping(OrderRequest request) {
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

    private OrderBrideGroom saveOrderBrideGroom(OrderRequest request) {
        OrderBrideGroom orderBrideGroom = new OrderBrideGroom();
        orderBrideGroom.setGroomName(request.getBrideGroom().getGroomName());
        orderBrideGroom.setGroomFather(request.getBrideGroom().getGroomFather());
        orderBrideGroom.setGroomMother(request.getBrideGroom().getGroomMother());
        orderBrideGroom.setGroomInstagram(request.getBrideGroom().getGroomInstagram());

        orderBrideGroom.setBrideName(request.getBrideGroom().getBrideName());
        orderBrideGroom.setBrideFather(request.getBrideGroom().getBrideFather());
        orderBrideGroom.setBrideMother(request.getBrideGroom().getBrideMother());
        orderBrideGroom.setBrideInstagram(request.getBrideGroom().getBrideInstagram());

        orderBrideGroom.setCreatedDate(ZonedDateTime.now());
        orderBrideGroom.setUpdatedDate(ZonedDateTime.now());
        return orderBrideGroomRepository.save(orderBrideGroom);
    }

    private void saveOrderVariant(OrderRequest request, Order order) throws InvalidRequestException {
        for (OrderVariantDto requestVariant : request.getVariants()) {
            Optional<Variant> masterOpt = variantRepository.findById(requestVariant.getVariantId());
            if (masterOpt.isEmpty()) {
                throw new InvalidRequestException("Invalid Variant Id");
            }
            Optional<Content> masterContentOpt = contentRepository.findById(requestVariant.getContentId());
            if (masterContentOpt.isEmpty()) {
                throw new InvalidRequestException("Invalid Variant Id");
            }
            Content masterContent = masterContentOpt.get();
            Variant master = masterOpt.get();
            OrderVariant orderVariant = new OrderVariant();
            orderVariant.setOrderId(order.getId());
            orderVariant.setVariantId(requestVariant.getVariantId());
            orderVariant.setVariantTypeCode(requestVariant.getVariantTypeCode());
            orderVariant.setVariantName(master.getName());
            orderVariant.setContentId(requestVariant.getContentId());
            orderVariant.setVariantContentName(masterContent.getName());
            orderVariant.setVariantContentPicture(masterContent.getPicture());
            orderVariant.setOrderVariantPriceId(null);
            orderVariant.setCreatedDate(ZonedDateTime.now());
            orderVariant.setUpdatedDate(ZonedDateTime.now());
            orderVariantRepository.save(orderVariant);
            if (StringUtils.hasText(requestVariant.getAdditionalInfoValue())) {
                OrderVariantInfo orderVariantInfo = new OrderVariantInfo();
                orderVariantInfo.setOrderId(order.getId());
                orderVariantInfo.setOrderVariantId(orderVariant.getId());
                orderVariantInfo.setValue(requestVariant.getAdditionalInfoValue());
                orderVariantInfo.setKey(requestVariant.getAdditionalInfoKey());
                orderVariantInfo.setCreatedDate(ZonedDateTime.now());
                orderVariantInfo.setUpdatedDate(ZonedDateTime.now());
                orderVariantInfoRepository.save(orderVariantInfo);
            }
        }
    }

    private BigDecimal calculateTotalVariantPrice(OrderRequest request, Order order) {
        List<ProductVariantPrice> productVariantPrices = productVariantPriceRepository.findByProductId(request.getProductId());
        Set<Long> selectedVariantIds = request.getVariants().stream().map(OrderVariantDto::getVariantId).collect(Collectors.toSet());
        BigDecimal totalVariantPrice = BigDecimal.ZERO;
        for (ProductVariantPrice it : productVariantPrices) {
            List<Long> variantIds = Arrays.stream(it.getVariantIds().split(",")).collect(Collectors.toList()).stream().map(Long::valueOf).collect(Collectors.toList());
            if (selectedVariantIds.containsAll(variantIds)) {
                variantIds.forEach(selectedVariantIds::remove);
                OrderVariantPrice orderVariantPrice = new OrderVariantPrice();
                orderVariantPrice.setPrice(it.getPrice());
                orderVariantPrice.setOrderId(order.getId());
                orderVariantPriceRepository.save(orderVariantPrice);
                totalVariantPrice = totalVariantPrice.add(it.getPrice());
                for (Long variantId : variantIds) {
                    List<OrderVariant> orderVariants = orderVariantRepository.findByOrderIdAndVariantId(order.getId(), variantId);
                    for (OrderVariant orderVariant : orderVariants) {
                        orderVariant.setOrderVariantPriceId(orderVariantPrice.getId());
                        orderVariantRepository.save(orderVariant);
                    }
                }
            }
        }

        return totalVariantPrice;
    }

    private void calculateTotalPrice(OrderRequest request, Order order) throws InvalidRequestException {
        Optional<Product> productOpt = productRepository.findById(request.getProductId());
        if (productOpt.isEmpty()) {
            throw new InvalidRequestException("Product is not existed");
        }
        Product product = productOpt.get();
        List<PricingRange> pricingRanges = pricingRangeRepository.findByProductId(product.getId());
        BigDecimal productPrice = null;
        for (PricingRange it : pricingRanges) {
            if (request.getQuantity() >= it.getQtyMin()) {
                if ((it.getQtyMax() == null) || (it.getQtyMax() != null && request.getQuantity() <= it.getQtyMax())) {
                    productPrice = it.getPrice();
                    break;
                }
            }
        }
        if (productPrice == null) {
            throw new InvalidRequestException("Invalid quantity");
        }
        order.setProductPrice(productPrice);
        BigDecimal totalVariantPrice = calculateTotalVariantPrice(request, order);
        order.setVariantPrice(totalVariantPrice);
        BigDecimal price = totalVariantPrice.add(productPrice);
        order.setSubTotal(price.multiply(BigDecimal.valueOf(order.getQuantity())));
        order.setGrandTotal(order.getSubTotal());
    }

    private void setWorkflowId(Product product, Order order) throws InvalidRequestException {
        Optional<ProductCategory> productCategoryOpt = productCategoryRepository.findById(product.getProductCategoryId());
        if (productCategoryOpt.isEmpty()) {
            throw new InvalidRequestException("Product category id is not existed");
        }
        ProductCategory productCategory = productCategoryOpt.get();
        if (!StringUtils.hasText(productCategory.getWorkflowCode())) {
            throw new InvalidRequestException("Product is not related with any workflow");
        }
        Optional<Workflow> workflowOpt = workflowRepository.findByWorkflowCodeAndIsDeleted(productCategory.getWorkflowCode(), false);
        if (workflowOpt.isEmpty()) {
            throw new InvalidRequestException("Product is not related with any workflow");
        }
        order.setWorkflowId(workflowOpt.get().getId());
    }

    private void saveEventDetail(OrderRequest request, Order order) {
        for (OrderEventDetailDto event : request.getEventDetail()) {
            OrderEventDetail orderEventDetail = new OrderEventDetail();
            orderEventDetail.setOrderId(order.getId());
            orderEventDetail.setName(event.getName());
            orderEventDetail.setVenue(event.getVenue());
            orderEventDetail.setDate(event.getDate());
            orderEventDetail.setTime(event.getTime());
            orderEventDetail.setCreatedDate(ZonedDateTime.now());
            orderEventDetail.setUpdatedDate(ZonedDateTime.now());
            orderEventDetailRepository.save(orderEventDetail);
        }
    }

    @Transactional
    public Order removeFromCart(String orderCode, String clientIp) throws InvalidRequestException {
        Optional<Order> orderOpt = orderRepository.findByOrderCode(orderCode);
        if (orderOpt.isEmpty()) {
            throw new InvalidRequestException("Order Code is not valid");
        }
        Order order = orderOpt.get();
        if(!clientIp.equals(order.getClientIpAddress())){
            throw new InvalidRequestException("Unauthorized Action");
        }

        historicalOrderStatusService.changeStatus(ChangeStatusRequest.builder()
                .orderId(order.getId())
                .status(OrderStatus.DRAFT.name())
                .updaterEmail(SystemConst.SYSTEM.name())
                .build());
        return order;
    }

    @Transactional
    public Order addToCart(String orderCode, String clientIp) throws InvalidRequestException {
        Optional<Order> orderOpt = orderRepository.findByOrderCode(orderCode);
        if (orderOpt.isEmpty()) {
            throw new InvalidRequestException("Order Code is not valid");
        }
        Order order = orderOpt.get();
        if(!clientIp.equals(order.getClientIpAddress())){
            throw new InvalidRequestException("Unauthorized Action");
        }
        historicalOrderStatusService.changeStatus(ChangeStatusRequest.builder()
                .orderId(order.getId())
                .status(OrderStatus.IN_CART.name())
                .updaterEmail(SystemConst.SYSTEM.name())
                .build());
        return order;
    }

    @Transactional
    public Order saveToDraft(OrderRequest request, String clientIp) throws InvalidRequestException {
        Order order = new Order();
        order.setShippingFee(BigDecimal.ZERO);
        order.setChargeFee(BigDecimal.ZERO);
        order.setCashback(BigDecimal.ZERO);
        order.setDiscount(BigDecimal.ZERO);
        order.setGrandTotal(BigDecimal.ZERO);
        order.setSubTotal(BigDecimal.ZERO);
        order.setProductPrice(BigDecimal.ZERO);
        order.setVariantPrice(BigDecimal.ZERO);
        order.setClientIpAddress(clientIp);

        order.setOrderCode(generateOrderCode(order));
        orderRepository.save(order);
        Customer customer = saveCustomer(request);
        order.setCustomerId(customer.getId());
        order.setCustomerName(customer.getName());
        order.setCustomerEmail(customer.getEmail());
        order.setCustomerPhoneNo(customer.getPhoneNumber());

        Optional<Product> productOpt = productRepository.findById(request.getProductId());
        if (productOpt.isEmpty()) {
            throw new InvalidRequestException("Invalid product id");
        }
        Product product = productOpt.get();
        order.setProductId(product.getId());
        order.setProductName(product.getName());
        order.setProductCode(product.getProductCode());
        order.setProductType(product.getProductType());

        historicalOrderStatusService.changeStatus(ChangeStatusRequest.builder()
                .orderId(order.getId())
                .status(OrderStatus.DRAFT.name())
                .updaterEmail(SystemConst.SYSTEM.name())
                .build());
        OrderShipping orderShipping = saveOrderShipping(request);
        order.setOrderShippingId(orderShipping.getId());
        OrderBrideGroom orderBrideGroom = saveOrderBrideGroom(request);
        order.setOrderBrideGroomId(orderBrideGroom.getId());
        saveEventDetail(request, order);

        order.setQuantity(request.getQuantity());
        saveOrderVariant(request, order);
        calculateTotalPrice(request, order);

        order.setNotes(request.getNotes());
        order.setCsReferralToken(request.getCsReferralToken());
        order.setCreatedDate(ZonedDateTime.now());
        setWorkflowId(product, order);
        orderRepository.save(order);

        return order;
    }

    private void setInvoiceShippingAddress(Long orderShippingId, InvoiceDto invoiceDto) throws InvalidRequestException {
        Optional<OrderShipping> orderShippingOps = orderShippingRepository.findById(orderShippingId);
        if (orderShippingOps.isEmpty()) {
            throw new InvalidRequestException("Invalid order");
        }
        OrderShipping orderShipping = orderShippingOps.get();
        Optional<Province> provinceOpt = provinceRepository.findById(orderShipping.getProvinceCode());
        provinceOpt.ifPresent(it -> invoiceDto.setProvinceName(it.getName()));
        Optional<City> cityOptional = cityRepository.findById(orderShipping.getCityCode());
        cityOptional.ifPresent(it -> invoiceDto.setCityName(it.getName()));
        Optional<District> district = districtRepository.findById(orderShipping.getDistrictCode());
        district.ifPresent(it -> invoiceDto.setDistrictName(it.getName()));
        Optional<Kelurahan> kelurahan = kelurahanRepository.findById(orderShipping.getKelurahanCode());
        kelurahan.ifPresent(it -> invoiceDto.setKelurahanName(it.getName()));
        invoiceDto.setPostalCode(orderShipping.getPostalCode());
        invoiceDto.setAddress(orderShipping.getAddress());
    }

    public InvoiceDto getInvoice(String orderCode) throws InvalidRequestException {
        Optional<Order> orderOpt = orderRepository.findByOrderCode(orderCode);
        if (orderOpt.isEmpty()) {
            throw new InvalidRequestException("Invalid order code");
        }
        Order order = orderOpt.get();

        List<OrderVariant> orderVariants = orderVariantRepository.findByOrderId(order.getId());
        List<InvoiceVariantDto> orderVariantDto = orderVariants.stream().map(it -> {
            List<OrderVariantInfo> infos = orderVariantInfoRepository.findByOrderVariantId(it.getId());
            return InvoiceVariantDto.builder()
                    .variantTypeCode(it.getVariantTypeCode())
                    .variantName(it.getVariantName())
                    .contentName(it.getVariantContentName())
                    .additionalInfo(infos.stream().map(info -> KeyValueDto
                            .builder()
                            .key(info.getKey())
                            .value(info.getValue()).build()).collect(Collectors.toList()))
                    .build();
        }).collect(Collectors.toList());
        InvoiceDto invoiceDto = InvoiceDto.builder()
                .status(order.getStatus())
                .orderCode(order.getOrderCode())
                .productName(order.getProductName())
                .productTypeCode(order.getProductType())
                .notes(order.getNotes())
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .customerPhoneNo(order.getCustomerPhoneNo())
                .quantity(order.getQuantity())
                .productPrice(order.getProductPrice())
                .variantPrice(order.getVariantPrice())
                .subTotal(order.getSubTotal())
                .shippingFee(order.getShippingFee())
                .chargeFee(order.getChargeFee())
                .cashback(order.getCashback())
                .discount(order.getDiscount())
                .grandTotal(order.getGrandTotal())
                .createdDate(order.getCreatedDate())
                .checkoutDate(order.getCheckoutDate())
                .variants(orderVariantDto)
                .build();

        setInvoiceShippingAddress(order.getOrderShippingId(), invoiceDto);
        return invoiceDto;
    }
}
