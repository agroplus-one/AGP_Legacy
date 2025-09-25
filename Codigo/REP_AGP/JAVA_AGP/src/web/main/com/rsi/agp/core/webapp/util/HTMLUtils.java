package com.rsi.agp.core.webapp.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

public final class HTMLUtils {
	
	private static final Log logger = LogFactory.getLog(HTMLUtils.class);
	
	public final static String ERR_OP_MSG = "El valor de opcion (OP) es incorrecto";	
	public final static String EOF = "\r\n";
	
	public static ModelAndView errorMessage(String component, String message){
		Map<String, Object> myModel = new HashMap<String, Object>();
		
		myModel.put("component", component);
		try {
			myModel.put("message", new String(message.getBytes(), "ISO8859-1"));
		} catch (UnsupportedEncodingException e) {
			myModel.put("message", message);
			logger.error("Excepcion : HTMLUtils - errorMessage", e);
		}
		
		return new ModelAndView("error", "result", myModel);
	}
	
	public static ModelAndView errorMessage(String component, String message, String code){
		Map<String, Object> myModel = new HashMap<String, Object>();
		
		myModel.put("component", component);
		try {
			myModel.put("code", new String(code.getBytes(), "ISO8859-1"));
			myModel.put("message", new String(message.getBytes(), "ISO8859-1"));
		} catch (UnsupportedEncodingException e) {
			myModel.put("code", code);
			myModel.put("message", message);
			logger.error("Excepcion : HTMLUtils - errorMessage", e);
		}
		
		return new ModelAndView("error", "result", myModel);
	}
	
	public static String alertMessage(String message){
		StringBuffer sb = new StringBuffer();
		String messageTmp;
		
		sb.append("<script language='JavaScript' type='text/javascript'>").append(EOF);
		sb.append("alert('");
		if(message.contains("'")){
			messageTmp = message.replace("'", "");
			sb.append(messageTmp);
		}else{
			sb.append(message);
		}
		sb.append("');").append(EOF);
		sb.append("</script>").append(EOF);
		
		return sb.toString();
	}
	
	public static Object getProperty(Object bean, String campo){
		
		try {
		
			return PropertyUtils.getProperty(bean, campo);
		
		}catch(Exception e){
			return null;
		}
	}
	
}
