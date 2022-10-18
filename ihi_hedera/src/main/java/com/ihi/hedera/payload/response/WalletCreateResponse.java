package com.ihi.hedera.payload.response;

public class WalletCreateResponse {
	private String account;
	private String privatekey;
	private long shard;
	private long real;
	private long accoundId;
	private String publickey;

	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPrivatekey() {
		return privatekey;
	}
	public void setPrivatekey(String privatekey) {
		this.privatekey = privatekey;
	}
	public long getShard() {
		return shard;
	}
	public void setShard(long shard) {
		this.shard = shard;
	}
	public long getReal() {
		return real;
	}
	public void setReal(long real) {
		this.real = real;
	}
	public long getAccoundId() {
		return accoundId;
	}
	public void setAccoundId(long accoundId) {
		this.accoundId = accoundId;
	}
	public String getPublickey() {
		return publickey;
	}
	public void setPublickey(String publickey) {
		this.publickey = publickey;
	}
	
	
}
