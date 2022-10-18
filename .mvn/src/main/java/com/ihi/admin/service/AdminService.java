package com.ihi.admin.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ihi.admin.payload.request.GenerateInvoice;

@Service
public interface AdminService {
	
	public ResponseEntity<?> generateInvoice(GenerateInvoice generateInvoice);
	
}
