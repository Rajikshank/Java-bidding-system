import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

public class ClientThread implements Runnable{
	
	int i,j;
	Socket Client_socket;
	String  request,request2;
	String msg,Id;
	ItemMap it;
	float result;
	float price [];
	float[] profit;
	String symbols_profit [];
	String symbols_price[];
	String qry [];
	boolean loop,bid,timeout,login;
	long time;
	ArrayList<String> symbollist;
	
	
	
	
	
	
	public ClientThread(Socket client,ItemMap it) {
		this.it=it;
		this.Client_socket=client;
		symbollist=new ArrayList<String>();
		time=Server.bid_time;
		loop=true;
		bid=true;
		timeout=false;
		login=false;
	}


	
	
	
	
	

	@Override
	public void run() {
		
	
	
        try {
        	
        	PrintWriter out = new  PrintWriter(Client_socket.getOutputStream(),true);
            BufferedReader in =new BufferedReader(new InputStreamReader(Client_socket.getInputStream()));
            

        	
        	
        		while(loop) 
        	{
        	
        			if(login==false) {
        				try {
                		out.println("please enter a unique id to register ?");
                        Id=in.readLine();
                        if(!Server.validate_input(Id)) {
                        	out.println(-1.0);
                        	continue;// skips the invalid inputs like special characters 
                        }
                        
                        if(Id.equalsIgnoreCase("off") ||System.currentTimeMillis()>=time )
                        	break;
                    synchronized(Server.mapId) {
                    	
                        if(!Server.mapId.containsKey(Integer.parseInt(Id))) {
                        	Server.mapId.put(Integer.parseInt(Id),symbollist);
                        	
                        	login=true;
                        	out.println(0);
                        }
                        else
                        {
                        	out.println(-1.0);
                        	continue;
                        }
                    }
                    
        				}
        				catch(Exception e) {
        					out.println(-1.0);//invalid entry of login id 
        					continue;
        				}
              	}
                		
        			
        	// here we are using timertask method for checking the timeout for the input
        	Timer sheduler =new Timer();
        	TimerTask task=new TimerTask() {
        		public void run() 
        		{
        			if(msg==null)
        				timeout=true;
        		}
        	};
        	
        			
        		if(bid )
        			{	
        				msg=null;
        	        	timeout=false;
        	        	sheduler.schedule(task, 10000);// we have changed the timeout to be 10sec instead of 0.5 sec
        				msg=in.readLine();
        				sheduler.cancel();
        				
        				if(timeout==true && time>System.currentTimeMillis()) 
        				{
        					out.println(-2.0);
        					continue;	
        				}
        				
        				
        				
        				//<<<<---- here we are entering to the subscription section to get notification on the bids --->>>
        				 	if( msg.equalsIgnoreCase("-1")) 
        					 {
        						bid=false;
        							request=in.readLine();
        						
        								if(!request.equals("-1")) //profit section
        							{
        									symbols_profit =request.split("\\s+");
        								profit=	this.retrieve_values(request,symbols_profit,true, out);
        							}
        						
        							request2=in.readLine();
        							
        								if(!request2.equals("-1"))//price section
        								{
        									symbols_price =request.split("\\s+");
        									price=this.retrieve_values(request,symbols_price,false, out);
        								}	
        								
        								
        								}
    										
        								
        							     
        				// <<<<------ bidding section----> here we are performing the task to update or check current price---->>> 
        				
                		else if(!msg.equalsIgnoreCase("-1")) 
 				 		{
                		  
                			if(time>System.currentTimeMillis()) //<<<<<< under the bidding time 
                			{
        					synchronized(it) 
        						{			
        				 			qry=msg.split("\\s+");	
        							if(qry.length==2) {
        								result=it.make_bid(qry[0], Float.parseFloat(qry[1]),Integer.parseInt(Id));
        								symbollist.add(qry[1]);
        								Server.mapId.replace(Integer.parseInt(Id),symbollist);//user id and the bid symbol will be placed as a record for a particular user 
        								out.println(result);							//reply with updated price or -1 depending on the query 
        								if(result!=-1.0)
        									System.out.println("[price] "+qry[0]+" "+result);
        							}
        							else if(qry.length==1) {
        								result=it.getprice(qry[0]);//reply with the current price of the symbol 
        								out.println(result);
        							}
        							
        							else {
        								result= (float) -1.0;  //reply for invalid bid/symbol					
        								out.println(result);
        							}
        								if(result==-1.0)
        								continue;
        							
        							
        								time=it.get(qry[0]).gettime();
        							if(System.currentTimeMillis()>(time-60000) && System.currentTimeMillis()<time) //checks time and updates time for the last 60 second bids 
        							{
        								System.out.println("bid time extended by 60 seconds due to the customer entry at last 60seconds ");// this function can be extended to any time limit
        								time=time+60000;
        								it.get(qry[0]).settime(time);
        							}
        							
        							it.notifyAll();							
        				 		}
                			}
                			
                			
                				else				// if timeup the server should disconnect the client to discard bidding 
                					loop=false; // this variable will break the loop and disconnect the client s
 				 		}
        	
        		
        			}
        		
        		//<<---- after subscription completed we are checking for a update here ---->>
        	else if(!bid ) {
        		
        		//	out.println("in waiting....");
        		Timer timer=new Timer();
        	  TimerTask checktime=new TimerTask() {
        		public void run() {
        			if(System.currentTimeMillis()>=time) {// this allows to check for the time while waiting in the notification section 
        				synchronized(it) {
        				it.notifyAll();
        				}
        				
        				}
        				
        		}
        	};
        	timer.scheduleAtFixedRate(checktime, 0,1000);     
        	
        	synchronized(it) {
        	try {
			it.wait();

        	if(!request.equals("-1")) // checks for the profit changes 
        	{
        	this.value_check(profit,symbols_profit, out,true);
        	}
        	
        	if(!request2.equals("-1")) {//checks for the price changes 
        		this.value_check(price,symbols_price, out,false);
        	}
        	
        	if(time<=System.currentTimeMillis())
        		loop=false;
				}
        	
        	catch (InterruptedException e)
        	{
			
				e.printStackTrace();
			}
        	
        	
            	
        	timer.cancel();	
        	}
        	
         }		
        		out.flush();
       }
          
        
        out.println(-2.0);// timeout 
        }
        
	
        
     
	
        catch (IOException e) {
        		System.out.println("IO expception detected....");// notify that the client has disconnected unexpectedly 
        }
        
        finally {
        	try {        		 
				Client_socket.close();
				System.out.println("client disconnected .............");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
        }
        
       
        
	}


	public float[] retrieve_values(String request,String [] symbols,boolean profit,PrintWriter out) {// this method will retrieve the current price or profit to provide notification 
		
		float value [] = new float[(symbols.length -1)];
		j=1;
		System.out.println(request);
		
		if(profit==true) {
		for( i=0;i<(symbols.length)-1;i++) 
		{
			value[i]=it.get(symbols[j]).getprofit();
	
			j++;
		
		}
		Server.checkarray(value, out);
		}
		else {
			for( i=0;i<(symbols.length)-1;i++) 
			{
				value[i]=it.getprice(symbols[j]);
				
				j++;
			}
			Server.checkarray(value, out);
			
		}
		
		
		return value;
		
	}

	
	
	public void value_check(float [] value,String [] symbols,PrintWriter out,boolean profit) {// this method will check for price or profit to provide notification 
		
		if(profit==true) {
		j=1; 
    	for(i=0;i<(symbols.length)-1;i++) //this loop checks for the profit update by comparing old profit  
    	{
    		if(it.containsKey(symbols[j])==false)
    			continue;
    		
    	if(value[i]<it.get(symbols[j]).getprofit())
    		{
    		value[i]=it.get(symbols[j]).getprofit();
    		out.println(symbols[j]+" "+"Profit"+" "+value[i]);
    		}
    		j++;	
    		}
		}
		
		
		
		else {
			
			j=1; 
        	for(i=0;i<(symbols.length)-1;i++) //this loop checks for the price update by comparing old price 
        	{
        		if(it.containsKey(symbols[j])==false)
        			continue;
        		
        	if(value[i]<it.get(symbols[j]).get_price())
        		{
        		value[i]=it.get(symbols[j]).get_price();
        		out.println(symbols[j]+" "+"Price"+" "+value[i]);
        		}
        		j++;	
        		}
			
		}
		
	}
	
	
	
	

}



	



