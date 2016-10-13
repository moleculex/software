package main;

import lorawan.GWMP;

public class Main
{   
	public static void main(String[] args) 
	{	
		new Main();		
	}

	public Main()
	{	
		try
		{
			/*UDP udp = new UDP();
	
			Thread threadUDPReceive = new Thread(udp.receive);
			threadUDPReceive.start();*/
			
			GWMP gwmp = new GWMP();
			
			//gwmp.test();
			
			Thread t0 = new Thread(gwmp.tunnel);
			t0.start();
				
			Thread t1 = new Thread(gwmp.queueHandler);
			t1.start();
			
			Thread t2 = new Thread(gwmp.user);
			t2.start();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
}


