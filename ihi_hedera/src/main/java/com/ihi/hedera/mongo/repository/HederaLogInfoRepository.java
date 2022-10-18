package com.ihi.hedera.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ihi.hedera.mongo.model.HederaLogInfo;


@Repository
public interface HederaLogInfoRepository extends MongoRepository<HederaLogInfo, Long>{

}
