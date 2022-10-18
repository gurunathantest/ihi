package com.ihi.hcs.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ihi.hcs.mongo.model.DataLogInfo;

@Repository
public interface DataLogInfoRepository extends MongoRepository<DataLogInfo, Long> {

}

