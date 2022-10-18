package com.ihi.admin.config;

import org.springframework.boot.actuate.mongo.MongoHealthIndicator;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class IhiMongoHealthIndicator extends MongoHealthIndicator {

	public IhiMongoHealthIndicator(MongoTemplate mongoTemplate) {
		super(mongoTemplate);
	}
}
