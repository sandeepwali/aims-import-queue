package com.solum.lidl.entity.solacemessage.payload.article;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DeleteArticleItems{

	private String stationCode;
	private List<String> items;









}
