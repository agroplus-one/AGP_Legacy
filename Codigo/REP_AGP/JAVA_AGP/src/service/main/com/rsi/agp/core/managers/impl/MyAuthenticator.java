package com.rsi.agp.core.managers.impl;

import java.net.Authenticator;
import java.net.PasswordAuthentication;


public class MyAuthenticator extends Authenticator 
{    
	private String user;  
	private String password;  
	       
	public MyAuthenticator(String user,String password) 
	{  
		this.user = user;  
	    this.password = password;  
	}  
	
	@Override  
	protected PasswordAuthentication getPasswordAuthentication() 
	{  
		PasswordAuthentication auth = new PasswordAuthentication(user,password.toCharArray());  
	    return auth;  
	}  
}  