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
	                this.Function(in,out);
	                // Command com=new Command(in,out);
	                
	                //data=com.readVersion();
	               // System.out.println("The reader version is: "+Integer.toHexString((data[8]) & 0xFF)+"."+Integer.toHexString((data[7]) & 0xFF));
	                
	                //this.readVersion(in,out);
	                
	                //byte []line_size=buffer[];
	                //if (line_size.length<2){
	                	
	                //}
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
            	
            	 Command com=new Command(in,out);
                if (c.equals("readVersion")){
                	data=com.readVersion();
 	                System.out.println("The reader version is: "+Integer.toHexString((data[8]) & 0xFF)+"."+Integer.toHexString((data[7]) & 0xFF));
 	                
                	
                }
                else if(c.equals("TagDetail")){
                	data=com.TagDetail();
                	byte chkErr=chkErrorISO(data);
                	if (chkErr!=0){
                	   
                	    System.out.println("Reader returned error code: " + chkErr);
                	    System.exit(1);
                    }
                	else if(data.toString().equals("error")){
                		 System.out.println("CheckSum Error");
                		 System.exit(1);
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
                
                else if(c.equals("write")){
                	int block=1;
                	byte[]dataToWrite=new byte[4];
                	System.out.println("Please enter the item's name: ");
                	try {
                		itemName = commandInput.readLine();
                    
    	            } catch (IOException e) {
    	                e.printStackTrace();
    	            }               	
                	System.out.println("Please enter the detail: ");
                	try {
                		itemDetail= commandInput.readLine();
                    
    	            } catch (IOException e) {
    	                e.printStackTrace();
    	            } 
                	System.out.println("The name of the item is: "+itemName+" and the item detail is: "+itemDetail);
                    byte[] dataAll=(itemDetail.length()+itemDetail).getBytes();
                    int remainLength=dataAll.length;
                    int index=0;
                    while(remainLength>0 ){
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
                	    data=com.Write(itemName, dataToWrite,block);
                	    remainLength-=4;
                	    
                	    block=block+1;
                    }
                }
               
                else if(c.equals("writeSingal")){
                	String Block="0";
                	System.out.println("Please enter the item's name: ");
                	try {
                		itemName = commandInput.readLine();
                    
    	            } catch (IOException e) {
    	                e.printStackTrace();
    	            }               	
                	System.out.println("Please enter the detail: ");
                	try {
                		itemDetail= commandInput.readLine();
                    
    	            } catch (IOException e) {
    	                e.printStackTrace();
    	            } 
                	System.out.println("Please enter the block Number: ");
                	try {
                		Block= commandInput.readLine();
                    
    	            } catch (IOException e) {
    	                e.printStackTrace();
    	            } 
                	System.out.println("The name of the item is: "+itemName+" and the item detail is: "+itemDetail);
                	data=com.WriteSingal(itemName, itemDetail,Block);
                }
                else if(c.equals("read")){
                	System.out.println("Please enter the item's name: ");
                	try {
                		itemName = commandInput.readLine();
                    
    	            } catch (IOException e) {
    	                e.printStackTrace();
    	            }    
                	System.out.println("The name of the item is: "+itemName);
                	data=com.Read(itemName);
                	byte chkErr=chkErrorISO(data);
                	if (chkErr!=0){
                	   
                	    System.out.println("Reader returned error code: " + chkErr);
                    }
                	System.out.println(Command.bytesToHexString(data, data.length));
                	
                }
                
                else{
                	System.out.println("Wrong command. Try again!");
                }
               
            }
            /*try{
            	
            	out.flush();
            }catch (IOException e) {
                e.printStackTrace();
            } */  
            
            }
               
	    }
	   
	   public static byte chkErrorISO(byte[] data){
		   byte error_code;
		    if ((data[1] == 0x0a) & (data[5] & 0x10) != 0 ){ 
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
		    return error_code;
	   }
	    /*public static class Function implements Runnable 
	    {
	    	InputStream in;
	    	OutputStream out;
	    	public Function ( InputStream in,OutputStream out )
	        {
	            this.in = in;
	            this.out = out;
	        }
	    	public void run ()
	        {
	    		       BufferedReader commandInput = new BufferedReader(new InputStreamReader(System.in));     
            String c=null;
            Command com=new Command(in,out);
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
                if (c.equals("readVersion")){
                	data=com.readVersion();
 	                System.out.println("The reader version is: "+Integer.toHexString((data[8]) & 0xFF)+"."+Integer.toHexString((data[7]) & 0xFF));
 	                
                	
                }
                else{
                	System.out.println("Wrong command. Try again!");
                }
               
            }
            }
		    	        
		                //commandInput.close();                    
		            		              
	        }
	    }
	    */
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
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	   // terminalInput.close();
	    }
}
