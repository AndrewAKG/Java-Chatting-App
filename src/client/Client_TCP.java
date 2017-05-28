package client;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Client_TCP {
	private Socket clientSocket =null;
	String name = null;
	boolean closed = true;
	String inFromServer;
	//ArrayList<String> clients = new ArrayList<String>();
	String clients = null;
	Object Object = null;
	BufferedReader OIS;
	
	
	
	public void setO(Object o) {
		System.out.println("set");
		this.Object = o;
	}
	public void setClients(String clients) {
		System.out.println("setClients");
		this.clients = clients;
		System.out.println(this.clients.toString());
	}
	
	public String getClients() {
		return clients;
	}
	public void setInFromServer(String inFromServer) {
		System.out.println("setInFrom");
		this.inFromServer = inFromServer;
		System.out.println(inFromServer);
	}
	public static String getIP() throws IOException {
				 BufferedReader	keyboardInput = new BufferedReader(new InputStreamReader(System.in));
				System.out.println(" Please enter IP you want to connect to");
					return keyboardInput.readLine();
			
		}
	 public static String getName() throws IOException {
		 BufferedReader	keyboardInput = new BufferedReader(new InputStreamReader(System.in));
		System.out.println(" Please enter Unique name");
			return keyboardInput.readLine();
	
}
	 
	 public String Join() throws Exception{
		 String name = null;
		 clientSocket = new Socket("127.0.0.1", 6000);
		 closed = false;
		 DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		 outToServer.writeBytes("Client" + '\n');
		 name = getName();
		 ClientSend cS = new ClientSend(clientSocket, "Name:"+name + '\n');
		 Thread t = new Thread(cS);
		 t.start();
		 System.out.println("Sent");
		 OIS = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		 String in = OIS.readLine();
		 if(in.trim().equalsIgnoreCase("You Joined Successfully!")){
			 this.name = name;
		  return in;
		 }else{
			 System.out.println(in + " else");

		  return Join();
		 }
	 }
	 
	 public void getMemberlist(String s) throws IOException, ClassNotFoundException{
		 ClientSend cS = new ClientSend(clientSocket ,"Show:" + s + '\n');
		 Thread t = new Thread(cS);
		 t.start();
		
//		 ArrayList<String> a = getClients();
//		 System.out.println(getClients().toString());
	 }


 public  void chat( int TTL) throws Exception
 {
 //BufferedReader inFromUser =new BufferedReader(new InputStreamReader(System.in));
 BufferedReader	keyboardInput = new BufferedReader(new InputStreamReader(System.in));
 String in =  keyboardInput.readLine();
	String [] a = in.split(",");
	if(a.length<2){
		System.out.println("Wrong input");
		System.out.println("enter destination, message");
		chat(0);
	}else{
	String Destination = a [0];
	String message = a[1];
 ClientSend cS = new ClientSend(clientSocket ,"Chat:"+ Destination + ","+message + "," + name+ '\n');
 Thread t = new Thread(cS);
 t.start();
 
	}
	
 }
 public  void chat(String source, String Destination, String message ,int TTL) throws Exception
 {
 //BufferedReader inFromUser =new BufferedReader(new InputStreamReader(System.in));

 ClientSend cS = new ClientSend(clientSocket ,"Chat:"+ Destination + ","+message + "," + source + '\n');
 Thread t = new Thread(cS);
 t.start();
 
	}
 
 public void Quit() throws IOException{
	 
	 ClientSend cS = new ClientSend(clientSocket,"Quit:e2fel" + '\n');
	 Thread t = new Thread(cS);
	 t.start();
	 closed = true;
 }
 public Client_TCP(Socket clientSocket){
	 this.clientSocket = clientSocket;
 }
 public Client_TCP() throws IOException, Exception{
	 Client();
 }
 public void Client() throws IOException, Exception{
	 System.out.println(Join());
	 BufferedReader	keyboardInput = new BufferedReader(new InputStreamReader(System.in));
	 ClientRecieve cR = new ClientRecieve(clientSocket,OIS);
	 cR.setClient(this);
	 Thread t = new Thread(cR);
	 t.start();
	 System.out.println("\n"+"use the following commands: " + "\n"+ "m - returns memberlist" + "\n" + "c- chat" + "\n" + "if you want to leave just say quit or bye");
	 while(true){
		 String in = keyboardInput.readLine();
		 if(closed ==false){
		 switch (in.toLowerCase()){
		 	case "m" :
		 		System.out.println("Do you want names withing the server? y or n");
		 		in = keyboardInput.readLine();
		 		switch(in){
		 		case "y" : getMemberlist("within"); break;
		 		case "n" : getMemberlist("else");break;
		 		}
		 		 break;
		 	case "c": System.out.println("enter destination, message");
		 	chat(0);
		 	break;
		 	case "quit": 
		 	case "bye" : Quit(); break;
		 	default: System.out.println("You didn't enter a correct command");
		 } 
		 }else{
			 switch(in.toLowerCase()){
			 case "rejoin": Client(); break;
			 case "quit": 
			 case "bye": System.exit(0);break;
			 default: System.out.println("Connection is Closed/lost \n if you want to rejoin - type rejoin" + "\n if you want to close write quit or bye" );

			 }
		 }
	
	 }
 }
 // my way
 public static void main(String args[]) throws Exception {
	 Client_TCP client = new Client_TCP();

}
	
 }
			