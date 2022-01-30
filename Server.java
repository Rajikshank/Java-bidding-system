import java.io.*;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class Server {
    // TODO : Define all required varible
    private  ItemMap item_map;
    ServerSocket Trade_server,Trade_publisher;
    static  Map <Integer,ArrayList> mapId=new HashMap<>();;
    static  long  bid_time=System.currentTimeMillis();

    public Server(ItemMap item_map) {
      this.item_map = item_map;
     
 
    }
    
    
    

    public void start() throws IOException {
        
    Runnable client_run=new Runnable() {// thread for port 2021
    	public void run() {
    		try {
    			
    			ServerSocket ss=new ServerSocket(2021);// creating a port using a thread 
    			 int count=1;
    			
    			while(true) {
    				Socket Client_socket =ss.accept();
    				System.out.println("Connection Success..... Client "+(count++)+" connected ");
    				ClientThread ct=new ClientThread(Client_socket,item_map);
    				Thread client=new Thread(ct);
    				client.start();
    			}
    			
    			
    			
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    };
    
    
    Runnable publisher_run=new Runnable() {// thread for port 2022
    	public void run() {
    		try {
    			ServerSocket ss=new ServerSocket(2022);
    			int count=1;
    			
    			while(true) {
    				Socket Publisher_Thread  =ss.accept();
    				System.out.println("Connection Success..... Publisher "+(count++)+" connected ");
    				PublisherThread pt=new PublisherThread(Publisher_Thread,item_map);
    				Thread publisher=new Thread(pt);
    				publisher.start();
    			}
    			
    			
    			
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    };
   
    Thread client_thread =new Thread(client_run);
    Thread publisher_thread= new Thread(publisher_run);
    client_thread.start();
    publisher_thread.start();
    
    	
}
    
    
 public static int checkarray(float [] array,PrintWriter out) {// this method will give result for the user input symbol is valid or not 
	 
	
	 boolean tr=true;
	 String str="";
	 int o=0;
	 for(float k:array) {
		 if(k!= -1)
			 str+="0 ";
		 else {
			 tr=false;
			 str+="-1 ";
		 }
		 
	 }
	 out.println(str);
	 if(tr==true)
		o=0;
	 else if(tr==false)
		o=-1;
	 return o;
	
	 
 }

 public static boolean validate_input(String input){
		boolean rtn=true;
		for(int i=0;i<input.length();i++)
		{
			if(input.charAt(i)>=33 && input.charAt(i)<46 || input.charAt(i)>=58 &&input.charAt(i)<=64 ||input.charAt(i)>=91 &&input.charAt(i)<=96 || input.charAt(i)>=123)
			{
				rtn=false;
				break;
			}
		}
			return rtn;
	}
 

}
  