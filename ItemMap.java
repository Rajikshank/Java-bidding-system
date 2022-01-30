import java.util.HashMap;
import java.util.Hashtable;

public  class ItemMap extends HashMap<String, Item>{

	// Note : Here we have suggested the usage of a  hashmap. However, use a preferred datastructure.
	// TODO : Implement any methods required to query/update the datastructure.

	
	
	
	public   void add(String symbol,Item value ) {// method to add a element 
		
		super.put(symbol, value);
	}
	
	
	public float getprice(String key) {// method for get the price 
		
		if(super.containsKey(key)==false)
			return -1;
		else
				return super.get(key).get_price();
		
	}
	
	public float make_bid(String key,float price,int id) {// method for update the bid acording to the price 
		if(super.containsKey(key)==false)
			return (float) -1.0;
		
		else {
		float updated_price=super.get(key).make_bid(price,id);
		return updated_price;
		}
	}
	
	
	
	public float profit_update(String key,int sec,float prft) {// method for updating the profit 
		if(super.containsKey(key)==false || super.get(key).getSecurity()!=sec)
			return -1;
		else {
			super.get(key).updateprofit(prft);
			return 0;
			}
		}	
	
	
	
}


