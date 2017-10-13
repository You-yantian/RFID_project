package connection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Database {
	String itemID,itemName,boughtDay,expireDay,getTime;
	int times;
	boolean status;
	Connection conn = null;
	String URL = "jdbc:mysql://localhost/rfid";
	public Database()
    {
        /*this.itemID=itemID;
        this.itemName=itemName;
        this.boughtDay=boughtDay;
        this.expireDay=expireDay;
        this.getTime=getTime;
        this.times=times;*/
		super();
    }
	
	public boolean storeData(String itemID,String itemName,String boughtDay,String expireDay,String getTime,int times){  
		 status=false;
		 try{
				//open database
		        Class.forName("com.mysql.jdbc.Driver");
		        conn = DriverManager.getConnection(URL, "root", "yantian94");
		        PreparedStatement ps1=conn.prepareStatement( "select * from items where iditems=? and name=?");  
		        	  ps1.setString(1,itemID);  
		        	  ps1.setString(2,itemName);
		        	  ResultSet rs=ps1.executeQuery();  
		        	  status=rs.next(); 
		        	  //if(status==true){
		        		  //change get times of item
		        		   
		        	  //}else{
		        		  //insert a new item
		        	    String sql ="INSERT INTO items (iditems, name, boughtDay,expireDay,times,useTime) VALUES (?, ?, ?, ?,?,?)";
		      	        PreparedStatement ps = conn.prepareStatement(sql);
		      	        ps.setString(1, itemID);
		      	        ps.setString(2, itemName);
		      	        ps.setString(3, boughtDay);
		      	        ps.setString(4, expireDay);   
		      	        ps.setInt(5,times);
		      	        ps.setString(6,getTime);
		      	        
		      	        ps.executeUpdate();
		        	  //}
		        
		 }catch (Exception e) {
				e.printStackTrace();
				status=false;
			}finally {
		         if (conn != null) {
		             try {
		                conn.close();
		             } catch (Exception e) {
		             }
		          }
		       }
	return status;
	}
	
	public String searchItem(String itemID){
		String name="error";
		try{  
			 Class.forName("com.mysql.jdbc.Driver");
		     conn = DriverManager.getConnection(URL, "root", "yantian94");  
		  PreparedStatement ps=conn.prepareStatement(  
		    "select name from items where iditems=? ");  
		  ps.setString(1,itemID);    
		  ResultSet rs=ps.executeQuery(); 
		  if (rs.next()==true){
		  name=rs.getString("name");  
		  }else{
			  System.out.println("Item not exist!");
		  }
		 }catch(Exception e){e.printStackTrace();}  
		return name;
	}
}
