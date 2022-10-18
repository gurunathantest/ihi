package com.ihi.hts.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import javax.validation.Valid;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.hedera.hashgraph.sdk.AccountBalanceQuery;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrecheckStatusException;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.PublicKey;
import com.hedera.hashgraph.sdk.ReceiptStatusException;
import com.hedera.hashgraph.sdk.Status;
import com.hedera.hashgraph.sdk.TokenAssociateTransaction;
import com.hedera.hashgraph.sdk.TokenBurnTransaction;
import com.hedera.hashgraph.sdk.TokenCreateTransaction;
import com.hedera.hashgraph.sdk.TokenGrantKycTransaction;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TokenInfoQuery;
import com.hedera.hashgraph.sdk.TokenMintTransaction;
import com.hedera.hashgraph.sdk.TokenUpdateTransaction;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.TransactionResponse;
import com.hedera.hashgraph.sdk.Transfer;
import com.hedera.hashgraph.sdk.TransferTransaction;
import com.ihi.hts.constant.ApiConstant;
import com.ihi.hts.constant.HederaHtsInfoConstnat;
import com.ihi.hts.exception.HtsException;
import com.ihi.hts.model.HederaFees;
import com.ihi.hts.model.TokenInfo;
import com.ihi.hts.model.User;
import com.ihi.hts.mongo.model.LogType;
import com.ihi.hts.payload.request.AssocaiteRequestV2;
import com.ihi.hts.payload.request.AssociateKycUserRequest;
import com.ihi.hts.payload.request.AssociateTokenAndEnableKycRequest;
import com.ihi.hts.payload.request.BurnTokenRequest;
import com.ihi.hts.payload.request.CreateTokenRequest;
import com.ihi.hts.payload.request.ExceptionLoggerRequest;
import com.ihi.hts.payload.request.HederaFeesRequest;
import com.ihi.hts.payload.request.MintTokenRequest;
import com.ihi.hts.payload.request.MultiSignTokenTransferRequest;
import com.ihi.hts.payload.request.NFTTokenRequest;
import com.ihi.hts.payload.request.TokenBalanceRequest;
import com.ihi.hts.payload.request.TokenBean;
import com.ihi.hts.payload.request.TransferTokenRequest;
import com.ihi.hts.payload.request.UpdateTokenRequest;
import com.ihi.hts.payload.request.saveHederaHtsInfoRequest;
import com.ihi.hts.payload.response.MessageResponse;
import com.ihi.hts.payload.response.TokenCreateResponse;
import com.ihi.hts.payload.response.TokenInfoResponse;
import com.ihi.hts.payload.response.TokenTransferResponse;
import com.ihi.hts.repository.HederaFeesRepo;
import com.ihi.hts.repository.TokenInfoRepository;
import com.ihi.hts.repository.UserRepository;
import com.ihi.hts.service.ExampleHelper;
import com.ihi.hts.service.HederaTokenService;
import com.ihi.hts.service.TokenHelperService;
import com.ihi.hts.utils.AsynchronousUtils;
import com.ihi.hts.utils.NFTTokenUtil;

@Service(value = "HederaTokenServiceImpl")
public class HederaTokenServiceImpl implements HederaTokenService {

	@Value("${H721_NETWORK}")
	private String network;

	@Autowired
	TokenHelperService tokenHelper;

	Client client = null;

	/*
	 * @Value("${mobile.app.tokenid}") private String tokenId;
	 */

	@Autowired
	MongoDBLoggerService mongoDBLoggerService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TokenInfoRepository tokenInfoRepository;

	@Autowired
	private Environment env;

	@Autowired
	NFTTokenUtil nftTokenUtil;

	@Autowired
	HederaFeesRepo hederaFeesRepo;

	@Autowired
	private AsynchronousUtils asynchronousUtils;

	@Autowired
	private JmsTemplate jmsTemplate;

