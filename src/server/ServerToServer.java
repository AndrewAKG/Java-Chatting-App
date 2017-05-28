package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import org.omg.CORBA.portable.OutputStream;


public class ServerToServer implements Runnable, Serializable{
	transient LinkedHashMap <String, Socket> ID = new LinkedHashMap<>();
	Socket connectionSocket = null;
	CopyOnWriteArrayList<Socket> servers = new CopyOnWriteArrayList<Socket>();
	
	public ServerToServer(LinkedHashMap<String, Socket> iD, Socket connectionSocket,CopyOnWriteArrayList<Socket> servers) {
		this.ID = iD;
		this.connectionSocket = connectionSocket;
		this.servers = servers;
	}
	public void memberListResponse() throws IOException, ClassNotFoundException{
		OutputStream s = (OutputStream) connectionSocket.getOutputStream();
		final ObjectOutputStream  outToClient =  new ObjectOutputStream(s);
		System.out.println(ID.toString());
		if(!ID.isEmpty()){
		/*for (Entry<String, Socket> entry : ID.entrySet()) {
		    String key = entry.getKey();
		    Socket value = entry.getValue();
		    outToClient.writeBytes(key +"," + value.getInetAddress().getHostAddress() + "," + value.getPort() + "," + value.getLocalPort() +  '\n');
		}*/
		outToClient.writeObject(ID);
		}else
			outToClient.writeBytes("empty" + '\n');
		//ObjectInputStream inFromServer =new ObjectInputStream(connectionSocket.getInputStream());
		
	}

	@Override
	public void run() {
		servers.add(connectionSocket);
		String serverSentence= null;
		BufferedReader inFromServer = null;
		
		try {
			while(!connectionSocket.isClosed()){
			inFromServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			serverSentence = inFromServer.readLine();
			System.out.println("ServerToServer: "+serverSentence);
			switch(serverSentence.substring(0, 5)){
			case "show:":try {
					memberListResponse();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
