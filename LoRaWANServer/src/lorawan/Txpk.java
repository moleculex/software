package lorawan;

import org.json.JSONObject;

public class Txpk 
{
	private long tmst;
    private int rfch;
    private double freq;
    private String modu;
    private String datr;
    private String codr;
    private boolean ipol;
    private int powe;
    private boolean imme;
    private int size;
    private String data;
    
	public Txpk()
	{
		
	}
	
	public JSONObject toJSON()
	{
		JSONObject txpk = new JSONObject();
		
		try
		{
			txpk.put("tmst", this.tmst)
				.put("rfch", this.rfch)
				.put("freq", this.freq)
				.put("modu", this.modu)
				.put("datr", this.datr)
				.put("codr", this.codr)
				.put("ipol", this.ipol)
				.put("powe", this.powe)
				.put("imme", this.imme)
			 	.put("size", this.size)
			 	.put("data", this.data);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		 	       		
		return txpk;
	}

	public long getTmst() {
		return tmst;
	}

	public void setTmst(long tmst) {
		this.tmst = tmst;
	}

	public int getRfch() {
		return rfch;
	}

	public void setRfch(int rfch) {
		this.rfch = rfch;
	}

	public double getFreq() {
		return freq;
	}

	public void setFreq(double freq) {
		this.freq = freq;
	}

	public String getModu() {
		return modu;
	}

	public void setModu(String modu) {
		this.modu = modu;
	}

	public String getDatr() {
		return datr;
	}

	public void setDatr(String datr) {
		this.datr = datr;
	}

	public String getCodr() {
		return codr;
	}

	public void setCodr(String codr) {
		this.codr = codr;
	}

	public boolean isIpol() {
		return ipol;
	}

	public void setIpol(boolean ipol) {
		this.ipol = ipol;
	}

	public int getPowe() {
		return powe;
	}

	public void setPowe(int powe) {
		this.powe = powe;
	}

	public boolean isImme() {
		return imme;
	}

	public void setImme(boolean imme) {
		this.imme = imme;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
