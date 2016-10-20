package lorawan;

public class JoinRequest {
	private byte[] appeui = new byte[8];
	private byte[] deveui = new byte[8];
	private byte[] devNonce = new byte[2];
	
	public byte[] getAppeui() {
		return appeui;
	}
	public void setAppeui(byte[] appeui) {
		this.appeui = appeui;
	}
	public byte[] getDeveui() {
		return deveui;
	}
	public void setDeveui(byte[] deveui) {
		this.deveui = deveui;
	}
	public byte[] getDevNonce() {
		return devNonce;
	}
	public void setDevNonce(byte[] devNonce) {
		this.devNonce = devNonce;
	}
}
