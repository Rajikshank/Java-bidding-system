import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class CSVReader {
    private String filename;
    public ItemMap item_map;
    private Item Trade_data;

    public CSVReader(String filename, ItemMap item_map){
        this.filename = filename;
        this.item_map = item_map;   //reference to the datastructure containing all the items.
    }

    
    public void read() throws IOException {
    	// TODO : Implement method to read CSV file (this.filename) and populat the datastructure (this.item_map)
    	
    	
    	InputStream inputStream = ClassLoader.getSystemResourceAsStream(this.filename);// get the file from class loader
			    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			   

        String line =  null;
        System.out.println("reading csv..");
        br.readLine();
        
        while((line = br.readLine()) != null){
            String[] columns = line.split(",");// splits string
     
            Trade_data	=new Item(Float.parseFloat(columns[1]),Integer.parseInt(columns[2]),Float.parseFloat(columns[3]),0,Server.bid_time);
            item_map.put(columns[0], Trade_data);
            
        }

        br.close();
    }
}
