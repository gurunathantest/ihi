package com.ihi.hedera.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ihi.hedera.payload.request.MultiSignWalletRequest;
import com.ihi.hedera.payload.request.WalletRequest;

@Service
public interface HederaWalletService {

	public ResponseEntity<?> createWallet();
	public ResponseEntity<?> v2CreateWallet(WalletRequest request);
	public ResponseEntity<?> multiSigncreateWallet(List<MultiSignWalletRequest> list);
	
}
