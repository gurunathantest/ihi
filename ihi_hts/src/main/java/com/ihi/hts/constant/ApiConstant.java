package com.ihi.hts.constant;

public class ApiConstant {

	public static final String HBAR = "/api/hts/hbar/balance";
	public static final String TOKEN_BALANCE = "/api/hts/token/balance";
	public static final String MINT_TOKEN = "/api/hts/mint/token";
	public static final String BURN_TOKEN = "/api/hts/burn/token";
	public static final String CURRENT_TOTAL_SUPPLY = "/api/hts/token/current/totalsupply/{token}/{clientId}";
	public static final String UPDATE_TOKEN = "/api/hts/update/token";
	public static final String CREATE_TOKEN = "/api/hts/create/token";
	public static final String CREATE_NFT_TOKEN = "/api/hts//nft/create";
	public static final String TRANSFER_TOKEN = "/api/hts/token/transfer";
	public static final String ASSOCIATE_KYC = "/api/hts/token/user/associatekyc";
	public static final String ASSOCIATE_KYC_USERS = "/api/hts/token/users/associatekyc";
	public static final String FEES_LIST = "/api/hts/fees/list/{page}/{size}";
	public static final String FEES_SAVE = "/api/hts/fees/save";
	public static final String TOKEN_SIMULATOR = "/api/hts/token/transfer/simulator";
	public static final String TRANSFER_TOKEN_POWER_TRANSTITION = "/api/hts/token/transfer/powertransition/mobile";
	public static final String MULISIGN_TRANSFER = "/api/hts/multisign/transfer";

}
