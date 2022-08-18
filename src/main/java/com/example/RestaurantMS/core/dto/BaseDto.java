package com.example.RestaurantMS.core.dto;

import lombok.Data;


@Data
public abstract class BaseDto<PK> {

    private PK id;

    private Long createdOn = System.currentTimeMillis();
    private Long modifiedOn = System.currentTimeMillis();


    private Long createdBy;

    private Long modifiedBy;

    private int version;

}

