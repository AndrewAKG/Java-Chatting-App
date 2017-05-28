package server;



import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedHashMap;

public class RecieveFromClient implements Runnable {
	Socket connectionSocket = null;
	BufferedReader inFromServer= null;
	LinkedHashMap<String, Socket> ID = new LinkedHashMap<String,Socket>();
	String name;
	Socket primaryServer;
	public RecieveFromClient(Socket connectionSocket, LinkedHashMap<String,Socket> ID, Socket primaryServer){
		this.connectionSocket = connectionSocket;
		this.ID = ID;
		this.primaryServer = primaryServer;
	}
	
	
	public String JoinResponse(String name) throws IOException{
		if(ID.containsKey(name)){
			return "Name Already In Use";
		}else{
			this.name = name;
			ID.put(name,connectionSocket);
			
			return "You Joined Successfully!";
		}
	}

	public void memberListResponse(Socket connectionSocket) throws IOException{
		DataOutputStream  outToClient =  new DataOutputStream(connectionSocket.getOutputStream()); 
		serverSend s = new serverSend(primaryServer, "show:kolo");
		Thread t = new Thread(s);
		t.start();
		ObjectOutputStream outTopServer = new ObjectOutputStream(primaryServer.getOutputStream());
		if(!ID.isEmpty()){
			outToClient.writeBytes(ID.toString() + '\n');
		}else{
			outToClient.writeBytes("there is no one there "+ '\n');
		}
	}
	public void route(String Destination, String message, String name) throws IOException{
		if (ID.containsKey(Destination)){
			Socket currSocket = ID.get(Destination);
			DataOutputStream  outToClient =  new DataOutputStream(currSocket.getOutputStream());   
			outToClient.writeBytes(name + ": " + message + '\n');
			}else {
			if(!routeMultiServer(Destination, message,name)){
			DataOutputStream  outToClient =  new DataOutputStream(connectionSocket.getOutputStream()); 
			outToClient.writeBytes(" Wrong Destination "+ '\n');
			}
		}
	}


	private boolean routeMultiServer(String destination, String message, String name) throws IOException {
		serverSend s = new serverSend(primaryServer, "chat:" +destination + "," + message + ","+ name);
		Thread t = new Thread(s);
		t.start();
		 BufferedReader inFromServer =new BufferedReader(new InputStreamReader(primaryServer.getInputStream()));
		if(inFromServer.readLine().equalsIgnoreCase("true"))
			return true;
			else
				return false;
	}

	public String [] arr(String s ){
		String [] a = s.split(",");
		return a;
	}
	@Override
	public void run() {
		DataOutputStream outToC;
		try {
			outToC = new DataOutputStream(connectionSocket.getOutputStream());
			outToC.writeBytes("in" + '\n');
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		
		BufferedReader inFromClient;
		String clientSentence;
		try {
			inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
						while (!connectionSocket.isClosed()){
							clientSentence = inFromClient.readLine();
							switch(clientSentence.substring(0, 5)){
							case "Name:" : 
								DataOutputStream  outToClient =  new DataOutputStream(connectionSocket.getOutputStream());   
								outToClient.writeBytes(JoinResponse(clientSentence.substring(5)) + '\n');
								break;
							case "Show:":
								memberListResponse(connectionSocket); break;
							case "Chat:":
								String [] a= arr(clientSentence.substring(5));
								route(a[0], a[1],a[2]);break;
							case "Quit:":  DataOutputStream  outToClient1 =  new DataOutputStream(connectionSocket.getOutputStream());   
							               outToClient1.writeBytes("You Are Offline" + '\n');
							               ID.remove(name);
							               connectionSocket.close();


							default:
							} 
						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
	}




