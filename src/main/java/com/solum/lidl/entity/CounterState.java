package com.solum.lidl.entity;

import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solum.lidl.entity.solacemessage.payload.article.ArticleTypeTwo;

import lombok.Data;

@Data
public class CounterState {
	private AtomicInteger itemCount;
	private AtomicInteger mainItemCount;
	private AtomicInteger subItemCount;
	private AtomicInteger itemSchCount;
	private AtomicInteger itemStrCount;
	private AtomicInteger delItemCount;
	private AtomicInteger delSubItemCount;

	public CounterState(){
		init();
	}
	void init() {
		this.itemCount  = new AtomicInteger();
		this.mainItemCount  = new AtomicInteger();
		this.subItemCount  = new AtomicInteger();
		this.itemSchCount  = new AtomicInteger();
		this.itemStrCount  = new AtomicInteger();
		this.delItemCount  = new AtomicInteger();
		this.delSubItemCount  = new AtomicInteger();
	}
	@JsonIgnore
	public void process(ArticleTypeTwo payload) {
		try {
			if(payload != null) {
				itemCount.incrementAndGet();

				if(payload.getId().contains("-")) {
					subItemCount.incrementAndGet();
				}else {
					mainItemCount.incrementAndGet();
				}

				if(!payload.getData().isEmpty()) {
					boolean schFound = payload.getData().keySet().stream().anyMatch(a->a.endsWith("_SCH"));
					if(schFound) {
						itemSchCount.incrementAndGet();
					}else if(payload.getData().keySet().stream().anyMatch(a->a.endsWith("_STR"))) {
						itemStrCount.incrementAndGet();
					}

				}
			}
		} catch (Exception e) {
		}
	}
	@JsonIgnore
	public void process(String articleId) {
		try {
			if(articleId !=null) {
				itemCount.incrementAndGet();
				if(articleId.contains("-")) {
					delSubItemCount.incrementAndGet();
				}else {
					delItemCount.incrementAndGet();
				}
			}
		} catch (Exception e) {
		}

	}

}
