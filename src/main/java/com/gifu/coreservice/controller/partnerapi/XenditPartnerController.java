package com.gifu.coreservice.controller.partnerapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gifu.coreservice.entity.Bill;
import com.gifu.coreservice.exception.JsonStringToObjectException;
import com.gifu.coreservice.exception.ObjectToJsonStringException;
import com.gifu.coreservice.model.request.XenditVaCallbackTopUpDto;
import com.gifu.coreservice.model.request.XenditVaCreateOrUpdateCallback;
import com.gifu.coreservice.model.response.SingleResourceResponse;
import com.gifu.coreservice.service.ObjectMapperService;
import com.gifu.coreservice.service.TimelineService;
import com.gifu.coreservice.service.XenditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "api/partner/xendit")
public class XenditPartnerController {

    @Autowired
    private XenditService xenditService;
    @Autowired
    private ObjectMapperService objectMapperService;
    @Autowired
    private TimelineService timelineService;

    @PostMapping("/callback/virtual-account")
    public ResponseEntity<String> postVirtualAccountXenditCallback(@RequestBody String requestBody) {

        try {
            log.info("Incoming VA UPDATE from xendit : {}", requestBody);
            XenditVaCreateOrUpdateCallback callback = objectMapperService.readToObject(requestBody, XenditVaCreateOrUpdateCallback.class);
            xenditService.handleModifyVaCallback(callback);
            return ResponseEntity.ok("Post request for virtual account callback received.");
        } catch (JsonStringToObjectException | ObjectToJsonStringException e) {
            log.error("Failed handling VA Update from Xendit, message="+e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/callback/paid")
    public ResponseEntity<String> postVirtualAccountPaidXenditCallback(@RequestBody String requestBody) {
        try {
            log.info("Incoming TOPUP NOTIFY from xendit : {}", requestBody);
            XenditVaCallbackTopUpDto callback = objectMapperService.readToObject(requestBody, XenditVaCallbackTopUpDto.class);
            Bill bill = xenditService.handleBillPaid(callback);
            if(bill!=null){
                timelineService.startProductionTimeline(bill);
            }
            return ResponseEntity.ok("Post request for virtual account callback received.");
        } catch (JsonStringToObjectException | ObjectToJsonStringException e) {
            log.error("Failed handling VA Update from Xendit, message="+e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }
}
