package server;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;

public class serSocket extends Socket implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public serSocket(){
		super();
	}
	public serSocket(String IP, int port) throws UnknownHostException, IOException{
		super(IP,port);
	}
	
}
