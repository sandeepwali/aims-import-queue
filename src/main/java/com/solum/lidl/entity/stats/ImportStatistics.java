package com.solum.lidl.entity.stats;


import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solum.lidl.entity.CounterState;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="ImportStatistics")
@IdClass(StatsPK.class)
public class ImportStatistics {

	@Id
	@Column(name = "date")
	private LocalDate date;
	@Id
	@Column(name = "sender_system_id")
	private String senderSystemId;
	@Id
	@Column(name = "sender_system_name")
	private String senderSystemName;

	@Column(name = "sender_company")
	private String senderCompany;
	@Id
	@Column(name = "sender_country")
	private String senderCountry;
	@Id
	@Column(name = "sender_warehouse")
	private String senderWarehouse;
	@Id
	@Column(name = "store_code")
	private String storeCode;
	@Column(name = "item_count")
	private Integer itemCount;
	@Column(name = "main_item_count")
	private Integer mainItemCount;
	@Column(name = "sub_item_count")
	private Integer subItemCount;
	@Column(name = "item_sch_count")
	private Integer itemSchCount;
	@Column(name = "item_str_count")
	private Integer itemStrCount;
	@Column(name = "del_item_count")
	private Integer delItemCount;
	@Column(name = "del_sub_item_count")
	private Integer delSubItemCount;

	@JsonIgnore
	public ImportStatistics update(CounterState state) {
		this.itemCount = this.itemCount + state.getItemCount().get();
		this.mainItemCount =  this.mainItemCount + state.getMainItemCount().get();
		this.itemSchCount = this.itemSchCount + state.getItemSchCount().get();
		this.itemStrCount = this.itemStrCount + state.getItemStrCount().get();
		this.delItemCount = this.delItemCount + state.getDelItemCount().get();
		this.delSubItemCount = this.delSubItemCount + state.getDelSubItemCount().get();
		this.subItemCount = this.subItemCount + state.getSubItemCount().get();
		return this;
	}
	@JsonIgnore
	public ImportStatistics assign(StatsPK pk) {
		this.date = pk.getDate();
		this.senderSystemId = pk.getSenderSystemId();
		this.senderSystemName = pk.getSenderSystemName();
		this.senderCompany = pk.getSenderCompany();
		this.senderCountry = pk.getSenderCountry();
		this.senderWarehouse = pk.getSenderWarehouse();
		this.storeCode = pk.getStoreCode();
		return this;

	}
	public void init() {
		this.itemCount = 0;
		this.mainItemCount =  0;
		this.itemSchCount = 0;
		this.itemStrCount = 0;
		this.delItemCount = 0;
		this.delSubItemCount = 0;
		this.subItemCount = 0;


	}
}
