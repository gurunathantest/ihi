package com.ihi.hts.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

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


/*
	 * PTTokenService is a boiler plates in RestAPI which is used to communicating and working together with business logics
	 */
public interface HederaTokenService {

	public ResponseEntity<MessageResponse> balanceHbar( String accountId ,String clientId);

	public ResponseEntity<?> transferToken(TransferTokenRequest transferTokenRequest);

	public ResponseEntity<MessageResponse> associatKycUser(AssociateKycUserRequest associateKycUserRequest);
	
	public ResponseEntity<MessageResponse> createNFTToken(NFTTokenRequest nftTokenRequest);
	
	public ResponseEntity<?> createToken(CreateTokenRequest createTokenRequest);
	
	public ResponseEntity<?> associateTokenAndEnableKyc(List<AssociateTokenAndEnableKycRequest> associateTokenAndEnableKycRequest);
	
	public ResponseEntity<MessageResponse> tokenBalance(TokenBalanceRequest tokenBalanceRequest);
	
	public ResponseEntity<MessageResponse> mintToken(MintTokenRequest mintTokenRequest);

	public ResponseEntity<MessageResponse> accountTokenBalance(String tokenid, String accountId,String accountPrivateKey);
	
	public ResponseEntity<?> currentTotalSupply(String token, String clientId);
	
	public ResponseEntity<MessageResponse> burnToken(BurnTokenRequest burnToken);

	public ResponseEntity<MessageResponse> getFeesList(PageRequest of);

	public ResponseEntity<MessageResponse> saveFees(HederaFeesRequest hederaFeesRequest);
	
	public ResponseEntity<?> tokenTransferSimulator(TransferTokenRequest transferTokenRequest);
	
	public ResponseEntity<?> transferTokenForPowerTransistionMobile(TransferTokenRequest transferTokenRequest);
	
	public ResponseEntity<?> updateToken(UpdateTokenRequest updateTokenRequest );
	
	
	public ResponseEntity<?> associateTokenAndKycV2(List<AssocaiteRequestV2> associateKycUserRequest);
	
	public ResponseEntity<?> multisignTokenTransfer(@Valid MultiSignTokenTransferRequest transferTokenRequest);
}
