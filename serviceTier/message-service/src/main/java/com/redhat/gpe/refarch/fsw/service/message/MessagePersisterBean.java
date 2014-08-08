package com.redhat.gpe.refarch.fsw.service.message;

import java.util.Date;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.Service;

import com.redhat.gpe.refarch.fsw.domain.model.Message;

@Service(MessagePersister.class)
public class MessagePersisterBean implements MessagePersister {
    
    private static Logger logger = Logger.getLogger(MessagePersisterBean.class);
    
    @Inject @Reference("AuditLoggerService")
    AuditLoggerService auditLogger;
    
    @PersistenceContext(unitName = "message")
    private EntityManager em;

    @Override
    public void persist(String message) {
        
        logger.debug("Processing message '" + message + "'");
        
        Message messageObj = new Message();
        messageObj.setMessage(message);
        messageObj.setDate(new Date());
        messageObj.setNode(System.getProperty("jboss.node.name"));
        em.persist(messageObj);  
        
        auditLogger.log(message);
    }

}
