package com.solum.lidl.service;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString @Builder
public class AimsPortalArticleLabel {
	private Integer statusCode;
	private Boolean success;
	private String errorMessage;
}
