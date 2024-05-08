package com.solum.lidl.entity.solacemessage.payload.statusmessage;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SourceLocation{
    @JsonProperty("SystemId")
    private String systemId;
    @JsonProperty("SystemName")
    private String systemName;
    @JsonProperty("Company")
    private String company;
}
