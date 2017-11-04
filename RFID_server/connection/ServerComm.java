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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

public class ServerComm {
	 public ServerComm()
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
	                ServerSocket serverSocket = null;
	                try
	                {
	                    serverSocket = new ServerSocket(4444);
	                } catch (IOException e)
	                {
	                    System.err.println("Could not listen on port: 4444.");
	                    System.exit(1);
	                }


	                this.Function(in,out,serverSocket);

	                serialPort.close();
	            }
	            else
	            {
	                System.out.println("Error: Only serial ports are handled by this example.");
	            }
	        }

	    }


/////////////////////////////////
	public static void Function (InputStream in,OutputStream out,ServerSocket serverSocket ){
		Socket clientSocket=new Socket();

		//(new ServerHandler(clientSocket)).start();
		//ServerHandler server=new ServerHandler(clientSocket);

		BufferedReader commandInput = new BufferedReader(new InputStreamReader(System.in));
		String c=null;
		Message message=new Message();
		//String itemName=null;
		String itemDetail=null;
		//String boughtDate=null;
		//String expireDate=null;
		System.out.println("Input the command: ");

		while(true){
			try
			{
				clientSocket = serverSocket.accept();
			} catch (IOException e)
			{
				System.err.println("socket accept failed.");
				System.exit(1);
			}
			BufferedInputStream clientIn;
			BufferedOutputStream clientOut;

			try {
				clientIn = new BufferedInputStream(clientSocket.getInputStream());
				clientOut = new BufferedOutputStream(clientSocket.getOutputStream());
				byte[] msg = new byte[30];
				int bytesRead=0;
				bytesRead=clientIn.read(msg, bytesRead, 30);
				byte[] msgJudge = new byte[bytesRead];
				System.arraycopy(msg, 0, msgJudge, 0, bytesRead);
				// if(c==null){
				c=new String(msgJudge);
				// }
				Message toClient = new Message();
				byte[] data;

				if ( c !=null )
				{

					// Command com=new Command(in,out);
					if (c.equals("readVersion")){
						Thread readVersion = new Thread(new readVersion(in,out,clientOut));
						readVersion.start();

					}
					else if(c.equals("TagDetail")){
						Thread TagDetail = new Thread(new TagDetail(in,out,clientOut));
						TagDetail.start();
					}


					else if(c.equals("read")){
						Thread read = new Thread(new read(in,out,clientOut));
						read.start();
					}


					else if(c.equals("record")){
						Thread record = new Thread(new record(in,out,clientOut));
						record.start();
					}

					else {
						Thread write = new Thread(new write(in,out,clientOut,c));
						write.start();
					}


				}
				clientOut.flush();
				out.flush();
				in.close();

			} catch (IOException e) {
				System.out.println(e.toString());
			}
		}

	}

	////MultiThread Task///////
	public static class readVersion implements Runnable
    {
			private	Command com;
			private BufferedOutputStream clientOut;
			public readVersion ( InputStream in,OutputStream out, BufferedOutputStream clientOut)
			{
			this.com=new Command(in,out);
			this.clientOut=clientOut;
			}
			public void run ()
	        {
				byte[]data=com.readVersion();
	            System.out.println("The reader version is: "+Integer.toHexString((data[8]) & 0xFF)+"."+Integer.toHexString((data[7]) & 0xFF));
	            try{
	            clientOut.write(data);
	            clientOut.flush();
	            }catch(Exception e){
	            	e.printStackTrace();
	            }
	        }
    }

		public static class TagDetail implements Runnable
	     {
				private Command com;
				private BufferedOutputStream clientOut;
				public TagDetail ( InputStream in,OutputStream out,BufferedOutputStream clientOut)
				{
					this.com=new Command(in,out);
					this.clientOut=clientOut;

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
               			data="RFID tag not read.".getBytes();
               			System.out.println("RFID tag not read.");
               			Thread.interrupted();
               		}
               		try{

        	            clientOut.write(data);
        	            clientOut.flush();
        	            }catch(Exception e){
        	            	e.printStackTrace();
        	            }
               		//System.out.println(Command.bytesToHexString(data, data.length));
               	}
		        }
	     }

		public static class write implements Runnable
	     {
			private String itemDetail=null;
			public Database item=new Database();
			private	Command com;

			private BufferedOutputStream clientOut;
			private Message message;
			private String c;
			public write ( InputStream in,OutputStream out,BufferedOutputStream clientOut,String c )
				{
				this.com=new Command(in,out);
				this.clientOut=clientOut;
				message=new Message();
				this.c=c;
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


		               ///***************************///
		           	try {
		           		//Thread.sleep(100);
		           		System.out.println("The received message is "+c);
		           		int len_Name=Integer.parseInt(c.substring(0,1));
		           		message.itemName=c.substring(1,1+len_Name);
		           		int len_boughtDate=Integer.parseInt(c.substring(1+len_Name,1+len_Name+1));
		           		message.boughtDate=c.substring(1+len_Name+1,1+len_Name+1+len_boughtDate);
		           		int len_expireDate=Integer.parseInt(c.substring(1+len_Name+1+len_boughtDate,1+len_Name+1+len_boughtDate+1));
		           		message.expireDate=c.substring(1+len_Name+1+len_boughtDate+1);
		           		//System.out.println("Please enter the maximum times you want to consume this item with in one week: ");
		           		//MaxTime=commandInput.readLine();
			            } catch (Exception e) {
			                e.printStackTrace();
			            }
		           	  itemDetail="00"+message.boughtDate+message.expireDate;

		           	   System.out.println("The name of the item is: "+message.itemName+" and the item detail is: "+itemDetail);
		               byte[] dataAll=(itemDetail.length()+itemDetail).getBytes();
		               int remainLength=dataAll.length;
		               int index=0;

		               while(remainLength>0 ){
		               	if(remainLength>=4){
		               		/*try{
	        	                Thread.sleep(50);
	        	                }catch (Exception e){
	        	            	      e.printStackTrace();
	        	             }*/
		               		for(index=0;index<4;index++){
		               			dataToWrite[index]=dataAll[dataAll.length-remainLength+3-index];
		               		}
		               	} else{
		               		dataToWrite=new byte[]{(byte)0,(byte)0,(byte)0,(byte)0};
		               		for(index=0;index<remainLength;index++){
		               			dataToWrite[3-index]=dataAll[dataAll.length-remainLength+index];
		               		}
		               	}
		           	    data=com.Write(message.itemName, dataToWrite,block,itemID.getBytes());
		           	    remainLength-=4;

		           	    block=block+1;
		               }

		               //*******Store the initial data into database*******//
		               try{

		                if (!data.toString().equals("error")){
		               	    Database item=new Database();
		                	Calendar cal = Calendar.getInstance();
		                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		                    String getTime=sdf.format(cal.getTime());
		               	    item.storeData(itemID, message.itemName, message.boughtDate, message.expireDate,getTime,0);
		               	    clientOut.write("Write Success".getBytes());
		                 }else{
		                	clientOut.write("Write failed! Try again".getBytes());
		                 }
		               }catch (Exception e){
 	            	      e.printStackTrace();
		               }
		           	}else{
	           			Thread.interrupted();
	           			System.out.println(("RFID tag ID haven't read."));
	           			try{
	           			clientOut.write("RFID tag ID haven't read.".getBytes());
	           			}catch(IOException e){
	           				e.printStackTrace();
	           			}
	           		}
		           		try{
		           			clientOut.flush();
		           		}catch(IOException e){
	           				e.printStackTrace();
	           			}
		           	}
		        }

	     }

		public static class read implements Runnable
	     {
				private String itemDetail=null;
				public Database item=new Database();
				private	Command com;
				private BufferedOutputStream clientOut;
				private Message message;
				public read ( InputStream in,OutputStream out,BufferedOutputStream clientOut )
				{
				this.com=new Command(in,out);
				this.clientOut=clientOut;
				message=new Message();
				}
				public void run ()
		        {
	            	///*******get item ID******///
	            	boolean success=true;
					byte []data=com.TagDetail();
	            	String itemID=null;
	            	byte chkErr=chkErrorISO(data);
	            	if (chkErr!=0){

	            	    System.out.println("Reader returned error code: " + chkErr);
	            	    success=false;
	            	    Thread.interrupted();
	                }
	            	else if(data.toString().equals("error1")){
	            		 System.out.println("CheckSum Error");
	            		 success=false;
	            		 Thread.interrupted();
	            	}
	            	else if(data.toString().equals("error2")){
	           		  	System.out.println("Achieve Item ID fails. Enter the command again!");
	           		  	success=false;
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


	            	///*****get item name*******///
	            	message.itemName=item.searchItem(itemID);
	            	///*************************///
	                	int NoBlock=5;
	                	int startBlock=1;
	                	int idx=0;

	                	System.out.println("The name of the item is: "+message.itemName);
	                	/*try{
	                	Thread.sleep(100);
	                	}catch (Exception e){
	            	            e.printStackTrace();
	                	}*/
	                	data=com.Read(message.itemName,itemID.getBytes());
	                	chkErr=chkErrorISO(data);
	                	if (chkErr!=0){

	                	    System.out.println("Reader returned error code: " + chkErr);
	                	    success=false;
	                	    Thread.interrupted();
	                    }
	                	 if(data.toString().equals("error")){
	                  		 System.out.println("Read Item Detail fails. Enter the command again!");
	                  		 success=false;
	                  		 Thread.interrupted();
	               	    }else{
	               	    	int length=(Integer.parseInt(Integer.toHexString((data[12]) & 0xFF))-30)*10+(Integer.parseInt(Integer.toHexString((data[11]) & 0xFF))-30);
	                    	System.out.println("Data length is: "+length);

	                    	message.times=(Integer.parseInt(Integer.toHexString((data[10]) & 0xFF))-30)*10+(Integer.parseInt(Integer.toHexString((data[9]) & 0xFF))-30);
	                    	System.out.println("Achieve times of this item is: "+ message.times);

	                    	message.boughtDate=byteToString(data[17])+byteToString(data[16])+byteToString(data[15])+byteToString(data[14])
	                    				+byteToString(data[22])+byteToString(data[21])+byteToString(data[20])+byteToString(data[19]);
	                    	System.out.println("Bought date of this item is: "+ message.boughtDate);
	                    	message.expireDate=byteToString(data[27])+byteToString(data[26])+byteToString(data[25])+byteToString(data[24])
	        								+byteToString(data[32])+byteToString(data[31])+byteToString(data[30])+byteToString(data[29]);
	                    	System.out.println("Expire date of this item is: "+ message.expireDate);

	            	  }
	            	 }else{
            			System.out.println(("RFID tag not read."));
            			success=false;
            			Thread.interrupted();
            		}

	            	}
	            	try{
	            	if (success==true){
	            		String Iteminfo=message.itemName.length()+message.itemName;
	            		byte [] Info=Iteminfo.getBytes();
	            		byte [] fullData=new byte[Info.length+data.length];
	            		System.arraycopy(Info,0,fullData,0,Info.length);
	            		System.arraycopy(data,0,fullData,Info.length,data.length);
	            		clientOut.write(fullData);
                    	clientOut.flush();

	            	}else{
	            		clientOut.write("0Read Tag detail failed. Try command again".getBytes());
	            		clientOut.flush();
	            	}
	            	}catch(Exception e){
            			e.printStackTrace();
            		}

		        }
	     }

		public static class record implements Runnable
	     {
			private String itemDetail=null;
			public Database item=new Database();
			private	Command com;

			private BufferedOutputStream clientOut;
			private Message message;
				public record ( InputStream in,OutputStream out,BufferedOutputStream clientOut )
				{
					this.com=new Command(in,out);
					this.clientOut=clientOut;
					message=new Message();
				}
				public void run ()
		        {
					try{
						byte []data=com.TagDetail();
						String itemID=null;
						//Thread.sleep(100);
						byte chkErr=chkErrorISO(data);

						if (chkErr!=0){

							System.out.println("Reader returned error code: " + chkErr);
							if(chkErr==100){
								System.out.println("Length of data is less than 10: "+com.bytesToHexString(data, data.length));
							}
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

								message.itemName=item.searchItem(itemID);
								System.out.println("The item's name is: "+message.itemName);


								//**********read item infomation************//
								if (!message.itemName.equals("error")){
									// Thread.sleep(100);
									data=com.Read(message.itemName,itemID.getBytes());
									chkErr=chkErrorISO(data);
									if (chkErr!=0){

										System.out.println("Reader returned error code: " + chkErr);
										Thread.interrupted();
									}else{
										int length=(Integer.parseInt(Integer.toHexString((data[12]) & 0xFF))-30)*10+(Integer.parseInt(Integer.toHexString((data[11]) & 0xFF))-30);
										System.out.println("Data length is: "+length);

										int Times=(Integer.parseInt(Integer.toHexString((data[10]) & 0xFF))-30)*10+(Integer.parseInt(Integer.toHexString((data[9]) & 0xFF))-30);
										System.out.println("Achieve times of this item is: "+ Times);
										Integer NewTimes=Times+1;
										message.boughtDate=byteToString(data[17])+byteToString(data[16])+byteToString(data[15])+byteToString(data[14])
												+byteToString(data[22])+byteToString(data[21])+byteToString(data[20])+byteToString(data[19]);
										System.out.println("Bought date of this item is: "+ message.boughtDate);
										message.expireDate=byteToString(data[27])+byteToString(data[26])+byteToString(data[25])+byteToString(data[24])
												+byteToString(data[32])+byteToString(data[31])+byteToString(data[30])+byteToString(data[29]);
										System.out.println("Expire date of this item is: "+ message.expireDate);
										//System.out.println(Command.bytesToHexString(data, data.length));
										Calendar cal = Calendar.getInstance();
										SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
										String getTime=sdf.format(cal.getTime());


										//*******record and write new data to tag******//
										int block=1;
										byte[]dataToWrite=new byte[4];
										if (message.boughtDate.length()==8 && message.expireDate.length()==8){
											itemDetail=NewTimes.toString();
											if(itemDetail.length()==1){
												itemDetail="0"+itemDetail;
											}
											itemDetail=itemDetail+message.boughtDate+message.expireDate;
											System.out.println("New data to write to tag is: "+itemDetail);
											byte[] dataAll=(itemDetail.length()+itemDetail).getBytes();
											int remainLength=dataAll.length;
											int index=0;
											while(remainLength>0 ){
												//Thread.sleep(50);
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
												data=com.Write(message.itemName, dataToWrite,block,itemID.getBytes());
												remainLength-=4;
												block=block+1;
											}

											if(chkErrorISO(data)==0){
												item.storeData(itemID, message.itemName, message.boughtDate, message.expireDate, getTime , NewTimes);
												System.out.println("Record success!");
												clientOut.write("Record success!".getBytes());
											}else{
												System.out.println("Fail to write new data. Try again");
												clientOut.write("Fail to write new data. Try again".getBytes());
											}
										}else{
											clientOut.write("Read incorrect item infomation(date). Try again".getBytes());
											System.out.println("Read incorrect item infomation(date). Try again ");
										}
									}
								}else{
									clientOut.write("Haven't achieved correct itemName. Try again".getBytes());
									System.out.println("Haven't achieved correct itemName. Try again ");
								}
							}else{
								System.out.println("RFID tag ID haven't read.");
								clientOut.write("RFID tag ID haven't read.".getBytes());
								Thread.interrupted();
							}

						}
						clientOut.flush();

						} catch(Exception e){
							e.printStackTrace();
						}
		        }
	     		}


	   public static String byteToString(byte data){
	         String cha;
	         Integer value=Integer.parseInt(Integer.toHexString(data & (byte)0xff))-30;
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
	            (new ServerComm()).connect(s);
	        }
	        catch ( Exception e )
	        {

	            e.printStackTrace();
	        }
	   // terminalInput.close();
	    }
}
