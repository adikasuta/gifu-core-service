package com.gifu.coreservice.model.response;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class SingleResourceResponse<T> {
    private String message;
    private T data;
    private String status;

    public SingleResourceResponse(T data){
        this.data = data;
        this.message = "Success";
        this.status = String.valueOf(HttpStatus.OK.value());
    }

    public SingleResourceResponse(String message){
        this.message = message;
        this.status = String.valueOf(HttpStatus.OK.value());
    }

    public SingleResourceResponse(String message, String status){
        this.message = message;
        this.status = status;
    }
}
