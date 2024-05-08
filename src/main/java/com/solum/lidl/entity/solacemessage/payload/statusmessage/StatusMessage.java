package com.solum.lidl.entity.solacemessage.payload.statusmessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StatusMessage{
	private String statusCode;
	private String statusMessage;
	private String statusType;
	private StatusDetails statusDetails;
}
