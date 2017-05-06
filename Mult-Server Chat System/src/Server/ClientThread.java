package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import ServerModel.ServerDefination;

public class ClientThread implements Runnable {
	/*---------------------------------------------------------------*/
	protected ServerSocket listeningSocket;
	protected ServerDefination serverDefination;
	protected int clientNum;

	/*---------------------------------------------------------------*/
	public ClientThread(ServerSocket listeningSocket, ServerDefination serverDefination, int clientNum) {
		this.listeningSocket = listeningSocket;
		this.serverDefination = serverDefination;
		this.clientNum = clientNum;
	}

	/*---------------------------------------------------------------*/
	@Override
	public void run() {
		try {
			// Listen for incoming connections forever
			while (true) {

				
				Socket clientSocket = listeningSocket.accept();
				System.out.println(Thread.currentThread().getName() + " - Client conection accepted");

				String hostName = clientSocket.getInetAddress().getHostName();
				int port = clientSocket.getLocalPort();
				System.out.println("HostName: " + hostName + "Port: " + port);
				clientNum++;

				ClientConnection clientConnection = new ClientConnection(serverDefination, clientSocket, clientNum);
				clientConnection.setName("ClientConnectionThread" + clientNum);
				clientConnection.start();
				// Register the new connection with the client manager
				ServerState.getInstance().clientConnected(clientConnection);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} 	finally {
			if (listeningSocket!= null) {
				try {
					listeningSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}	
	}

