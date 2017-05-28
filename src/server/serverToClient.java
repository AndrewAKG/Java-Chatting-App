package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import client.Client_TCP;

public class serverToClient implements Runnable, Serializable {
	 LinkedHashMap <String, Socket> ID = new LinkedHashMap<>();
	 CopyOnWriteArrayList<Socket> servers = new CopyOnWriteArrayList<>();
	 Socket connectionSocket = null;
	 String name = null;
	 Socket primaryServer = null;
	boolean server;
	DataOutputStream oTC;
	public serverToClient(Socket connectionSocket, LinkedHashMap <String , Socket> ID, CopyOnWriteArrayList<Socket> servers, boolean server) throws IOException{
		this.connectionSocket = connectionSocket;
		this.ID = ID;
		this.servers = servers;
		this.oTC = new DataOutputStream(connectionSocket.getOutputStream());
		this.server = server;
	}

	public String JoinResponse(String name) throws IOException{
		//getAllMembers();
		if(ID.containsKey(name)){
			return "Name Already In Use";
		}else{
			this.name = name;
			ID.put(name,connectionSocket);
			return "You Joined Successfully!";
		}
	}

	public ArrayList<String> getAllMembers () throws IOException, ClassNotFoundException{
		System.out.println("ingetAll Members");
		ArrayList<String> all = new ArrayList<>();
		for(int i =0;i<= servers.size()-1;i++){
			System.out.println(servers.toString());
		serverSend s = new serverSend(servers.get(i), "Show:within" + '\n');
		Thread t = new Thread(s);
		t.start();
		    BufferedReader in = new BufferedReader(new InputStreamReader(servers.get(i).getInputStream()));
		    String arr [] = getClients(in.readLine());
		    for(int j = 0; j<= arr.length-1; j++){
		    	all.add(arr[i]);
		    }
		    System.out.println("62 : "+all.toString());
	}
		return all;
}
	private String[] getClients(String in) {
		String in2 = in.substring(1,in.length());
		String arr[] = in2.split(",");
		return arr;
	}

	/*public void getAllMembers () throws IOException{
		System.out.println("insideservers");
		for(int i =0;i<= servers.size()-1;i++){
			System.out.println(servers.toString());
		serverSend s = new serverSend(servers.get(i), "show:kolo" + '\n');
		Thread t = new Thread(s);
		t.start();
		BufferedReader inFromServer =new BufferedReader(new InputStreamReader(servers.get(i).getInputStream()));		
		String in = inFromServer.readLine();
		if(!in.equalsIgnoreCase("empty")){
			System.out.println("not Empty");
			String arr [] = in.split(",");
			//Socket Sock = new Socket(arr[1],Integer.parseInt(arr[2]),null, Integer.parseInt(arr[3]));
			ID.put(arr[0], null);
			while(!inFromServer.readLine().equalsIgnoreCase("done")){
				arr= in.split(",");
				//Sock = new Socket(arr[1],Integer.parseInt(arr[2]),null, Integer.parseInt(arr[3]));
				ID.put(arr[0], null);
				in = inFromServer.readLine();
			}
		}
		
	}
	}*/
	public void memberListResponse(Socket connectionSocket, boolean within) throws IOException, ClassNotFoundException{
		System.out.println(within);
		ArrayList<String> Names = new ArrayList<>();
		ArrayList<String> all = new ArrayList<>();
		if(!ID.isEmpty()){
			for (Entry<String, Socket> entry : ID.entrySet()) {
			    String key = entry.getKey();
			    Names.add(key);
			}
		}
			if(within){
				System.out.println(Names.toString());
				if(!Names.isEmpty()){
				oTC.writeBytes(Names.toString() + '\n');
				}else{
					oTC.writeBytes("there is no one there "+ '\n');
				}
			}else{
				if(!servers.isEmpty()){
				all = getAllMembers();
				all.addAll(Names);
				if(!all.isEmpty()){
					System.out.println(all.toString());
					oTC.writeBytes(all.toString() + '\n');
				}else{
					oTC.writeBytes("there is no one there "+ '\n');
				}
				}
			}
				

	
}
	public void route(String Destination, String message, String name) throws IOException{
		System.out.println("in route");
		if (ID.containsKey(Destination)){
			
			Socket currSocket = ID.get(Destination);
			if(currSocket == this.connectionSocket)
				oTC.writeBytes(name + ": " + message + '\n');
			else{
			DataOutputStream oTC1 = new DataOutputStream(currSocket.getOutputStream());
			//oTC =  new ObjectOutputStream(currSocket.getOutputStream());
			oTC1.writeBytes(name + ": " + message + '\n');}
			}else {
			if(!routeMultiServer(Destination, message,name)){
			//ObjectOutputStream  oTC =  new ObjectOutputStream(connectionSocket.getOutputStream()); 
			oTC.writeBytes(" Wrong Destination "+ '\n');
			}
		}
	}


	private boolean routeMultiServer(String destination, String message, String name) throws IOException {
		for(int i =0;i<= servers.size()-1;i++){
		serverSend s = new serverSend(servers.get(i), "chat:" +destination + "," + message + ","+ name);
		Thread t = new Thread(s);
		t.start();
		 BufferedReader inFromServer =new BufferedReader(new InputStreamReader(servers.get(i).getInputStream()));
			if(inFromServer.readLine().equalsIgnoreCase("true"))
				return true;
		}
		return false;
		
	}

	public String [] arr(String s ){
		String [] a = s.split(",");
		return a;
	}


	@Override
	public void run() {
		DataOutputStream outToC;
		if(server==true){
			servers.add(connectionSocket);
			
		}
		
		BufferedReader inFromClient;
		String clientSentence;
		try {
			inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
						while (!connectionSocket.isClosed()){
							clientSentence = inFromClient.readLine();
							System.out.println("182: " + clientSentence);
							
								switch(clientSentence.substring(0, 5)){
								case "Name:" : 
									oTC.writeBytes(JoinResponse(clientSentence.substring(5)) + '\n');
									break;
								case "Show:":
									try {
										String a = clientSentence.substring(5);
										System.out.println(a);
										if(a.trim().equalsIgnoreCase("within")){
											memberListResponse(connectionSocket,true);
										}else{
											memberListResponse(connectionSocket,false);

										}
									} catch (ClassNotFoundException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} break;
								case "Chat:":
									String [] a= arr(clientSentence.substring(5));
									route(a[0], a[1],a[2]);
									break;
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

