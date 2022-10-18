package com.ihi.hcs.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PrivateKey;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvUtils {
	
	public static String envPath= "";
	public static String netWork = "";
	public static String operatorPrivateKey = "";
	public static String operatorId = "";
	public static String topicId = "";
	public static String jasyptSecretKey ="";
	
	static {
		Properties properties = new Properties();
		InputStream inputStream = EnvUtils.class.getClassLoader().getResourceAsStream("env.properties");
		if (Objects.nonNull(inputStream)) {
			try {
				properties.load(inputStream);
			} catch (IOException e) {
			}
		}
		if (properties.getProperty("H721_NETWORK").equalsIgnoreCase("mainnet")) {
			envPath = "src/main/mainnet-env";
			netWork = "mainnet";
		} else if (properties.getProperty("H721_NETWORK").equalsIgnoreCase("testnet")) {
			envPath = "src/main/testnet-env";
			netWork = "testnet";
		}
		operatorPrivateKey = properties.getProperty("H721_OEPRATOR_KEY");
		operatorId = properties.getProperty("H721_OPERATOR_ID");
		topicId = properties.getProperty("H721_TOPIC");
		jasyptSecretKey = properties.getProperty("jasypt.secret.key");
		System.out.println(topicId);
	}
    static Dotenv getEnv() {
        return Dotenv.configure().directory(envPath).filename(".env").load();
    }

    public static Hedera_Environment getHederaEnvironment() {
        String _env = Objects.requireNonNull(getEnv().get("HEDERA_ENVIRONMENT"));
        return Hedera_Environment.valueOf(_env);
    }

    public static String getMirrorNodeAddress() {
        return Objects.requireNonNull(getEnv().get("MIRROR_NODE_ADDRESS"));
    }

    public static AccountId getOperatorId() {
        return AccountId.fromString(Objects.requireNonNull(getEnv().get("OPERATOR_ID")));
    }

    public static PrivateKey getOperatorKey() {
        return PrivateKey.fromString(Objects.requireNonNull(getEnv().get("OPERATOR_KEY")));
    }
    
    public static String getTopicId() {
        String topicId = Objects.requireNonNull(getEnv().get("TOPIC_ID"));
        return topicId;
    }
    public enum Hedera_Environment {
        TESTNET,
        MAINNET
    }
}
