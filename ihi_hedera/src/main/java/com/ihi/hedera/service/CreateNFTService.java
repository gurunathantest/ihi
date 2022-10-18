package com.ihi.hedera.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ihi.hedera.payload.request.CreateFileRequest;
import com.ihi.hedera.payload.request.FileCreationRequest;
import com.ihi.hedera.payload.request.NFTAssociateRequest;
import com.ihi.hedera.payload.request.NFTCreationRequest;
import com.ihi.hedera.payload.request.NFTMintRequest;
import com.ihi.hedera.payload.request.NFTTransferRequest;
import com.ihi.hedera.payload.request.UserNftCreationRequest;



@Service
public interface CreateNFTService {

	public ResponseEntity<?> createFile(FileCreationRequest fileCreationRequest)throws InterruptedException;

	public ResponseEntity<?> fileCreate(CreateFileRequest createFileRequest);

	public ResponseEntity<?> getFile(String fileId);

	public ResponseEntity<?> createNFTToken(NFTCreationRequest nftCreationRequest);

	public ResponseEntity<?> mintNFTToken(NFTMintRequest nftMintRequest);

	public ResponseEntity<?> associateNFTToken(NFTAssociateRequest nftAssociateRequest);

	public ResponseEntity<?> transferNFTToken(NFTTransferRequest nftTransferRequest);

	public ResponseEntity<?> createUserNft(UserNftCreationRequest userNftCreationRequest);

	public ResponseEntity<?> getNft(String nftId,long serialNumber);
}
