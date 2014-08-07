package com.redhat.gpe.refarch.hq.client;

import java.util.logging.Logger;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;

public class ConsumerThread extends Thread {
    
    private HornetQService service;
    private String destination;
    private String destinationType;
    private int messageCount = 100;
    private int receiveTimeOut = 3000;
    private int received = 0;
    private int transactions = 0;
    boolean breakOnNull = false;
    int sleep;
    int transactionBatchSize;
    
    private static final Logger log = Logger.getLogger(ConsumerThread.class.getName());
    
    public ConsumerThread(HornetQService service, String destination, String destinationType) {
        this.service = service;
        this.destination = destination;
        this.destinationType = destinationType;
    }

    @Override
    public void run() {
        
        MessageConsumer consumer = null;
        try {
            consumer = service.createConsumer(destination, destinationType);
            while (received < messageCount) {
                Message msg = consumer.receive(receiveTimeOut);
                if (msg != null) {
                    log.info("Received " + (msg instanceof TextMessage ? ((TextMessage)msg).getText() : msg.getJMSMessageID()));
                    received++;
                } else {
                    if (breakOnNull) {
                        break;
                    }
                }                
            }
            
            if (transactionBatchSize > 0 && received > 0 && received % transactionBatchSize == 0) {
                log.info("Committing transaction: " + transactions++);
                service.getDefaultSession().commit();
            }
            
            if (sleep > 0) {
                Thread.sleep(sleep);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (consumer != null) {
                try {
                    consumer.close();
                } catch (JMSException ignore) {}
            }
        }
        
        log.info("Consumer thread finished");
        
    }

    public void setSleep(int sleep) {
        this.sleep = sleep;
    }

    public void setTransactionBatchSize(int transactionBatchSize) {
        this.transactionBatchSize = transactionBatchSize;
    }

    public int getReceived() {
        return received;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messagecount) {
        this.messageCount = messagecount;
    }

    public void setBreakOnNull(boolean breakOnNull) {
        this.breakOnNull = breakOnNull;
    }
}
