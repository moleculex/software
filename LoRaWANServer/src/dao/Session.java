package dao;

public class Session 
{
	private int id;
	private byte[] devEUI;
	private byte[] devAddr;
	private byte[] devNonce;
	private byte[] appNonce;
	private byte[] gweui;
	
	public Session(){
	
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public byte[] getDeveui() {
		return devEUI;
	}
	public void setDeveui(byte[] devEUI) {
		this.devEUI = devEUI;
	}
	
	public byte[] getDevAddr() {
		return devAddr;
	}
	public void setDevAddr(byte[] devAddr) {
		this.devAddr = devAddr;
	}
	
	public byte[] getDevNonce() {
		return devNonce;
	}
	public void setDevNonce(byte[] devNonce) {
		this.devNonce = devNonce;
	}
	
	public byte[] getAppNonce() {
		return appNonce;
	}
	public void setAppNonce(byte[] appNonce) {
		this.appNonce = appNonce;
	}

	public byte[] getGweui() {
		return gweui;
	}

	public void setGweui(byte[] gweui) {
		this.gweui = gweui;
	}
}
