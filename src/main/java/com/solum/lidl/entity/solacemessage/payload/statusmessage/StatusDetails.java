package com.solum.lidl.entity.solacemessage.payload.statusmessage;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StatusDetails{
	private Date statusTimeStamp;
	private String sourceHost;
	private SourceLocation sourceLocation;
	private String detailedDescription;
	private String sourcePrimaryPort;
}
