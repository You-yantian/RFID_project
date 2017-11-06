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
	//String URL = "jdbc:mysql://localhost/rfid";
	//String userName="root";
	//String Password="yantian94";
	String URL = "jdbc:mysql://omnitor-internal.cr7dcjbockme.eu-central-1.rds.amazonaws.com/wai_rfid";
	String userName="hettjej";
	String Password="waiwaiwai";
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

	public boolean storeData(String itemID,String itemName,String boughtDay,String expireDay,String getTime,int times,int MaxTimes){
		 status=false;
		 boolean exist=false;
		 String sql;
		 try{
				//open database
		        Class.forName("com.mysql.jdbc.Driver");
		        conn = DriverManager.getConnection(URL, userName, Password);

		        PreparedStatement ps1=conn.prepareStatement( "select * from food where iditems=? ");
		        	  ps1.setString(1,itemID);

		        	  //ps1.setString(2,itemName);
		        	  ResultSet rs=ps1.executeQuery();
		        	  exist=rs.next();
		        	  if(exist==true){
		        		  //tag already been used before rewrite new info into tag
		        		  sql="UPDATE food SET name=?,boughtDay=?,expireDay=?,times=?,timeStamp=?,MaxTimes=?,finished=? WHERE iditems = ?";


		        	  }else{
		        		  //tag haven't been used, insert a new food
		        	    sql ="INSERT INTO food ( name, boughtDay,expireDay,times,timeStamp,MaxTimes,finished,iditems) VALUES (?, ?, ?, ?,?,?,?,?)";

		        	  }
		        	    PreparedStatement ps = conn.prepareStatement(sql);

		      	        ps.setString(1, itemName);
		      	        ps.setString(2, boughtDay);
		      	        ps.setString(3, expireDay);
		      	        ps.setInt(4,times);
		      	        ps.setString(5,getTime);
		      	        ps.setInt(6,MaxTimes);
		      	        ps.setInt(7,0);
		      	        ps.setString(8, itemID);
		      	        int n=ps.executeUpdate();
		      	        if(n!=0){
		      	        	status=true;
		      	        }

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

	public boolean updateData(String itemID,String itemName,String timeStamp,int times){
		 status=false;
		 try{
				//open database
		        Class.forName("com.mysql.jdbc.Driver");
		        conn = DriverManager.getConnection(URL, userName, Password);
		        PreparedStatement ps1=conn.prepareStatement( "select * from food where iditems=? and name=? ");
		        ps1.setString(1,itemID);
		        ps1.setString(2,itemName);

		        	  ResultSet rs1=ps1.executeQuery();
		        	  status=rs1.next();
		        	  if(status==true){
		        		//update a exit item
		        		String sql1="INSERT INTO timeStamp (ID_food, name,timeStamp) VALUES (?, ?, ?)";
		        		String sql="UPDATE food SET times=?,timeStamp=? WHERE iditems = ?";
		        	    PreparedStatement ps = conn.prepareStatement(sql);
		      	        ps.setInt(1,times);
		      	        ps.setString(2,timeStamp);
		      	        ps.setString(3,itemID);
		      	        ps.executeUpdate();
		      	        PreparedStatement ps2 = conn.prepareStatement(sql1);
		      	        ps2.setString(1, itemID);
		      	        ps2.setString(2,itemName);
		      	        ps2.setString(3, timeStamp);
		      	        ps2.executeUpdate();
		        	  }else{
		        		  status=false;//food doesn't exist
		        	  }


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

	public Message searchItem(String itemID){
		Message item=new Message();
		item.itemName="error";
		try{
			 Class.forName("com.mysql.jdbc.Driver");
		     conn = DriverManager.getConnection(URL, userName, Password);
		  PreparedStatement ps=conn.prepareStatement(
		    "select name,MaxTimes from food where iditems=? ");
		  ps.setString(1,itemID);
		  ResultSet rs=ps.executeQuery();
		  if (rs.next()==true){
		   item.itemID=itemID;
		   item.itemName=rs.getString("name");
		   item.Maxtimes=rs.getInt("MaxTimes");
		  }else{
			  System.out.println("Item not exist!");
		  }
		 }catch(Exception e){e.printStackTrace();}
		return item;
	}
}