	@Override
	public ResponseEntity<MessageResponse> balanceHbar(String accountId, String clientId) {
		client = tokenHelper.setOperator();
		try {
			Hbar balance = new AccountBalanceQuery().setAccountId(ExampleHelper.getOperatorId(accountId))
					.execute(client).hbars;

			return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
					.message(env.getProperty("token.balance.success")).response(balance.toString())
					.httpStatus(HttpStatus.OK).build());

		} catch (Exception e) {
			// TRY ACTIVE MQ
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("issue.balance.token") + "," + e.getMessage(),
						ApiConstant.HBAR, accountId, "null", "null", clientId, LogType.ACTIVE_MQ.name());

			} // TRY Async thread
			catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(
						env.getProperty("issue.balance.token") + "," + e.getMessage(), ApiConstant.HBAR, accountId,
						"null", "null", clientId, LogType.ASYNC_THREAD.name());

			}
			throw new HtsException(e.getMessage());
		}
	}

	@Override
	public ResponseEntity<?> transferToken(TransferTokenRequest transferTokenRequest) {
		client = tokenHelper.setOperator();
		TokenId tokenId = TokenId.fromString(transferTokenRequest.getTokenId());
		long amount = transferTokenRequest.getAmount();
		TransferTransaction transaction = new TransferTransaction().setTransactionMemo("transfer token")
				.addTokenTransfer(tokenId, AccountId.fromString(transferTokenRequest.getFromSenderId()), -amount)
				.addTokenTransfer(tokenId, AccountId.fromString(transferTokenRequest.getToAccountId()), amount)
				.freezeWith(client);
		TransactionResponse txResponse = null;

		try {
			// Sign with the client operator key and submit the transaction to a Hedera
			txResponse = transaction.sign(ExampleHelper.getOperatorKey(transferTokenRequest.getFromSenderKey()))
					.execute(client);
		} catch (Exception e) {
			try {

				exceptionMessgeIntoMongoLoggerActiveMQ(
						env.getProperty("issue.in.transaction.response") + "," + e.getMessage(),
						ApiConstant.TRANSFER_TOKEN, transferTokenRequest.toString(), transferTokenRequest.getUserId(),
						"null", transferTokenRequest.getClientId(), LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(
						env.getProperty("issue.in.transaction.response") + "," + e.getMessage(),
						ApiConstant.TRANSFER_TOKEN, transferTokenRequest.toString(), transferTokenRequest.getUserId(),
						"null", transferTokenRequest.getClientId(), LogType.ASYNC_THREAD.name());
			}
			throw new HtsException(e.getMessage());
		}

		// Request the receipt of the transaction
		TransactionReceipt receipt = null;
		try {
			receipt = txResponse.getReceipt(client);
			try {
				saveHederaHtsInfoActiveMQ(HederaHtsInfoConstnat.OTHERS, HederaHtsInfoConstnat.TRANSACTION_GET_RECEIPT,
						transferTokenRequest.getClientId(), transferTokenRequest.getUserId(), LogType.ACTIVE_MQ.name());
			} catch (Exception e) {
				asynchronousUtils.saveHederaHtsInfo(HederaHtsInfoConstnat.OTHERS,
						HederaHtsInfoConstnat.TRANSACTION_GET_RECEIPT, transferTokenRequest.getClientId(),
						transferTokenRequest.getUserId(), LogType.ASYNC_THREAD.name());
			}
		} catch (Exception e) {
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(
						env.getProperty("issue.transaction.receipt") + "," + e.getMessage(), ApiConstant.TRANSFER_TOKEN,
						transferTokenRequest.toString(), transferTokenRequest.getUserId(), receipt.toString(),
						transferTokenRequest.getClientId(), LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(
						env.getProperty("issue.transaction.receipt") + "," + e.getMessage(), "/api/hts/token/transfer",
						transferTokenRequest.toString(), transferTokenRequest.getUserId(), receipt.toString(),
						transferTokenRequest.getClientId(), LogType.ASYNC_THREAD.name());
			}
			throw new HtsException(e.getMessage());
		}

		// Get the transaction consensus status
		Status transactionStatus = receipt.status;
		if (!transactionStatus.equals(Status.SUCCESS)) {
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("issue.in.transaction.response"),
						ApiConstant.TRANSFER_TOKEN, transferTokenRequest.toString(), transferTokenRequest.getUserId(),
						transactionStatus.name().toString(), transferTokenRequest.getClientId(),
						LogType.ACTIVE_MQ.name());
			} catch (JmsException e) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(env.getProperty("issue.in.transaction.response"),
						ApiConstant.TRANSFER_TOKEN, transferTokenRequest.toString(), transferTokenRequest.getUserId(),
						transactionStatus.name().toString(), transferTokenRequest.getClientId(),
						LogType.ASYNC_THREAD.name());
			}
			throw new HtsException("Transfer failed on iHI Service");
		}

		String remainingBalance = "";

		TokenBean tokenBean = TokenBean.builder().operatorId(transferTokenRequest.getFromSenderId())
				.clientId(transferTokenRequest.getClientId()).tokenId(transferTokenRequest.getTokenId()).build();
		try {
			String balance = initialBalanceToken(tokenBean);
			remainingBalance = balance.toString();
		} catch (Exception e) {
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("issue.initiate.balance") + "," + e.getMessage(),
						ApiConstant.TRANSFER_TOKEN, transferTokenRequest.toString(), transferTokenRequest.getUserId(),
						"null", transferTokenRequest.getClientId(), LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(
						env.getProperty("issue.initiate.balance") + "," + e.getMessage(), ApiConstant.TRANSFER_TOKEN,
						transferTokenRequest.toString(), transferTokenRequest.getUserId(), "null",
						transferTokenRequest.getClientId(), LogType.ASYNC_THREAD.name());
			}
			throw new HtsException(e.getMessage());
		}

		return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
				.message(env.getProperty("token.transfer.success")).response(remainingBalance).httpStatus(HttpStatus.OK)
				.build());

	}

	@Override
	public ResponseEntity<MessageResponse> associatKycUser(AssociateKycUserRequest associateKycUserRequest) {
		TokenBean tokenBean = TokenBean.builder().operatorId(associateKycUserRequest.getOperatorId())
				.operatorKey(associateKycUserRequest.getOperatorKey())
				.adminOperatorId(associateKycUserRequest.getAdminOperatorId())
				.adminOperatorKey(associateKycUserRequest.getAdminOperatorKey())
				.tokenId(associateKycUserRequest.getTokenId()).build();
		try {
			associateToken(tokenBean);
			enableKYC(tokenBean);
		} catch (Exception e) {
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("issue.associatKycUser") + "," + e.getMessage(),
						ApiConstant.ASSOCIATE_KYC, associateKycUserRequest.toString(),
						associateKycUserRequest.getUserId(), "null", associateKycUserRequest.getClientId(),
						LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(
						env.getProperty("issue.associatKycUser") + "," + e.getMessage(), ApiConstant.ASSOCIATE_KYC,
						associateKycUserRequest.toString(), associateKycUserRequest.getUserId(), "null",
						associateKycUserRequest.getClientId(), LogType.ASYNC_THREAD.name());
			}
			throw new HtsException(e.getMessage());
		}
		String initailBalanceToken = "";
		TokenBean tokenBean1 = TokenBean.builder().operatorId(associateKycUserRequest.getOperatorId())
				.tokenId(associateKycUserRequest.getTokenId()).clientId(associateKycUserRequest.getClientId()).build();

		try {
			initailBalanceToken = initialBalanceToken(tokenBean1);
		} catch (Exception e) {
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(
						env.getProperty("issue.initial.Balance.Token") + "," + e.getMessage(),
						ApiConstant.ASSOCIATE_KYC, associateKycUserRequest.toString(),
						associateKycUserRequest.getUserId(), "null", associateKycUserRequest.getClientId(),
						LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(
						env.getProperty("issue.initial.Balance.Token") + "," + e.getMessage(),
						ApiConstant.ASSOCIATE_KYC, associateKycUserRequest.toString(),
						associateKycUserRequest.getUserId(), "null", associateKycUserRequest.getClientId(),
						LogType.ASYNC_THREAD.name());
			}
			throw new HtsException(e.getMessage());
		}

		try {

			saveHederaHtsInfoActiveMQ(HederaHtsInfoConstnat.TOKEN, HederaHtsInfoConstnat.TOKEN_ASSOCIATE,
					associateKycUserRequest.getClientId(), associateKycUserRequest.getUserId(),
					LogType.ACTIVE_MQ.name());
		} catch (JmsException jms) {

			asynchronousUtils.saveHederaHtsInfo(HederaHtsInfoConstnat.TOKEN, HederaHtsInfoConstnat.TOKEN_ASSOCIATE,
					associateKycUserRequest.getClientId(), associateKycUserRequest.getUserId(),
					LogType.ASYNC_THREAD.name());

		}
		return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
				.message(env.getProperty("associate.kyc.user.success")).response(initailBalanceToken)
				.httpStatus(HttpStatus.OK).build());
	}

	private String initialBalanceToken(TokenBean tokenBean) throws Exception {
		client = tokenHelper.setOperator();

		try {
			long balanceLong = 0;
			Map tokenBalance = new AccountBalanceQuery()
					.setAccountId(ExampleHelper.getOperatorId(tokenBean.getOperatorId())).execute(client).tokens;

			if (Objects.nonNull(tokenBalance) && !tokenBalance.isEmpty()) {
				balanceLong = (long) tokenBalance.get(TokenId.fromString(tokenBean.getTokenId()));
			}
			return Long.toString(balanceLong);
		} catch (Exception e) {
			try {

				exceptionMessgeIntoMongoLoggerActiveMQ(
						env.getProperty("issue.initial.Balance.Token") + "," + e.getMessage(), "initialBalanceToken",
						tokenBean.toString(), tokenBean.getUserId(), "null", tokenBean.getClientId(),
						LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(
						env.getProperty("issue.initial.Balance.Token") + "," + e.getMessage(), "initialBalanceToken",
						tokenBean.toString(), tokenBean.getUserId(), "null", tokenBean.getClientId(),
						LogType.ASYNC_THREAD.name());
			}
			throw new HtsException(e.getMessage());
		}
	}

	private void enableKYC(TokenBean tokenBean) throws Exception {
		client = tokenHelper.setOperator();
		try {
			Status transactionStatus = new TokenGrantKycTransaction()
					.setAccountId(ExampleHelper.getOperatorId(tokenBean.getOperatorId()))
					.setTokenId(TokenId.fromString(tokenBean.getTokenId())).freezeWith(client)
					.sign(ExampleHelper.getOperatorKey(tokenBean.getAdminOperatorKey())).execute(client)
					.getReceipt(client).status;
			if (transactionStatus.equals(Status.SUCCESS)) {
				try {
					saveHederaHtsInfoActiveMQ(HederaHtsInfoConstnat.TOKEN, HederaHtsInfoConstnat.TOKEN_GRANT_KYC,
							tokenBean.getClientId(), tokenBean.getUserId(), LogType.ACTIVE_MQ.name());
				} catch (JmsException jms) {
					asynchronousUtils.saveHederaHtsInfo(HederaHtsInfoConstnat.TOKEN,
							HederaHtsInfoConstnat.TOKEN_GRANT_KYC, tokenBean.getClientId(), tokenBean.getUserId(),
							LogType.ASYNC_THREAD.name());
				}
			} else {
				try {
					exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("issue.enablekyc"), "enableKYC",
							tokenBean.toString(), tokenBean.getUserId(), "null", tokenBean.getClientId(),
							LogType.ACTIVE_MQ.name());
				} catch (JmsException jms) {
					asynchronousUtils.exceptionMessgeIntoMongoLogger(env.getProperty("issue.enablekyc"), "enableKYC",
							tokenBean.toString(), tokenBean.getUserId(), "null", tokenBean.getClientId(),
							LogType.ASYNC_THREAD.name());
				}
				throw new HtsException(env.getProperty("issue.enablekyc"));
			}
		} catch (Exception e) {
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("issue.enablekyc") + "," + e.getMessage(),
						"enableKYC", tokenBean.toString(), tokenBean.getUserId(), "null", tokenBean.getClientId(),
						LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(
						env.getProperty("issue.enablekyc") + "," + e.getMessage(), "enableKYC", tokenBean.toString(),
						tokenBean.getUserId(), "null", tokenBean.getClientId(), LogType.ASYNC_THREAD.name());
			}
			throw new HtsException(e.getMessage());
		}
	}

	private void associateToken(TokenBean tokenBean) throws Exception {
		client = tokenHelper.setOperator();
		try {

			List<TokenId> tokenList = new ArrayList<>();
			tokenList.add(TokenId.fromString(tokenBean.getTokenId()));

			Status transactionStatus = new TokenAssociateTransaction()
					.setAccountId(ExampleHelper.getOperatorId(tokenBean.getOperatorId()))
					.setMaxTransactionFee(new Hbar(100)).setTokenIds(tokenList).freezeWith(client)
					.sign(ExampleHelper.getOperatorKey(tokenBean.getOperatorKey())).execute(client)
					.getReceipt(client).status;
			if (transactionStatus.equals(Status.SUCCESS)
					|| transactionStatus.equals(Status.TOKEN_ALREADY_ASSOCIATED_TO_ACCOUNT)) {
				try {
					saveHederaHtsInfoActiveMQ(HederaHtsInfoConstnat.TOKEN, HederaHtsInfoConstnat.TOKEN_ASSOCIATE,
							tokenBean.getClientId(), tokenBean.getUserId(), LogType.ACTIVE_MQ.name());
				} catch (JmsException jms) {
					asynchronousUtils.saveHederaHtsInfo(HederaHtsInfoConstnat.TOKEN,
							HederaHtsInfoConstnat.TOKEN_ASSOCIATE, tokenBean.getClientId(), tokenBean.getUserId(),
							LogType.ASYNC_THREAD.name());
				}
			} else {
				try {
					exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("issue.associate.token"), "associateToken",
							tokenBean.toString(), tokenBean.getUserId(), transactionStatus.name(),
							tokenBean.getClientId(), LogType.ACTIVE_MQ.name());
				} catch (JmsException jms) {
					asynchronousUtils.exceptionMessgeIntoMongoLogger(env.getProperty("issue.associate.token"),
							"associateToken", tokenBean.toString(), tokenBean.getUserId(), transactionStatus.name(),
							tokenBean.getClientId(), LogType.ASYNC_THREAD.name());
				}
				throw new HtsException(env.getProperty("issue.associate.token"));
			}
		} catch (Exception e) {
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("issue.associate.token") + "," + e.getMessage(),
						"associateToken", tokenBean.toString(), tokenBean.getUserId(), "null", tokenBean.getClientId(),
						LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(
						env.getProperty("issue.associate.token") + "," + e.getMessage(), "associateToken",
						tokenBean.toString(), tokenBean.getUserId(), "null", tokenBean.getClientId(),
						LogType.ASYNC_THREAD.name());
			}
			throw new HtsException(e.getMessage());
		}
	}

	private Hbar getRemainingBalance(AccountId accountId) throws TimeoutException, PrecheckStatusException {
		Hbar remainingBalance = new AccountBalanceQuery().setAccountId(accountId).execute(client).hbars;
		return remainingBalance;
	}

	@Override
	public ResponseEntity<MessageResponse> createNFTToken(NFTTokenRequest nftTokenRequest) {
		String tokenId = null;

		try {
			TokenId nftTokenId = nftTokenUtil.createToken(nftTokenRequest.getFileId(), nftTokenRequest.getOperatorId(),
					nftTokenRequest.getOperatorKey());
			if (Objects.nonNull(nftTokenId)) {
				tokenId = nftTokenId.toString();
			}
		} catch (Exception e) {
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("issue.nft") + "," + e.getMessage(),
						ApiConstant.CREATE_NFT_TOKEN, nftTokenRequest.toString(), nftTokenRequest.getUserId(), "null",
						nftTokenRequest.getClientId(), LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(env.getProperty("issue.nft") + "," + e.getMessage(),
						ApiConstant.CREATE_NFT_TOKEN, nftTokenRequest.toString(), nftTokenRequest.getUserId(), "null",
						nftTokenRequest.getClientId(), LogType.ASYNC_THREAD.name());
			}
			throw new HtsException(e.getMessage());
		}

		try {
			saveHederaHtsInfoActiveMQ(HederaHtsInfoConstnat.TOKEN, HederaHtsInfoConstnat.TOKEN_CREATE,
					nftTokenRequest.getClientId(), nftTokenRequest.getUserId(), LogType.ACTIVE_MQ.name());
		} catch (JmsException jms) {

			asynchronousUtils.saveHederaHtsInfo(HederaHtsInfoConstnat.TOKEN, HederaHtsInfoConstnat.TOKEN_CREATE,
					nftTokenRequest.getClientId(), nftTokenRequest.getUserId(), LogType.ASYNC_THREAD.name());
		}
		return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
				.message(env.getProperty("nft.token.created.success")).response(tokenId).httpStatus(HttpStatus.OK)
				.build());
	}

	@Override
	public ResponseEntity<?> createToken(CreateTokenRequest createTokenRequest) {

		AccountId adminAccountId = AccountId.fromString(createTokenRequest.getTreasuryAccountId());
		PrivateKey adminPrivateKey = PrivateKey.fromString(createTokenRequest.getTreasuryPrivateKey());
		client = tokenHelper.setOperator();

		TokenCreateTransaction tokenTxn = new TokenCreateTransaction().setTokenName(createTokenRequest.getTokenName())
				.setTokenSymbol(createTokenRequest.getSymbol()).setDecimals(createTokenRequest.getDecimal())
				.setInitialSupply(createTokenRequest.getInitialSupply()).setTreasuryAccountId(adminAccountId)
				.setMaxTransactionFee(new Hbar(100));

		TokenCreateResponse tokenResponse = TokenCreateResponse.builder().build();
		tokenResponse.setTokenName(tokenTxn.getTokenName());
		tokenResponse.setSymbol(tokenTxn.getTokenSymbol());
		tokenResponse.setDecimal(tokenTxn.getDecimals());
		tokenResponse.setInitialSupply(tokenTxn.getInitialSupply());
		tokenResponse.setTreasuryAccountId(String.valueOf(tokenTxn.getTreasuryAccountId()));

		tokenTxn.setAdminKey(ExampleHelper.getOperatorKey(tokenHelper.getOperatorPrivateKey()).getPublicKey()); // iHI
																												// Admin
																												// Key
		tokenResponse.setAdminKey(String.valueOf(tokenTxn.getAdminKey()));

		tokenTxn.setFreezeKey(adminPrivateKey.getPublicKey());
		tokenResponse.setFreezeKey(String.valueOf(tokenTxn.getFreezeKey()));

		tokenTxn.setKycKey(adminPrivateKey.getPublicKey());
		tokenResponse.setKycKey(String.valueOf(tokenTxn.getKycKey()));

		tokenTxn.setSupplyKey(adminPrivateKey.getPublicKey());
		tokenResponse.setSupplyManagerKey(String.valueOf(tokenTxn.getSupplyKey()));

		tokenTxn.setWipeKey(adminPrivateKey.getPublicKey());
		tokenResponse.setWipeKey(String.valueOf(tokenTxn.getWipeKey()));

		tokenTxn.setAutoRenewAccountId(adminAccountId);
		tokenResponse.setTokenRenewalAccount(String.valueOf(tokenTxn.getAutoRenewAccountId()));

		try {
			TransactionResponse txResponse = tokenTxn.freezeWith(client)
					.sign(PrivateKey.fromString(tokenHelper.getOperatorPrivateKey())).sign(adminPrivateKey)
					.execute(client);

			// Request the receipt of the transaction
			TransactionReceipt receipt = txResponse.getReceipt(client);

			// Get the token ID from the receipt
			TokenId tokenId = receipt.tokenId;
			tokenResponse.setTokenId(tokenId.toString());
			// TokenBalance
			Map tokenBalance = new AccountBalanceQuery().setAccountId(adminAccountId).execute(client).tokens;
			long initialTokenBalance = (long) tokenBalance.get(TokenId.fromString(tokenId.toString()));
			tokenResponse.setTokenInitValue(initialTokenBalance);

		} catch (Exception e) {
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("Token.creation.failed") + "," + e.getMessage(),
						ApiConstant.CREATE_TOKEN, createTokenRequest.toString(), createTokenRequest.getUserId(), "null",

						createTokenRequest.getClientId(), LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(
						env.getProperty("Token.creation.failed") + "," + e.getMessage(), ApiConstant.CREATE_TOKEN,
						createTokenRequest.toString(), createTokenRequest.getUserId(), "null",
						createTokenRequest.getClientId(), LogType.ASYNC_THREAD.name());
			}
			throw new HtsException(e.getMessage());
		}

		Optional<User> userInfo = userRepository.findByUsername(createTokenRequest.getClientId());
		if (userInfo.isPresent()) {
			tokenInfoRepository
					.save(TokenInfo.builder().user(userInfo.get()).tokenId(tokenResponse.getTokenId()).build());
			TokenInfoResponse tokenInfoResponse = TokenInfoResponse.builder().userId(userInfo.get().getEmail())
					.tokenId(tokenResponse.getTokenId()).createdTime(DateTime.now().toString()).build();
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("token.deatils"), ApiConstant.CREATE_TOKEN,
						tokenInfoResponse.toString(), createTokenRequest.getUserId(), "null",
						createTokenRequest.getClientId(), LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(env.getProperty("token.deatils"),
						ApiConstant.CREATE_TOKEN, tokenInfoResponse.toString(), createTokenRequest.getUserId(), "null",
						createTokenRequest.getClientId(), LogType.ASYNC_THREAD.name());
			}
		}

		try {
			saveHederaHtsInfoActiveMQ(HederaHtsInfoConstnat.TOKEN, HederaHtsInfoConstnat.TOKEN__CREATE_CUSTOM,
					createTokenRequest.getClientId(), createTokenRequest.getUserId(), LogType.ACTIVE_MQ.name());

		} catch (JmsException jms) {
			asynchronousUtils.saveHederaHtsInfo(HederaHtsInfoConstnat.TOKEN, HederaHtsInfoConstnat.TOKEN__CREATE_CUSTOM,
					createTokenRequest.getClientId(), createTokenRequest.getUserId(), LogType.ASYNC_THREAD.name());
		}
		return ResponseEntity.ok(
				MessageResponse.builder().status(HttpStatus.OK.value()).message(env.getProperty("token.create.success"))
						.response(tokenResponse).httpStatus(HttpStatus.OK).build());
	}

	@Override
	public ResponseEntity<?> associateTokenAndEnableKyc(
			List<AssociateTokenAndEnableKycRequest> associateTokenAndEnableKycRequest) {
		associateTokenAndEnableKycRequest.stream().forEach(securityList -> {
			TokenBean tBeans = new TokenBean();
			tBeans.setOperatorId(securityList.getOperatorId());
			tBeans.setOperatorKey(securityList.getOperatorKey());
			tBeans.setAdminOperatorId(securityList.getAdminOperatorId());
			tBeans.setAdminOperatorKey(securityList.getAdminOperatorKey());
			tBeans.setTokenId(securityList.getTokenId());
			try {
				associateToken(tBeans);
			} catch (Exception e) {
				try {
					exceptionMessgeIntoMongoLoggerActiveMQ(
							env.getProperty("issue.associate.token") + "," + e.getMessage(),
							ApiConstant.ASSOCIATE_KYC_USERS, securityList + "", securityList.getUserId(), "null",
							securityList.getClientId(), LogType.ACTIVE_MQ.name());
				} catch (JmsException jms) {
					asynchronousUtils.exceptionMessgeIntoMongoLogger(
							env.getProperty("issue.associate.token") + "," + e.getMessage(),
							ApiConstant.ASSOCIATE_KYC_USERS, securityList + "", securityList.getUserId(), "null",
							securityList.getClientId(), LogType.ASYNC_THREAD.name());
				}
				throw new HtsException(e.getMessage());
			}
			try {
				enableKYC(tBeans);
			} catch (Exception e) {
				try {
					exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("issue.enablekyc") + "," + e.getMessage(),
							ApiConstant.ASSOCIATE_KYC_USERS, securityList + "", securityList.getUserId(), "null",
							securityList.getClientId(), LogType.ACTIVE_MQ.name());

				} catch (JmsException jms) {
					asynchronousUtils.exceptionMessgeIntoMongoLogger(
							env.getProperty("issue.enablekyc") + "," + e.getMessage(), ApiConstant.ASSOCIATE_KYC_USERS,
							securityList + "", securityList.getUserId(), "null", securityList.getClientId(),
							LogType.ASYNC_THREAD.name());
				}
				throw new HtsException(e.getMessage());
			}
		});

		return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
				.message("Associated token and enable kyc successfully").httpStatus(HttpStatus.OK).build());
	}

	@Override
	public ResponseEntity<MessageResponse> tokenBalance(TokenBalanceRequest tokenBalanceRequest) {

		TokenId myTokenId = TokenId.fromString(tokenBalanceRequest.getTokenId());
		long balance;
		try {
			client = tokenHelper.setOperator();

			AccountId myAccId = AccountId.fromString(tokenBalanceRequest.getOperatorId());
			AccountBalanceQuery queryBal = new AccountBalanceQuery().setAccountId(myAccId);
			Map tokenBalance = queryBal.execute(client).tokens;
			balance = (long) tokenBalance.get(myTokenId);

		} catch (Exception e) {
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("account.balance.failed") + "," + e.getMessage(),
						ApiConstant.TOKEN_BALANCE, tokenBalanceRequest.toString(), tokenBalanceRequest.getUserId(),
						"null", tokenBalanceRequest.getClientId(), LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(
						env.getProperty("account.balance.failed") + "," + e.getMessage(), ApiConstant.TOKEN_BALANCE,
						tokenBalanceRequest.toString(), tokenBalanceRequest.getUserId(), "null",
						tokenBalanceRequest.getClientId(), LogType.ASYNC_THREAD.name());
			}
			throw new HtsException(env.getProperty("account.balance.failed") + "," + e.getMessage());
		}

		return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
				.message(env.getProperty("account.balance.success")).response(balance).httpStatus(HttpStatus.OK)
				.build());

	}

	@Override
	public ResponseEntity<MessageResponse> mintToken(MintTokenRequest mintTokenRequest) {
		client = tokenHelper.setOperator();
		PrivateKey adminPrivateKey = PrivateKey.fromString(mintTokenRequest.getTokenOwnerPrivateKey());

		TokenMintTransaction minitransaction = new TokenMintTransaction()
				.setTokenId(TokenId.fromString(mintTokenRequest.getTokenId())).setAmount(mintTokenRequest.getAmount());
		try {
			TransactionResponse txResponse = minitransaction.freezeWith(client).sign(adminPrivateKey).execute(client);
			TransactionReceipt receipt = txResponse.getReceipt(client);
			Status transactionStatus = receipt.status;
			if (transactionStatus.equals(Status.SUCCESS)) {
				try {
					saveHederaHtsInfoActiveMQ(HederaHtsInfoConstnat.TOKEN, HederaHtsInfoConstnat.TOKEN_MINT,
							mintTokenRequest.getClientId(), mintTokenRequest.getUserId(), LogType.ACTIVE_MQ.name());
				} catch (JmsException jms) {
					asynchronousUtils.saveHederaHtsInfo(HederaHtsInfoConstnat.TOKEN, HederaHtsInfoConstnat.TOKEN_MINT,
							mintTokenRequest.getClientId(), mintTokenRequest.getUserId(), LogType.ASYNC_THREAD.name());
				}
				return currentTotalSupply(mintTokenRequest.getTokenId(), mintTokenRequest.getClientId());
			} else {
				try {

					exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("issue.mint.token"), ApiConstant.MINT_TOKEN,
							mintTokenRequest.toString(), mintTokenRequest.getUserId(), transactionStatus.name(),
							mintTokenRequest.getClientId(), LogType.ACTIVE_MQ.name());
				} catch (JmsException jms) {

					asynchronousUtils.exceptionMessgeIntoMongoLogger(env.getProperty("issue.mint.token"),
							ApiConstant.MINT_TOKEN, mintTokenRequest.toString(), mintTokenRequest.getUserId(),
							transactionStatus.name(), mintTokenRequest.getClientId(), LogType.ASYNC_THREAD.name());

				}
				throw new HtsException("Mint token failed! hedera status : " + transactionStatus);
			}
		} catch (Exception e) {
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("failed") + "," + e.getMessage(),
						ApiConstant.MINT_TOKEN, mintTokenRequest.toString(), mintTokenRequest.getUserId(), "null",
						mintTokenRequest.getClientId(), LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(env.getProperty("failed") + "," + e.getMessage(),
						ApiConstant.MINT_TOKEN, mintTokenRequest.toString(), mintTokenRequest.getUserId(), "null",
						mintTokenRequest.getClientId(), LogType.ASYNC_THREAD.name());
			}
			throw new HtsException(e.getMessage());
		}
	}

	@Override
	public ResponseEntity<MessageResponse> accountTokenBalance(String tokenid, String accid, String adminPrivateKey) {
		TokenId tokenId = TokenId.fromString(tokenid);
		try {
			client = tokenHelper.setClientAdminOperator(accid, adminPrivateKey);
			AccountId accountId = AccountId.fromString(accid);
			AccountBalanceQuery querybal = new AccountBalanceQuery().setAccountId(accountId);
			Map tokenBalance = querybal.execute(client).tokens;
			return ResponseEntity
					.ok(MessageResponse.builder().status(HttpStatus.OK.value()).message(env.getProperty("success"))
							.response(tokenBalance.get(tokenId)).httpStatus(HttpStatus.OK).build());
		} catch (Exception e) {

			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("failed") + "," + e.getMessage(),
						ApiConstant.TOKEN_BALANCE, tokenid + "," + accid, "null", "null", "null",
						LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(env.getProperty("failed") + "," + e.getMessage(),
						ApiConstant.TOKEN_BALANCE, tokenid + "," + accid, "null", "null", "null",
						LogType.ASYNC_THREAD.name());
			}
			throw new HtsException(e.getMessage());
		}

	}

	@Override
	public ResponseEntity<MessageResponse> currentTotalSupply(String token, String clientId) {
		client = tokenHelper.setOperator();
		TokenId tokenId = TokenId.fromString(token);
		// Create the query
		TokenInfoQuery query = new TokenInfoQuery().setTokenId(tokenId);
		long tokenSupply;
		try {
			// Sign with the client operator private key, submit the query to the network
			// and get the token supply
			tokenSupply = query.execute(client).totalSupply;
		} catch (Exception e) {
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("current.total.failed") + "," + e.getMessage(),
						ApiConstant.CURRENT_TOTAL_SUPPLY, token, "null", "null", clientId, LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(
						env.getProperty("current.total.failed") + "," + e.getMessage(),
						ApiConstant.CURRENT_TOTAL_SUPPLY, token, "null", "null", clientId, LogType.ASYNC_THREAD.name());
			}
			throw new HtsException(e.getMessage());
		}
		return ResponseEntity.ok(
				MessageResponse.builder().status(HttpStatus.OK.value()).message(env.getProperty("current.total.supply"))
						.response(tokenSupply).httpStatus(HttpStatus.OK).build());
	}

	@Override
	public ResponseEntity<MessageResponse> burnToken(BurnTokenRequest burnToken) {
		client = tokenHelper.setOperator();
		PrivateKey adminPrivateKey = PrivateKey.fromString(burnToken.getTokenOwnerPrivateKey());
		TokenBurnTransaction burnTransaction = new TokenBurnTransaction()
				.setTokenId(TokenId.fromString(burnToken.getTokenId())).setAmount(burnToken.getAmount());
		try {
			TransactionResponse transactionResponse = burnTransaction.freezeWith(client).sign(adminPrivateKey)
					.execute(client);
			TransactionReceipt receipt = transactionResponse.getReceipt(client);
			Status transactionStatus = receipt.status;
			if (transactionStatus.equals(Status.SUCCESS)) {
				try {

					saveHederaHtsInfoActiveMQ(HederaHtsInfoConstnat.TOKEN, HederaHtsInfoConstnat.TOKEN_BURN,
							burnToken.getClientId(), burnToken.getUserId(), LogType.ACTIVE_MQ.name());
				} catch (JmsException jms) {
					asynchronousUtils.saveHederaHtsInfo(HederaHtsInfoConstnat.TOKEN, HederaHtsInfoConstnat.TOKEN_BURN,
							burnToken.getClientId(), burnToken.getUserId(), LogType.ASYNC_THREAD.name());
				}

				return currentTotalSupply(burnToken.getTokenId(), burnToken.getClientId());
			} else {
				try {
					exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("issue.burn.token"), ApiConstant.BURN_TOKEN,
							burnToken.toString(), burnToken.getUserId(), receipt.toString(), burnToken.getClientId(),
							LogType.ACTIVE_MQ.name());
				} catch (JmsException jms) {
					asynchronousUtils.exceptionMessgeIntoMongoLogger(env.getProperty("issue.burn.token"),
							ApiConstant.BURN_TOKEN, burnToken.toString(), burnToken.getUserId(), receipt.toString(),
							burnToken.getClientId(), LogType.ASYNC_THREAD.name());
				}
				throw new HtsException("Burn token failed! hedera status : " + transactionStatus);
			}

		} catch (Exception e) {
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("failed") + "," + e.getMessage(),
						ApiConstant.BURN_TOKEN, burnToken.toString(), burnToken.getUserId(), "null",
						burnToken.getClientId(), LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(env.getProperty("failed") + "," + e.getMessage(),
						ApiConstant.BURN_TOKEN, burnToken.toString(), burnToken.getUserId(), "null",
						burnToken.getClientId(), LogType.ASYNC_THREAD.name());
			}
			throw new HtsException(e.getMessage());
		}
	}

	private Client setOperator() {
		Client client = null;
		String operatorId = env.getProperty("H721_OPERATOR_ID");
		String operatorKey = env.getProperty("H721_OEPRATOR_KEY");
		if (network.equalsIgnoreCase("testnet")) {
			client = Client.forTestnet();
			client.setOperator(ExampleHelper.getOperatorId(operatorId), ExampleHelper.getOperatorKey(operatorKey));

		} else if (network.equalsIgnoreCase("mainnet")) {
			client = Client.forMainnet();
			client.setOperator(ExampleHelper.getOperatorId(operatorId), ExampleHelper.getOperatorKey(operatorKey));

		}
		return client;
	}

	public ResponseEntity<MessageResponse> getFeesList(PageRequest pageable) {
		Page<HederaFees> result = hederaFeesRepo.findAll(pageable);

		if (result.isEmpty()) {
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("no.record.found"), ApiConstant.FEES_LIST,
						"null", "null", "null", "null", LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(env.getProperty("no.record.found"),
						ApiConstant.FEES_LIST, "null", "null", "null", "null", LogType.ASYNC_THREAD.name());
			}
			return ResponseEntity
					.ok(new MessageResponse(HttpStatus.OK.value(), env.getProperty("no.record.found"), result));
		}

		return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
				.message(env.getProperty("list.fees.info.success")).response(result).httpStatus(HttpStatus.OK).build());
	}

	public ResponseEntity<MessageResponse> saveFees(HederaFeesRequest hederaFeesRequest) {
		HederaFees hederaFees = null;
		try {
			hederaFees = HederaFees.builder().operations(hederaFeesRequest.getOperations())
					.service(hederaFeesRequest.getService()).updateDate(new DateTime())
					.priceInUsd(hederaFeesRequest.getPriceInUsd()).build();
			if (hederaFeesRequest.getId() != null) {
				hederaFees.setId(hederaFeesRequest.getId());
			}
			hederaFees = hederaFeesRepo.save(hederaFees);

			if (Objects.isNull(hederaFees)) {
				try {
					exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("fees.save.failed"), ApiConstant.FEES_SAVE,
							hederaFeesRequest.toString(), hederaFeesRequest.getUserId(), "null",
							hederaFeesRequest.getClientId(), LogType.ACTIVE_MQ.name());
				} catch (JmsException jms) {
					asynchronousUtils.exceptionMessgeIntoMongoLogger(env.getProperty("fees.save.failed"),
							ApiConstant.FEES_SAVE, hederaFeesRequest.toString(), hederaFeesRequest.getUserId(), "null",
							hederaFeesRequest.getClientId(), LogType.ASYNC_THREAD.name());
				}
				return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
						.message(env.getProperty("fees.save.failed")).response(hederaFees).httpStatus(HttpStatus.OK)
						.build());
			}
		} catch (Exception e) {
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("fees.save.failed") + "," + e.getMessage(),
						ApiConstant.FEES_SAVE, hederaFeesRequest.toString(), hederaFeesRequest.getUserId(), "null",
						hederaFeesRequest.getClientId(), LogType.ACTIVE_MQ.name());

			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(
						env.getProperty("fees.save.failed") + "," + e.getMessage(), ApiConstant.FEES_SAVE,
						hederaFeesRequest.toString(), hederaFeesRequest.getUserId(), "null",
						hederaFeesRequest.getClientId(), LogType.ASYNC_THREAD.name());
			}
			return ResponseEntity.ok(
					MessageResponse.builder().status(HttpStatus.OK.value()).message(env.getProperty("fees.save.failed"))
							.response(hederaFees).httpStatus(HttpStatus.OK).build());
		}

		return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
				.message(env.getProperty("fees.save.success")).response(hederaFees).httpStatus(HttpStatus.OK).build());
	}

	@Override
	public ResponseEntity<?> tokenTransferSimulator(TransferTokenRequest transferTokenRequest) {
		client = tokenHelper.setOperator();
		TokenId tokenId = TokenId.fromString(transferTokenRequest.getTokenId());
		long amount = transferTokenRequest.getAmount();
		TransferTransaction transaction = new TransferTransaction().setTransactionMemo("transfer token")
				.addTokenTransfer(tokenId, AccountId.fromString(transferTokenRequest.getFromSenderId()), -amount)
				.addTokenTransfer(tokenId, AccountId.fromString(transferTokenRequest.getToAccountId()), amount)
				.freezeWith(client);
		TransactionResponse txResponse = null;
		List<Transfer> transactionChargeList;
		try {
			// Sign with the client operator key and submit the transaction to a Hedera
			txResponse = transaction.sign(ExampleHelper.getOperatorKey(transferTokenRequest.getFromSenderKey()))
					.execute(client);
			transactionChargeList = txResponse.getRecord(client).transfers;
		} catch (Exception e) {
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(
						env.getProperty("issue.in.transaction.response") + "," + e.getMessage(),
						ApiConstant.TOKEN_SIMULATOR, transferTokenRequest.toString(), transferTokenRequest.getUserId(),
						"null", transferTokenRequest.getClientId(), LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(
						env.getProperty("issue.in.transaction.response") + "," + e.getMessage(),
						ApiConstant.TOKEN_SIMULATOR, transferTokenRequest.toString(), transferTokenRequest.getUserId(),
						"null", transferTokenRequest.getClientId(), LogType.ASYNC_THREAD.name());
			}
			throw new HtsException(e.getMessage());
		}

		// Request the receipt of the transaction
		TransactionReceipt receipt = null;
		try {
			receipt = txResponse.getReceipt(client);
		} catch (Exception e) {
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(
						env.getProperty("issue.transaction.receipt") + "," + e.getMessage(),
						ApiConstant.TOKEN_SIMULATOR, transferTokenRequest.toString(), transferTokenRequest.getUserId(),
						receipt.toString(), transferTokenRequest.getClientId(), LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(
						env.getProperty("issue.transaction.receipt") + "," + e.getMessage(),
						ApiConstant.TOKEN_SIMULATOR, transferTokenRequest.toString(), transferTokenRequest.getUserId(),
						receipt.toString(), transferTokenRequest.getClientId(), LogType.ASYNC_THREAD.name());
			}
			throw new HtsException(e.getMessage());
		}

		// Get the transaction consensus status
		Status transactionStatus = receipt.status;
		if (!transactionStatus.equals(Status.SUCCESS)) {
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("issue.in.transaction.response"),
						ApiConstant.TOKEN_SIMULATOR, transferTokenRequest.toString(), transferTokenRequest.getUserId(),
						receipt.status.toString(), transferTokenRequest.getClientId(), LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(env.getProperty("issue.in.transaction.response"),
						ApiConstant.TOKEN_SIMULATOR, transferTokenRequest.toString(), transferTokenRequest.getUserId(),
						receipt.status.toString(), transferTokenRequest.getClientId(), LogType.ASYNC_THREAD.name());
			}
			throw new HtsException("Transfer failed on iHI Service");
		}

		String remainingBalance = "";

		TokenBean tokenBean = TokenBean.builder().operatorId(transferTokenRequest.getFromSenderId())
				.clientId(transferTokenRequest.getClientId()).tokenId(transferTokenRequest.getTokenId()).build();

		try {
			String balance = initialBalanceToken(tokenBean);
			remainingBalance = balance.toString();
		} catch (Exception e) {
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("issue.initiate.balance") + "," + e.getMessage(),
						ApiConstant.TOKEN_SIMULATOR, transferTokenRequest.toString(), transferTokenRequest.getUserId(),
						receipt.status.toString(), transferTokenRequest.getClientId(), LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(
						env.getProperty("issue.initiate.balance") + "," + e.getMessage(), ApiConstant.TOKEN_SIMULATOR,
						transferTokenRequest.toString(), transferTokenRequest.getUserId(), receipt.status.toString(),
						transferTokenRequest.getClientId(), LogType.ASYNC_THREAD.name());
			}
			throw new HtsException(e.getMessage());
		}

		return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
				.message(env.getProperty("token.transfer.success")).response(transactionChargeList)
				.httpStatus(HttpStatus.OK).build());

	}

	@Override
	public ResponseEntity<?> transferTokenForPowerTransistionMobile(TransferTokenRequest transferTokenRequest) {
		client = tokenHelper.setOperator();
		TokenId tokenId = TokenId.fromString(transferTokenRequest.getTokenId());
		long amount = transferTokenRequest.getAmount();
		TransferTransaction transaction = new TransferTransaction().setTransactionMemo("transfer token")
				.addTokenTransfer(tokenId, AccountId.fromString(transferTokenRequest.getFromSenderId()), -amount)
				.addTokenTransfer(tokenId, AccountId.fromString(transferTokenRequest.getToAccountId()), amount)
				.freezeWith(client);
		TransactionResponse txResponse = null;
		List<Transfer> transactionChargeList;
		try {
			// Sign with the client operator key and submit the transaction to a Hedera
			txResponse = transaction.sign(ExampleHelper.getOperatorKey(transferTokenRequest.getFromSenderKey()))
					.execute(client);
			transactionChargeList = txResponse.getRecord(client).transfers;
		} catch (Exception e) {
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(
						env.getProperty("issue.in.transaction.response") + "," + e.getMessage(),
						ApiConstant.TRANSFER_TOKEN, transferTokenRequest.toString(), transferTokenRequest.getUserId(),
						"null", transferTokenRequest.getClientId(), LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(
						env.getProperty("issue.in.transaction.response") + "," + e.getMessage(),
						ApiConstant.TRANSFER_TOKEN, transferTokenRequest.toString(), transferTokenRequest.getUserId(),
						"null", transferTokenRequest.getClientId(), LogType.ASYNC_THREAD.name());
			}
			throw new HtsException(e.getMessage());
		}

		// Request the receipt of the transaction
		TransactionReceipt receipt = null;
		try {
			receipt = txResponse.getReceipt(client);
			try {
				saveHederaHtsInfoActiveMQ(HederaHtsInfoConstnat.OTHERS, HederaHtsInfoConstnat.TRANSACTION_GET_RECEIPT,
						transferTokenRequest.getClientId(), transferTokenRequest.getUserId(), LogType.ACTIVE_MQ.name());
			} catch (JmsException e) {
				asynchronousUtils.saveHederaHtsInfo(HederaHtsInfoConstnat.OTHERS,
						HederaHtsInfoConstnat.TRANSACTION_GET_RECEIPT, transferTokenRequest.getClientId(),
						transferTokenRequest.getUserId(), LogType.ASYNC_THREAD.name());
			}
		} catch (Exception e) {
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(
						env.getProperty("issue.transaction.receipt") + "," + e.getMessage(), ApiConstant.TRANSFER_TOKEN,
						transferTokenRequest.toString(), transferTokenRequest.getUserId(), receipt.toString(),
						transferTokenRequest.getClientId(), LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(
						env.getProperty("issue.transaction.receipt") + "," + e.getMessage(), ApiConstant.TRANSFER_TOKEN,
						transferTokenRequest.toString(), transferTokenRequest.getUserId(), receipt.toString(),
						transferTokenRequest.getClientId(), LogType.ASYNC_THREAD.name());
			}
			throw new HtsException(e.getMessage());
		}

		// Get the transaction consensus status
		Status transactionStatus = receipt.status;
		if (!transactionStatus.equals(Status.SUCCESS)) {
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("issue.in.transaction.response"),
						ApiConstant.TRANSFER_TOKEN, transferTokenRequest.toString(), transferTokenRequest.getUserId(),
						"null", transferTokenRequest.getClientId(), LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(env.getProperty("issue.in.transaction.response"),
						ApiConstant.TRANSFER_TOKEN, transferTokenRequest.toString(), transferTokenRequest.getUserId(),
						"null", transferTokenRequest.getClientId(), LogType.ASYNC_THREAD.name());
			}
			throw new HtsException("Transfer failed on iHI Service");
		}

		String remainingBalance = "";

		TokenBean tokenBean = TokenBean.builder().operatorId(transferTokenRequest.getFromSenderId())
				.clientId(transferTokenRequest.getClientId()).tokenId(transferTokenRequest.getTokenId()).build();
		try {
			String balance = initialBalanceToken(tokenBean);
			remainingBalance = balance.toString();
		} catch (Exception e) {
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("issue.initiate.balance") + "," + e.getMessage(),
						ApiConstant.TRANSFER_TOKEN, transferTokenRequest.toString(), transferTokenRequest.getUserId(),
						"null", transferTokenRequest.getClientId(), LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(
						env.getProperty("issue.initiate.balance") + "," + e.getMessage(), ApiConstant.TRANSFER_TOKEN,
						transferTokenRequest.toString(), transferTokenRequest.getUserId(), "null",
						transferTokenRequest.getClientId(), LogType.ASYNC_THREAD.name());
			}
			throw new HtsException(e.getMessage());
		}

		TokenTransferResponse tokenTransferResponse = TokenTransferResponse.builder()
				.netWorkFee(transactionChargeList.get(1).amount.getValue())
				.nodeFee(transactionChargeList.get(0).amount.getValue())
				.transactionFee(transactionChargeList.get(2).amount.getValue())
				.balanceRemain(Double.parseDouble(remainingBalance)).build();

		return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
				.message(env.getProperty("token.transfer.success")).response(tokenTransferResponse)
				.httpStatus(HttpStatus.OK).build());

	}

	@Override
	public ResponseEntity<?> updateToken(UpdateTokenRequest updateTokenRequest) {
		client = tokenHelper.setOperator();

		TokenUpdateTransaction transaction = new TokenUpdateTransaction()
				.setTokenId(new TokenId(updateTokenRequest.getShard(), updateTokenRequest.getRealm(),
						updateTokenRequest.getNum()))
				.setTokenName(updateTokenRequest.getTokenName()).setTokenSymbol(updateTokenRequest.getSymbol());

		// Freeze the unsigned transaction, sign with the admin private key of the
		// token, submit the transaction to a Hedera network
		TransactionResponse txResponse = null;
		try {
			txResponse = transaction.freezeWith(client)
					.sign(PrivateKey.fromString(updateTokenRequest.getAdminPrivateKey())).execute(client);
		} catch (Exception e) {
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(
						env.getProperty("issue.in.transaction.response") + "," + e.getMessage(),
						ApiConstant.UPDATE_TOKEN, updateTokenRequest.toString(), updateTokenRequest.getUserId(), "null",
						updateTokenRequest.getClientId(), LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(
						env.getProperty("issue.in.transaction.response") + "," + e.getMessage(),
						ApiConstant.UPDATE_TOKEN, updateTokenRequest.toString(), updateTokenRequest.getUserId(), "null",
						updateTokenRequest.getClientId(), LogType.ASYNC_THREAD.name());
			}
			throw new HtsException(e.getMessage());
		}

		// Request the receipt of the transaction
		TransactionReceipt receipt = null;
		try {
			receipt = txResponse.getReceipt(client);
			try {
				saveHederaHtsInfoActiveMQ(HederaHtsInfoConstnat.OTHERS, HederaHtsInfoConstnat.TRANSACTION_GET_RECEIPT,
						updateTokenRequest.getClientId(), updateTokenRequest.getUserId(), LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.saveHederaHtsInfo(HederaHtsInfoConstnat.OTHERS,
						HederaHtsInfoConstnat.TRANSACTION_GET_RECEIPT, updateTokenRequest.getClientId(),
						updateTokenRequest.getUserId(), LogType.ASYNC_THREAD.name());
			}
		} catch (Exception e) {
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(
						env.getProperty("issue.transaction.receipt") + "," + e.getMessage(), ApiConstant.UPDATE_TOKEN,
						updateTokenRequest.toString(), updateTokenRequest.getUserId(), "null",
						updateTokenRequest.getClientId(), LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(
						env.getProperty("issue.transaction.receipt") + "," + e.getMessage(), ApiConstant.UPDATE_TOKEN,
						updateTokenRequest.toString(), updateTokenRequest.getUserId(), "null",
						updateTokenRequest.getClientId(), LogType.ASYNC_THREAD.name());
			}
			throw new HtsException(e.getMessage());
		}

		// Get the transaction consensus status
		Status transactionStatus = receipt.status;
		if (!transactionStatus.equals(Status.SUCCESS)) {
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("issue.in.transaction.response"),
						ApiConstant.UPDATE_TOKEN, updateTokenRequest.toString(), updateTokenRequest.getUserId(),
						receipt.toString(), updateTokenRequest.getClientId(), LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(env.getProperty("issue.in.transaction.response"),
						ApiConstant.UPDATE_TOKEN, updateTokenRequest.toString(), updateTokenRequest.getUserId(),
						receipt.toString(), updateTokenRequest.getClientId(), LogType.ASYNC_THREAD.name());
			}
			throw new HtsException("Transaction status failed on iHI Service");
		}
		return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
				.message(transactionStatus.name()).httpStatus(HttpStatus.OK).build());
	}

	public void exceptionMessgeIntoMongoLoggerActiveMQ(String message, String api, String payload, String userId,
			String clientId, String transactionReceipt, String logType) throws JmsException {
		ExceptionLoggerRequest loggerRequest = ExceptionLoggerRequest.builder().message(message).api(api).userId(userId)
				.payload(payload).clientId(clientId).transactionReceipt(transactionReceipt).logType(logType).build();
		jmsTemplate.convertAndSend("hts_exception_message_mongodblogger", loggerRequest);
	}

	public void saveHederaHtsInfoActiveMQ(String service, String operation, String clientId, String userId,
			String logType) throws JmsException {
		saveHederaHtsInfoRequest infoRequest = saveHederaHtsInfoRequest.builder().clientId(clientId)
				.operation(operation).service(service).userId(userId).logType(logType).build();
		jmsTemplate.convertAndSend("hts_saveHederaHtsInfo", infoRequest);
	}

	
	@Override
	public ResponseEntity<?> associateTokenAndKycV2(List<AssocaiteRequestV2> associateKycUserRequest1) {
		client = tokenHelper.setOperator();
		associateKycUserRequest1.stream().forEachOrdered(associateKycUserRequest -> {
			try {
				List<TokenId> tokenList = new ArrayList<>();
				tokenList.add(TokenId.fromString(associateKycUserRequest.getTokenId()));
				List<AccountId> nodeId = Collections
						.singletonList(new AccountId(associateKycUserRequest.getResponseWallet().size()));

				TokenAssociateTransaction transaction = new TokenAssociateTransaction()
						.setAccountId(ExampleHelper.getOperatorId(associateKycUserRequest.getOperatorId()))
						.setNodeAccountIds(nodeId).setTokenIds(tokenList).freezeWith(client);

				associateKycUserRequest.getResponseWallet().forEach(privateKeys -> {
					byte[] signature4 = PrivateKey.fromString(privateKeys.getPrivateKey()).signTransaction(transaction);
				});

				TransactionResponse TransactionResponse = transaction.execute(client);

				TransactionReceipt receipt = TransactionResponse.getReceipt(client);

				Status transactionStatus = receipt.status;
				if (transactionStatus.equals(Status.SUCCESS)
						|| transactionStatus.equals(Status.TOKEN_ALREADY_ASSOCIATED_TO_ACCOUNT)) {
					try {
						saveHederaHtsInfoActiveMQ(HederaHtsInfoConstnat.TOKEN, HederaHtsInfoConstnat.TOKEN_ASSOCIATE,
								associateKycUserRequest.getClientId(), associateKycUserRequest.getUserId(),
								LogType.ACTIVE_MQ.name());
					} catch (JmsException jms) {
						asynchronousUtils.saveHederaHtsInfo(HederaHtsInfoConstnat.TOKEN,
								HederaHtsInfoConstnat.TOKEN_ASSOCIATE, associateKycUserRequest.getClientId(),
								associateKycUserRequest.getUserId(), LogType.ASYNC_THREAD.name());
					}
				} else {
					try {
						exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("issue.associate.token"),
								"associateToken", associateKycUserRequest.toString(),
								associateKycUserRequest.getUserId(), transactionStatus.name(),
								associateKycUserRequest.getClientId(), LogType.ACTIVE_MQ.name());
					} catch (JmsException jms) {
						asynchronousUtils.exceptionMessgeIntoMongoLogger(env.getProperty("issue.associate.token"),
								"associateToken", associateKycUserRequest.toString(),
								associateKycUserRequest.getUserId(), transactionStatus.name(),
								associateKycUserRequest.getClientId(), LogType.ASYNC_THREAD.name());
					}
					throw new HtsException(env.getProperty("issue.associate.token"));
				}

				System.out.println(transactionStatus);

			} catch (Exception e) {
				try {
					exceptionMessgeIntoMongoLoggerActiveMQ(
							env.getProperty("issue.associate.token") + "," + e.getMessage(), "associateToken",
							associateKycUserRequest.toString(), associateKycUserRequest.getUserId(), "null",
							associateKycUserRequest.getClientId(), LogType.ACTIVE_MQ.name());
				} catch (JmsException jms) {
					asynchronousUtils.exceptionMessgeIntoMongoLogger(
							env.getProperty("issue.associate.token") + "," + e.getMessage(), "associateToken",
							associateKycUserRequest.toString(), associateKycUserRequest.getUserId(), "null",
							associateKycUserRequest.getClientId(), LogType.ASYNC_THREAD.name());
				}
				throw new HtsException(e.getMessage());
			}

			TokenBean tokenBean = TokenBean.builder().operatorId(associateKycUserRequest.getOperatorId())
					.adminOperatorId(associateKycUserRequest.getAdminOperatorId())
					.adminOperatorKey(associateKycUserRequest.getAdminOperatorKey())
					.tokenId(associateKycUserRequest.getTokenId()).build();
			try {
				enableKYC(tokenBean);
			} catch (Exception e) {
				try {
					exceptionMessgeIntoMongoLoggerActiveMQ(
							env.getProperty("issue.associatKycUser") + "," + e.getMessage(), ApiConstant.ASSOCIATE_KYC,
							associateKycUserRequest.toString(), associateKycUserRequest.getUserId(), "null",
							associateKycUserRequest.getClientId(), LogType.ACTIVE_MQ.name());
				} catch (JmsException jms) {
					asynchronousUtils.exceptionMessgeIntoMongoLogger(
							env.getProperty("issue.associatKycUser") + "," + e.getMessage(), ApiConstant.ASSOCIATE_KYC,
							associateKycUserRequest.toString(), associateKycUserRequest.getUserId(), "null",
							associateKycUserRequest.getClientId(), LogType.ASYNC_THREAD.name());
				}
				throw new HtsException(e.getMessage());
			}
		});
		return ResponseEntity.ok(MessageResponse.builder().message("Associate and enable kyc is done successfully")
				.status(HttpStatus.OK.value()).httpStatus(HttpStatus.OK).build());

	}

	@Override
	public ResponseEntity<?> multisignTokenTransfer(@Valid MultiSignTokenTransferRequest transferTokenRequest) {
		client = tokenHelper.setOperator();
		List<AccountId> nodeId = Collections
				.singletonList(new AccountId(transferTokenRequest.getResponseWallet().size()));
		TokenId tokenId = TokenId.fromString(transferTokenRequest.getTokenId());
		TransferTransaction transferTransaction = new TransferTransaction().setTransactionMemo("multi-sign transaction")
				.addTokenTransfer(tokenId, AccountId.fromString(transferTokenRequest.getSenderId()),
						-transferTokenRequest.getAmount())
				.addTokenTransfer(tokenId, AccountId.fromString(transferTokenRequest.getRecieverId()),
						transferTokenRequest.getAmount())
				.setNodeAccountIds(nodeId).freezeWith(client);

		// Signer one signs the transaction with their private key
		transferTokenRequest.getResponseWallet().forEach(privateKeys -> {
			byte[] signature1 = PrivateKey.fromString(privateKeys.getPrivateKey()).signTransaction(transferTransaction);
		});

		TransactionResponse submitTx = null;
		try {
			// Sign with the client operator key and submit the transaction to a Hedera
			submitTx = transferTransaction.execute(client);
		} catch (Exception e) {
			try {

				exceptionMessgeIntoMongoLoggerActiveMQ(
						env.getProperty("issue.in.transaction.response") + "," + e.getMessage(),
						ApiConstant.TRANSFER_TOKEN, transferTokenRequest.toString(), transferTokenRequest.getUserId(),
						"null", transferTokenRequest.getClientId(), LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(
						env.getProperty("issue.in.transaction.response") + "," + e.getMessage(),
						ApiConstant.TRANSFER_TOKEN, transferTokenRequest.toString(), transferTokenRequest.getUserId(),
						"null", transferTokenRequest.getClientId(), LogType.ASYNC_THREAD.name());
			}
			throw new HtsException(e.getMessage());
		}

		// Get the transaction ID
		TransactionId txId = submitTx.transactionId;
		// Print the transaction ID to the console
		System.out.println("The transaction ID " + txId);

		TransactionReceipt receipt = null;
		try {
			receipt = submitTx.getReceipt(client);
			try {
				saveHederaHtsInfoActiveMQ(HederaHtsInfoConstnat.OTHERS, HederaHtsInfoConstnat.TRANSACTION_GET_RECEIPT,
						transferTokenRequest.getClientId(), transferTokenRequest.getUserId(), LogType.ACTIVE_MQ.name());
			} catch (Exception e) {
				asynchronousUtils.saveHederaHtsInfo(HederaHtsInfoConstnat.OTHERS,
						HederaHtsInfoConstnat.TRANSACTION_GET_RECEIPT, transferTokenRequest.getClientId(),
						transferTokenRequest.getUserId(), LogType.ASYNC_THREAD.name());
			}
		} catch (Exception e) {
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(
						env.getProperty("issue.transaction.receipt") + "," + e.getMessage(), ApiConstant.TRANSFER_TOKEN,
						transferTokenRequest.toString(), transferTokenRequest.getUserId(), receipt.toString(),
						transferTokenRequest.getClientId(), LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(
						env.getProperty("issue.transaction.receipt") + "," + e.getMessage(), "/api/hts/token/transfer",
						transferTokenRequest.toString(), transferTokenRequest.getUserId(), receipt.toString(),
						transferTokenRequest.getClientId(), LogType.ASYNC_THREAD.name());
			}
			throw new HtsException(e.getMessage());
		}

		Status transactionStatus = receipt.status;
		System.out.println("Status" + transactionStatus);
		if (!transactionStatus.equals(Status.SUCCESS)) {
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("issue.in.transaction.response"),
						ApiConstant.TRANSFER_TOKEN, transferTokenRequest.toString(), transferTokenRequest.getUserId(),
						transactionStatus.name().toString(), transferTokenRequest.getClientId(),
						LogType.ACTIVE_MQ.name());
			} catch (JmsException e) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(env.getProperty("issue.in.transaction.response"),
						ApiConstant.TRANSFER_TOKEN, transferTokenRequest.toString(), transferTokenRequest.getUserId(),
						transactionStatus.name().toString(), transferTokenRequest.getClientId(),
						LogType.ASYNC_THREAD.name());
			}
			throw new HtsException("Transfer failed on iHI Service");
		}

		String remainingBalance = "";

		TokenBean tokenBean = TokenBean.builder().operatorId(transferTokenRequest.getSenderId())
				.clientId(transferTokenRequest.getClientId()).tokenId(transferTokenRequest.getTokenId()).build();
		try {
			String balance = initialBalanceToken(tokenBean);
			remainingBalance = balance.toString();
		} catch (Exception e) {
			try {
				exceptionMessgeIntoMongoLoggerActiveMQ(env.getProperty("issue.initiate.balance") + "," + e.getMessage(),
						ApiConstant.TRANSFER_TOKEN, transferTokenRequest.toString(), transferTokenRequest.getUserId(),
						"null", transferTokenRequest.getClientId(), LogType.ACTIVE_MQ.name());
			} catch (JmsException jms) {
				asynchronousUtils.exceptionMessgeIntoMongoLogger(
						env.getProperty("issue.initiate.balance") + "," + e.getMessage(), ApiConstant.TRANSFER_TOKEN,
						transferTokenRequest.toString(), transferTokenRequest.getUserId(), "null",
						transferTokenRequest.getClientId(), LogType.ASYNC_THREAD.name());
			}
			throw new HtsException(e.getMessage());
		}

		return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
				.message(env.getProperty("token.transfer.success")).response(remainingBalance).httpStatus(HttpStatus.OK)
				.build());

	}

}
