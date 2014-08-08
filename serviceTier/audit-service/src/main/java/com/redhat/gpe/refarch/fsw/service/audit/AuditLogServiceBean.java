package com.redhat.gpe.refarch.fsw.service.audit;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.switchyard.component.bean.Service;

import com.redhat.gpe.refarch.fsw.domain.model.AuditLog;


@Service(AuditLogService.class)
public class AuditLogServiceBean implements AuditLogService {
    
    private static Logger logger = Logger.getLogger(AuditLogServiceBean.class);
    
    @PersistenceContext(unitName = "log")
    private EntityManager em;

    @Override
    public void log(String log) {
        
        logger.debug("Logging message '" + log + "'");
        
        AuditLog messageObj = new AuditLog();
        messageObj.setLog(log);
        messageObj.setDate(new Date());
        messageObj.setNode(System.getProperty("jboss.node.name"));
        em.persist(messageObj);          
    }

}
