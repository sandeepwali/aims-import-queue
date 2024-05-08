package com.solum.lidl.entity.stats;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Embeddable;

import com.solum.lidl.entity.solacemessage.metadata.MetaData;

import lombok.Data;

@Embeddable
@Data
public class StatsPK implements Serializable { 
	private static final long serialVersionUID = 1L;

	private LocalDate  date;
	private String senderSystemId;
	private String senderSystemName;
	private String senderCompany;
	private String senderCountry;
	private String senderWarehouse;
	private String storeCode;

	public   StatsPK() {
	}

	public StatsPK(MetaData  metaData, String storeCode) {
		this.date = LocalDate.now();
		this.senderSystemId = metaData.getSender().getSystemId();
		this.senderSystemName = metaData.getSender().getSystemName();
		this.senderCompany = metaData.getSender().getCompany();
		this.senderCountry = metaData.getSender().getCountry();
		this.senderWarehouse = metaData.getSender().getWarehouse();
		this.storeCode = storeCode;
	}
	@Override
	public int hashCode() {
		return Objects.hash(date + senderSystemId +senderSystemName+senderCompany+ senderCountry+senderWarehouse+storeCode);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StatsPK other = (StatsPK) obj;
		return Objects.equals(date, other.date) 
				&& Objects.equals(senderSystemId, other.senderSystemId)
				&& Objects.equals(senderSystemName, other.senderSystemName)
				&& Objects.equals(senderCompany, other.senderCompany)
				&& Objects.equals(senderCountry, other.senderCountry)
				&& Objects.equals(senderWarehouse, other.senderWarehouse)
				&& Objects.equals(storeCode, other.storeCode);
	}

}
