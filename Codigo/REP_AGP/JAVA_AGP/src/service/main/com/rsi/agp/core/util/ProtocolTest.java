package com.rsi.agp.core.util;

// javac ProtocolTest.java && java ProtocolTest

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLContext;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

public class ProtocolTest {
    
    public static void main(String[] args) throws Exception {

        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null,null,null);

        SSLSocketFactory factory = (SSLSocketFactory)context.getSocketFactory();
        SSLSocket socket = (SSLSocket)factory.createSocket();

        String[] protocols = socket.getSupportedProtocols();

        System.out.println("Supported Protocols: " + protocols.length);
        for(int i = 0; i < protocols.length; i++)
        {
             System.out.println("  " + protocols[i]);
        }

        socket.setEnabledProtocols(new String[]{"TLSv1.2" });
        protocols = socket.getEnabledProtocols();

        System.out.println("Enabled Protocols: " + protocols.length);
        for(int i = 0; i < protocols.length; i++)
        {
             System.out.println("  " + protocols[i]);
        }
    }
}