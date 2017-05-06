package Server;

import java.net.Socket;


public class UserInfo {
	private String indentity;
	private String currentChatRoom;
	//private Socket clientSocket;

	protected ClientConnection currentCC;
	protected boolean isOwner;
	//managingThread???
	public UserInfo(String indentity, ClientConnection currentCC,String currentChatRoom, Socket clientSocket,boolean isOwner) {
		
		this.indentity = indentity;
		this.currentChatRoom = currentChatRoom;
		//this.clientSocket = clientSocket;
		this.isOwner=isOwner;
		this.currentCC=currentCC;
	}
	public ClientConnection getCurrentCC() {
		return currentCC;
	}
	public void setCurrentCC(ClientConnection currentCC) {
		this.currentCC = currentCC;
	}
	public String getIndentity() {
		return indentity;
	}
	public void setIndentity(String indentity) {
		this.indentity = indentity;
	}
	public String getCurrentChatRoom() {
		return currentChatRoom;
	}
	public void setCurrentChatRoom(String currentChatRoom) {
		this.currentChatRoom = currentChatRoom;
	}
//	//public Socket getClientSocket() {
//		return clientSocket;
//	}
//	public void setClientSocket(Socket clientSocket) {
//		this.clientSocket = clientSocket;
//	}
	public void setClientName(String identityName) {
		// TODO Auto-generated method stub
		
	}
	public boolean isOwner() {
		return isOwner;
	}
	public void setOwner(boolean isOwner) {
		this.isOwner = isOwner;
	}
	
	
	

}
