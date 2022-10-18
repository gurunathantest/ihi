package com.ihi.hts.scheduler;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hedera.hashgraph.sdk.AccountBalance;
import com.hedera.hashgraph.sdk.AccountBalanceQuery;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.PrecheckStatusException;
import com.hedera.hashgraph.sdk.PrivateKey;

@Component
public class HbarCheckBalance {

	@Value("${H721_OPERATOR_ID}")
	private String WalletId;
	@Value("${H721_OEPRATOR_KEY}")
	private String AdminKey;
	public static final long HBAR = 1000;

	@Autowired
	private SendEmail email;
	@Scheduled(cron = "0 0 0/4 * * *")
	//@Scheduled(cron = "0/10 * * * * *")
	public void hbarCheckBalance() throws TimeoutException, PrecheckStatusException, IOException, MessagingException {
		AccountId adminId = AccountId.fromString(WalletId);
		PrivateKey adminPKey = PrivateKey.fromString(AdminKey);
		Client client = Client.forTestnet();
		client.setOperator(adminId, adminPKey);
		AccountBalance accountBalanceNew = new AccountBalanceQuery().setAccountId(adminId).execute(client);
		long amount = accountBalanceNew.hbars.getValue().intValue();
		
		if (amount < HBAR) {
			email.sendEmailHBar(amount);
		}
		client.close();
		
	}

}
