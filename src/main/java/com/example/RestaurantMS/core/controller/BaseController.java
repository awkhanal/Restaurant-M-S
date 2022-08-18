package com.example.RestaurantMS.core.controller;

import com.example.RestaurantMS.core.dto.BaseMapper;
import com.example.RestaurantMS.core.service.Service;

import com.example.RestaurantMS.core.utils.PaginationUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.RestaurantMS.core.utils.CommonUtils.exceptionHandled;
import static com.example.RestaurantMS.core.utils.CommonUtils.getResponseBody;


@Slf4j
@CrossOrigin(origins = "*")
public abstract class BaseController<E, D, I> {


    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    private final Service<E, I> service;

    private final BaseMapper<E, D> mapper;


    public BaseController(Service<E, I> service, BaseMapper<E, D> mapper) {
        this.service = service;
        this.mapper = mapper;
    }


    protected abstract Logger getLogger();


    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody D dto) {
        int dtoNameLength = dto.getClass().getSimpleName().length();
        String entityName = dto.getClass().getSimpleName().substring(0, dtoNameLength - 3);
        return exceptionHandled(() ->
                        mapper.mapEntityToDto(service.save(mapper.mapDtoToEntity(dto), Optional.empty())), log, HttpStatus.CREATED,
                entityName + " Created successfully", "Not Created successfully"
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable I id) {
        return exceptionHandled(() ->
                        mapper.mapEntityToDto(service.findOne(id).get()),
                log, HttpStatus.OK,
                "single item fetched successfully",
                "error in fetching a single record"
        );
    }


    @GetMapping("/multiple")
    public ResponseEntity<?> getMultipleRecordsById(@RequestParam("ids") List<I> ids){
        return exceptionHandled(() ->
                        mapper.mapEntitiesToDtos(service.findAllById(ids)),
                log, HttpStatus.OK,
                "multiples records fetched successfully",
                "error in fetching multiple records"
        );
    }


    @PutMapping ("/{id}")
    public ResponseEntity<?> update(@PathVariable I id, @RequestBody D dto) {
        int dtoNameLength = dto.getClass().getSimpleName().length();
        String entityName = dto.getClass().getSimpleName().substring(0, dtoNameLength - 3);
        return exceptionHandled(() ->
                        service.save(mapper.mapDtoToEntity(dto), Optional.of(id)), log, HttpStatus.ACCEPTED,
                entityName + " Updated successfully", "Not Updated successfully"
        );
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable I id) {
        return exceptionHandled(() -> {
                    service.deleteById(id);
                    return null;
                }, log, HttpStatus.OK,
                "Deletion successful for id " + id, "Error in deleting item with id: " + id);
    }



    @PostMapping("/bulk-delete")
    public ResponseEntity<?> deleteAllById(@RequestBody List<I> listOfIds) {
        return exceptionHandled(() -> {
                    service.deleteAllById(listOfIds);
                    return null;
                }, log, HttpStatus.OK,
                "Deletion successful for ids " + listOfIds, "Error in deleting items");
    }



    @GetMapping()
    public ResponseEntity<?> getAll(@RequestParam(required = false) Map<String, String> requestParams,
                                    @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size) {
        if (requestParams.isEmpty() || (page.equals(null) && size.equals(null))) {
            return exceptionHandled(() ->
                            mapper.mapEntitiesToDtos(service.findAll()),
                    log, HttpStatus.OK,
                    "All records successfully fetched",
                    "Error in fetching records"
            );
        } else {
            final Pageable pageable = PaginationUtils.pageable(page, size);
            final Map<String, String> filters = PaginationUtils
                    .excludePageableProperties(requestParams);

            final Page<E> entities = service.findPageableBySpec(filters, pageable);
            final Page<D> dtos = new PageImpl<>(mapper.mapEntitiesToDtos(entities.getContent()),
                    pageable, entities.getTotalElements());

            return getResponseBody(HttpStatus.OK, dtos, "Content of page " + page + " and size " + size + " retrieved");
        }
    }

}
