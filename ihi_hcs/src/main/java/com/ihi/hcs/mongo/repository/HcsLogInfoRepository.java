package com.ihi.hcs.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ihi.hcs.mongo.model.HcsLogInfo;


@Repository
public interface HcsLogInfoRepository extends MongoRepository<HcsLogInfo, Long>{

}
