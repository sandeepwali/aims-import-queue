package com.solum.lidl.service.dto;

import java.util.List;

import com.solum.lidl.common.util.KeyGenerator;
import com.solum.lidl.entity.solacemessage.payload.article.ArticleTypeTwo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleParam {

	private String customBatchId = KeyGenerator.getKeyByDateFormat();

	private List<ArticleTypeTwo> dataList;

	public List<ArticleTypeTwo> getDataList() {
		return this.dataList;
	}

	public void setCustomBatchId(String customBatchId) {
		if (customBatchId != null && !customBatchId.isEmpty())
			this.customBatchId = customBatchId;
	}

	public String getCustomBatchId() {
		return this.customBatchId;
	}
}
