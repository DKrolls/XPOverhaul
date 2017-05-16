package io.github.dkrolls.XPOverhaul;

import java.util.UUID;

public class XPAccount implements Comparable<XPAccount>{
	private int balance;
	private UUID uuid;
	
	public XPAccount(int balance, UUID uuid){
		this.balance = balance;
		this.uuid = uuid;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public UUID getUUID() {
		return uuid;
	}

	@Override
	public int compareTo(XPAccount o) {
		return balance - o.getBalance();
	}
	
	@Override
	public String toString(){
		return "Balance: "+balance+" | UUID: "+uuid.toString();
	}
}
