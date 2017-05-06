package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import ServerModel.ServerDefination;

public class ServerThread implements Runnable {
	
 
	/*---------------------------------------------------------------*/
	protected ServerSocket listeningSocket;
	protected ServerDefination serverDefination;
    protected int serverNumber;
	/*---------------------------------------------------------------*/
	public ServerThread(ServerSocket listeningSocket, ServerDefination serverDefination,int serverNumber ) {
		this.listeningSocket = listeningSocket;
		this.serverDefination = serverDefination;
		this.serverNumber=serverNumber;
	}

	/*---------------------------------------------------------------*/
	@Override
	public void run() {

		
			// Listen for incoming connections forever
			while (true) {
				try {
				Socket serverSocket = listeningSocket.accept();
				System.out.println(Thread.currentThread().getName() +serverNumber +" - server conection accepted");

				String hostName = serverSocket.getInetAddress().getHostName();
				int port = serverSocket.getLocalPort();
				System.out.println("HostName: " + hostName + "Port: " + port);
				serverNumber++;

				ServerConnection serverConnection = new ServerConnection(serverDefination, serverSocket);
				serverConnection.setName("ServerConnectionThread"+serverNumber );
				serverConnection.start();
				// Register the new connection with the client manager
				//ServerState.getInstance().clientConnected(clientConnection);
			

		} catch (Exception e) {
			e.printStackTrace();
		} 
//				finally {
//					if (listeningSocket!= null) {
//						try {
//							listeningSocket.close();
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}
//				}
	}
}
}
