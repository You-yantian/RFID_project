package connection;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
public class TwoWaySerialComm {
	 public TwoWaySerialComm()
	    {
	        super();
	    }
	    
	    void connect ( String portName ) throws Exception
	    {
	        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
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
	                this.readVersion(in,out);
	                
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
	    
	    public void readVersion(InputStream in,OutputStream out){
	    	byte[]command=new byte[]{(byte)0x01, (byte)0x09, (byte)0, (byte)0,(byte) 0,(byte) 0, (byte)0xf0, (byte)0xf8,(byte)0x07};
            //System.out.println(bytesToHexString(command,command.length));
            
            byte[] buffer=new byte[20];
            
            int len=-1;
            try
            {	
            	 out.write(command);
                 len = in.read(buffer,0,1) ;
                 String SOF=(bytesToHexString(buffer,len));
                 len = in.read(buffer,1,1) ;
                 int length_data = buffer[1] & 0xFF; 
                 String data=bytesToHexString(buffer,len);
                 len = in.read(buffer,2,length_data-2) ; 
                 
                 System.out.println("The reader version is: "+Integer.toHexString((buffer[8]) & 0xFF)+"."+Integer.toHexString((buffer[7]) & 0xFF));
                 //System.out.println(bytesToHexString(buffer,length_data));
                 //System.out.println(length_data);
                
                 //System.out.println(len);
                	
                
                
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }            
            
	    }
	    
	    public static String bytesToHexString(byte[] src,int length){  
	        StringBuilder stringBuilder = new StringBuilder("");  
	        if (src == null || length <= 0) {  
	            return null;  
	        }  
	        for (int i = 0; i < length; i++) {  
	            int v = src[i] & 0xFF;  
	            String hv = Integer.toHexString(v);  
	            if (hv.length() < 2) {  
	                stringBuilder.append(0);  
	            }  
	            stringBuilder.append(hv);  
	        }  
	        return stringBuilder.toString();  
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
	                int c = 0;
	                while ( ( c = System.in.wirte()) > -1 )
	                {
	                    this.out.write(c);
	                }                
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
	    	terminalInput.close();
	    try
	        {   
	            (new TwoWaySerialComm()).connect(s);
	        }
	        catch ( Exception e )
	        {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	    }
}
