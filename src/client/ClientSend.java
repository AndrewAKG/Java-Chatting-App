package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientSend implements Runnable{
	Socket serverSocket = null;
	String clientSentence = null;
	public ClientSend(Socket serverSocket){
		this.serverSocket = serverSocket;
		
	}
	public ClientSend(Socket serverSocket, String clientSentence){
		this.serverSocket = serverSocket;
		this.clientSentence = clientSentence; 
	}
	@Override
	public void run() {
		try {
			DataOutputStream outToServer = new DataOutputStream(serverSocket.getOutputStream());
			outToServer.writeBytes(clientSentence);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
