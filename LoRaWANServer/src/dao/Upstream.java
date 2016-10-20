package dao;

public class Upstream 
{
	private int id;
	private byte[] deveui;
	private byte[] gweui;
	private String rxpk;
	private byte[] data;
	
	public Upstream(){
		
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public byte[] getDeveui() {
		return deveui;
	}
	public void setDeveui(byte[] deveui) {
		this.deveui = deveui;
	}
	public byte[] getGweui() {
		return gweui;
	}
	public void setGweui(byte[] gweui) {
		this.gweui = gweui;
	}
	public String getRxpk() {
		return rxpk;
	}
	public void setRxpk(String rxpk) {
		this.rxpk = rxpk;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
}
