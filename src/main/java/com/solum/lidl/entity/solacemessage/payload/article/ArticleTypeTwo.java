package com.solum.lidl.entity.solacemessage.payload.article;

import java.util.Map;
import java.util.Objects;

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
public class ArticleTypeTwo extends ArticleTypeOne{

	private String stationCode;
    private String id;
    private String name;
    private String nfc;
    private String[] eans;
    private Map<String, String> data;


	@Override
	public int hashCode() {
		return Objects.hash(id + stationCode);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArticleTypeTwo other = (ArticleTypeTwo) obj;
		return Objects.equals(id, other.id) && Objects.equals(stationCode, other.stationCode);
	}



}
