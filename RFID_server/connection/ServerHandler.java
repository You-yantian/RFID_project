package connection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class ServerHandler {
	private Socket clientSocket;
	public ServerHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
	
}
