package connection;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.BufferedReader;
//import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TwoWaySerialComm {
	 public TwoWaySerialComm()
	    {
	        super();
	    }
	    
	void connect ( String portName ) throws Exception
	    {
	        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
	        byte []data;
	        if ( portIdentifier.isCurrentlyOwned() )
	        {
	            System.out.println("Error: Port is currently in use");
	        }
	        else
	        {
	            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
	            
	            if ( commPort instanceof SerialPort )
	            {
	                SerialPort serialPort = (SerialPort) commPort;
	                serialPort.setSerialPortParams(57600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
	                
	                InputStream in = serialPort.getInputStream();
	                OutputStream out = serialPort.getOutputStream();
	                //(new Thread(new Function(in,out))).start();
	                ////1st////
	                this.Function(in,out);
	                // Command com=new Command(in,out);
	                	              
	                //(new Thread(new SerialReader(in))).start();
	                //(new Thread(new SerialWriter(out))).start();
	                serialPort.close();
	            }
	            else
	            {
	                System.out.println("Error: Only serial ports are handled by this example.");
	            }
	        }
	        
	    }
	public static void Function (InputStream in,OutputStream out ){
    	
    	BufferedReader commandInput = new BufferedReader(new InputStreamReader(System.in));     
        String c=null;
        String itemName=null;
        String itemDetail=null;
        String boughtDate=null;
        String expireDate=null;
        Command com=new Command(in,out);
   /// public void run ()
   //{    
        System.out.println("Input the command: ");
        
        while(true){
        	try {
                c = commandInput.readLine();
               
            
            } catch (IOException e) {
                e.printStackTrace();
            }
        
        	byte[] data;
		
        	if ( c !=null )
        	{
        	
        	// Command com=new Command(in,out);
        	if (c.equals("readVersion")){
        	Thread readVersion = new Thread(new readVersion(in,out));              		
            readVersion.start();
            }
        	else if(c.equals("TagDetail")){
        	Thread TagDetail = new Thread(new TagDetail(in,out));              		
        	TagDetail.start();
            }
            
        	else if(c.equals("write")){
        		Message mess=new Message();
        		try {
            		System.out.println("Please enter the item's name: ");
            		mess.itemName = commandInput.readLine();
            		System.out.println("Please enter the date you bought this item: ");
            		mess.boughtDate=commandInput.readLine();
            		System.out.println("Please enter the expire date of this item: ");
            		mess.expireDate=commandInput.readLine();
            		//System.out.println("Please enter the maximum times you want to consume this item with in one week: ");
            		//mess.Maxtimes=commandInput.readLine();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }      
        			Thread write = new Thread(new write(in,out,mess));              		
        			write.start();
            }
           
            
        	else if(c.equals("read")){
        	 Thread read = new Thread(new read(in,out));              		
     	     read.start();
            }
            
            
        	else if(c.equals("record")){
    	   	Thread record = new Thread(new record(in,out));              		
   	     	record.start();
            ////////
            }
       
            else{
            	System.out.println("Wrong command. Try again!");
            }
           
        }
        	try{
        		out.flush();
        		in.close();
        	}
        	catch ( IOException e )
        	{
        		e.printStackTrace();
            
        	}           
        }
       // }
           
    }
		public static class readVersion implements Runnable 
     {
			private	Command com;
			public readVersion ( InputStream in,OutputStream out )
			{
			com=new Command(in,out);
			}
			public void run ()
	        {
				byte[]data=com.readVersion();
	            System.out.println("The reader version is: "+Integer.toHexString((data[8]) & 0xFF)+"."+Integer.toHexString((data[7]) & 0xFF));
	                
	        }
     }
		
		public static class TagDetail implements Runnable 
	     {
				private Command com;
				public TagDetail ( InputStream in,OutputStream out )
				{
				com=new Command(in,out);
				}
				public void run ()
		        {
					byte[]data=com.TagDetail();
                	byte chkErr=chkErrorISO(data);
                	if (chkErr!=0){
                	   
                	    System.out.println("Reader returned error code: " + chkErr);
                	    Thread.interrupted();
                    }
                	else if(data.toString().equals("error")){
                		 System.out.println("CheckSum Error");
                		 Thread.interrupted();
                	}
                	else{
                		//String.format("%8s", Integer.toBinaryString(b2 & 0xFF)).replace(' ', '0');
                		if(data[7]==0x01){
                			System.out.println("Transponder ID: 0x" 
                					+ String.format("%2s",Integer.toHexString((data[20]) & 0xFF)).replace(' ','0') + String.format("%2s",Integer.toHexString((data[19]) & 0xFF)).replace(' ','0')
                					+ String.format("%2s",Integer.toHexString((data[18]) & 0xFF)).replace(' ','0') + String.format("%2s",Integer.toHexString((data[17]) & 0xFF)).replace(' ','0')
                					//+ Integer.toHexString((data[18]) & 0xFF) + Integer.toHexString((data[17]) & 0xFF)
                					+ String.format("%2s",Integer.toHexString((data[16]) & 0xFF)).replace(' ','0') + String.format("%2s",Integer.toHexString((data[15]) & 0xFF)).replace(' ','0')
                					+ String.format("%2s",Integer.toHexString((data[14]) & 0xFF)).replace(' ','0') + String.format("%2s",Integer.toHexString((data[13]) & 0xFF)).replace(' ','0'));
     
                			System.out.println("DSFID: 0x" +  String.format("%2s",Integer.toHexString((data[12]) & 0xFF)).replace(' ','0'));
                		}else{
                			System.out.println(("RFID tag not read."));
                		}
                		//System.out.println(Command.bytesToHexString(data, data.length));
                	}
		        }
	     }
		
		public static class write implements Runnable 
	     {		private String itemName=null;
         		private String itemDetail=null;
         		private String boughtDate=null;
         		private String expireDate=null;
         		private Command com;
         		public BufferedReader commandInput = new BufferedReader(new InputStreamReader(System.in));     
				public write ( InputStream in,OutputStream out,Message mess )
				{
				com=new Command(in,out);
				itemName=mess.itemName;
				boughtDate=mess.boughtDate;				
				expireDate=mess.expireDate;
				}
				public void run ()
		        {
					
					int block=1;
                	byte[]dataToWrite=new byte[4];
                	byte []data=null;
                	String MaxTime="00";
                	 ///****get the UID of the tag***///
                    byte[] data1=com.TagDetail();
                	String itemID=null;
                	byte chkErr=chkErrorISO(data1);
                	if (chkErr!=0){
                	   
                	    System.out.println("Reader returned error code: " + chkErr);
                	   // System.exit(1);
                    }
                	else if(data1.toString().equals("error1")){
                		 System.out.println("CheckSum Error");
                		 Thread.interrupted();
                	}
                	else if(data1.toString().equals("error2")){
               		   System.out.println("Achieve Item ID fails. Enter the command again!");
               		  Thread.interrupted();
               	    }
                	else{
                		//String.format("%8s", Integer.toBinaryString(b2 & 0xFF)).replace(' ', '0');
                		 
                		if(data1[7]==0x01){
                			itemID=String.format("%2s",Integer.toHexString((data1[20]) & 0xFF)).replace(' ','0') + String.format("%2s",Integer.toHexString((data1[19]) & 0xFF)).replace(' ','0')
                					+ String.format("%2s",Integer.toHexString((data1[18]) & 0xFF)).replace(' ','0') + String.format("%2s",Integer.toHexString((data1[17]) & 0xFF)).replace(' ','0')
                					//+ Integer.toHexString((data[18]) & 0xFF) + Integer.toHexString((data[17]) & 0xFF)
                					+ String.format("%2s",Integer.toHexString((data1[16]) & 0xFF)).replace(' ','0') + String.format("%2s",Integer.toHexString((data1[15]) & 0xFF)).replace(' ','0')
                					+ String.format("%2s",Integer.toHexString((data1[14]) & 0xFF)).replace(' ','0') + String.format("%2s",Integer.toHexString((data1[13]) & 0xFF)).replace(' ','0');
                			System.out.println("ID is :"+ itemID);
                			//System.out.println("DSFID: 0x" +  String.format("%2s",Integer.toHexString((data[12]) & 0xFF)).replace(' ','0'));
                		}else{
                			System.out.println(("RFID tag not read."));
                			Thread.interrupted();
                		}
                	
                    ///***************************///
                	try{
    	                Thread.sleep(100);
    	                }catch (Exception e){	                	      
    	            	      e.printStackTrace();	            	        
    	             }
                	itemDetail="00"+boughtDate+expireDate;
                	//try {
                		//System.out.println("Please enter the detail: ");
                		//itemDetail= commandInput.readLine();
                    
    	           // } catch (IOException e) {
    	              //  e.printStackTrace();
    	            //} 
                	//System.out.println("The name of the item is: "+itemName+" and the item detail is: "+itemDetail);
                    byte[] dataAll=(itemDetail.length()+itemDetail).getBytes();
                    int remainLength=dataAll.length;
                    int index=0;
              
                    while(remainLength>0 ){
                    	try{
        	                Thread.sleep(50);
        	                }catch (Exception e){	                	      
        	            	      e.printStackTrace();	            	        
        	             }
                    	if(remainLength>=4){
                    		for(index=0;index<4;index++){
                    			dataToWrite[index]=dataAll[dataAll.length-remainLength+3-index];
                    		}
                    	} else{
                    		dataToWrite=new byte[]{(byte)0,(byte)0,(byte)0,(byte)0};
                    		for(index=0;index<remainLength;index++){
                    			dataToWrite[3-index]=dataAll[dataAll.length-remainLength+index];
                    		}
                    	}
                	    data=com.Write(itemName, dataToWrite,block,itemID.getBytes());
                	    remainLength-=4;
                	    
                	    block=block+1;
                    }
                    
                    //*******Store the initial data into database*******//
                    
                	
                    if (!data.toString().equals("error")){
                    	Database item=new Database();
                    	Calendar cal = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        String getTime=sdf.format(cal.getTime());
                    	item.storeData(itemID, itemName, boughtDate, expireDate,getTime,0);
                    }
                	}   
		        }
	     }
		
		public static class read implements Runnable 
	     {
				private String itemName=null;
				private String itemDetail=null;
				private String boughtDate=null;
				private String expireDate=null;	
				public Database item=new Database();
				private	Command com;
				public read ( InputStream in,OutputStream out )
				{
				com=new Command(in,out);
				}
				public void run ()
		        {
	            	///*******get item ID******///
	            	byte []data=com.TagDetail();
	            	String itemID=null;
	            	byte chkErr=chkErrorISO(data);
	            	if (chkErr!=0){
	            	   
	            	    System.out.println("Reader returned error code: " + chkErr);
	            	    Thread.interrupted();
	                }
	            	else if(data.toString().equals("error1")){
	            		 System.out.println("CheckSum Error");
	            		 Thread.interrupted();
	            	}
	            	else if(data.toString().equals("error2")){
	           		  System.out.println("Achieve Item ID fails. Enter the command again!");
	           		   Thread.interrupted();
	           	    }
	            	else{
	            		//String.format("%8s", Integer.toBinaryString(b2 & 0xFF)).replace(' ', '0');
	            		if(data[7]==0x01){
	            			itemID=String.format("%2s",Integer.toHexString((data[20]) & 0xFF)).replace(' ','0') + String.format("%2s",Integer.toHexString((data[19]) & 0xFF)).replace(' ','0')
	            					+ String.format("%2s",Integer.toHexString((data[18]) & 0xFF)).replace(' ','0') + String.format("%2s",Integer.toHexString((data[17]) & 0xFF)).replace(' ','0')
	            					//+ Integer.toHexString((data[18]) & 0xFF) + Integer.toHexString((data[17]) & 0xFF)
	            					+ String.format("%2s",Integer.toHexString((data[16]) & 0xFF)).replace(' ','0') + String.format("%2s",Integer.toHexString((data[15]) & 0xFF)).replace(' ','0')
	            					+ String.format("%2s",Integer.toHexString((data[14]) & 0xFF)).replace(' ','0') + String.format("%2s",Integer.toHexString((data[13]) & 0xFF)).replace(' ','0');
	            			System.out.println("ID is :"+ itemID);
	            			//System.out.println("DSFID: 0x" +  String.format("%2s",Integer.toHexString((data[12]) & 0xFF)).replace(' ','0'));
	            		}else{
	            			System.out.println(("RFID tag not read."));
	            			Thread.interrupted();
	            		}
	    
	            	
	            	///*****get item name*******///
	            	itemName=item.searchItem(itemID);
	            	///*************************///
	                	int NoBlock=5;
	                	int startBlock=1;
	                	int idx=0;
	                	/*try {
	                		itemName = commandInput.readLine();
	                    
	    	            } catch (IOException e) {
	    	                e.printStackTrace();
	    	            }   */ 
	                	System.out.println("The name of the item is: "+itemName);
	                	try{
	                	Thread.sleep(100);
	                	}catch (Exception e){	                	      
	            	            e.printStackTrace();	            	        
	                	}
	                	data=com.Read(itemName,itemID.getBytes());
	                	chkErr=chkErrorISO(data);
	                	if (chkErr!=0){
	                	   
	                	    System.out.println("Reader returned error code: " + chkErr);
	                	    Thread.interrupted();
	                    }
	                	 if(data.toString().equals("error")){
	                  		 System.out.println("Read Item Detail fails. Enter the command again!");
	                  		 Thread.interrupted();
	               	    }else{
	                	int length=(Integer.parseInt(Integer.toHexString((data[12]) & 0xFF))-30)*10+(Integer.parseInt(Integer.toHexString((data[11]) & 0xFF))-30);
	                	System.out.println("Data length is: "+length);
	                	
	                	int Times=(Integer.parseInt(Integer.toHexString((data[10]) & 0xFF))-30)*10+(Integer.parseInt(Integer.toHexString((data[9]) & 0xFF))-30);                	
	                	System.out.println("Achieve times of this item is: "+ Times);
	                	
	                	boughtDate=byteToString(data[17])+byteToString(data[16])+byteToString(data[15])+byteToString(data[14])
	                				+byteToString(data[22])+byteToString(data[21])+byteToString(data[20])+byteToString(data[19]);
	                	System.out.println("Bought date of this item is: "+ boughtDate);
	                	expireDate=byteToString(data[27])+byteToString(data[26])+byteToString(data[25])+byteToString(data[24])
	    								+byteToString(data[32])+byteToString(data[31])+byteToString(data[30])+byteToString(data[29]);
	                	System.out.println("Expire date of this item is: "+ expireDate);
	                	
	            	  }
	            	}
					   
		        }
	     }
		
		public static class record implements Runnable 
	     {
				private String itemName=null;
				private String itemDetail=null;
				private String boughtDate=null;
				private String expireDate=null;	
				public Database item=new Database();	
				private	Command com;
				public record ( InputStream in,OutputStream out )
				{
				com=new Command(in,out);
				}
				public void run ()
		        {
					byte []data=com.TagDetail();
                	String itemID=null;
                	byte chkErr=chkErrorISO(data);
                	
                	if (chkErr!=0){
                	   
                	    System.out.println("Reader returned error code: " + chkErr);
                	    if(chkErr==100){
                	    	System.out.println("Length of data is less than 10: "+com.bytesToHexString(data, data.length));
                	    }
                	   // System.exit(1);
                    }
                	else if(data.toString().equals("error1")){
                		 System.out.println("CheckSum Error");
                	}
                	else if(data.toString().equals("error2")){
               		 System.out.println("Achieve Item ID fails. Enter the command again!");
               	    }
                	else{
                		//String.format("%8s", Integer.toBinaryString(b2 & 0xFF)).replace(' ', '0');
                		if(data[7]==0x01){
                			itemID=String.format("%2s",Integer.toHexString((data[20]) & 0xFF)).replace(' ','0') + String.format("%2s",Integer.toHexString((data[19]) & 0xFF)).replace(' ','0')
                					+ String.format("%2s",Integer.toHexString((data[18]) & 0xFF)).replace(' ','0') + String.format("%2s",Integer.toHexString((data[17]) & 0xFF)).replace(' ','0')
                					//+ Integer.toHexString((data[18]) & 0xFF) + Integer.toHexString((data[17]) & 0xFF)
                					+ String.format("%2s",Integer.toHexString((data[16]) & 0xFF)).replace(' ','0') + String.format("%2s",Integer.toHexString((data[15]) & 0xFF)).replace(' ','0')
                					+ String.format("%2s",Integer.toHexString((data[14]) & 0xFF)).replace(' ','0') + String.format("%2s",Integer.toHexString((data[13]) & 0xFF)).replace(' ','0');
                			System.out.println("ID is :"+ itemID);
                			//System.out.println("DSFID: 0x" +  String.format("%2s",Integer.toHexString((data[12]) & 0xFF)).replace(' ','0'));
                		}else{
                			System.out.println(("RFID tag not read."));
                		}
        
                	}
                	itemName=item.searchItem(itemID);
                	System.out.println("The item's name is: "+itemName);
                	try{
	                	Thread.sleep(100);
	                	}catch (Exception e){	                	      
	            	            e.printStackTrace();	            	        
	                	}
                //**********read item infomation************//
                if (!itemName.equals("error")){
                	data=com.Read(itemName,itemID.getBytes());
                	chkErr=chkErrorISO(data);
                	if (chkErr!=0){
                	   
                	    System.out.println("Reader returned error code: " + chkErr);
                    }
                	int length=(Integer.parseInt(Integer.toHexString((data[12]) & 0xFF))-30)*10+(Integer.parseInt(Integer.toHexString((data[11]) & 0xFF))-30);
                	System.out.println("Data length is: "+length);
                	
                	int Times=(Integer.parseInt(Integer.toHexString((data[10]) & 0xFF))-30)*10+(Integer.parseInt(Integer.toHexString((data[9]) & 0xFF))-30);                	
                	System.out.println("Achieve times of this item is: "+ Times);
                	Integer NewTimes=Times+1;
                	boughtDate=byteToString(data[17])+byteToString(data[16])+byteToString(data[15])+byteToString(data[14])
                				+byteToString(data[22])+byteToString(data[21])+byteToString(data[20])+byteToString(data[19]);
                	System.out.println("Bought date of this item is: "+ boughtDate);
                	expireDate=byteToString(data[27])+byteToString(data[26])+byteToString(data[25])+byteToString(data[24])
    								+byteToString(data[32])+byteToString(data[31])+byteToString(data[30])+byteToString(data[29]);
                	System.out.println("Expire date of this item is: "+ expireDate);
                	//System.out.println(Command.bytesToHexString(data, data.length));
                	Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    String getTime=sdf.format(cal.getTime());
                	
                    
                    //*******record and write new data to tag******//
                    int block=1;
                    byte[]dataToWrite=new byte[4];
                    if (boughtDate.length()==8 && expireDate.length()==8){
                	  itemDetail=NewTimes.toString();
                	  if(itemDetail.length()==1){
                		itemDetail="0"+itemDetail;
                	  }
                	  itemDetail=itemDetail+boughtDate+expireDate;
                	  System.out.println("New data to write to tag is: "+itemDetail);
                      byte[] dataAll=(itemDetail.length()+itemDetail).getBytes();
                      int remainLength=dataAll.length;
                      int index=0;
                      while(remainLength>0 ){
                    	  try{
      	                	Thread.sleep(50);
      	                	}catch (Exception e){	                	      
      	            	            e.printStackTrace();	            	        
      	                	}
                    	 if(remainLength>=4){
                    	 	for(index=0;index<4;index++){
                    			dataToWrite[index]=dataAll[dataAll.length-remainLength+3-index];
                    		}
                    	 } else{
                    		dataToWrite=new byte[]{(byte)0,(byte)0,(byte)0,(byte)0};
                    		for(index=0;index<remainLength;index++){
                    			dataToWrite[3-index]=dataAll[dataAll.length-remainLength+index];
                    		}
                    	 } 
                	    data=com.Write(itemName, dataToWrite,block,itemID.getBytes());
                	    remainLength-=4;
                	    
                	    block=block+1;
                      }
                    	if(chkErrorISO(data)==0){
                    		item.storeData(itemID, itemName, boughtDate, expireDate, getTime , NewTimes);
                    		System.out.println("Record success!");
                    	}else{
                    		System.out.println("Fail to write new data. Try again");
                    	}
                    }else{
                    	System.out.println("Read incorrect item infomation(date). Try again ");
                    }
                }else{
                	System.out.println("Haven't achieved correct itemName. Try again ");
                }   
		        }
	     }
		
	   
	  
	   public static String byteToString(byte data){
	         String cha;
	         Integer value=Integer.parseInt(Integer.toHexString(data & 0xff))-30;
	         cha=Integer.toString(value);
	         return cha;
	   }
	  
	   public static byte chkErrorISO(byte[] data){
		   byte error_code;
		    if (data[1] == 0x0a) {
		    	if ((data[5] & 0x10) != 0 ){ 
		       error_code = data[7];
		        /*error_meaning = {
		            "0x1" : "Transponder not found.",
		            "0x2" : "Command not supported.",
		            "0x4" : "Packet flags invalid for command.",
		            }.get(hex(rddat[7]), "Unknown error code.")*/
		         } 
		         else{
		         error_code = 0  ;
		         String error_meaning = "OK";
		         }
		    }
		    else if(data.length<10){
		    	error_code=100;
		    }
		    else{
		    	error_code = 0  ;
		         String error_meaning = "OK";
		    }
		    return error_code;
	   }
	   
	    
	    public static void main ( String[] args )
	    {   
	    	System.out.println("Input the port: ");
	    	Scanner terminalInput = new Scanner(System.in);
	    	String s = terminalInput.nextLine();  
	    	
	    try
	        {   
	            (new TwoWaySerialComm()).connect(s);
	        }
	        catch ( Exception e )
	        {
	            
	            e.printStackTrace();
	        }
	   // terminalInput.close();
	    }
}
