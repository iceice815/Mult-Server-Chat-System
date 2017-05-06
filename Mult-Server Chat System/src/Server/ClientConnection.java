package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



import JsonTool.Marshaling;
import JsonTool.Unmarshaling;
import ServerModel.ServerDefination;
import ServerModel.ServerInfo;
import ChatRoomModel.LocalChatRoom;
import ChatRoomModel.RemoteChatRoom;



public class ClientConnection extends Thread {
    private ServerDefination server = null;
    private String identity;
    private UserInfo userinfo;
	private Socket clientSocket;
	private BufferedReader reader;
	private BufferedWriter writer;
	//This queue holds messages sent by the client or messages intended for the client from other threads
	//private BlockingQueue<Message> messageQueue;
	private int clientNum;
    protected final Lock lock = new ReentrantLock();
    protected final Condition cond = lock.newCondition();
	
	public ClientConnection(ServerDefination server,Socket clientSocket,int clientNum) {
		try {
			this.clientSocket = clientSocket;
			reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
			writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));	
			//messageQueue = new LinkedBlockingQueue<Message>();
			this.userinfo = new UserInfo("",this,"",null,false);
			this.clientNum = clientNum;
			this.server = server;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void boardCastAll(String boardCastMsg) {
		Set<UserInfo> all_UserInfo= (Set<UserInfo>) this.server.getUserList().values();
		for (UserInfo userInfo : all_UserInfo) {
			userInfo.getCurrentCC().write(boardCastMsg);
		}
		
		
		
	}
	/*----------------------------------------------------------------*/
	public void write_Server(Socket serverSocket, String msg_Lock){
		try {
			BufferedWriter server_write =new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
		    server_write.write(msg_Lock+"\n");
		    server_write.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public BufferedReader read_Server(Socket serverSocket) throws IOException{
		BufferedReader server_reader = new BufferedReader(
				new InputStreamReader(serverSocket.getInputStream(), "UTF-8"));
		return server_reader;
	}
/*------------------------------------------------------------------*/
	@Override
	public void run() {
		
		try {
			
			System.out.println(Thread.currentThread().getName() 
					+ " - Processing message from client " + clientNum + "  messages");
			
			//Monitor the queue to process any incoming messages (consumer)
			String clientMsg = null;
			String serverMsg =null;
			Unmarshaling UnmarshalTool= new Unmarshaling();
			Marshaling MarshalTool= new Marshaling();
			while((clientMsg = reader.readLine())!=null) {
				System.out.println(Thread.currentThread().getName() + " - Message from client " + clientNum
						+ " received: " + clientMsg);
					//Unmarshaling UnmarshalTool= new Unmarshaling();
					String type = UnmarshalTool.getJson(clientMsg, "type");
					
				/*-----------------------------------------------------------------*/	
					// send LOCKIDENTITY to all the other servers
					if (type.equals("newidentity")){
						lock.lock();//start to lock
						String server_id =userinfo.getIndentity();
						String identityName =UnmarshalTool.getJson(clientMsg, "identity");
						
						boolean isnew = true;
						this.identity = identityName;
						if ( identityName.length() <= 16 && identityName.length() >= 3&&identityName != null 
								&& (identityName.substring(0, 1).matches("[a-zA-Z0-9]"))) {
	
							
							//本地判断有没有
							if (server.userList.containsKey(identityName)==false
								&& server.lockidentity.contains(identityName) == false){
								//没有
									//server.lockidentity.add(identityName);
									String lockidentitymessage=Marshaling.Lockidentity(identityName, server.getServerid());
									//System.out.println(lockidentitymessage);
									
									for (ServerInfo serverInfo : Server.serverinfo_list.values()) {
										if (!(serverInfo.getServerid()).equals(server.getServerid())) {
											String address = serverInfo.getServerAddress();
											int port = serverInfo.getCoordinationPort();
											Socket serverSocket = new Socket(address, port);
											write_Server(serverSocket,lockidentitymessage);
											//write to another server
											BufferedReader reader_Server=read_Server(serverSocket);
											while((serverMsg=reader_Server.readLine())!=null){
												System.out.println(serverMsg);
												String returnMsgType = UnmarshalTool.getJson(serverMsg, "type");
												String isLock = UnmarshalTool.getJson(serverMsg, "locked");

												if ((returnMsgType.equals("lockidentity")) && (isLock.equals("false"))){
													isnew = false;		
													//System.out.println("111");
													
												}
												break;
											}
										}
									}
									
								if(isnew=true){
									//System.out.println("111");
									server.lockidentity.add(identityName);
									//userinfo.setClientSocket(clientSocket);
									userinfo.setIndentity(identityName);
									//加进mainHall
									userinfo.setCurrentChatRoom(server.mainhall.getRoomid());
									//更新memberlist
									server.userList.put(identityName, userinfo);
									HashSet<String> memberlist =server.mainhall.getMemberSet();
									server.mainhall.setMemberSet(memberlist);
									
									//String clientmsg =Marshaling.putJsonRoomchange(identityName, newchatroom);
									String newchatroom = server.userList.get(identityName).getCurrentChatRoom();
									String clientmsg =Marshaling.putJsonRoomchange(identityName, newchatroom);
									
									List<ClientConnection> clients = ServerState.getInstance().getConnectedClients();
									for (ClientConnection client : clients) {
										if (client.getUserInfo().getCurrentChatRoom().equals(newchatroom)) {
											client.write(clientmsg);
											//broadcast

										} 
									}
									String msg = Marshaling.putJsonNewIdentity();
									System.out.println(msg);
									write(msg);
								}
						   }else{
							   String msgfail=Marshaling.putJsonFailNewIdentity();
							   write(msgfail);
							
						   }
				lock.unlock();
			}
	/*-----------------------------------------------------------------*/	
	// List
		}else if (type.equals("list")){
			Set<String> localRoomList=server.localroomList.keySet();
			Set<String> remoteRoomList = server.remoteroomList.keySet();
			String msg = Marshaling.putJsonList(localRoomList, remoteRoomList);
			write(msg);	
    /*-----------------------------------------------------------------*/	
	// who
		}else if (type.equals("who")){
			
			String currentchatroom = server.userList.get(this.identity).getCurrentChatRoom();
			HashSet<String> memberlist = server.localroomList.get(currentchatroom).getMemberSet();
			String owner = server.localroomList.get(currentchatroom).getOwner();
			String msg =Marshaling.Who(memberlist, currentchatroom, owner);
			write(msg);
	/*-----------------------------------------------------------------*/	
	// createrom
		}else if(type.equals("createroom")){
			
			lock.lock();
			boolean isnewRoom=true;
			String roomid = UnmarshalTool.getJson(clientMsg, "roomid");
			if (roomid != null && roomid.length() <= 16 && roomid.length() >= 3
					&& (roomid.substring(0, 1).matches("[a-zA-Z0-9]"))) {
				
				if (server.localroomList.containsKey(roomid) == false
						&& server.remoteroomList.containsKey(roomid) == false) {
					
					String lockroomMsg=Marshaling.LockRoom(server.getServerid(),roomid);
					
					//write to every server
					for (ServerInfo serverInfo : Server.serverinfo_list.values()) {
						if (!(serverInfo.getServerid()).equals(server.getServerid())) {
							String address = serverInfo.getServerAddress();
							int port = serverInfo.getCoordinationPort();
							Socket serverSocket = new Socket(address, port);
							write_Server(serverSocket,lockroomMsg);
							//write to another server
							BufferedReader reader_Server=read_Server(serverSocket);
							while((serverMsg=reader_Server.readLine())!=null){
								System.out.println(serverMsg);
								String returnMsgType = UnmarshalTool.getJson(serverMsg, "type");
								String isLock = UnmarshalTool.getJson(serverMsg, "locked");

								if ((returnMsgType.equals("lockroomid")) && (isLock.equals("false"))){
									isnewRoom = false;		
									//System.out.println("111");	
								}
								break;
							}
						}
					}
					if(isnewRoom=true){
					
					
					
					//successfully to create chatroom
					HashSet<String> memberlist = new HashSet<String>();
					memberlist.add(identity);
					//lock the roomid
					server.lockroomid.add(roomid);
					String previousroomid = server.userList.get(identity).getCurrentChatRoom();
					List<ClientConnection> clients = ServerState.getInstance().getConnectedClients();
					 String msgBroad=Marshaling.putJsonSuccCreateRoomBroad(roomid, previousroomid, identity);
	                   
					for (ClientConnection client : clients) {
						if (client.getUserInfo().getCurrentChatRoom().equals(previousroomid)) {
							client.write(msgBroad);
						}

				}
					//  createroom and set the it to be owner 
					server.localroomList.put(roomid, new LocalChatRoom(roomid, identity, memberlist));
					server.userList.get(identity).setCurrentChatRoom(roomid);
					//removerge identity from previous room
					
					 String msg=Marshaling.putJsonSuccCreateroom(roomid);
	                    write(msg);
					server.localroomList.get(previousroomid).getMemberSet().remove(identity);
					}}else{
					String msg3 =Marshaling.FailCreateRoom(roomid);
					write(msg3);

				}
				
			
			lock.unlock();
		/*-----------------------------------------------------------------*/	
		// join
		
				}
				}else if (type.equals("join")){
				String roomid = UnmarshalTool.getJson(clientMsg, "roomid");
			    
				String currentroom = server.userList.get(identity).getCurrentChatRoom();
				
				System.out.println(currentroom);
				
				if (server.localroomList.containsKey(roomid) == false&&server.remoteroomList.containsKey(roomid)==false
						|| server.localroomList.get(currentroom).getOwner().equals(identity)) {
					String msg5=Marshaling.JoinNonRoom(currentroom, identity);
					System.out.println(msg5);
					write(msg5);
			}//not success join
				else{
					if(server.localroomList.containsKey(roomid)){
						String msg6=Marshaling.putJsonJoinLocalRoom(identity, currentroom, roomid);
						//broadcast to two rooms
						List<ClientConnection> clients = ServerState.getInstance().getConnectedClients();  
						for (ClientConnection client : clients) {
								if (client.getUserInfo().getCurrentChatRoom().equals(currentroom)
										|| client.getUserInfo().getCurrentChatRoom().equals(roomid)) {
									client.write(msg6);
								}
								
							}
						Set newMemberset = new HashSet();
					    server.localroomList.get(roomid).setMemberSet((HashSet<String>) newMemberset);
					    newMemberset.add(identity);
					    server.localroomList.get(roomid).setMemberSet((HashSet<String>) newMemberset);
					    server.userList.get(identity).setCurrentChatRoom(roomid);
                        server.localroomList.get(currentroom).getMemberSet().remove(identity);			    

                    
					}else{
//						
                         RemoteChatRoom remote = server.getRemoteroomList().get(roomid);
						 String remote_Serverid = remote.getManagedServer();
						 String server_Host=Server.serverinfo_list.get(remote_Serverid).getServerAddress();
						 int server_Port=Server.serverinfo_list.get(remote_Serverid).getClientsPort();
						 String server_PortID=Integer.toString(server_Port);
						 String msg = Marshaling.JoinOutSucss(roomid, server_Host, server_PortID);
						 //System.out.println(msg);
						 write(msg);
						 String currentroom1= userinfo.getCurrentChatRoom();
						 server.lockidentity.remove(identity);
						 server.getUserList().remove(identity);
						 
					                       }
					
				}
				
				
		}
					/*-----------------------------------------------------------------*/	
					// deleteroom		
				else if (type.equals("deleteroom")){
		String deleteroomid = UnmarshalTool.getJson(clientMsg, "roomid");

		if ((server.localroomList.get(deleteroomid).getOwner().equals(identity) == true)) {
			
			List<ClientConnection> users = ServerState.getInstance().getConnectedClients();
			for (ClientConnection user : users) {
				if (user.getUserInfo().getCurrentChatRoom().equals(deleteroomid)) {

					server.userList.get(user.getUserInfo().getIndentity())
							.setCurrentChatRoom(server.mainhall.getRoomid());
					
				}
			}
			
			//tell clients
			
			List<ClientConnection> clients = ServerState.getInstance().getConnectedClients();
			for (ClientConnection client : clients) {
				if (server.localroomList.get(deleteroomid).getMemberSet().contains( client.getUserInfo().getIndentity())) {
					 String msgTochatroom =Marshaling.DeleteRoomsucc(server.mainhall.getRoomid(), deleteroomid, client.getUserInfo().getIndentity());
						client.write(msgTochatroom);
						server.mainhall.getMemberSet().add(client.getUserInfo().getIndentity());
						List<ClientConnection> members = ServerState.getInstance().getConnectedClients();
						for (ClientConnection mem : members) {
							if (mem.getUserInfo().getCurrentChatRoom().equals(server.mainhall.getRoomid())) {
								
								mem.write(msgTochatroom);
							}
						}
					}
				}
			//to owner
			String msgTochatroom =Marshaling.DeleteRoomsucc(server.mainhall.getRoomid(), deleteroomid, identity);
			write(msgTochatroom);
			//tell other server

			String msgToServer=Marshaling.DeleteRoomToServer(deleteroomid, server.getServerid());
			for (ServerInfo serverInfo : Server.serverinfo_list.values()) {
				if (!(serverInfo.getServerid()).equals(server.getServerid())) {
					String address = serverInfo.getServerAddress();
					int port = serverInfo.getCoordinationPort();
					Socket serverSocket = new Socket(address, port);
					write_Server(serverSocket,msgToServer);
				}
			}
			
		server.localroomList.remove(deleteroomid);
		String deleteroomMsgToOwner= Marshaling.putJsonDeleteMsgToOwner(deleteroomid);
		write(deleteroomMsgToOwner);
		}
		else{
			String failedeleteMsg=Marshaling.FailDelete(deleteroomid);
			write(failedeleteMsg);
		}
		}
					/*-----------------------------------------------------------------*/	
					//message
		else if(type.equals("message")){
			String currentRoom=server.userList.get(identity).getCurrentChatRoom();
			String content= UnmarshalTool.getJson(clientMsg, "content");
			String chatMsg= Marshaling.Message(currentRoom, content);
			
			List<ClientConnection> clients = ServerState.getInstance().getConnectedClients();
			for (ClientConnection client : clients) {
				if (client.getUserInfo().getCurrentChatRoom().equals(currentRoom)) {
					client.write(chatMsg);
				}
			}	
		}
					/*-----------------------------------------------------------------*/	
					//movejoin
		else if(type.equals("movejoin")){
			String roomid =UnmarshalTool.getJson(clientMsg, "roomid");
			String former =UnmarshalTool.getJson(clientMsg, "former");
			String identityName =UnmarshalTool.getJson(clientMsg, "identity");
			if(server.localroomList.containsKey(roomid)){
				String msgMJ=Marshaling.serverChange(server.getServerid());
				System.out.println(msgMJ);
				write(msgMJ);
				server.lockidentity.add(identityName);
				
				
				
				userinfo.setCurrentChatRoom(roomid);
				//更新memberlist
				server.userList.put(identity, userinfo);
				HashSet<String> memberlist =server.mainhall.getMemberSet();
				server.mainhall.setMemberSet(memberlist);
				String currentRoom=server.userList.get(identity).getCurrentChatRoom();
				
				String chatMsg = Marshaling.roomServerChange(former, roomid, identityName);
				List<ClientConnection> clients = ServerState.getInstance().getConnectedClients();
				for (ClientConnection client : clients) {
					if (client.getUserInfo().getCurrentChatRoom().equals(currentRoom)) {
						client.write(chatMsg);
					}
				}

			}
		}
					/*-----------------------------------------------------------------*/	
					//quit
		else if(type.equals("quit")){
			String currentUserChatRoom = server.userList.get(identity).getCurrentChatRoom();
			server.userList.remove(identity);
			server.localroomList.get(currentUserChatRoom).getMemberSet();
			
			 //send to server
			String deleteroomSevrMsg=Marshaling.QuitToServer(identity);
			   for (ServerInfo serverInfo : Server.serverinfo_list.values()) {
					if (!(serverInfo.getServerid()).equals(server.getServerid())) {
						String address = serverInfo.getServerAddress();
						int port = serverInfo.getCoordinationPort();
						Socket serverSocket = new Socket(address, port);
						write_Server(serverSocket,deleteroomSevrMsg);
					}
				}
			
			if(server.localroomList.get(currentUserChatRoom).getOwner().equals(identity)==true){
				String quitMsg= Marshaling.QuitTocient(identity);
				write(quitMsg);
				server.lockidentity.remove(identity);
				server.lockroomid.remove(currentUserChatRoom);
				server.userList.remove(identity);
				//disconnecting client
		        server.localroomList.get(currentUserChatRoom).getMemberSet().remove(identity);
	            
	           
	         // sent to mainhall
				List<ClientConnection> clients = ServerState.getInstance().getConnectedClients();
				for (ClientConnection client : clients) {
					if (client.getUserInfo().getCurrentChatRoom().equals(currentUserChatRoom)
							&& (client.getUserInfo().getIndentity().equals(identity) == false)) {
			          String deleteroomMsg = Marshaling.putJsonQuitSucss(server.mainhall.getRoomid(), currentUserChatRoom, client.getUserInfo().getIndentity());
			          client.write(deleteroomMsg);
			          server.mainhall.getMemberSet().add(client.getUserInfo().getIndentity());
					
			          List<ClientConnection> members = ServerState.getInstance().getConnectedClients();
						for (ClientConnection mem : members) {
							if (mem.getUserInfo().getCurrentChatRoom().equals(server.mainhall.getRoomid())) {
								mem.write(deleteroomMsg);

							}
						}
					}
				}
				server.localroomList.remove(currentUserChatRoom);

			}else{
				String quitMsg=Marshaling.QuitTocient(identity);
				write(quitMsg);
				server.userList.remove(identity);
				server.lockidentity.remove(identity);
			}
		}
			}
				
			clientSocket.close();
			ServerState.getInstance().clientDisconnected(this);
			System.out.println(Thread.currentThread().getName() 
					+ " - Client " + clientNum + " disconnected");
					
		 }catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void write(String msg) {
		try {
			writer.write(msg + "\n");
			writer.flush();
			System.out.println(Thread.currentThread().getName() + " - Message sent to client " + clientNum);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// Needs to be synchronized because multiple threads can me invoking this
	// method at the same
	// time
public UserInfo getUserInfo(){
	return userinfo;
}
}
