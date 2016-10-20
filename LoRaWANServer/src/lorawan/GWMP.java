package lorawan;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedList;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONArray;
import org.json.JSONObject;

import dao.Session;
import dao.Upstream;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;

public class GWMP
{
	private DatagramSocket serverSocket;
	
	Gateway gateway;
	LinkedList<Gateway> queue;
	
	public static final byte version = 0x01;
	public static final byte[] token = new byte[]{0x00, 0x00};
	public static final byte[] netId = new byte[]{0x03, 0x02, 0x01};
	
	public static final byte PUSH_DATA = 0x00;
    public static final byte PULL_DATA = 0x02;
    public static final byte PULL_ACK = 0x04;
    public static final byte PULL_RESP = 0x03;
	
	public Rxpk rxpk;
	public Txpk txpk;
	
	byte[] appKey;
	
	JoinRequest joinRequest;
	JoinAccept joinAccept;
	Session session;
	org.hibernate.Session hSession;
	Configuration cfg;
	
	byte[] dsDevAddr;
	byte[] dsData;
	
	byte[] joinGweui;

	String state = "ack";
	
	public GWMP()
	{	
		try
		{
			serverSocket = new DatagramSocket(1700);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		gateway = new Gateway();
		queue = new LinkedList<Gateway>();
		
        appKey = new byte[]{0x13, 0x34, 0x13, 0x34, 0x13, 0x34, 0x13, 0x34, 0x13, 0x34, 0x13, 0x34, 0x13, 0x34, 0x13, 0x34};
		
		rxpk = new Rxpk();
		txpk = new Txpk();
		
		joinRequest = new JoinRequest();
		joinAccept = new JoinAccept();
		session = null;
		
		cfg = new Configuration();
		cfg.configure("Hibernate.cfg.xml");
	}
	
	public Runnable tunnel = new Runnable()
	{
		public void run()
		{
	        for(;;)
            {
	        	try
	        	{
					byte[] receiveData = new byte[1024];
		        	DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	                serverSocket.receive(receivePacket);
	                String sentence = new String( receivePacket.getData());
	                System.out.println("RECEIVED: " + sentence);
	                
	                Gateway gw = new Gateway();
	                gw.IPAddress = receivePacket.getAddress();
	                gw.port = receivePacket.getPort();
	                gw.data = receiveData;
	                
	                queue.add(gw);
	                
	                gateway.IPAddress = gw.IPAddress;
	                gateway.port = gw.port;
	        	}
	        	catch(Exception e)
				{
					e.printStackTrace();
				}
            }
		}
	};	
	
	class Gateway
	{
		private byte[] EUI;
		InetAddress IPAddress;
		int port;
		byte[] data = new byte[1024];
		int dataSize;
		
		public Gateway()
		{
			EUI = new byte[8];
		}
	}
		
	public Runnable queueHandler = new Runnable()
	{
		public void run()
		{
	        for(;;)
            {
	        	Gateway gw = new Gateway();
	        	gw = queue.poll();
	        	
	        	if(gw != null)
	        	{
					switch(gw.data[3])
			        {
			        	case PULL_DATA:	
			        		pullData(gw);
			        		break;
						
						case PUSH_DATA:	
							pushData(gw);
							break;
			        }
	        	}
            }
        }
	};
	
	public Runnable user = new Runnable()
	{
		public void run()
		{
	        for(;;)
            {
	        	/*BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	            System.out.println("Enter x to transmit");
	            System.out.println();
	            String s = null;
	            try
	            {
	            	s = br.readLine();
	            }
	            catch(Exception e)
	            {
	            	e.printStackTrace();
	            }
	        	
	        	if(s.equals("x"))
	        	{
	        		System.out.println("Sending...");
	        		state = "data";

	        	}*/
	        	
	        	BufferedReader is;
	            PrintWriter os;
	            Socket socket = null;

	            try
	            {
	            	ServerSocket httpServerSocket = new ServerSocket(8888);
					socket = httpServerSocket.accept();

	                is = new BufferedReader(new InputStreamReader(socket.getInputStream()));

	                String request = is.readLine();
	                
	                String strDeveui = request.substring(request.indexOf("deveui") + 7, request.indexOf("deveui") + 7 + 16);
	                String strData = request.substring(request.indexOf("data") + 5, request.indexOf("data") + 5 + 14);
	             
	                System.out.println(strDeveui);
	                System.out.println(strData);
	                
	                byte[] deveui = javax.xml.bind.DatatypeConverter.parseHexBinary(strDeveui);
	                byte[] data = javax.xml.bind.DatatypeConverter.parseHexBinary(strData);
	                
	                @SuppressWarnings("deprecation")
		    		SessionFactory sf = cfg.buildSessionFactory();
		        	hSession = sf.openSession();	    		
					hSession.beginTransaction();
					
					session = (Session)hSession.createCriteria(Session.class).add(Restrictions.eq("deveui", deveui)).uniqueResult();
					dsDevAddr = session.getDevAddr();
					
		        	hSession.flush();
		        	hSession.close();
		        	
		        	dsData = data;
		        	
		        	System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(dsDevAddr));
	    			System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(dsData));
	                
	                state = "data";
		        	
	                String response = "Sent....";

	                os = new PrintWriter(socket.getOutputStream(), true);

	                os.print("HTTP/1.1 200 OK" + "\r\n");
	                os.print("Content-Length: " + response.length() + "\r\n");
	                os.print("Content-Type: text/plain" + "\r\n\r\n");

	                os.print(response + "\r\n");
	                os.flush();
	                socket.close();
	                httpServerSocket.close();
	            }
	            catch (Exception e) 
	            {
	            	e.printStackTrace();
	            }

            }
        }
	};
		
	public void pullData(Gateway gw)
    {	
		DatagramPacket sendPacket;
		
		ByteBuffer pkt;
    	pkt = ByteBuffer.allocate(1024);
    	pkt.order(ByteOrder.LITTLE_ENDIAN);
    	
    	ByteBuffer dataUnencrypted;
    	
    	String data = null;
		
    	JSONObject t = new JSONObject();
    	String t_str = null;
    	
		switch(state)
		{
			case "ack":
		   		gw.data[3] = PULL_ACK;
	    		sendPacket = new DatagramPacket(gw.data, gw.data.length, gw.IPAddress, gw.port);
	        	try
	        	{
	        		serverSocket.send(sendPacket);
	        	}
	        	catch(Exception e)
	        	{
	        		e.printStackTrace();
	        	}
				break;
				
			case "join":
		        pkt.put(version);
		        pkt.put(token);
		        pkt.put(PULL_RESP);
		             
		        dataUnencrypted = ByteBuffer.allocate(16);
		        dataUnencrypted.order(ByteOrder.LITTLE_ENDIAN);
		        
		        ByteBuffer tmp0, tmp1; 
		        
    			new Random().nextBytes(joinAccept.getAppNonce());
    			byte[] deveui = joinRequest.getDeveui();
    			joinAccept.setDevAddr(new byte[]{deveui[0], deveui[1], deveui[2], deveui[3]});
    			//joinAccept.setDevAddr(new byte[]{0x17, 0x77, (byte)0x96, 0x06});//possibly use 4 LSB from deveui
    			joinAccept.setRFU(new byte[]{0x00, 0x00});
    			
    			System.out.println("AppNonce: " + javax.xml.bind.DatatypeConverter.printHexBinary(joinAccept.getAppNonce()));
    			System.out.println("NetId: " + javax.xml.bind.DatatypeConverter.printHexBinary(netId));
    			System.out.println("DevAddr: " + javax.xml.bind.DatatypeConverter.printHexBinary(joinAccept.getDevAddr()));
	
		        
				dataUnencrypted.put(joinAccept.getAppNonce());
				dataUnencrypted.put(netId);
				dataUnencrypted.put(joinAccept.getDevAddr());
				dataUnencrypted.put(joinAccept.getRFU());
				
				tmp0 = ByteBuffer.allocate(13);
				tmp0.order(ByteOrder.LITTLE_ENDIAN);
				tmp0.put(PHYPayload.MHDR.MType.JOIN_ACCEPT);
				tmp0.put(Arrays.copyOfRange(dataUnencrypted.array(), 0, 12));
				joinAccept.setMic(computeMic(tmp0, appKey));
				
				dataUnencrypted.put(joinAccept.getMic());
				
				byte[] dataEncrypted = new byte[16];
				dataEncrypted = decrypt(dataUnencrypted, appKey);
				
				tmp1 = ByteBuffer.allocate(17);
				tmp1.order(ByteOrder.LITTLE_ENDIAN);
				tmp1.put(PHYPayload.MHDR.MType.JOIN_ACCEPT);
				tmp1.put(dataEncrypted);
				data = Base64.getEncoder().encodeToString(tmp1.array());
				
		        txpk.setTmst(rxpk.getTmst() + 5000000);
		        txpk.setRfch(0);
		        txpk.setFreq(rxpk.getFreq());
		        txpk.setModu("LORA");
		        txpk.setDatr(rxpk.getDatr());
		        txpk.setCodr(rxpk.getCodr());
		        txpk.setIpol(true);
		        txpk.setPowe(14);
		        txpk.setImme(false);
		        txpk.setSize(17);
		        txpk.setData(data);    
		      
		        t = txpk.toJSON();
		        
		        try 
		        {
					t_str = new JSONObject().put("txpk", t).toString();
				} 
		        catch (Exception e) 
		        {
					e.printStackTrace();
				}
		        
		        pkt.put(t_str.getBytes());
			
		        System.out.println(new String(pkt.array()));
	        	sendPacket = new DatagramPacket(pkt.array(), pkt.capacity() - pkt.remaining(), gw.IPAddress, gw.port);
	        	try
	        	{
	        		serverSocket.send(sendPacket);
	        	}
	        	catch(Exception e)
	        	{
	        		e.printStackTrace();
	        	}
	    	        	
	        	@SuppressWarnings("deprecation")
	    		SessionFactory sf = cfg.buildSessionFactory();
	        	hSession = sf.openSession();	    		
				hSession.beginTransaction();
				
				session = (Session)hSession.createCriteria(Session.class).add(Restrictions.eq("deveui", new byte[]{deveui[7], deveui[6], deveui[5], deveui[4], deveui[3], deveui[2] , deveui[1], deveui[0]})).uniqueResult();
				session.setDevAddr(joinAccept.getDevAddr());
				session.setDevNonce(joinRequest.getDevNonce());
	        	session.setAppNonce(joinAccept.getAppNonce());
	        	session.setGweui(joinGweui);
	        	
	        	hSession.save(session);
	        	hSession.flush();
	        	hSession.getTransaction().commit();
	        	hSession.close();
	        
	        	
	        	state = "ack";
				break;
			
			case "data":     		
		        pkt.order(ByteOrder.LITTLE_ENDIAN);
		        pkt.put(version);
		        pkt.put(token);
		        pkt.put(PULL_RESP);     
				
		        ByteBuffer d = ByteBuffer.allocate(12);
		        d.put((byte)0x00);
		        //d.put(joinAccept.getDevAddr());
		        d.put(dsDevAddr);
		        //d.put(new byte[]{0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11});
		        d.put(dsData);
		        data = Base64.getEncoder().encodeToString(d.array());
				
		        //txpk.setTmst(rxpk.getTmst() + 5000000);
		        txpk.setRfch(0);
		        txpk.setFreq(869.525);
		        txpk.setModu("LORA");
		        txpk.setDatr("SF12BW125");
		        txpk.setCodr("4/5");
		        txpk.setIpol(true);
		        txpk.setPowe(14);
		        txpk.setImme(true);
		        //txpk.setSize(12);
		        txpk.setSize(5 + dsData.length);
		        txpk.setData(data);    
		         
		        t = txpk.toJSON();
		        
		        try 
		        {
					t_str = new JSONObject().put("txpk", t).toString();
				} 
		        catch (Exception e) 
		        {
					e.printStackTrace();
				}
		        
		        pkt.put(t_str.getBytes());
			
		        System.out.println(new String(pkt.array()));
	        	sendPacket = new DatagramPacket(pkt.array(), pkt.capacity() - pkt.remaining(), gateway.IPAddress, gateway.port);
	        	try
	        	{
	        		serverSocket.send(sendPacket);
	        	}
	        	catch(Exception e)
	        	{
	        		e.printStackTrace();
	        	}
	        	
	        	state = "ack";
				break;
		}			
    }
		
	public void pushData(Gateway gw)
    {
		byte[] temp = new byte[1024];
		String tempStr;
		
		System.arraycopy( gw.data, 4, gw.EUI, 0, 8);
		System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(gw.EUI));
		
		System.arraycopy( gw.data, 12, temp, 0, gw.data.length - 12);
		tempStr = (new String(temp));
		
		if(tempStr.substring(0,7).equals("{\"rxpk\""))
		{
			try
			{
				JSONArray j = new JSONArray(tempStr.substring(8));
				rxpk = new Rxpk(j);	
				byte[] data = Base64.getDecoder().decode(rxpk.getData());
				
				if(data[0] == PHYPayload.MHDR.MType.JOIN_REQUEST)
				{		
					System.arraycopy(data, 1, joinRequest.getAppeui(), 0, 8);
					System.arraycopy(data, 9, joinRequest.getDeveui(), 0, 8);
					System.arraycopy(data, 17, joinRequest.getDevNonce(), 0, 2);
					
					System.out.println("AppEUI: " + javax.xml.bind.DatatypeConverter.printHexBinary(joinRequest.getAppeui()));
					System.out.println("DevEUI: " + javax.xml.bind.DatatypeConverter.printHexBinary(joinRequest.getDeveui()));
					System.out.println("DevNonce: " + javax.xml.bind.DatatypeConverter.printHexBinary(joinRequest.getDevNonce()));
									
					joinGweui = gw.EUI;
					try 
					{						
					  state = "join";
					} 
					catch (Exception e) 
					{
					  System.out.println("deveui does not exist in Session table");
					}		
				}
			
				
				if(data[0] == PHYPayload.MHDR.MType.UNCONF_DATA_UP)
				{	
			        ByteBuffer a = ByteBuffer.allocate(16);
			        a.order(ByteOrder.LITTLE_ENDIAN);
					
					ByteBuffer b = ByteBuffer.allocate(16);
			        b.order(ByteOrder.LITTLE_ENDIAN);
			        b.put((byte)0x02);
			        
			        byte[] deveui;
			      
			        try 
					{	
			    		@SuppressWarnings("deprecation")
			    		SessionFactory sf = cfg.buildSessionFactory();
			    		hSession = sf.openSession();
			        	hSession.beginTransaction();
			        	
			        	session = (Session)hSession.createCriteria(Session.class).add(Restrictions.eq("devAddr", new byte[]{data[1], data[2], data[3], data[4]})).uniqueResult(); 	
			        	deveui = session.getDeveui();
			        	b.put(session.getAppNonce());
				        b.put(netId);
				        b.put(session.getDevNonce());
				        
				        hSession.flush();
			        	hSession.close();
				        
				        /*b.put(joinAccept.getAppNonce());
				        b.put(joinAccept.getNetId());
				        b.put(joinRequest.getDevNonce());*/
				        
				        byte[] key = encrypt(b, appKey);
				        
				        a.put((byte) 0x01);
				        a.put(new byte[]{0x00, 0x00, 0x00, 0x00});
				        a.put((byte)0x00);//direction - uplink
				        a.put(data[1]); //devaddr[4]
				        a.put(data[2]);
				        a.put(data[3]);
				        a.put(data[4]);
				        a.put((byte)0x00);//fcnt[4]
				        a.put((byte)0x00);
				        a.put(data[6]);
				        a.put(data[7]);        
				        a.put((byte) 0x00);
				        a.put((byte) 0x01);
				        
				        byte[] s = encrypt(a, key);
				        
				        byte[] payload = new byte[16];
				        System.arraycopy(data, 9, payload, 0, 7);
				        
				        for (int i = 0; i < 7; i++) 
				        {
				            payload[i] = (byte) (s[i] ^ payload[i]);
				        }
				        System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(payload));
				        
				        try 
						{
				        
				    		hSession = sf.openSession();
				         hSession.beginTransaction();
				         
						  Upstream upstream = new Upstream();
						  upstream.setDeveui(deveui);
						  upstream.setGweui(gw.EUI);
						  upstream.setRxpk(tempStr);
						  upstream.setData(payload);
						  
						  hSession.save(upstream);
						  hSession.flush();
						  hSession.getTransaction().commit();
						  hSession.close();
						} 
						catch (Exception e) 
						{
							e.printStackTrace();
						  //System.out.println("gweui does not exist in Upstream table");
						}
					}
			        catch(Exception e)
			        {
			        	System.out.println("devAddr does not exist in Session table");
			        }	
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
    }
	
	public void test() throws Exception
	{
		//encryption before mic
		
		/*String d = "QBd3lgYAAAAK+YnS6LlYvzTN2k4="; 
		byte[] data = Base64.getDecoder().decode(d);
		byte[] appNonce = new byte[]{(byte)0x59, (byte)0x2D, (byte)0x92};
		byte[] netId = new byte[]{0x03, 0x02, 0x01};
		byte[] devNonce = new byte[]{0x61, 0x5C};
		
        ByteBuffer a = ByteBuffer.allocate(16);
        a.order(ByteOrder.LITTLE_ENDIAN);*/
		
        //NwkSKey: key = aes128 encrypt(AppKey, 0x01 + AppNonce + NetID + DevNonce + pad16) - think not used
        //AppSKey: aes128 encrypt(AppKey, 0x02 + AppNonce + NetID + DevNonce + pad16) 
		/*ByteBuffer b = ByteBuffer.allocate(16);
        b.order(ByteOrder.LITTLE_ENDIAN);
        b.put((byte)0x02);
        b.put(appNonce);
        b.put(netId);
        b.put(devNonce);
        
        byte[] key = encrypt(b, appKey);
        
        a.put((byte) 0x01);
        a.put(new byte[]{0x00, 0x00, 0x00, 0x00});
        a.put((byte)0x00);//direction - uplink
        a.put(data[1]); //devaddr[4]
        a.put(data[2]);
        a.put(data[3]);
        a.put(data[4]);
        a.put((byte)0x00);//fcnt[4]
        a.put((byte)0x00);
        a.put(data[6]);
        a.put(data[7]);        
        a.put((byte) 0x00);
        a.put((byte) 0x01);
        
        byte[] s = encrypt(a, key);
        
        byte[] payload = new byte[16];
        System.arraycopy(data, 9, payload, 0, 7);
        
        for (int j = 0; j < 7; j++) 
        {
            payload[j] = (byte) (s[j] ^ payload[j]);
        }
        System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(payload));*/
		
		/*Configuration cfg = new Configuration();
		cfg.configure("Hibernate.cfg.xml");
		
		@SuppressWarnings("deprecation")
		SessionFactory sf = cfg.buildSessionFactory();
		org.hibernate.Session session = sf.openSession();
		Session s = null;
		
		try {
		  s = (Session) session.load(Session.class, new byte[]{0x00,0x04,(byte)0xa3,0x0b, 0x00, 0x1a, 0x40, 0x6d});
		} catch (Exception e) {
		  // No Users with id 'userId' exists
		}
		System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(s.getDevAddr()));*/
		/*session = (Session)hSession.createCriteria(Session.class).add(Restrictions.eq("devAddr", new byte[]{0x6d, 0x40, 0x1a, 0x00})).uniqueResult(); 
		System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(session.getAppNonce()));*/
		
		 /*Upstream upstream = (Upstream) hSession.load(Upstream.class, new byte[]{(byte)0xAA, 0x55, 0x5A, 0x00, 0x00, 0x00, 0x00, 0x00});
		 System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(upstream.getGweui()));*/
	}
	
    
	public byte[] computeMic(ByteBuffer data, byte [] appKey) 
	{         
		byte[] retval = new byte[4];
        
        try 
        {
            AesCmac aesCmac = new AesCmac();
            aesCmac.init(new SecretKeySpec(appKey, "AES"));
            aesCmac.updateBlock(data.array());
            retval = Arrays.copyOfRange(aesCmac.doFinal(), 0, 4);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        
        return retval;
    }
	
	public byte[] encrypt(ByteBuffer data, byte[] appKey) 
	{
		byte[] s = null;
		
        try 
        {
            Key aesKey = new SecretKeySpec(appKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            s = cipher.doFinal(data.array());
        } 
        catch (Exception ex) 
        {
            ex.printStackTrace();
        }	
        
        return s;
	}
	
	public byte[] decrypt(ByteBuffer data, byte[] appKey) 
	{
		byte[] s = null;
		
        try 
        {
            Key aesKey = new SecretKeySpec(appKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            s = cipher.doFinal(data.array());
        } 
        catch (Exception ex) 
        {
            ex.printStackTrace();
        }	
        
        return s;
	}
}
