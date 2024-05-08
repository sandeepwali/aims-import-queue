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
public class Correlation{
    @JsonProperty("CorrelationID") 
    private String correlationID;
}
