package com.ihi.hedera.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ihi.hedera.mongo.model.DataLogInfo;

@Repository
public interface DataLogInfoRepository extends MongoRepository<DataLogInfo, Long> {

}

