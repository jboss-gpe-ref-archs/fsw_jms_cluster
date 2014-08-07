package com.redhat.gpe.refarch.hq.client;

import java.util.HashMap;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.core.remoting.impl.netty.TransportConstants;
import org.hornetq.jms.client.HornetQConnectionFactory;


public class HornetQService {
    
    private boolean transacted = false;
    private String user;
    private String password;
    
    private HornetQConnectionFactory connectionFactory;
    private Connection defaultConnection;
    private Session defaultSession;
    
    private boolean started = false;
    
    public HornetQService(String host, String port, String user, String password) {
        this.user = user;
        this.password = password;
        initConnectionFactory(host, port);
    }
    
    private void initConnectionFactory(String host, String port) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(TransportConstants.HOST_PROP_NAME, host);
        params.put(TransportConstants.PORT_PROP_NAME, port);
        TransportConfiguration transportConfiguration = new TransportConfiguration(NettyConnectorFactory.class.getName(), params);
        connectionFactory = HornetQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF, transportConfiguration);
    }
    
    public void setTransacted(boolean transacted) {
        this.transacted = transacted;
    }
    
    public void start() throws JMSException {
        defaultConnection = connectionFactory.createConnection(user, password);
        defaultSession = defaultConnection.createSession(transacted, transacted ? Session.SESSION_TRANSACTED : Session.AUTO_ACKNOWLEDGE);
        defaultConnection.start();
    }
    
    public MessageProducer createProducer(String destination, String destinationType) throws JMSException {
        Destination dest = createDestination(destination, destinationType);
        return defaultSession.createProducer(dest);        
    }
    
    public MessageConsumer createConsumer(String destination, String destinationType) throws JMSException {
        Destination dest = createDestination(destination, destinationType);
        return defaultSession.createConsumer(dest);
    }
    
    public Destination createDestination(String destination, String destinationType) {
        if (destinationType.equals("topic")) {
            return HornetQJMSClient.createTopic(destination);
        } else {
            return HornetQJMSClient.createQueue(destination);
        }
    }
    
    public Message createBytesMessage(byte[] payload) throws JMSException {
        BytesMessage message = defaultSession.createBytesMessage();
        message.writeBytes(payload);
        return message;
    }

    public Message createTextMessage(String text) throws JMSException {
        return defaultSession.createTextMessage(text);
    }
    
    public Session getDefaultSession() {
        return defaultSession;
    }
    
    public Connection getDefaultConnection() {
        return defaultConnection;
    }
    
    public void stop() {
        if (started) {
            if (defaultConnection != null) {
                try {
                    defaultConnection.close();
                } catch (JMSException ignored) {}
            }
        }
        started = false;
    }
 
}
