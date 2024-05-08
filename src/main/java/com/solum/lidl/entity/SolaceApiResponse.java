package com.solum.lidl.entity;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SolaceApiResponse {
	
	private Collections collections;
	private Map<String, String> data = new HashMap<>();
	private Map<String, String> links = new HashMap<>();
	private Meta meta;
}
