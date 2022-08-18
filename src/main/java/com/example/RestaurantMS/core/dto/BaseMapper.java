package com.example.RestaurantMS.core.dto;

import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.time.Instant;
import java.util.Date;
import java.util.List;


public abstract class BaseMapper<S, D> {

    public static final String SPRING_MODEL = "spring";

    public abstract D mapEntityToDto(S s);

    public abstract S mapDtoToEntity(D d);

    public abstract List<D> mapEntitiesToDtos(List<S> s);

    public abstract List<S> mapDtosToEntities(List<D> d);

    @Mappings({
            @Mapping(source = "createdOn", target = "createdOn"),
            @Mapping(source = "modifiedOn", target = "modifiedOn")
    })
    public Long mapDateToLong(Date date) {
        return date.getTime();
    }

    @Mappings({
            @Mapping(source = "createdOn", target = "createdOn"),
            @Mapping(source = "modifiedOn", target = "modifiedOn")
    })
    public Date mapLongToDate(Long currentTimeMillis) {
        return Date.from(Instant.ofEpochMilli(currentTimeMillis));
    }


}
