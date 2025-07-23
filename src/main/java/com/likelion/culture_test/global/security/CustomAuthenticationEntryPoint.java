package com.likelion.culture_test.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.culture_test.global.exceptions.ErrorResponse;
import com.likelion.culture_test.global.exceptions.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        ErrorResponse errorResponse = ErrorResponse.from(ErrorCode.UNAUTHORIZED);

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(errorResponse.getStatus());

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
