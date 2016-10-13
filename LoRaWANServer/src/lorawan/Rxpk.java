package lorawan;

import org.json.JSONArray;
import org.json.JSONObject;


public class Rxpk 
{
	private long tmst;
    private String time;
    private int chan;
    private int rfch;
    private double freq;
    private int stat;
    private String modu;
    private String datr;
    private String codr;
    private double lsnr;
    private int rssi;
    private int size;
    private String data;
    
    public Rxpk()
    {
    	
    }
    
	public Rxpk(JSONArray rxpkArr)
	{
		try
		{
			JSONObject rxpk = rxpkArr.getJSONObject(0);
			tmst = rxpk.getLong("tmst");
			time = rxpk.getString("time");
			chan = rxpk.getInt("chan");
			rfch = rxpk.getInt("rfch");
			freq = rxpk.getDouble("freq");			
			stat = rxpk.getInt("stat");
			modu = rxpk.getString("modu");
			datr = rxpk.getString("datr");
			codr = rxpk.getString("codr");
			lsnr = rxpk.getDouble("lsnr");
			rssi = rxpk.getInt("rssi");		
			size = rxpk.getInt("size");
			data = rxpk.getString("data");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public long getTmst() {
		return tmst;
	}

	public void setTmst(long tmst) {
		this.tmst = tmst;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getChan() {
		return chan;
	}

	public void setChan(int chan) {
		this.chan = chan;
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

	public int getStat() {
		return stat;
	}

	public void setStat(int stat) {
		this.stat = stat;
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

	public double getLsnr() {
		return lsnr;
	}

	public void setLsnr(double lsnr) {
		this.lsnr = lsnr;
	}

	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
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
