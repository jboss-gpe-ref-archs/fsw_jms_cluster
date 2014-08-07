package com.redhat.gpe.refarch.hq.client;

import java.util.logging.Logger;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

public class ProducerThread extends Thread {
    
    private HornetQService service;
    private String destination;
    private String destinationType;
    private int messageCount = 100;
    private int messageSize = 0;
    private int sleep = 0;
    private boolean persistent;
    private int transactionBatchSize;
    private int sentCount;
    private int transactions = 0;
    private String messageText;
    private byte[] payload;
    
    private static final Logger log = Logger.getLogger(ProducerThread.class.getName());
    
    public ProducerThread(HornetQService service, String destination, String destinationType) {
        this.service = service;
        this.destination = destination;
        this.destinationType = destinationType;
    }

    @Override
    public void run() {
        MessageProducer producer = null;
        try {
            producer = service.createProducer(destination, destinationType);
            producer.setDeliveryMode(persistent ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT);
            initPayLoad();
            for (sentCount = 0; sentCount < messageCount; sentCount++) {
                Message message = createMessage(sentCount);
                producer.send(message);
                log.info("Sent: " + (message instanceof TextMessage ? ((TextMessage) message).getText() : message.getJMSMessageID()));
                
                if (transactionBatchSize > 0 && sentCount > 0 && sentCount % transactionBatchSize == 0) {
                    log.info("Committing transaction: " + transactions++);
                    service.getDefaultSession().commit();
                }
                
                if (sleep > 0) {
                    Thread.sleep(sleep);
                }
            }            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (producer != null) {
                try {
                    producer.close();
                } catch (JMSException ignore) {}
            }
        }
        log.info("Producer thread finished");
    }
    
    private void initPayLoad() {
        if (messageSize > 0) {
            payload = new byte[messageSize];
            for (int i=0; i<payload.length; i++) {
                payload[i] = '.';
            }
        }
    }

    protected Message createMessage(int i) throws Exception {
        if (payload != null) {
            return service.createBytesMessage(payload);
        } else {
            return service.createTextMessage(messageText + " " + i);
        }
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public void setMessageSize(int messageSize) {
        this.messageSize = messageSize;
    }

    public void setSleep(int sleep) {
        this.sleep = sleep;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public void setTransactionBatchSize(int transactionBatchSize) {
        this.transactionBatchSize = transactionBatchSize;
    }

    public int getSentCount() {
        return sentCount;
    }

    public void setSentCount(int sentCount) {
        this.sentCount = sentCount;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

}
