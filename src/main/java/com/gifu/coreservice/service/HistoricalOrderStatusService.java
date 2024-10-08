package com.gifu.coreservice.service;

import com.gifu.coreservice.entity.HistoricalOrderStatus;
import com.gifu.coreservice.entity.Order;
import com.gifu.coreservice.exception.InvalidRequestException;
import com.gifu.coreservice.model.request.ChangeStatusRequest;
import com.gifu.coreservice.repository.HistoricalOrderStatusRepository;
import com.gifu.coreservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HistoricalOrderStatusService {

    @Autowired
    private HistoricalOrderStatusRepository historicalOrderStatusRepository;
    @Autowired
    private OrderRepository orderRepository;

    public List<HistoricalOrderStatus> findByOrderIdAndStatusIn(Long orderId, List<String> statuses, Pageable pageable) {
        return historicalOrderStatusRepository.findByOrderIdAndStatusIn(orderId, statuses, pageable);
    }

    public HistoricalOrderStatus changeStatus(ChangeStatusRequest request) throws InvalidRequestException {
        Optional<Order> orderOpt = orderRepository.findById(request.getOrderId());
        if (orderOpt.isEmpty()) {
            throw new InvalidRequestException("Order is not existed", null);
        }
        Order order = orderOpt.get();
        order.setStatus(request.getStatus());
        order.setUpdatedDate(ZonedDateTime.now());
        order.setUpdatedBy(request.getUpdaterEmail());
        orderRepository.save(order);
        HistoricalOrderStatus historicalOrderStatus = new HistoricalOrderStatus();
        historicalOrderStatus.setOrderId(order.getId());
        historicalOrderStatus.setStatus(request.getStatus());
        historicalOrderStatus.setCreatedDate(ZonedDateTime.now());
        historicalOrderStatus.setCreatedBy(request.getUpdaterEmail());
        return historicalOrderStatusRepository.save(historicalOrderStatus);
    }
}
