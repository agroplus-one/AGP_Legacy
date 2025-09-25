package com.rsi.agp.core.webapp.action;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.mvc.multiaction.MethodNameResolver;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

public class DefaultParameterMethodNameResolver implements MethodNameResolver {

	private String paramName, defaultMethod;
	   
    public String getHandlerMethodName( HttpServletRequest request)throws NoSuchRequestHandlingMethodException {
        
    	String name = request.getParameter(paramName);
        
        if (name == null || "".equals(name))
            name = defaultMethod;
        
        if (name == null)
            throw new NoSuchRequestHandlingMethodException( request );
        
        return name;
    }
   
    public void setParamName(String paramName){
        this.paramName = paramName;
    }
   
    public void setDefaultMethod(String defaultMethod){
        this.defaultMethod = defaultMethod;
    }

}
