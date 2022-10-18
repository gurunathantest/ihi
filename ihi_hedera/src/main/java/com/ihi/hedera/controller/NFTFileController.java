package com.ihi.hedera.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ihi.hedera.payload.request.CreateFileRequest;
import com.ihi.hedera.payload.request.FileCreationRequest;
import com.ihi.hedera.payload.request.NFTAssociateRequest;
import com.ihi.hedera.payload.request.NFTCreationRequest;
import com.ihi.hedera.payload.request.NFTMintRequest;
import com.ihi.hedera.payload.request.NFTTransferRequest;
import com.ihi.hedera.payload.request.UserNftCreationRequest;
import com.ihi.hedera.service.CreateNFTService;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/nft")
public class NFTFileController {

	@Autowired
	CreateNFTService nftReportService;

	@RequestMapping(value = "/file/create", method = RequestMethod.POST)
	public ResponseEntity<?> createFile(@Valid  @RequestBody FileCreationRequest fileCreationRequest) throws InterruptedException {
		return nftReportService.createFile(fileCreationRequest);
	}
	
	@PostMapping(value = "/v2/file/create")
	public ResponseEntity<?> fileCreation(@Valid  @RequestBody CreateFileRequest createFileRequest) {
		return nftReportService.fileCreate(createFileRequest);
	}
	
	@GetMapping(value = "/v2/file/get/{fileId}")
	public ResponseEntity<?> getFile(@PathVariable String fileId) {
		return nftReportService.getFile(fileId);
	}
	
	@PostMapping(value = "/v2/nft/create")
	public ResponseEntity<?> createNFTToken(@Valid @RequestBody NFTCreationRequest nftCreationRequest){
		return nftReportService.createNFTToken(nftCreationRequest);
	}
	
	@PostMapping(value = "/v2/nft/mint")
	public ResponseEntity<?> mintNFTToken(@Valid @RequestBody NFTMintRequest nftMintRequest){
		return nftReportService.mintNFTToken(nftMintRequest);
	}
	
	@PostMapping(value = "/v2/nft/associate")
	public ResponseEntity<?> associateNFTToken(@Valid @RequestBody NFTAssociateRequest nftAssociateRequest){
		return nftReportService.associateNFTToken(nftAssociateRequest);
	}
	
	@PostMapping(value = "/v2/nft/transfer")
	public ResponseEntity<?> transferNFTToken(@Valid @RequestBody NFTTransferRequest nftTransferRequest){
		return nftReportService.transferNFTToken(nftTransferRequest);
	}
	
	@PostMapping(value = "/v2/nft/user/create")
	public ResponseEntity<?> createUserNft(@Valid @RequestBody UserNftCreationRequest userNftCreationRequest){
		return nftReportService.createUserNft(userNftCreationRequest);
	}
	
	@GetMapping(value = "/v2/nft/get/{nftId}/{serialNumber}")
	public ResponseEntity<?> getNft(@PathVariable String nftId,@PathVariable long serialNumber) {
		return nftReportService.getNft(nftId,serialNumber);
	}
}
