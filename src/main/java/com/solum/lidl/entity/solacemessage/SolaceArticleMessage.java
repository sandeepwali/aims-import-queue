package com.solum.lidl.entity.solacemessage;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.solum.lidl.entity.solacemessage.metadata.MetaData;
import com.solum.lidl.entity.solacemessage.payload.article.Article;
import com.solum.lidl.entity.solacemessage.payload.article.ArticleTypeOne;
import com.solum.lidl.entity.solacemessage.payload.article.ArticleTypeTwo;

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
public class SolaceArticleMessage {

	    @JsonProperty("MetaData") 
	    private MetaData metaData;
	    @JsonProperty("Payload") 
	    private List<ArticleTypeTwo> payload;
	    
	
}
