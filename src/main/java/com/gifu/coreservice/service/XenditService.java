package com.gifu.coreservice.service;

import com.gifu.coreservice.config.XenditConfiguration;
import com.gifu.coreservice.config.xenditmerchant.*;
import com.gifu.coreservice.entity.Bill;
import com.gifu.coreservice.entity.XenditClosedVa;
import com.gifu.coreservice.entity.XenditVaPayment;
import com.gifu.coreservice.enumeration.BillStatus;
import com.gifu.coreservice.enumeration.XenditVaStatus;
import com.gifu.coreservice.exception.ObjectToJsonStringException;
import com.gifu.coreservice.model.request.XenditVaCallbackTopUpDto;
import com.gifu.coreservice.model.request.XenditVaCreateOrUpdateCallback;
import com.gifu.coreservice.repository.BillRepository;
import com.gifu.coreservice.repository.XenditClosedVaRepository;
import com.gifu.coreservice.repository.XenditVaPaymentRepository;
import com.gifu.coreservice.utils.StringUtils;
import com.xendit.XenditClient;
import com.xendit.enums.BankCode;
import com.xendit.exception.XenditException;
import com.xendit.model.FixedVirtualAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class XenditService {

    @Autowired
    private XenditConfiguration xenditConfiguration;
    @Autowired
    private BniConfiguration bniConfiguration;
    @Autowired
    private BriConfiguration briConfiguration;
    @Autowired
    private MandiriConfiguration mandiriConfiguration;
    @Autowired
    private PermataConfiguration permataConfiguration;
    @Autowired
    private SampoernaConfiguration sampoernaConfiguration;

    @Autowired
    private XenditClosedVaRepository xenditClosedVaRepository;
    @Autowired
    private XenditVaPaymentRepository xenditVaPaymentRepository;
    @Autowired
    private ObjectMapperService objectMapperService;
    @Autowired
    private BillRepository billRepository;

    private Map<BankCode, MerchantConfig> getMerchantConfigMapper() {
        Map<BankCode, MerchantConfig> map = new HashMap<>();
        map.put(BankCode.BRI, briConfiguration);
        map.put(BankCode.MANDIRI, mandiriConfiguration);
        map.put(BankCode.BNI, bniConfiguration);
        map.put(BankCode.PERMATA, permataConfiguration);
        map.put(BankCode.SAHABAT_SAMPOERNA, sampoernaConfiguration);
        return map;
    }

    private String getFullVaNumber(String vaNumber, MerchantConfig config) {
        return config.getSubprefix() + vaNumber + Optional.ofNullable(config.getSubsuffix()).orElse("");
    }

    private String generateVaNumber() {
        String generatedVa = StringUtils.generateRandomNumericString(Integer.parseInt(xenditConfiguration.getDynamicVaLength()));
        if (generatedVa.endsWith("0")) {
            generatedVa = generatedVa.substring(0, generatedVa.length() - 1) + "1";
        }
        List<XenditClosedVa> sameActiveNumbers = xenditClosedVaRepository.findByVaNumberAndStatusIn(generatedVa, Arrays.asList(XenditVaStatus.ACTIVE.name(), XenditVaStatus.PENDING.name()));
        boolean needToRegenerate = false;
        for (XenditClosedVa it : sameActiveNumbers) {
            if (ZonedDateTime.now().isAfter(it.getExpirationDate())) {
                it.setStatus(XenditVaStatus.INACTIVE.name());
                xenditClosedVaRepository.save(it);
            } else {
                needToRegenerate = true;
            }
        }
        if (needToRegenerate) {
            generateVaNumber();
        }
        return generatedVa;
    }

    @Transactional
    public void createVaClose(Bill bill) throws ObjectToJsonStringException {
        String vaNumber = generateVaNumber();
        Map<BankCode, MerchantConfig> merchantConfigs = getMerchantConfigMapper();
        for (BankCode bank : merchantConfigs.keySet()) {
            XenditClosedVa xenditClosedVa = new XenditClosedVa();
            xenditClosedVa.setBillId(bill.getId());
            xenditClosedVa.setVaNumber(vaNumber);
            xenditClosedVa.setFullVaNumber(getFullVaNumber(vaNumber, merchantConfigs.get(bank)));
            xenditClosedVa.setExpectedAmount(bill.getAmount());
            xenditClosedVa.setBankCode(bank.getText());
            xenditClosedVa.setName(bill.getCustomerName());
            xenditClosedVa.setExpirationDate(bill.getExpiryDate());
            xenditClosedVa.setStatus(XenditVaStatus.PENDING.name());
            UUID uuid = UUID.randomUUID();
            xenditClosedVa.setExternalId(uuid.toString());
            xenditClosedVa.setCreatedDate(ZonedDateTime.now());
            xenditClosedVaRepository.save(xenditClosedVa);
            requestGenerateVA(xenditClosedVa);
        }
    }

    private void requestExpireVA(XenditClosedVa xenditClosedVa) throws ObjectToJsonStringException {
        try {
            XenditClient xenditClient = new XenditClient.Builder()
                    .setApikey(xenditConfiguration.getApiKey())
                    .build();
            Map<String, Object> closedVAMap = new HashMap<>();
            ZonedDateTime expirationDate = ZonedDateTime.now().minusDays(1);
            closedVAMap.put("expiration_date", expirationDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            FixedVirtualAccount virtualAccount = xenditClient.fixedVirtualAccount.update(xenditClosedVa.getXenditId(), closedVAMap);
            xenditClosedVa.setStatus(virtualAccount.getStatus());
            xenditClosedVa.setExpirationDate(expirationDate);
            xenditClosedVaRepository.save(xenditClosedVa);
        } catch (XenditException e) {
            String message = Optional.ofNullable(e.getMessage()).orElse("-");
            log.error("Got XenditException when requestExpireVA, externalId=" + xenditClosedVa.getExternalId() + "; message=" + message + "; code=" + e.getErrorCode(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("code", e.getErrorCode());
            errorResponse.put("message", message);
            xenditClosedVa.setResponseDate(ZonedDateTime.now());
            xenditClosedVa.setResponsePayload(objectMapperService.writeToString(errorResponse));
            xenditClosedVaRepository.save(xenditClosedVa);
        } catch (Exception e) {
            String message = Optional.ofNullable(e.getMessage()).orElse("-");
            log.error("Got Exception when requestExpireVA, externalId=" + xenditClosedVa.getExternalId() + "; message=" + message, e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("code", "-");
            errorResponse.put("message", message);
            xenditClosedVa.setResponseDate(ZonedDateTime.now());
            xenditClosedVa.setResponsePayload(objectMapperService.writeToString(errorResponse));
            xenditClosedVaRepository.save(xenditClosedVa);
        }
    }

    private void requestGenerateVA(XenditClosedVa xenditClosedVa) throws ObjectToJsonStringException {
        try {
            XenditClient xenditClient = new XenditClient.Builder()
                    .setApikey(xenditConfiguration.getApiKey())
                    .build();
            Map<String, Object> closedVAMap = new HashMap<>();
            closedVAMap.put("external_id", xenditClosedVa.getExternalId());
            closedVAMap.put("bank_code", xenditClosedVa.getBankCode());
            closedVAMap.put("name", xenditClosedVa.getName());
            closedVAMap.put("virtual_account_number", xenditClosedVa.getFullVaNumber());
            closedVAMap.put("is_closed", true);
            closedVAMap.put("is_single_use", true);
            closedVAMap.put("expected_amount", xenditClosedVa.getExpectedAmount().toPlainString());
            closedVAMap.put("expiration_date", xenditClosedVa.getExpirationDate().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            FixedVirtualAccount virtualAccount = xenditClient.fixedVirtualAccount.createClosed(closedVAMap);
            xenditClosedVa.setStatus(virtualAccount.getStatus());
            xenditClosedVa.setXenditId(virtualAccount.getId());
            xenditClosedVa.setResponseDate(ZonedDateTime.now());
            xenditClosedVa.setResponsePayload(objectMapperService.writeToString(virtualAccount));
            xenditClosedVaRepository.save(xenditClosedVa);
        } catch (XenditException e) {
            String message = Optional.ofNullable(e.getMessage()).orElse("-");
            log.error("Got XenditException when requestGenerateVa, externalId=" + xenditClosedVa.getExternalId() + "; message=" + message + "; code=" + e.getErrorCode(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("code", e.getErrorCode());
            errorResponse.put("message", message);
            xenditClosedVa.setResponseDate(ZonedDateTime.now());
            xenditClosedVa.setResponsePayload(objectMapperService.writeToString(errorResponse));
            xenditClosedVaRepository.save(xenditClosedVa);
        } catch (Exception e) {
            String message = Optional.ofNullable(e.getMessage()).orElse("-");
            log.error("Got Exception when requestGenerateVa, externalId=" + xenditClosedVa.getExternalId() + "; message=" + message, e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("code", "-");
            errorResponse.put("message", message);
            xenditClosedVa.setResponseDate(ZonedDateTime.now());
            xenditClosedVa.setResponsePayload(objectMapperService.writeToString(errorResponse));
            xenditClosedVaRepository.save(xenditClosedVa);
        }
    }

    public void handleModifyVaCallback(XenditVaCreateOrUpdateCallback callback) throws ObjectToJsonStringException {
        Optional<XenditClosedVa> closedVaOpt = xenditClosedVaRepository.findByXenditId(callback.getId());
        if (closedVaOpt.isEmpty()) {
            log.warn("Closed Va with xenditId=" + callback.getId() + " is not found");
            return;
        }
        XenditClosedVa closedVa = closedVaOpt.get();
        Optional<Bill> billOpt = billRepository.findById(closedVa.getBillId());
        if (billOpt.isEmpty()) {
            log.warn("Closed Va with xenditId=" + callback.getId() + " has no bill");
            return;
        }
        Bill bill = billOpt.get();
        if(BillStatus.PAID.name().equals(bill.getStatus())){
            log.warn("Closed Va with xenditId=" + callback.getId() + " already paid");
            return;
        }
        if (!ZonedDateTime.now().isAfter(callback.getExpirationDate()) &&
                XenditVaStatus.ACTIVE.name().equals(callback.getStatus()) &&
                BillStatus.PENDING.name().equals(bill.getStatus())) {
            bill.setStatus(BillStatus.READY_TO_PAY.name());
            billRepository.save(bill);
            //TODO: we need to make sure that all va banks are created, before changing bill status to ready to pay
            //TODO: send payment instruction email after all xendit va is created
        }

        closedVa.setStatus(callback.getStatus());
        closedVa.setCallbackDate(ZonedDateTime.now());
        closedVa.setCallbackPayload(objectMapperService.writeToString(callback));
        xenditClosedVaRepository.save(closedVa);
    }


    @Transactional
    public Bill handleBillPaid(XenditVaCallbackTopUpDto callback) throws ObjectToJsonStringException {
        Optional<XenditVaPayment> existingCallback = xenditVaPaymentRepository.findByXenditId(callback.getId());
        if (existingCallback.isPresent()) {
            log.warn("Payment callback with xenditId=" + callback.getId() + " already processed before");
            return null;
        }
        Optional<XenditClosedVa> closedVaOpt = xenditClosedVaRepository.findByXenditId(callback.getXenditIdForVa());
        if (closedVaOpt.isEmpty()) {
            log.warn("Closed Va with xenditId=" + callback.getXenditIdForVa() + " is not found");
            return null;
        }
        XenditClosedVa closedVa = closedVaOpt.get();
        Optional<Bill> billOpt = billRepository.findById(closedVa.getBillId());
        if (billOpt.isEmpty()) {
            log.warn("Closed Va with xenditId=" + callback.getXenditIdForVa() + " has no bill");
            return null;
        }
        Bill bill = billOpt.get();
        if (bill.getBillPaymentId() != null && bill.getPaymentDate() != null) {
            log.warn("Bill with id=" + bill.getId() + " already paid");
            return null;
        }
        XenditVaPayment newPayment = new XenditVaPayment();
        newPayment.setXenditId(callback.getId());
        newPayment.setVaXenditId(callback.getXenditIdForVa());
        newPayment.setVaExternalId(callback.getExternalId());
        newPayment.setBankCode(callback.getBankCode());
        newPayment.setVaNumber(callback.getAccountNumber());
        newPayment.setTransactionDate(callback.getTransactionTimestamp());
        newPayment.setAmount(new BigDecimal(callback.getAmount()));
        newPayment.setBillId(bill.getId());
        newPayment.setCallbackDate(ZonedDateTime.now());
        newPayment.setCallbackPayload(objectMapperService.writeToString(callback));
        xenditVaPaymentRepository.save(newPayment);
        bill.setStatus(BillStatus.PAID.name());
        bill.setPaymentDate(callback.getTransactionTimestamp());
        bill.setBillPaymentId(newPayment.getId());
        billRepository.save(bill);

        List<XenditClosedVa> relatedVAs = xenditClosedVaRepository.findByBillId(bill.getId());
        for(XenditClosedVa va : relatedVAs){
            if(!Objects.equals(va.getId(), closedVa.getId())){
                requestExpireVA(va);
            }
        }
        return bill;
    }
}
