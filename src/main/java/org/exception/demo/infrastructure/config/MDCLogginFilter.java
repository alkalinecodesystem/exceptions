package org.exception.demo.infrastructure.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class MDCLogginFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		try {
			MDC.put("TRACE-ID", String.valueOf(System.currentTimeMillis()));
			chain.doFilter(request, response);
		} finally {
			MDC.remove("TRACE-ID");
		}

	}

}
