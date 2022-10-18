package com.ihi.hcs.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ihi.hcs.mongo.model.HederaHtsInfo;

@Repository
public interface HederaHtsInfoRepository extends MongoRepository<HederaHtsInfo, Long> {

}
