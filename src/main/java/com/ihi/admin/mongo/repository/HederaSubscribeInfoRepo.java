package com.ihi.admin.mongo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ihi.admin.mongo.model.HederaSubscribeInfo;

@Repository
public interface HederaSubscribeInfoRepo extends MongoRepository<HederaSubscribeInfo, Long>{


	Page<HederaSubscribeInfo> findByClientId(String clientId, Pageable pageable);

	List<HederaSubscribeInfo> findByClientId(String clientId);
}
