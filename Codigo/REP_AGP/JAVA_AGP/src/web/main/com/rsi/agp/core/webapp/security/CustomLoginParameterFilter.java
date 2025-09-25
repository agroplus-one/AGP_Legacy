package com.rsi.agp.core.webapp.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.filter.GenericFilterBean;

import com.rsi.agp.core.webapp.util.StringUtils;

public class CustomLoginParameterFilter extends GenericFilterBean {

	private static final Log LOGGER = LogFactory.getLog(CustomLoginParameterFilter.class);

    @Override
    public void doFilter(
      ServletRequest request, ServletResponse response, 
      FilterChain chain) throws IOException, ServletException { 
        HttpServletRequest req = (HttpServletRequest) request;
        String codTerminal = req.getParameter("codTerminal");
        LOGGER.debug("Init - CustomLoginParameterFilter");
        LOGGER.debug("codTerminal  : " + codTerminal);
        LOGGER.debug("codTerminal2  : " + req.getSession().getAttribute("codTerminal"));
        LOGGER.debug("codTerminal3 : " + req.getAttribute("codTerminal"));
        if (!StringUtils.isNullOrEmpty(codTerminal)) {
        	req.getSession().setAttribute("codTerminal", codTerminal);
        }
        chain.doFilter(request, response);
    }
}