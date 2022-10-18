package com.ihi.admin.mongo.repository;

import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ihi.admin.mongo.model.HederaHtsInfo;

@Repository
public interface HederaHtsInfoRepository extends MongoRepository<HederaHtsInfo, Long> {
	
	Page<HederaHtsInfo> findAllByClientIdAndCreatedTimeBetween(String clientId,DateTime fromdate,DateTime todate,Pageable pageable);
	
}
