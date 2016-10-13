package lorawan;

public class JoinRequest {
	private byte[] appEUI = new byte[8];
	private byte[] devEUI = new byte[8];
	private byte[] devNonce = new byte[2];
	
	public byte[] getAppEUI() {
		return appEUI;
	}
	public void setAppEUI(byte[] appEUI) {
		this.appEUI = appEUI;
	}
	public byte[] getDevEUI() {
		return devEUI;
	}
	public void setDevEUI(byte[] devEUI) {
		this.devEUI = devEUI;
	}
	public byte[] getDevNonce() {
		return devNonce;
	}
	public void setDevNonce(byte[] devNonce) {
		this.devNonce = devNonce;
	}
}
