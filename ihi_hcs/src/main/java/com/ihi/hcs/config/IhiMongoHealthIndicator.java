package com.ihi.hcs.config;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.boot.actuate.mongo.MongoHealthIndicator;

@Component
public class IhiMongoHealthIndicator extends MongoHealthIndicator {

	public IhiMongoHealthIndicator(MongoTemplate mongoTemplate) {
		super(mongoTemplate);
	}
}
