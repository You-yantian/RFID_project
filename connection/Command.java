package connection;

//import gnu.io.CommPort;
//import gnu.io.CommPortIdentifier;
//import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import java.nio.ByteBuffer;
public class Command {
	InputStream in;
    OutputStream out;
	public Command(InputStream in,OutputStream out)
    {
       this.in=in;
       this.out=out;
    }
	
	//**********************Reader Detail************************//
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
              out.flush();
              in.close();
              //in.reset();
              //System.out.println("The reader version is: "+Integer.toHexString((buffer[8]) & 0xFF)+"."+Integer.toHexString((buffer[7]) & 0xFF));
              //System.out.println(bytesToHexString(buffer,length_data));
              //System.out.println(length_data);
             
              //System.out.println(len);
             	                    
         }
         catch ( IOException e )
         {
             e.printStackTrace();
             System.exit(1);
         }            
         return buffer;
	    }
	 
	 //**********************Tag Detail************************//
	  public byte[] TagDetail(){
		  ByteBuffer command_detail=ByteBuffer.allocate(13);
		  // 0: SOF
		  // 1 & 2: length LSB and MSB respectively, filled in later
		  // 3 & 4: TI reader address fields, alsways set to 0
		  // 5: TI reader command flags
		  // 6: TI reader ISO pass thru command, always 0x60
          command_detail.put(new byte[]{(byte)0x01,(byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0x60});
          //7: ISO reader config byte 0.The value in this case is 0x11
          // 8: Tag flags.  In this case indicating 1 time slot (0x27)
          // 9: The ISO command.  In this case 0x01
          // 10: The mask length for doing the inventory.  In this case it is 0
          command_detail.put(new byte[]{(byte)0x11,(byte) 0x27, (byte)0x01, (byte)0});
          //the two checksum bytes
          command_detail.put(new byte[]{(byte)0,(byte)0});
          
	      byte[]command=command_detail.array();
	      Integer command_len = command.length;
	      command[1]=command_len.byteValue();
          //System.out.println(bytesToHexString(command,command_len));
          byte chksum=0;
          int idx=0;
          while(idx<(command_len-2)){
        	  chksum^=command[idx];
        	  idx+=1;
          }
          command[command_len-2]=chksum;
          command[command_len - 1] = new Integer(chksum ^  0xFF).byteValue();
          //System.out.println("new command string is: "+bytesToHexString(command,command_len));

      byte[] buffer_tag=new byte[30];
      int length_data=0;
      int len=-1;
      try
      {	
      	   out.write(command);
           len = in.read(buffer_tag,0,1) ;
           String SOF=(bytesToHexString(buffer_tag,len));
           while(!SOF.equals("01")){
        	   len = in.read(buffer_tag,0,1) ;
               SOF=(bytesToHexString(buffer_tag,len));
           }
           len = in.read(buffer_tag,1,1) ; 
           length_data = buffer_tag[1] & 0xFF;
           System.out.println("The data length of read Tag Detailis: "+buffer_tag[1]);                                      	    
           if (buffer_tag[1]>2){
           len = in.read(buffer_tag,2,length_data-2) ; 
           String data=bytesToHexString(buffer_tag,length_data);
           }
           out.flush();
           in.close();
           //System.out.println("The data is: "+data);
           //System.out.println(length_data);	                    
      }
      catch ( IOException e )
      {
          e.printStackTrace();
          System.exit(1);
      }            
      if(length_data>2){
         chksum = 0;
         idx = 0;
    	 while (idx < (length_data-2)){
    		      chksum ^= buffer_tag[idx];
    		      idx += 1;
    	 }
    	 if (chksum != (buffer_tag[length_data - 2])){  
    		System.out.println("Checksum error!");
    		return "error".getBytes();
    	 }
    	 else 
    		 return buffer_tag;
      }
      else
    	  return "error".getBytes();
	  }
	  
	  
	//***********************Read Item Detail****************************//
	  public byte[] Read(String itemName){
		  byte[] buffer=new byte[50];
		  byte[] UID;
		  switch(itemName){
		  case "milk":
			  UID=new byte[]{(byte)0xE0,(byte)0x07,(byte)0x00,(byte)0x00,(byte)0x1F,(byte)0x90,(byte)0x84,(byte)0x3D};
			  break;
		  case "egg":
			  UID=new byte[]{(byte)0xE0,(byte)0x07,(byte)0x00,(byte)0x00,(byte)0x1F,(byte)0x90,(byte)0x84,(byte)0x39};
			  break;
		  case "carrot" :
			  UID=new byte[]{(byte)0xE0,(byte)0x07,(byte)0x00,(byte)0x00,(byte)0x1F,(byte)0x90,(byte)0x84,(byte)0x38};
			  break;
		  default:
			  UID=null;
			  break;
		  }
		  ByteBuffer command_detail=ByteBuffer.allocate(22);
		  // 0: SOF
		  // 1 & 2: length LSB and MSB respectively, filled in later
		  // 3 & 4: TI reader address fields, alsways set to 0
		  // 5: TI reader command flags
		  // 6: TI reader ISO pass thru command, always 0x60
          command_detail.put(new byte[]{(byte)0x01,(byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0x60});
          // 7: ISO reader config byte 0.The value in this case is 0x11
          // 8: Tag flags. Option flag must be set in this command. o_f=1, s_f=0, a_f=1
          // 9: The ISO command.  In this case 0x23
          command_detail.put(new byte[]{(byte)0x11,(byte) 0x63, (byte)0x23});
          //8 bytes for the UID, 1 byte each for the starting block and number of blocks.
          command_detail.put(new byte[]{0,0,0,0,0,0,0,0,0,0});
          //the two checksum bytes
          command_detail.put(new byte[]{0,0});
          byte[]command=command_detail.array();
          
          //****fill in the data command length******
	      Integer command_len = command.length;
	      command[1]=command_len.byteValue();
	      byte chksum=0;
          int idx=0;
        //****fill in the UID******
          if(UID!=null){
          for(idx=1;idx<9;idx++){
        	  command[9+idx]=UID[8-idx];
          }
          }else{
        	  System.out.println("Wrong item name!");
			  System.exit(1);
          }
          //****fill in the Block number******
          command[18]=(byte)0x01;
        //****fill in the read number of Blocks******
          //the command field for "number of blocks" is always set to the number of blocks requested minus 1.
          command[19]=(byte)0x04;
        //****fill in the checksum******
          idx=0;
          while(idx<(command_len-2)){
        	  chksum^=command[idx];
        	  idx+=1;
          }
          command[command_len-2]=chksum;
          command[command_len - 1] = new Integer(chksum ^  0xFF).byteValue();
          
          System.out.println("new command string is: "+bytesToHexString(command,command_len));
          int length_data=0;
          int len=-1;
          //int start=0;
          //byte[]temp=new byte[10];
          try
          {	
          	   buffer=null;
          	   buffer=new byte[50];
        	   out.write(command);
               len = in.read(buffer,0,1) ;
               String SOF=(bytesToHexString(buffer,len));
               while(!SOF.equals("01")){
            	   len = in.read(buffer,0,1) ;
                   SOF=(bytesToHexString(buffer,len));
               }
               len = in.read(buffer,1,1) ; 
               length_data = buffer[1] & 0xFF;
               System.out.println("The data length is: "+buffer[1]);
               if (buffer[1]>2){                                 	              
                 len = in.read(buffer,2,length_data-2) ;               
                 String data=bytesToHexString(buffer,length_data);
                 
                 System.out.println("The data is: "+data);
               
               //System.out.println(length_data);	           
                 
               }
               out.flush();
               in.close();
          }
          catch ( IOException e )
          {
              e.printStackTrace();
              System.exit(1);
          }           
		  return buffer;
	  }
	  
	  
	//***********************Write Item Detail****************************//
	  public byte[] Write(String itemName, byte[] dataToWrite,int block){
		  byte[] UID;
		  switch(itemName){
		  case "milk":
			  UID=new byte[]{(byte)0xE0,(byte)0x07,(byte)0x00,(byte)0x00,(byte)0x1F,(byte)0x90,(byte)0x84,(byte)0x3D};
			  break;
		  case "egg":
			  UID=new byte[]{(byte)0xE0,(byte)0x07,(byte)0x00,(byte)0x00,(byte)0x1F,(byte)0x90,(byte)0x84,(byte)0x39};
			  break;
		  case "carrot" :
			  UID=new byte[]{(byte)0xE0,(byte)0x07,(byte)0x00,(byte)0x00,(byte)0x1F,(byte)0x90,(byte)0x84,(byte)0x38};
			  break;
		  default:
			  UID=null;
			  break;
		  }
		  
		  ByteBuffer command_detail=ByteBuffer.allocate(25);
		  // 0: SOF
		  // 1 & 2: length LSB and MSB respectively, filled in later
		  // 3 & 4: TI reader address fields, alsways set to 0
		  // 5: TI reader command flags
		  // 6: TI reader ISO pass thru command, always 0x60
          command_detail.put(new byte[]{(byte)0x01,(byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0x60});
          // 7: ISO reader config byte 0.The value in this case is 0x11
          // 8: Tag flags. Option flag must be set in this command. o_f=1, s_f=0, a_f=1
          // 9: The ISO command.  In this case 0x21
          command_detail.put(new byte[]{(byte)0x11,(byte) 0x63, (byte)0x21});
          //8 bytes for the UID, 1 byte for the block number and 4 bytes for the block data.
          command_detail.put(new byte[]{0,0,0,0,0,0,0,0,0,0,0,0,0});
          //the two checksum bytes
          command_detail.put(new byte[]{0,0});
          byte[]command=command_detail.array();
          
          
          //****fill in the data command length******
	      Integer command_len = command.length;
	      command[1]=command_len.byteValue();
	      byte chksum=0;
          int idx=0;
          //****fill in the UID******
          if(UID!=null){
        	  for(idx=1;idx<9;idx++){
            	  command[9+idx]=UID[8-idx];
              }
          }else{
        	  System.out.println("Wrong item name!");
			  System.exit(1);
          }
     	  //****fill in the Block number******
          command[18]=(byte)(block);
          
          //****fill in the Block Data******
           System.out.println("the length of data to write is "+dataToWrite.length);
           System.out.println("the content of data to write is "+bytesToHexString(dataToWrite,dataToWrite.length));
           byte result=0;
           byte[] buffer_write=new byte[80];
           int length_data=0;
           int len=-1;
           for(idx=0;idx<4;idx++){
         	   command[19+idx]=dataToWrite[idx];
             }
               
        	   //****fill in the checksum******
               idx=0;
               while(idx<(command_len-2)){
             	  chksum^=command[idx];
             	  idx+=1;
               }
               command[command_len-2]=chksum;
               command[command_len-1] = new Integer(chksum ^  0xFF).byteValue();
               
               
               //**********write to Tag***********               
               try
               {	
            	    System.out.println("new command string is: "+bytesToHexString(command,command_len));
            	    buffer_write=new byte[80];
            	    out.flush();
            	    out.write(command);
                    len = in.read(buffer_write,0,1);
                    String SOF=(bytesToHexString(buffer_write,len));
                    /*while(!SOF.equals("01")){
                  	   len = in.read(buffer_write,0,1) ;
                         SOF=(bytesToHexString(buffer_write,len));
                     }*/
                    len = in.read(buffer_write,1,1) ;
                    length_data = buffer_write[1] & 0xFF;
                    if(buffer_write[1]>2){
                    len = in.read(buffer_write,2,length_data-2) ; 
                    String data=bytesToHexString(buffer_write,length_data);
                    System.out.println("The returned data is: "+data);
                    System.out.println("The length of returned data is: "+length_data);	
                    }else{
                    	System.out.println("Data return failed. The returned SOF is: "+SOF+" The returned length is: "+buffer_write[1]);
                    }
            	    
                    /*if ((buffer.length == 10) & (buffer[5] & 0x10) != 0 ){ 
         		       result = buffer[7];
         		       System.out.println("Error occur. The error code is: "+result);
         		        //error_meaning = {
         		          //  "0x1" : "Transponder not found.",
         		          // "0x2" : "Command not supported.",
         		          //  "0x4" : "Packet flags invalid for command.",
         		          //  }.get(hex(rddat[7]), "Unknown error code.")
         		    }
         		    else{
         		        result = 0  ;
         		        //String error_meaning = "OK";
         		    }*/
               }
               catch ( IOException e )
               {
                   e.printStackTrace();
                   System.exit(1);
               }   

           
         
		  return buffer_write;
	  }
	//***********************Show Hex String****************************//
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
