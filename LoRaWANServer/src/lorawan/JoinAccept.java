package lorawan;

public class JoinAccept 
{
    private byte[] appNonce;
    private byte[] netId;
    private byte[] devAddr; 
    private byte[] RFU;
    private byte[] mic;
    
    public JoinAccept()
    {
    	appNonce = new byte[3];
        netId = new byte[3];
        devAddr = new byte[4]; 
        RFU = new byte[2];
        mic = new byte[4];
    }
    
	public byte[] getAppNonce() {
		return this.appNonce;
	}
	public void setAppNonce(byte[] appNonce) {
		this.appNonce = appNonce;
	}
	public byte[] getNetId() {
		return this.netId;
	}
	public void setNetId(byte[] netId) {
		this.netId = netId;
	}
	public byte[] getDevAddr() {
		return this.devAddr;
	}
	public void setDevAddr(byte[] devAddr) {
		this.devAddr = devAddr;
	}
	public byte[] getRFU() {
		return this.RFU;
	}
	public void setRFU(byte[] rFU) {
		RFU = rFU;
	}
	public byte[] getMic() {
		return this.mic;
	}
	public void setMic(byte[] mic) {
		this.mic = mic;
	}
}
