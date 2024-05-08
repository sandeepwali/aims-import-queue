package com.solum.lidl.entity.solacemessage.metadata;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MessageIdentifier {

		@JsonProperty("UID") 
		private String uID;
	    @JsonProperty("BusinessIdentifier") 
	    private List<BusinessIdentifier> businessIdentifier;
}
