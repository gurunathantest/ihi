package com.ihi.hedera.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ihi.hedera.payload.request.MultiSignWalletRequest;
import com.ihi.hedera.payload.request.WalletRequest;
import com.ihi.hedera.service.HederaWalletService;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/hedera")
public class HederaWalletController {

	@Autowired
	HederaWalletService hederaWalletService;
	
	
	@RequestMapping(value = "/wallet/create", method = RequestMethod.POST)
	public ResponseEntity<?> createWallet() {
		return hederaWalletService.createWallet();
	}
	
	@RequestMapping(value = "/wallet/create/v2", method = RequestMethod.POST)
	public ResponseEntity<?> createWallet(@RequestBody WalletRequest request) {
		return hederaWalletService.v2CreateWallet(request);
	}
	
	@RequestMapping(value = "/multisignwallet/create", method = RequestMethod.POST)
	public ResponseEntity<?> multiSigncreateWallet(@RequestBody List<MultiSignWalletRequest> list) {
		return hederaWalletService.multiSigncreateWallet(list);
	}
}
