package connection;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
public class Command {
	InputStream in;
    OutputStream out;
	public Command(InputStream in,OutputStream out)
    {
       this.in=in;
       this.out=out;
    }
    
	 public byte[] readVersion(){
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
              
              //System.out.println("The reader version is: "+Integer.toHexString((buffer[8]) & 0xFF)+"."+Integer.toHexString((buffer[7]) & 0xFF));
              //System.out.println(bytesToHexString(buffer,length_data));
              //System.out.println(length_data);
             
              //System.out.println(len);
             	                    
         }
         catch ( IOException e )
         {
             e.printStackTrace();
         }            
         return buffer;
	    }
	 
	  public byte[] TagDetail(){
	    	byte[]command=new byte[]{(byte)0x01, (byte)0x09, (byte)0, (byte)0,(byte) 0,(byte) 0, (byte)0x05, (byte)0x0D,(byte)0xF2};
      //System.out.println(bytesToHexString(command,command.length));
      
      byte[] buffer=new byte[30];
      
      int len=-1;
      try
      {	
      	   out.write(command);
           len = in.read(buffer,0,1) ;
           String SOF=(bytesToHexString(buffer,len));
           len = in.read(buffer,1,1) ;
           int length_data = buffer[1] & 0xFF; 
           
           len = in.read(buffer,2,length_data-2) ; 
           String data=bytesToHexString(buffer,length_data);
           System.out.println("The data is: "+data);
           System.out.println(length_data);
           

           //System.out.println(len);
          	                    
      }
      catch ( IOException e )
      {
          e.printStackTrace();
      }            
      return buffer;
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
}
