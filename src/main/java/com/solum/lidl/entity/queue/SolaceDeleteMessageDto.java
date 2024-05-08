package com.solum.lidl.entity.queue;

import org.springframework.integration.acks.AcknowledgmentCallback;

import com.solum.lidl.entity.solacemessage.SolaceDeleteArticleMessage;

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
public class SolaceDeleteMessageDto {

	private SolaceDeleteArticleMessage payload;
	private AcknowledgmentCallback callback;
	
}
