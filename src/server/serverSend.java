package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class serverSend implements Runnable{
	Socket primaryServer = null;
	String serverSentence = null;
	public serverSend(Socket primarySocket){
		this.primaryServer = primaryServer;
		
	}
	public serverSend(Socket primaryServer, String serverSentence){
		this.primaryServer = primaryServer;
		this.serverSentence = serverSentence; 
	}
	@Override
	public void run() {
		try {
			DataOutputStream outToServer = new DataOutputStream(primaryServer.getOutputStream());
			outToServer.writeBytes(serverSentence);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
