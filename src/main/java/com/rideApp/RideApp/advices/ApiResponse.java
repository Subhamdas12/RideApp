package com.rideApp.RideApp.advices;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private LocalDateTime timeStamp;
    private T data;
    private ApiError apiError;

    public ApiResponse() {
        this.timeStamp = LocalDateTime.now();
    }

    public ApiResponse(T data) {
        this();
        this.data = data;
    }

    public ApiResponse(ApiError apiError) {
        this();
        this.apiError = apiError;
    }

}
