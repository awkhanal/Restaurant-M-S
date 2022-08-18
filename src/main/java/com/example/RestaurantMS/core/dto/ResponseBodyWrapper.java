package com.example.RestaurantMS.core.dto;


import lombok.*;

@Data
@EqualsAndHashCode(callSuper=false)
@Builder
public class ResponseBodyWrapper<T> {
    private T payload;
    private String message;
}

