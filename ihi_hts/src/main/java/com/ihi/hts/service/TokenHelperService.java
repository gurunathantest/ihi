package com.ihi.hts.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;

@Service(value = "TokenHelperService")
public class TokenHelperService {

	@Autowired
	Environment env;

	@Value("${H721_NETWORK}")
	private String network;
	
	private Client client = null;
	
	public TokenHelperService() {
	
	}
	
	public Client setOperator() {
		if(client !=null)
			return client;
		
		String operatorId=env.getProperty("H721_OPERATOR_ID");
		String operatorKey=env.getProperty("H721_OEPRATOR_KEY");
		if (network.equalsIgnoreCase("testnet")) {
			client = Client.forTestnet();
			/*try {
				 Map network = new HashMap<String, AccountId>();
				 network.put("0.testnet.hedera.com:50211", new AccountId(3));
			        network.put("34.94.106.61:50211", new AccountId(3));
			        network.put("50.18.132.211:50211", new AccountId(3));
			        network.put("138.91.142.219:50211", new AccountId(3));

			        network.put("1.testnet.hedera.com:50211", new AccountId(4));
			        network.put("35.237.119.55:50211", new AccountId(4));
			        network.put("3.212.6.13:50211", new AccountId(4));
			        network.put("52.168.76.241:50211", new AccountId(4));

			        network.put("2.testnet.hedera.com:50211", new AccountId(5));
			        network.put("35.245.27.193:50211", new AccountId(5));
			        network.put("52.20.18.86:50211", new AccountId(5));
			        network.put("40.79.83.124:50211", new AccountId(5));

			        network.put("3.testnet.hedera.com:50211", new AccountId(6));
			        network.put("34.83.112.116:50211", new AccountId(6));
			        network.put("54.70.192.33:50211", new AccountId(6));
			        network.put("52.183.45.65:50211", new AccountId(6));

			        network.put("4.testnet.hedera.com:50211", new AccountId(7));
			        network.put("34.94.160.4:50211", new AccountId(7));
			        network.put("54.176.199.109:50211", new AccountId(7));
			        network.put("13.64.181.136:50211", new AccountId(7));

			        network.put("5.testnet.hedera.com:50211", new AccountId(8));
			        network.put("34.106.102.218:50211", new AccountId(8));
			        network.put("35.155.49.147:50211", new AccountId(8));
			        network.put("13.78.238.32:50211", new AccountId(8));

			        network.put("6.testnet.hedera.com:50211", new AccountId(9));
			        network.put("34.133.197.230:50211", new AccountId(9));
			        network.put("52.14.252.207:50211", new AccountId(9));
			        network.put("52.165.17.231:50211", new AccountId(9));
			        client.setNetwork(network);
				}catch(Exception e) {
					
				}*/
			client.setOperator(ExampleHelper.getOperatorId(operatorId),ExampleHelper.getOperatorKey(operatorKey));

		} else if (network.equalsIgnoreCase("mainnet")) {
			client = Client.forMainnet();
			client.setOperator(ExampleHelper.getOperatorId(operatorId), ExampleHelper.getOperatorKey(operatorKey));

		}
		return client;
	}

	public String getOperatorPrivateKey() {
		String ss=env.getProperty("H721_OEPRATOR_KEY");
		 return env.getProperty("H721_OEPRATOR_KEY");
	}

	public String getOperatorAccountId() {
		String ss=env.getProperty("H721_OPERATOR_ID");
		 return env.getProperty("H721_OPERATOR_ID");
	}
	
	public Client setClientAdminOperator(String accountId,String accountPrivateKey) {
		Client client = null;
		if (network.equalsIgnoreCase("testnet")) {
			client = Client.forTestnet();
			client.setOperator(ExampleHelper.getOperatorId(accountId),ExampleHelper.getOperatorKey(accountPrivateKey));

		} else if (network.equalsIgnoreCase("mainnet")) {
			client = Client.forMainnet();
			client.setOperator(ExampleHelper.getOperatorId(accountId), ExampleHelper.getOperatorKey(accountPrivateKey));

		}
		return client;
	}
}
