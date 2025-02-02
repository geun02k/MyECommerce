package com.myecommerce.MyECommerce.security.exception.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

// AuthenticationEntryPoint 인터페이스 (401, 인증되지않음)
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final HandlerExceptionResolver handlerExceptionResolver;

    // commence()
    // : 인증되지 않은 요청 발생 시 호출되는 메서드.
    //   예외처리를 직접 수행하지 않고 HandlerExceptionResolver로 넘긴다.
    @Override
    public void commence(@NonNull HttpServletRequest request,
                         @NonNull HttpServletResponse response,
                         @NonNull AuthenticationException authException)
            throws IOException, ServletException {
        handlerExceptionResolver.resolveException(request, response, null, authException);
    }
}
