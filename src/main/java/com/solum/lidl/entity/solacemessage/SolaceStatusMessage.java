package com.solum.lidl.entity.solacemessage;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.solum.lidl.entity.solacemessage.metadata.MetaData;
import com.solum.lidl.entity.solacemessage.payload.statusmessage.StatusMessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SolaceStatusMessage {

	    @JsonProperty("MetaData") 
	    private MetaData metaData;
	    @JsonProperty("Payload") 
	    private StatusMessage payload;
	    
	
}
