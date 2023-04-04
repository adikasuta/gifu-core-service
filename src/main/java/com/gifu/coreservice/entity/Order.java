package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "order_transaction")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "order_checkout_id")
    private Long orderCheckoutId;
    @Column(name = "order_code")
    private String orderCode;

    @Column(name = "product_id")
    private Long productId;
    @Column(name = "product_name")
    private String productName;
    @Column(name = "product_code")
    private String productCode;
    @Column(name = "product_type")
    private String productType;

    @Column(name = "status")
    private String status;

    @Column(name = "customer_id")
    private Long customerId;
    @Column(name = "customer_name")
    private String customerName;
    @Column(name = "customer_email")
    private String customerEmail;
    @Column(name = "customer_phone_no")
    private String customerPhoneNo;

    @Column(name = "order_shipping_id")
    private Long orderShippingId;
    @Column(name = "order_bride_groom_id")
    private Long orderBrideGroomId;

    @Column(name = "quantity")
    private Integer quantity;
    @Column(name = "product_price")
    private BigDecimal productPrice;
    @Column(name = "variant_price")
    private BigDecimal variantPrice;
    @Column(name = "sub_total")
    private BigDecimal subTotal;
    @Column(name = "shipping_fee")
    private BigDecimal shippingFee;
    @Column(name = "charge_fee")
    private BigDecimal chargeFee;
    @Column(name = "cashback")
    private BigDecimal cashback;
    @Column(name = "discount")
    private BigDecimal discount;
    @Column(name = "grand_total")
    private BigDecimal grandTotal;

    @Column(name = "deadline")
    private ZonedDateTime deadline;

    @Column(name = "notes")
    private String notes;
    @Column(name = "cs_referral_token")
    private String csReferralToken;

    @Column(name = "first_payment_date")
    private ZonedDateTime firstPaymentDate;
    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "checkout_date")
    private ZonedDateTime checkoutDate;
    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;
    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name="workflow_id")
    private Long workflowId;
    @Column(name="client_ip_address")
    private String clientIpAddress;
}
