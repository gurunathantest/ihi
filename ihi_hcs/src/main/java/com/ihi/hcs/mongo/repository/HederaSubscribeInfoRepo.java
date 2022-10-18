package com.ihi.hcs.mongo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ihi.hcs.mongo.model.HederaSubscribeInfo;

@Repository
public interface HederaSubscribeInfoRepo extends MongoRepository<HederaSubscribeInfo, Long>{


	Page<HederaSubscribeInfo> findByClientId(String clientId, Pageable pageable);

}
