package com.gifu.coreservice.model.request;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class SearchDashboardOrderRequest {
    private String productType;
    private ZonedDateTime periodFrom;
    private ZonedDateTime periodUntil;
    private String query;
}
