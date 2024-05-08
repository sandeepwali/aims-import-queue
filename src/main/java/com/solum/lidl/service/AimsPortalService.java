package com.solum.lidl.service;

import java.util.UUID;

import com.solum.lidl.service.dto.ArticleParam;

public interface AimsPortalService {

	/**
	 *
	 * @param requestId
	 * @param articleParam
	 * @return
	 */
	AimsPortalUpdateArticle updateArticles(UUID requestId, ArticleParam articleParam);
	
	AimsPortalUpdateArticle deleteArticle(String articleId, String stationCode);

}
