package com.ihi.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ihi.admin.model.HederaFees;

@Repository
public interface HederaFeesRepo extends JpaRepository<HederaFees, String>{

	HederaFees findByServiceAndOperations(String service,String operation);
	
}
