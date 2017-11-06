package application;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;


public class ServerConnection {
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private int lengthOfWord;
    public ServerConnection() {
        String host="Localhost";
        try {
            Socket clientSocket = new Socket(host, 4444);
            in = new BufferedInputStream(clientSocket.getInputStream());
            out = new BufferedOutputStream(clientSocket.getOutputStream());
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + host + ".");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: "
                               + host + ".");
            System.exit(1);
        }
    }

    String StartServer() throws IOException {
        String info="fail";
    	String toServer="readVersion";
        byte[] fromServer = new byte[20];
        byte[] msg = toServer.getBytes();
        out.write(msg, 0, msg.length);
        out.flush();
        int n = in.read(fromServer, 0, 20);
        info="The reader version is: "+Integer.toHexString((fromServer[8]) & 0xFF)+"."+Integer.toHexString((fromServer[7]) & 0xFF);

        return info;
    }

    String TagDetail() throws IOException {
        String info="fail";
    	String toServer="TagDetail";
        byte[] data = new byte[30];
        byte[] msg = toServer.getBytes();
        out.write(msg, 0, msg.length);
        out.flush();
        int n = in.read(data, 0, 30);
        info="Transponder ID: 0x"
				+ String.format("%2s",Integer.toHexString((data[20]) & 0xFF)).replace(' ','0') + String.format("%2s",Integer.toHexString((data[19]) & 0xFF)).replace(' ','0')
				+ String.format("%2s",Integer.toHexString((data[18]) & 0xFF)).replace(' ','0') + String.format("%2s",Integer.toHexString((data[17]) & 0xFF)).replace(' ','0')
				//+ Integer.toHexString((data[18]) & 0xFF) + Integer.toHexString((data[17]) & 0xFF)
				+ String.format("%2s",Integer.toHexString((data[16]) & 0xFF)).replace(' ','0') + String.format("%2s",Integer.toHexString((data[15]) & 0xFF)).replace(' ','0')
				+ String.format("%2s",Integer.toHexString((data[14]) & 0xFF)).replace(' ','0') + String.format("%2s",Integer.toHexString((data[13]) & 0xFF)).replace(' ','0');

        return info;
    }

    Item read() throws IOException {
    	Item info=new Item();
    	String toServer="read";
        byte[] fullData = new byte[50];
        byte[] data=new byte[50];
        byte[] itemName=new byte[10];
        byte[] msg = toServer.getBytes();
        out.write(msg, 0, msg.length);
        out.flush();
        try{
        Thread.sleep(300);
        }catch(Exception e){
        	e.printStackTrace();
        }
        int len = in.read(fullData,0,50);
        int length_data = fullData[0]-48;
        if(length_data==0){
        	info.itemName="Read Tag detail failed. Try command again";
        }else{
        System.arraycopy(fullData,1,itemName,0,length_data);
        System.out.println("The Length of item Name is: "+length_data);
        System.arraycopy(fullData,length_data+1,data,0,len-length_data-1);
        info.itemName=new String(itemName);
        info.times=(Integer.parseInt(Integer.toHexString((data[10]) & 0xFF))-30)*10+(Integer.parseInt(Integer.toHexString((data[9]) & 0xFF))-30);

    	info.boughtDate=byteToString(data[17])+byteToString(data[16])+byteToString(data[15])+byteToString(data[14])
    				+byteToString(data[22])+byteToString(data[21])+byteToString(data[20])+byteToString(data[19]);

    	info.expireDate=byteToString(data[27])+byteToString(data[26])+byteToString(data[25])+byteToString(data[24])
						+byteToString(data[32])+byteToString(data[31])+byteToString(data[30])+byteToString(data[29]);

    	System.out.println("The Name of this item is: "+info.itemName);
    	System.out.println("Bought date of this item is: "+ info.boughtDate);
    	System.out.println("Achieve times of this item is: "+ info.times);
    	System.out.println("Expire date of this item is: "+ info.expireDate);
        }
        return info;
    }


    String record() throws IOException {
    	String info;
    	String toServer="record";
        byte[] data = new byte[50];
        byte[] msg = toServer.getBytes();
        out.write(msg, 0, msg.length);
        out.flush();
        int n = in.read(data, 0, 50);
        info=new String(data);
        return info;
    }
    String write(String toServer) throws IOException {
    	String info;
        byte[] data = new byte[30];
        byte[] msg = toServer.getBytes();
        out.write(msg, 0, msg.length);
        out.flush();
        int n = in.read(data, 0, 50);
        info=new String(data);
        return info;
    }


    public static String byteToString(byte data){
        String cha;
        Integer value=Integer.parseInt(Integer.toHexString(data & 0xff))-30;
        cha=Integer.toString(value);
        return cha;
  }
}
