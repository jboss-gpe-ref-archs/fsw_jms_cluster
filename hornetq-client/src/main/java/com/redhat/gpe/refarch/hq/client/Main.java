package com.redhat.gpe.refarch.hq.client;

import java.util.Arrays;
import java.util.LinkedList;

import javax.jms.JMSException;

public class Main {
    
    private static final String DEFAULT_ACTION = "producer";
    private static final String DEFAULT_DESTINATION = "test";
    private static final String DEFAULT_DESTINATION_TYPE = "queue";
    private static final String DEFAULT_BROKER_HOST = "localhost";
    private static final String DEFAULT_BROKER_PORT = "5445";
    private static final String DEFAULT_USERNAME = "user";
    private static final String DEFAULT_PASSWORD = "user";
    private static final String DEFAULT_MESSAGE_TEXT = "Message";
    
    private String action;
    private String destination;
    private String destinationType;
    private String host;
    private String port;
    private String user;
    private String password;
    private int size = 0;
    private int batchSize = 0;
    private int count = 100;
    private int sleep = 0;
    private boolean persistent = true;
    private String text;

    public static void main(String[] args) {
        
        Main main = new Main();
        
        //process the arguments
        LinkedList<String> arg1 = new LinkedList<String>(Arrays.asList(args));
        while (!arg1.isEmpty()) {
            try {
                String arg = arg1.removeFirst();
                if ("--action".equals(arg)) {
                    main.action = shift(arg1);
                } else if ("--host".equals(arg)) {
                    main.host = shift(arg1);
                } else if ("--port".equals(arg)) {
                    main.port = shift(arg1);
                } else if ("--destinationType".equals(arg)) {
                    main.destinationType = shift(arg1);
                } else if ("--destination".equals(arg)) {
                    main.destination = shift(arg1);
                } else if ("--batchSize".equals(arg)) {
                    main.batchSize = Integer.parseInt(shift(arg1));
                } else if ("--count".equals(arg)) {
                    main.count = Integer.parseInt(shift(arg1));
                } else if ("--size".equals(arg)) {
                    main.size = Integer.parseInt(shift(arg1));
                } else if ("--text".equals(arg)) {
                    main.text = shift(arg1);
                } else if ("--sleep".equals(arg)) {
                    main.sleep = Integer.parseInt(shift(arg1));
                } else if ("--persistent".equals(arg)) {
                    main.persistent = Boolean.valueOf(shift(arg1)).booleanValue();
                } else if ("--user".equals(arg)) {
                    main.user = shift(arg1);
                } else if ("--password".equals(arg)) {
                    main.password = shift(arg1);
                } else if ("--help".equals(arg)) {
                    displayHelpAndExit(0);   
                } else {
                    System.err.println("Invalid usage: unknown option: " + arg);
                    displayHelpAndExit(1);
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid usage: argument not a number");
                displayHelpAndExit(1);
            }
        }
        
        main.execute();
        System.exit(0);
    }
    
    private void execute() {
        initAction();
        initBrokerUrl();
        initDestination();
        initUsernameAndPassword();
        initMessageText();
        System.out.println("Using destination: " + destination + ", on broker: " + host + ":" + port);
        
        HornetQService hornetQService = new HornetQService(host, port, user, password);
        hornetQService.setTransacted(batchSize > 0);
        
        try {
            hornetQService.start();
            
            if ("producer".equals(action)) {
                ProducerThread producerThread = new ProducerThread(hornetQService, destination, destinationType);
                producerThread.setMessageCount(count);
                producerThread.setMessageSize(size);
                producerThread.setMessageText(text);
                producerThread.setSleep(sleep);
                producerThread.setPersistent(persistent);
                producerThread.setTransactionBatchSize(batchSize);
                producerThread.run();
                System.out.println("Produced: " + producerThread.getSentCount());
                
            } else if ("consumer".equals(action)) {
                ConsumerThread consumerThread = new ConsumerThread(hornetQService, destination, destinationType);
                consumerThread.setMessageCount(count);
                consumerThread.setSleep(sleep);
                consumerThread.setTransactionBatchSize(batchSize);
                System.out.println("Waiting for: " + count + " messages");
                consumerThread.run();
                System.out.println("Consumed: " + consumerThread.getReceived() + " messages");
                
            } else {
                displayHelpAndExit(1);
            }
            
        } catch (JMSException e) {
            System.err.println("Execution failed with: " + e);
            e.printStackTrace(System.err);
            System.exit(2);
        } finally {
            hornetQService.stop();
        }
        
    }
    
    private static String shift(LinkedList<String> argl) {
        if (argl.isEmpty()) {
            System.out.println("Invalid usage: Missing argument");
            displayHelpAndExit(1);
        }
        return argl.removeFirst();
    }
    
    private void initDestination() {
        if (destination == null) {
            destination = DEFAULT_DESTINATION;
        }
        if (destinationType == null || destinationType != "topic") {
            destinationType = DEFAULT_DESTINATION_TYPE;
        }
    }
    
    private void initAction() {
        if (action == null) {
            action = DEFAULT_ACTION;
        }
    }
    
    private void initBrokerUrl() {
        if (host == null) {
            host = DEFAULT_BROKER_HOST;
        }
        if (port == null) {
            port = DEFAULT_BROKER_PORT;
        }
    }
    
    private void initUsernameAndPassword() {
        if (user == null) {
            user = DEFAULT_USERNAME;
        }
        if (password == null) {
            password = DEFAULT_PASSWORD;
        }
    }
    
    private void initMessageText() {
        if (text == null && size == 0) {
            text = DEFAULT_MESSAGE_TEXT;
        }
    }
    
    private static void displayHelpAndExit(int exitCode) {
        System.out.println(" usage   : [OPTIONS]");
        System.out.println(" options : [--action consumer|producer] - send or receive messages; default producer");
        System.out.println("           [--host  .. ] Host name or IP address of the HornetQ broker; default 'localhost'");
        System.out.println("           [--port  .. ] Port for connection to the HornetQ broker; default '5445'");
        System.out.println("           [--destinationType queue|topic] type of the destination ; default queue");
        System.out.println("           [--destination ..] name of the destination ; default 'test'");
        System.out.println("           [--batchSize   N] - use send and receive transaction batches of size N; default 0, no jms transactions");
        System.out.println("           [--count       N] - number of messages to send or receive; default 100");
        System.out.println("           [--size        N] - size in bytes of a BytesMessage; default 0, a simple TextMessage is used");
        System.out.println("           [--text      .. ] - text to use when using a text message; default default 'Message'");
        System.out.println("           [--sleep       N] - millisecond sleep period between sends or receives; default 0");
        System.out.println("           [--persistent  true|false] - use persistent or non persistent messages; default true");
        System.out.println("           [--user      .. ] - connection user name; default 'user'");
        System.out.println("           [--password  .. ] - connection password; default 'user'");        
        
        System.out.println("");

        System.exit(exitCode);
    }
    
    

}
