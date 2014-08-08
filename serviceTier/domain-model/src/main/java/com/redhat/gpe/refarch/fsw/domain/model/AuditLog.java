package com.redhat.gpe.refarch.fsw.domain.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="auditlog")
@SequenceGenerator(name="auditLogIdSeq", sequenceName="auditLogIdSeq")
public class AuditLog implements Serializable {
    
    private static final long serialVersionUID = 2296528696021789637L;

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator="auditLogIdSeq")
    private long id;
    
    @Column(name="log")
    private String log;
    
    @Column(name="submitted")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    
    @Column(name="node")
    private String node;

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public long getId() {
        return id;
    }

}
