
public class Item {
    private float price;
    private int security;
    private float profit;
    private int id;
    private long Time;

    public Item(float price, int security, float profit,int id,long Time){
        this.price = price;
        this.security = security;
        this.profit = profit;
        this.Time=Time;
        this.id=id;
    }

    public float get_price()
    {
    	return this.price;
    }

    public void update_price(float price)
    {
    	this.price = price;
    }
    
    
    public float updateprofit(float proft)
    {
    	this.profit=proft;
    	return 0;
    }
    
    
    public int getSecurity()
    {
    	return this.security;
    }
    
    public long gettime() 
    {
    	return this.Time;
    }

    public float getprofit() 
    {
    	return this.profit;
    }

    public void settime(long time) 
    {
    	this.Time=time;
    }
    
    private void lastbidId(int id) 
    {
    	this.id=id;
    }
    
    public int getlastbidId() 
    {
    	return this.id;
    }
    
    public  float make_bid(float new_price,int id)
    {
    	// TODO: Implement this.
    	if(price<new_price) {
    		
    		this.price=new_price;
    		lastbidId(id);
    		return  this.price;
    		
    	}
    	else
    		return -1;
    }

    // TODO : Implement any other functions for security and profit (This is not needed for the first part of the project)

}
