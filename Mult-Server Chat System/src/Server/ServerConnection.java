package Server;

import java.awt.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ChatRoomModel.RemoteChatRoom;
import JsonTool.Marshaling;
import JsonTool.Unmarshaling;
import ServerModel.ServerDefination;


public class ServerConnection extends Thread {
	
	
	private Socket serverSocket;
	protected BufferedReader reader;
	protected BufferedWriter writer;
	//private int clientNum;
	private ServerDefination server = null;
	private String identity;
	
	public ServerConnection(ServerDefination server, Socket serversocket) {
		try {
			this.serverSocket = serversocket;
			this.reader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream(), "UTF-8"));
			this.writer = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream(), "UTF-8"));
			this.server=server;
			
			System.out.println("1111");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void run() {

		try {

			System.out.println(Thread.currentThread().getName() + " - Reading messages from client's " + " connection"); 
			Unmarshaling UnmarshalTool= new Unmarshaling();
			Marshaling MarshalTool= new Marshaling();
			String serverMsg = null;
			
			while ((serverMsg = reader.readLine()) != null) {
				System.out.println(serverMsg);
			//DataOutputStream output=new DataOutputStream(serversocket.getOutputStream());
			//DataInputStream datainput = new DataInputStream(serversocket.getInputStream());

				JSONParser parser = new JSONParser();
				JSONObject jobject = (JSONObject) parser.parse(serverMsg);
				String type = (String) jobject.get("type");
				
				if (type.equals("lockidentity")){
					
					String serverid=(String) jobject.get("serverid");
					String identityName=(String) jobject.get("identity");
					//locked=(String) jobject.get("locked");
				
					//send message to sending socket
					
					if(!jobject.containsKey("locked") ){
					if(server.lockidentity.contains(identity)||server.userList.containsKey(identity)){
						//contains and 
						//send message to another server.
						String lockidentity_Server_Message1=Marshaling.LockidentityServer
								(identityName, serverid, "false");
						//not contains
						write(lockidentity_Server_Message1);
						
						
					}else {
						//not contains ,add to locklist
						String lockidentity_Server_Message2=Marshaling.LockidentityServer
								(identityName, serverid, "true");
						write(lockidentity_Server_Message2);
						server.lockidentity.add(identityName);
					}
					}else {
						break;
					}
					//receive relea
//				}else if(type.equals("releaseidentity")){
//					
//					String serverid=(String) jobject.get("serverid");
//					String identity=(String) jobject.get("identity");
//					server.lockidentity.remove("identity");
					
				
				
				
				}else if(type.equals("lockroomid")){
				
					String roomid = (String) jobject.get("roomid");
					String remoteid = (String) jobject.get("serverid");
					//String serverid = (String) jobject.get("serverid");

					//JSONObject lockroomid = new JSONObject();
                    if(!jobject.containsKey("locked")){
					if (server.remoteroomList.containsKey(roomid) || server.localroomList.containsKey(roomid)) {
						//lockroomid.put("approved", "false");
                    String lockroomidservermessage=
                    		Marshaling.LockRoomServer(server.getServerid(), roomid, "false");
						write(lockroomidservermessage);

					} else {
						String lockroomidservermessage=
	                    		Marshaling.LockRoomServer(server.getServerid(), roomid, "true");
						System.out.println(lockroomidservermessage);
							write(lockroomidservermessage);
							server.remoteroomList.put(roomid, new RemoteChatRoom(roomid, remoteid));
					}

				}else{
					break;
					}

				}else if(type.equals("deleteroom")){
					String serverid = (String) jobject.get("serverid");
					String roomid = (String) jobject.get("roomid");
					server.remoteroomList.remove(roomid);
					//server will remove the room from remote roomlist;
					
				}
				else if(type.equals("quit")){
					String identity= (String) jobject.get("identity");
					server.lockidentity.remove(identity);
					server.userList.remove(identity);
				}

			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void write(String msg) {
		try {
			writer.write(msg + "\n");
			writer.flush();
			System.out.println(Thread.currentThread().getName() + " - Message sent to server ");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}	
	
	
	
	

