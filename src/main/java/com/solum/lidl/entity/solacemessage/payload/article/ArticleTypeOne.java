package com.solum.lidl.entity.solacemessage.payload.article;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ArticleTypeOne {

	private String stationCode;
    private String id;
    private String name;
    private Map<String, String> data = new HashMap<>();

}
