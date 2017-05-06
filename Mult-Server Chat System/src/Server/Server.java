package Server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import ServerModel.ServerDefination;
import ServerModel.ServerInfo;
import ChatRoomModel.RemoteChatRoom;

public class Server {

	static int clientSocket_Port = 0;
	static int serverSocket_Port = 0;
	static Runnable server_Connection;
	static Runnable client_Connection;
	public static Map<String, ServerInfo> serverinfo_list = new HashMap<String, ServerInfo>();

	public static void main(String[] args) throws CmdLineException, NumberFormatException, IOException {
		/*---------------------------------------------------------------*/
		// sockets used to listen to client and server,
		// initial those two ServerSocket as null

		ServerSocket listeningSocket = null;
		ServerSocket ListeningServerSocket = null;

		/*---------------------------------------------------------------*/
		// Object that will store the parsed command line arguement
		// parser provied by args4j
		CmdLineArgs argsBean = new CmdLineArgs();
		CmdLineParser parser = new CmdLineParser(argsBean);
		// implementation of parguments parse
		parser.parseArgument(args);
		String serverid_From_Paser = argsBean.getServerid();
		String path = argsBean.getserversconf();
		/*---------------------------------------------------------------*/
		// read serverIcong from file
		FileReader file = new FileReader(path);
		String line = null;
		BufferedReader reader = new BufferedReader(file);
		while ((line = reader.readLine()) != null) {
			String[] info_FromFile = line.split("\t");
			String serverID_FromFile = info_FromFile[0];
			String serveraddress_FromFile = info_FromFile[1];
			int clientport_FromFile = Integer.valueOf(info_FromFile[2]);
			int coordination_Port_FromFile = Integer.valueOf(info_FromFile[3]);

			if (serverID_FromFile.equals(serverid_From_Paser)) {
				ServerInfo createServer = new ServerInfo(serverID_FromFile, serveraddress_FromFile, clientport_FromFile,
						coordination_Port_FromFile);
				clientSocket_Port = createServer.getClientsPort();
				serverSocket_Port = createServer.getCoordinationPort();
			} else {
				ServerInfo server = new ServerInfo(serverID_FromFile, serveraddress_FromFile, clientport_FromFile,
						coordination_Port_FromFile);
				serverinfo_list.put(serverID_FromFile, server);
			}
		}
		reader.close();
		try {
			/*---------------------------------------------------------------*/
			// Create a server socket listening to client
			listeningSocket = new ServerSocket(clientSocket_Port);
			System.out.println(Thread.currentThread().getName() + " - Server"+serverid_From_Paser+" listening to client on port"
					+ clientSocket_Port + " for a connection");
			/*---------------------------------------------------------------*/
			// Create a server socket listening to server.
			ListeningServerSocket = new ServerSocket(serverSocket_Port);
			System.out.println(Thread.currentThread().getName() + " - Server "+serverid_From_Paser+"listening to server on port"
					+ serverSocket_Port + " for a connection");

			/*---------------------------------------------------------------*/
			// initialize the server
			ServerDefination initial_Server = new ServerDefination();
			initial_Server.mainhall.setRoomid("MainHall-" + serverid_From_Paser);
			initial_Server.setServerid(serverid_From_Paser);
			initial_Server.localroomList.put(initial_Server.mainhall.getRoomid(), initial_Server.mainhall);
			/*---------------------------------------------------------------*/
			// update serverstate
			for (String remote_Serverid : serverinfo_list.keySet()) {
				
				System.out.println("Server Info List contains: " + remote_Serverid);

				initial_Server.remoteroomList.put("MainHall-" + remote_Serverid,
						new RemoteChatRoom("MainHall-" + remote_Serverid, remote_Serverid));
			}
			/*---------------------------------------------------------------*/
			// construct serverlistenning thread and clientlistening thread
			server_Connection = new ServerThread(ListeningServerSocket, initial_Server,0);
			Thread severThread = new Thread(server_Connection);
			severThread.start();
			client_Connection = new ClientThread(listeningSocket, initial_Server,0);
			Thread clientThread = new Thread(client_Connection);
			clientThread.start();

		} catch (Exception e) {
			e.printStackTrace();

		}
	}
}
