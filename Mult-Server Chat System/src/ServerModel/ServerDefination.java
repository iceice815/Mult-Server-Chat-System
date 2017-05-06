package ServerModel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import ChatRoomModel.LocalChatRoom;
import ChatRoomModel.RemoteChatRoom;
import Server.UserInfo;


public class ServerDefination {
	
 public LocalChatRoom mainhall;
 public String serverid;
 public Set<String> lockroomid ;
 public Set<String> lockidentity;
 public HashMap<String, Server.UserInfo> userList ;
 public HashMap<String, LocalChatRoom> localroomList;
 public HashMap<String, RemoteChatRoom> remoteroomList;

public ServerDefination() {
	mainhall = new LocalChatRoom("", "", new HashSet());
	serverid = null;
	lockroomid = new HashSet();
	lockidentity = new HashSet();
	userList = new HashMap();
	localroomList = new HashMap();
	remoteroomList = new HashMap();
}
public String getServerid() {
	return serverid;
}
public void setServerid(String serverid) {
	this.serverid = serverid;
}
public LocalChatRoom getMainhall() {
	return mainhall;
}
public void setMainhall(LocalChatRoom mainhall) {
	this.mainhall = mainhall;
}
public Set<String> getLockroomid() {
	return lockroomid;
}
public void setLockroomid(Set<String> lockroomid) {
	this.lockroomid = lockroomid;
}
public Set<String> getLockidentity() {
	return lockidentity;
}
public void setLockidentity(Set<String> lockidentity) {
	this.lockidentity = lockidentity;
}
public HashMap<String, Server.UserInfo> getUserList() {
	return userList;
}
public void setUserList(HashMap<String, Server.UserInfo> userList) {
	this.userList = userList;
}
public HashMap<String, LocalChatRoom> getLocalroomList() {
	return localroomList;
}
public void setLocalroomList(HashMap<String, LocalChatRoom> localroomList) {
	this.localroomList = localroomList;
}
public HashMap<String, RemoteChatRoom> getRemoteroomList() {
	return remoteroomList;
}
public void setRemoteroomList(HashMap<String, RemoteChatRoom> remoteroomList) {
	this.remoteroomList = remoteroomList;
}

 
 

}
