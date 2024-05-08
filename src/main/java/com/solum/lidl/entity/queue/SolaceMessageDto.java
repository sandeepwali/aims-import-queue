package com.solum.lidl.entity.queue;

import com.solum.lidl.entity.solacemessage.SolaceArticleMessage;

import org.springframework.integration.acks.AcknowledgmentCallback;

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
public class SolaceMessageDto {

	private SolaceArticleMessage payload;
	private AcknowledgmentCallback callback;
	
}
