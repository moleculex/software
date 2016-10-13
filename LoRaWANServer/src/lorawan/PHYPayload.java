package lorawan;

public class PHYPayload 
{
	public class MHDR
	{
		public class MType
		{
		    public static final byte JOIN_REQUEST = 0x00;
		    public static final byte JOIN_ACCEPT = 0x20;
		    public static final byte UNCONF_DATA_UP = 0x40;
		    public static final byte UNCONF_DATA_DOWN = 0x60;
		    public static final byte CONF_DATA_UP = (byte)0x80;
		    public static final byte CONF_DATA_DOWN = (byte) 0xA0;
		}
	}
}
