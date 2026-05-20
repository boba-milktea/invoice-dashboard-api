package edu.hyf.invoice.auth;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j

public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        log.warn("--REQUEST ARRIVED: " + httpServletRequest.getMethod() + " " + httpServletRequest.getRequestURI());

        filterChain.doFilter(servletRequest, servletResponse);

        log.warn("--RESPONSE SENT" + httpServletRequest.getRequestURI());
    }
}
