package com.solum.lidl.config;

import com.solum.lidl.custom.exception.AimsPortalDownException;

import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {
	private final ErrorDecoder errorDecoder = new Default();
    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()){
         
            case 503:
                return new AimsPortalDownException("AIMS Portal is down");
            default:
            	return errorDecoder.decode(methodKey, response);
        }
    }
}