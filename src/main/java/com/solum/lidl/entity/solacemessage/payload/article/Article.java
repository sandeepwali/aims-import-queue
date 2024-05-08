package com.solum.lidl.entity.solacemessage.payload.article;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

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
@Component
public class Article {

	private String stationCode;
    private String id;
    private String name;
    private String nfc;
    private Object originPrice;
    private Object salePrice;
    private Object discountPercent;
    private Map<String, String> data = new HashMap<>();
    //private Date createdDate;
    //private Date modifiedDate;
    private String reservedOne;
    private String reservedTwo;
    private String reservedThree;

}
