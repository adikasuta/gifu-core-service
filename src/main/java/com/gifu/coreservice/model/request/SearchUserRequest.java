package com.gifu.coreservice.model.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchUserRequest {
    private String searchQuery;
    private Long roleId;
}
