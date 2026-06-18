package com.project.uber.uberApp.advices;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;


@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true; // Apply to all responses , since we have to convert each and every response body so make it "true".
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        if(body instanceof byte[]) { //added extra else code giving error , not in AnujBhaiya code.
            return body;
        }
        if(body instanceof ApiResponse<?>){

            if(request.getURI().getPath().contains("/actuator")) return body;

            if(request.getURI().getPath().contains("/v3/api-docs")
                    || request.getURI().getPath().contains("/swagger-ui"))
            {
                return body;
            }
            return body;
        }
        return new ApiResponse<>(body);
    }

}

