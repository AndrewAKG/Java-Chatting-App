package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
	transient Socket connectionSocket = null;
	transient LinkedHashMap <String, Socket> ID = new LinkedHashMap<>();
	transient CopyOnWriteArrayList<Socket> servers = new CopyOnWriteArrayList<>();
	transient Socket primaryServer = null;
	transient Thread t2;
	transient Thread t;
	public static void main(String[] args) throws IOException {
		Server s = new Server();
	}
	public Server() throws IOException{
		ServerSocket welcomeSocket = new ServerSocket(6000);
		primaryServer = new Socket("127.0.0.1", 5000);
		DataOutputStream ots = new DataOutputStream((primaryServer.getOutputStream()));
		ots.writeBytes("server" + '\n');
			serverToClient rFC2 = new serverToClient(primaryServer,ID, servers,true);
			t2 = new Thread(rFC2);
			t2.start();
		while(true){
		connectionSocket = welcomeSocket.accept();
		try {
			/*BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			if(inFromClient.readLine().equalsIgnoreCase("Client")){
				serverToClient rFC = new serverToClient(connectionSocket,ID, servers);
				Thread t = new Thread(rFC);
				t.start();
			}else{
				ServerToServer s = new ServerToServer(ID, connectionSocket, servers);
				Thread t = new Thread(s);
				t.start();
			}*/
			
				BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
				String clientSentence = inFromClient.readLine();
				if(clientSentence.equalsIgnoreCase("Server")){
					serverToClient rFC = new serverToClient(connectionSocket,ID, servers,true);
					t = new Thread(rFC);
					t.start();
				}else{
					serverToClient rFC = new serverToClient(connectionSocket,ID, servers,false);
					t = new Thread(rFC);
					t.start();
				}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
}