package client;



import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientRecieve implements Runnable {
	Socket serverSocket = null;
	//BufferedReader inFromServer= null;
	Client_TCP client;
	BufferedReader inFromServer;
	

	public void setClient(Client_TCP client) {
		this.client = client;
	}
	public ClientRecieve(Socket serverSocket, BufferedReader oIS){
		this.inFromServer = oIS;
		this.serverSocket = serverSocket;
	}
	@Override
	public void run() {
		try {
			
			while(true)
			{
				
				//client.setO(inFromServer.readObject());
				String o = inFromServer.readLine();
					 client.setInFromServer(o);
					// System.out.println(a.toString());
				
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}




