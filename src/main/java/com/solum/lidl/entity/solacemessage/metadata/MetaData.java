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
public class MetaData{
    @JsonProperty("Version") 
    private String version;
    @JsonProperty("Sender") 
    private SenderOrReceiver sender;
    @JsonProperty("Receiver") 
    private List<SenderOrReceiver> receiver;
    @JsonProperty("MessageSpecification") 
    private MessageSpecification messageSpecification;
    @JsonProperty("MessageIdentifier") 
    private MessageIdentifier messageIdentifier;
    @JsonProperty("Correlation") 
    private Correlation correlation;
    @JsonProperty("CreationDate") 
    private String creationDate;
}
