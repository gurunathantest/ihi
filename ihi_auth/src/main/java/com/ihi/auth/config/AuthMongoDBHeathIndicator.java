package com.ihi.auth.config;

import org.springframework.boot.actuate.mongo.MongoHealthIndicator;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class AuthMongoDBHeathIndicator extends MongoHealthIndicator {

	public AuthMongoDBHeathIndicator(MongoTemplate mongoTemplate) {
		super(mongoTemplate);
	}

}
