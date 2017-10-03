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
	                this.Function(in, out);
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
                else if(c.equals("TagDetail")){
                	data=com.TagDetail();
                	if ((data[5] & 0x10) != 0){

                	    /*error_meaning = {
                	        "0x1" : "Transponder not found.",
                	        "0x2" : "Command not supported.",
                	        "0x3" : "Packet checksum invalid.",
                	        "0x4" : "Packet flags invalid for command.",
                	        "0x5" : "General write failure.",
                	        "0x6" : "Write failure due to locked block.",
                	        "0x7" : "Transponder does not support function.",
                	        "0xf" : "Undefined error."
                	        }.get(hex(response[7]), "Unknown error code.")
        				*/
                	    System.out.println("Reader returned error code: " + (data[7]));
                   }
                }
                else{
                	System.out.println("Wrong command. Try again!");
                }
               
            }
            }
               
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
	    /** */
	  /*  public static class SerialReader implements Runnable 
	    {
	        InputStream in;
	        
	        public SerialReader ( InputStream in )
	        {
	            this.in = in;
	        }
	        
	        public void run ()
	        {
	            byte[] buffer = new byte[1024];
	            int len = -1;
	            try
	            {
	                while ( ( len = this.in.read(buffer)) > -1 )
	                {
	                    System.out.print(new String(buffer,0,len));
	                }
	            }
	            catch ( IOException e )
	            {
	                e.printStackTrace();
	            }            
	        }
	    }

	    /** */
	   /* public static class SerialWriter implements Runnable 
	    {
	        OutputStream out;
	        
	        public SerialWriter ( OutputStream out )
	        {
	            this.out = out;
	        }
	        
	        public void run ()
	        {
	            try
	            {                
	                String c=null;
	                System.out.println("Input the command: ");
	    	        Scanner commandInput = new Scanner(System.in);
	    			c = commandInput.nextLine();  
	    			byte[]command;
	    			//Command com=new Command(in,out);
	                while ( c !=null )
	                {
	                    if (c.equals("readVersion")){
	                    	
	                    	command=new byte[]{(byte)0x01, (byte)0x09, (byte)0, (byte)0,(byte) 0,(byte) 0, (byte)0xf0, (byte)0xf8,(byte)0x07};
	                    	 this.out.write(command);
	                    }
	                   
	                }
	                commandInput.close();                    
	            }
	            catch ( IOException e )
	            {
	                e.printStackTrace();
	            }     
	               
	        }
	    }*/
	    
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
