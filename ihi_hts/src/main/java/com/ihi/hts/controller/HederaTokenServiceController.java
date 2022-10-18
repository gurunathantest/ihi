package com.ihi.hts.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ihi.hts.payload.request.AssocaiteRequestV2;
import com.ihi.hts.payload.request.AssociateKycUserRequest;
import com.ihi.hts.payload.request.AssociateTokenAndEnableKycRequest;
import com.ihi.hts.payload.request.BurnTokenRequest;
import com.ihi.hts.payload.request.CreateTokenRequest;
import com.ihi.hts.payload.request.HederaFeesRequest;
import com.ihi.hts.payload.request.MintTokenRequest;
import com.ihi.hts.payload.request.MultiSignTokenTransferRequest;
import com.ihi.hts.payload.request.NFTTokenRequest;
import com.ihi.hts.payload.request.TokenBalanceRequest;
import com.ihi.hts.payload.request.TransferTokenRequest;
import com.ihi.hts.payload.request.UpdateTokenRequest;
import com.ihi.hts.payload.response.MessageResponse;
import com.ihi.hts.service.HederaTokenService;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/hts")
public class HederaTokenServiceController {

	@Autowired
	private HederaTokenService hederaTokenService;

	

	@RequestMapping(value = "/hbar/balance", method = RequestMethod.POST, produces = { "application/json" })
	public ResponseEntity<MessageResponse> hbarBalance(
			@RequestParam(name = "accountId" , required = true) String accountId,
			@RequestParam(name = "clientId" , required = false) String clientId) {
		return hederaTokenService.balanceHbar(accountId,clientId);
	}
	
	
	@RequestMapping(value = "/token/transfer", method = RequestMethod.POST)
	public ResponseEntity<?> transferToken(@Valid @RequestBody TransferTokenRequest  transferTokenRequest)  {
		return hederaTokenService.transferToken(transferTokenRequest);
	}
	

	@RequestMapping(value = "/token/user/associatekyc", method = RequestMethod.POST, produces = { "application/json" })
	public ResponseEntity<MessageResponse> associatKycUser(@Valid @RequestBody AssociateKycUserRequest associateKycUserRequest)  {
		return hederaTokenService.associatKycUser(associateKycUserRequest);
	}

	@RequestMapping(value = "/nft/create", method = RequestMethod.POST)
	public ResponseEntity<MessageResponse> createNFTToken(@Valid @RequestBody NFTTokenRequest nftTokenRequest){
		return hederaTokenService.createNFTToken(nftTokenRequest);
	}
	
	@PostMapping("/token/create")
	public ResponseEntity<?> createToken(
		@Valid	@RequestBody CreateTokenRequest createTokenRequest){
		return hederaTokenService.createToken(createTokenRequest);
	}
	
	@PostMapping("/token/users/associatekyc")
	public ResponseEntity<?> associateTokenAndEnableKyc(@Valid @RequestBody List<AssociateTokenAndEnableKycRequest> associateTokenAndEnableKycRequest){
		return hederaTokenService.associateTokenAndEnableKyc(associateTokenAndEnableKycRequest);
	}
	
	//This api is used to get wallet's token balance
	@PostMapping("/token/balance")
	public ResponseEntity<?> tokenBalance(@Valid @RequestBody TokenBalanceRequest tokenBalanceRequest){
		return hederaTokenService.tokenBalance(tokenBalanceRequest);
	}
	
	@RequestMapping(value = "/token/mint", method= RequestMethod.POST)
	public ResponseEntity<MessageResponse> MintToken(@Valid @RequestBody MintTokenRequest mintTokenRequest){
		return hederaTokenService.mintToken(mintTokenRequest);
	}
	
	@PostMapping("/token/current/totalsupply/{token}/{clientId}")
	public ResponseEntity<?> currentTotalSupply(@PathVariable(name = "token" ,required = true) String token,
			@PathVariable(name = "clientId" , required = false) String cleintId){
		return hederaTokenService.currentTotalSupply(token,cleintId);
	}
	@RequestMapping(value = "/token/burn",method = RequestMethod.POST)
	public ResponseEntity<MessageResponse> burnToken(@Valid @RequestBody BurnTokenRequest burnToken){
		return hederaTokenService.burnToken(burnToken);	
	}
	
	@GetMapping(value = "/fees/list/{page}/{size}")
	public ResponseEntity<MessageResponse> getFeesList(@PathVariable(value = "page") int page,
			@PathVariable(value = "size") int size){
		return hederaTokenService.getFeesList(PageRequest.of(page, size, Direction.ASC, "service"));	
	} 
	
	@PostMapping("/fees/save")
	public ResponseEntity<MessageResponse> saveFees( @RequestBody HederaFeesRequest hederaFeesRequest){
		return hederaTokenService.saveFees(hederaFeesRequest);
	}
	
	@RequestMapping(value = "/token/transfer/simulator", method = RequestMethod.POST)
	public ResponseEntity<?> tokenTransferSimulator(@Valid @RequestBody TransferTokenRequest  transferTokenRequest)  {
		return hederaTokenService.tokenTransferSimulator(transferTokenRequest);
	}
	
	@RequestMapping(value = "/token/transfer/powertransition/mobile", method = RequestMethod.POST)
	public ResponseEntity<?> transferTokenPowertransitionMobile(@Valid @RequestBody TransferTokenRequest  transferTokenRequest)  {
		return hederaTokenService.transferTokenForPowerTransistionMobile(transferTokenRequest);
	}
	
	@PostMapping("/update/token")
	public ResponseEntity<?> updateToken(@RequestBody UpdateTokenRequest updateTokenRequest){
		return hederaTokenService.updateToken(updateTokenRequest);
	}
	
	
	@PostMapping(value = "/associate/kyc/multisign/wallet")
	public ResponseEntity<?> associateTokenForMultisign(@RequestBody List<AssocaiteRequestV2> assocaiteRequestV2){
		return hederaTokenService.associateTokenAndKycV2(assocaiteRequestV2);
	}
	
	@PostMapping(value = "/multisign/token/transfer")
	public ResponseEntity<?> multiWalletTokenTrasfer(@Valid @RequestBody MultiSignTokenTransferRequest  transferTokenRequest){
		return hederaTokenService.multisignTokenTransfer(transferTokenRequest);
	}
	
	
}
