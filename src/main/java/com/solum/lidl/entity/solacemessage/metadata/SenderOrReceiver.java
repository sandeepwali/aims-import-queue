package com.solum.lidl.entity.solacemessage.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SenderOrReceiver {
	@JsonProperty("SystemId") 
	private String systemId;
    @JsonProperty("SystemName") 
    private String systemName;
    @JsonProperty("Company") 
    private String company;
    @JsonProperty("Warehouse") 
    private String warehouse;
    @JsonProperty("Country") 
    private String country;    

}
