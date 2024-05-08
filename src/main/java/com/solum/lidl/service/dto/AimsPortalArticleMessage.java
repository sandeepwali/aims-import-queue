package com.solum.lidl.service.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AimsPortalArticleMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private String stationCode;
	@NotNull
	@Size(min = 1)
	private String id;

	@NotNull
	@Size(min = 1)
	private String name;

	private String nfc;

	private String originPrice;

	private String salePrice;

	private String discountPercent;

	
	@NotNull
	@Size(min = 1)
	@Builder.Default
	private Map<String, String> data = new HashMap<>();

}
