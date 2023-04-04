package com.gifu.coreservice.controller.publicapi;

import com.gifu.coreservice.entity.Order;
import com.gifu.coreservice.exception.InvalidRequestException;
import com.gifu.coreservice.model.dto.CartItemDto;
import com.gifu.coreservice.model.dto.InvoiceDto;
import com.gifu.coreservice.model.request.OrderCheckoutRequest;
import com.gifu.coreservice.model.request.OrderRequest;
import com.gifu.coreservice.model.response.SingleResourceResponse;
import com.gifu.coreservice.service.ObjectMapperService;
import com.gifu.coreservice.service.OrderPaymentService;
import com.gifu.coreservice.service.OrderService;
import com.gifu.coreservice.utils.SessionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "public/api/order")
public class PublicOrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderPaymentService orderPaymentService;
    @Autowired
    private ObjectMapperService objectMapperService;

    private static final String CUSTOMER_EMAIL_HEADER_KEY = "client_gifu";

    @PostMapping
    public ResponseEntity<SingleResourceResponse<Map<String,String>>> postOrder(
            @RequestBody OrderRequest payload,
            HttpServletRequest request
    ) {
        try {
            String clientIp = SessionUtils.getClientIP(request);
            Order order = orderService.saveToDraft(payload, clientIp);
            Map<String,String> mapper = new HashMap<>();
            mapper.put("orderCode",order.getOrderCode());
            return ResponseEntity.ok(new SingleResourceResponse<>(mapper));
        } catch (InvalidRequestException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new SingleResourceResponse<>(ex.getMessage(), HttpStatus.BAD_REQUEST.value())
            );
        } catch (Exception ex) {
            log.error("ERROR POST ORDER: " + ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    @PostMapping("/{orderCode}")
    public ResponseEntity<SingleResourceResponse<String>> addToCart(
            @PathVariable String orderCode,
            HttpServletRequest request
    ) {
        try {
            String ipAddress = SessionUtils.getClientIP(request);
            if (!StringUtils.hasText(ipAddress)) {
                throw new InvalidRequestException("Invalid request");
            }
            orderService.addToCart(orderCode, ipAddress);
            return ResponseEntity.ok(new SingleResourceResponse<>("Success"));
        } catch (InvalidRequestException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new SingleResourceResponse<>(ex.getMessage(), HttpStatus.BAD_REQUEST.value())
            );
        } catch (Exception ex) {
            log.error("ERROR ADD TO CART ORDER: " + ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    @DeleteMapping("/{orderCode}")
    public ResponseEntity<SingleResourceResponse<String>> removeFromCart(
            @PathVariable String orderCode,
            HttpServletRequest request
    ) {
        try {
            String ipAddress = SessionUtils.getClientIP(request);
            if (!StringUtils.hasText(ipAddress)) {
                throw new InvalidRequestException("Invalid request");
            }
            orderService.removeFromCart(orderCode, ipAddress);
            return ResponseEntity.ok(new SingleResourceResponse<>("Success"));
        } catch (InvalidRequestException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new SingleResourceResponse<>(ex.getMessage(), HttpStatus.BAD_REQUEST.value())
            );
        } catch (Exception ex) {
            log.error("ERROR REMOVE FROM CART ORDER: " + ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    @GetMapping("/cart")
    public ResponseEntity<SingleResourceResponse<List<CartItemDto>>> findCartItems(
            HttpServletRequest request
    ) {
        try {
            String ipAddress = SessionUtils.getClientIP(request);
            if (!StringUtils.hasText(ipAddress)) {
                throw new InvalidRequestException("Invalid request");
            }
            List<CartItemDto> cartItems = orderPaymentService.getCartItems(ipAddress);
            return ResponseEntity.ok(new SingleResourceResponse<>(cartItems));
        } catch (InvalidRequestException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new SingleResourceResponse<>(ex.getMessage(), HttpStatus.BAD_REQUEST.value())
            );
        } catch (Exception ex) {
            log.error("ERROR GET CART ORDER: " + ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    @PostMapping("/checkout")
    public ResponseEntity<SingleResourceResponse<String>> checkoutOrders(
            @RequestBody OrderCheckoutRequest payload,
            HttpServletRequest request
    ) {
        try {
            String ipAddress = SessionUtils.getClientIP(request);
            if (!StringUtils.hasText(ipAddress)) {
                throw new InvalidRequestException("Invalid request");
            }
            orderPaymentService.orderCheckout(payload, ipAddress);
            return ResponseEntity.ok(new SingleResourceResponse<>("Success"));
        } catch (InvalidRequestException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new SingleResourceResponse<>(ex.getMessage(), HttpStatus.BAD_REQUEST.value())
            );
        } catch (Exception ex) {
            log.error("ERROR POST CHECKOUT: " + ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    @GetMapping("/{orderCode}/invoice")
    public ResponseEntity<SingleResourceResponse<InvoiceDto>> getInvoice(
            @PathVariable String orderCode
    ) {
        try {

            InvoiceDto invoice = orderService.getInvoice(orderCode);
            return ResponseEntity.ok(new SingleResourceResponse<>(invoice));
        } catch (InvalidRequestException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new SingleResourceResponse<>(ex.getMessage(), HttpStatus.BAD_REQUEST.value())
            );
        } catch (Exception ex) {
            log.error("ERROR GET INVOICE: " + ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }


}
