package com.example.RestaurantMS.core.utils;



import com.example.RestaurantMS.core.dto.ResponseBodyWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;


public class CommonUtils {
    public static <T> ResponseEntity<ResponseBodyWrapper<T>> exceptionHandled(Callable<T> function, Logger logger, HttpStatus status,
                                                                              String successMessage, String errorLog) {
        try {
            T result = function.call();
            return getResponseBody(status, result, successMessage);
        } catch (DataIntegrityViolationException e) {
            //Separating string to get specific error message.
            String[] errors = StringUtils.split(e.getRootCause().getMessage(), "\n");
            String errorMessage = StringUtils.trim(ArrayUtils.isEmpty(errors) ? e.getMessage() : errors[errors.length - 1]);
            logger.error(errorLog + errorMessage, e);
            return getResponseBody(HttpStatus.CONFLICT, null, errorMessage);
        } catch (NoSuchElementException e){
            logger.error(errorLog + e.getMessage(), e);
            return getResponseBody(HttpStatus.BAD_REQUEST, null, e.getMessage());
        } catch (HttpClientErrorException ex){
            logger.error(errorLog + ex.getMessage(), ex);
            return getResponseBody(HttpStatus.UNPROCESSABLE_ENTITY, null, ex.getMessage().substring(4));
        }catch (Exception ex) {
            logger.error(errorLog + ex.getMessage(), ex);
            return getResponseBody(HttpStatus.INTERNAL_SERVER_ERROR, null, ex.getMessage());
        }
    }


    public static <T> ResponseEntity<ResponseBodyWrapper<T>> getResponseBody(HttpStatus status, T payload, String message) {
        return ResponseEntity.status(status).body(ResponseBodyWrapper.<T>builder().payload(payload).message(message).build());
    }

    public static void setErrorResponse(HttpServletResponse response, int statusCode, String errorMessage) throws IOException {
        response.resetBuffer();
        response.setContentType("application/json");
        response.setStatus(statusCode);
        response.getOutputStream().print(new ObjectMapper().writeValueAsString(ResponseBodyWrapper.builder().message(errorMessage).build()));
        response.flushBuffer();
    }
}




