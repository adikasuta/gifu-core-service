package com.gifu.coreservice.model.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class SearchDashboardOrderRequest {
    private String productType;
    private LocalDate periodFrom;
    private LocalDate periodUntil;
    private String query;
    private List<String> statuses;
}
