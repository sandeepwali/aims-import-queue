package com.solum.lidl.service.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.batch.core.BatchStatus;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class AimsPortalBatch implements Serializable {

	public AimsPortalBatch() {
		super();
	}

	public AimsPortalBatch(Long id) {
		super();
		this.id = id;
	}

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String stationCode;
	private String customBatchId;
	private long requestSequence;
	private String messageProvider;
	private AimsPortalBatchType type;
	private BatchStatus status;
	private String messageParam;
	private int numberOfItems;
	private long serverId;
	private Date created;
	private Date lastModified;

	public enum AimsPortalBatchType {
		ARTICLE
	}
}