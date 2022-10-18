package com.ihi.hcs.mongo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ihi.hcs.mongo.model.HcsSubscribeInfo;
import com.ihi.hcs.mongo.model.HederaSubscribeInfo;

@Repository
public interface HcsSubscribeInfoRepo extends MongoRepository<HcsSubscribeInfo, Long>{

	Page<HederaSubscribeInfo> findByClientId(String clientId, PageRequest pageable);

}
