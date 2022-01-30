import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;


//<<<----- everything here same as the clientthread class except time mechanism and few other things-->>>


public class PublisherThread implements Runnable{
	int i,j;
	Socket publisher_socket;
	String  request,request2;
	String msg,Id;
	ItemMap it;
	float result;
	float price [];
	float[] profit;
	String symbols_profit [];
	String symbols_price[];
	String qry [];
	boolean loop=true,bid=true,timeout=false,login=false;
	long time=Server.bid_time;
	ArrayList<String> symbollist;
	
	
	
	
	
	
	
	
	public PublisherThread(Socket client,ItemMap it) {
		this.it=it;
		this.publisher_socket=client;
		

	}

	

	

	@Override
	public void run() {
		// TODO Auto-generated method stub
	

        try {
        	
        	PrintWriter out = new  PrintWriter(publisher_socket.getOutputStream(),true);
            BufferedReader in =new BufferedReader(new InputStreamReader(publisher_socket.getInputStream()));
           
            
        	
        	
        	
            

        	
        	while(loop) {

        		if(login==false) {
        		out.println("please enter a unique id to register ?");
                Id=in.readLine();
                if(Id.equalsIgnoreCase("off"))
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
        		
      
if(bid) {
            msg=in.readLine();
        	qry=msg.split("\\s+");
    
        if(!msg.equalsIgnoreCase("-1")) {
        	synchronized(it) {
        	result=it.profit_update(qry[0], Integer.parseInt(qry[1]),Float.parseFloat(qry[2]));//entering query for the method which changes the profit 
        	out.println(result);
        	if(result==-1.0)
        		continue;
        	else
        		System.out.println("[prft] "+qry[0]+" "+qry[2]);
        	it.notifyAll();
        	}
        }
        
        else if( msg.equalsIgnoreCase("-1")) 
			 {
				bid=false;
					request=in.readLine();
				
						if(!request.equals("-1")) //checks profit 
					{
							symbols_profit =request.split("\\s+");
							profit=this.retrieve_values(symbols_profit, true, out);
					}
				
					request2=in.readLine();
					
						if(!request2.equals("-1"))//checks price 
						{
							
							symbols_price =request2.split("\\s+");
							price=this.retrieve_values(symbols_price, false, out);
						}	
						
						
					}     
}	
else if(!bid ) {
	
	//out.println("in waiting....");
Timer timer=new Timer();
TimerTask checktime=new TimerTask() {
public void run() {
	if(System.currentTimeMillis()>=time) {
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

if(!request.equals("-1"))//notification for profit 
{

	this.value_check(profit,symbols_profit, out,true);
}
if(!request2.equals("-1")) {// notification for price

	this.value_check(price, symbols_price, out, false);
}

if(time<=System.currentTimeMillis())
loop=false;
}

catch (InterruptedException e)
{
// TODO Auto-generated catch block
e.printStackTrace();
}


timer.cancel();	
}

}		

        	
        
        	}
        		
      
        	 out.println(-2.0);
        }
        
	    catch (IOException e) {
        		System.out.println("IO expception detected....");
        }
        
        finally {
        	try {
				publisher_socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        System.out.println("loop exited");
        
	}
	
	
	
	
public float[] retrieve_values(String [] symbols,boolean profit,PrintWriter out) {// this method will retrieve the current price or profit to provide notification 
		
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



	



