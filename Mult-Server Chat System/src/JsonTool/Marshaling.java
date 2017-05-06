package JsonTool;

import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Marshaling {
   public static String Lockidentity(String identityName,String serverid){
	   JSONObject lockidentity = new JSONObject();
		lockidentity.put("type", "lockidentity");
		lockidentity.put("identity", identityName);
		lockidentity.put("serverid", serverid);
		String lockidentitymessage = lockidentity.toJSONString();
	    return lockidentitymessage;
   }
   public static String LockidentityServer(String identityName,String serverid,String bool){
	   JSONObject lockidentity = new JSONObject();
		lockidentity.put("type", "lockidentity");
		lockidentity.put("identity", identityName);
		lockidentity.put("serverid", serverid);
		lockidentity.put("locked", bool);
		String lockidentitymessage = lockidentity.toJSONString();
	    return lockidentitymessage;
   }
   
	    
   public static String putJsonReleaseidentity(String identityName,String serverid){
	     
	    JSONObject realeaseidentity = new JSONObject();
		realeaseidentity.put("type", "realeaseidentity");
		realeaseidentity.put("identity", identityName);
		realeaseidentity.put("serverid", serverid);
		
		return realeaseidentity.toJSONString();
   }
   public static String putJsonRoomchange(String identityName,String newchatroom ){
	     
   JSONObject roomchange = new JSONObject();
	roomchange.put("type", "roomchange");
	roomchange.put("identity", identityName);
	roomchange.put("former", "");
	roomchange.put("roomid", newchatroom);
	return roomchange.toJSONString();
   }
   
   public static String putJsonNewIdentity(){
	   JSONObject newidentity = new JSONObject();
		newidentity.put("type", "newidentity");
		newidentity.put("approved", "true");
		return newidentity.toJSONString();
   }
   public static String putJsonFailNewIdentity(){
	   JSONObject newidentity = new JSONObject();
		newidentity.put("type", "newidentity");
		newidentity.put("approved", "false");
		return newidentity.toJSONString();
   }
   public static String putJsonList( Set localChatRoomList, Set remoteChatRoomList){
	   JSONObject list = new JSONObject();
		JSONArray array = new JSONArray();
		array.addAll(localChatRoomList);
		array.addAll(remoteChatRoomList);
		list.put("rooms", array);
		list.put("type", "roomlist");
		return list.toJSONString();
   }
   public static String Who(Set memberlist, String currentchatroom,String owner){
	   JSONObject who = new JSONObject();
		JSONArray array = new JSONArray();
		array.addAll(memberlist);
		who.put("type", "roomcontents");
		who.put("roomid", currentchatroom);
		who.put("identities", array);
		who.put("owner", owner);
		return who.toJSONString();
   }

   public static String LockRoom(String serverid,String roomid){
	   JSONObject lockRoom =new JSONObject();
	   lockRoom.put("type", "lockroomid");
	   lockRoom.put("serverid", serverid);
	   lockRoom.put("roomid", roomid);
	   return lockRoom.toJSONString();
	
	   
   }
   
   public static String LockRoomServer(String serverid,String roomid,String bool){
	   JSONObject lockRoom =new JSONObject();
	   lockRoom.put("type", "loockroomid");
	   lockRoom.put("serverid", serverid);
	   lockRoom.put("roomid", roomid);
	   lockRoom.put("locked", bool);
	   return lockRoom.toJSONString();
	
	   
   }
   
  public static String putJsonSuccCreateroom(String roomid){
	  JSONObject createroomsucc = new JSONObject();
		createroomsucc.put("type", "createroom");
		createroomsucc.put("roomid", roomid);
		createroomsucc.put("approved", "true");
		return createroomsucc.toJSONString();
  }
 public static String putJsonSuccCreateRoomBroad(String roomid,String previousroomid,String identity){
	    JSONObject createroomsuccbroad = new JSONObject();
		createroomsuccbroad.put("type", "roomchange");
		createroomsuccbroad.put("roomid", roomid);
		createroomsuccbroad.put("former", previousroomid);
		createroomsuccbroad.put("identity", identity);
		return createroomsuccbroad.toJSONString();
 }
 public static String putJsonRealseRoomid(String roomid,String server){
	    JSONObject releaseRoom = new JSONObject();
	    releaseRoom.put("type", "releaseroomid");
	    releaseRoom.put("serverid", server);
	    releaseRoom.put("roomid", roomid);
	    releaseRoom.put("approved", "true");
	    return releaseRoom.toJSONString();
	   
}
 
public static String FailCreateRoom(String roomid){
	    JSONObject createroomfail = new JSONObject();
	    createroomfail.put("type", "createroom");
	    createroomfail.put("roomid", roomid);
	    createroomfail.put("approved", "false");
	    return createroomfail.toJSONString();
	   
  }
public static String JoinNonRoom(String currentroom,String identity)  {
	JSONObject joinroomunsuccess = new JSONObject();
	joinroomunsuccess.put("type", "roomchange");
	joinroomunsuccess.put("roomid", currentroom);
	joinroomunsuccess.put("former", currentroom);
	joinroomunsuccess.put("identity", identity);
	return joinroomunsuccess.toJSONString();
}
public static String putJsonJoinLocalRoom(String identity,String currentroom,String roomid){
JSONObject joinLocalRoom = new JSONObject();
   joinLocalRoom.put("type", "roomchange");
   joinLocalRoom.put("identity", identity);
   joinLocalRoom.put("former", currentroom);
   joinLocalRoom.put("roomid", roomid);
return joinLocalRoom.toJSONString();
}

public static String DeleteRoomToServer(String deleteroomid, String serverid){
	JSONObject deleteroomserver = new JSONObject();
	deleteroomserver.put("type", "deleteroom");
	deleteroomserver.put("roomid", deleteroomid);
	deleteroomserver.put("serverid", serverid);
	return deleteroomserver.toJSONString();
	
}

public static String DeleteRoomsucc(String mainhall,String deleteroomid,String identity){
	JSONObject deleteroomsucc = new JSONObject();
	deleteroomsucc.put("type", "roomchange");
	deleteroomsucc.put("roomid", mainhall);
	deleteroomsucc.put("former", deleteroomid);
	deleteroomsucc.put("identity", identity);
	return deleteroomsucc.toJSONString();
	
}
public static String putJsonDeleteMsgToOwner(String deleteroomid){
   JSONObject deleteroomowner = new JSONObject();
   deleteroomowner.put("type", "deleteroom");
   deleteroomowner.put("roomid", deleteroomid);
   deleteroomowner.put("approved", "true");
return deleteroomowner.toJSONString();
}

public static String FailDelete(String deleteroomid){
	   JSONObject faildeleteroom = new JSONObject();
	   faildeleteroom.put("type", "deleteroom");
	   faildeleteroom.put("roomid", deleteroomid);
	   faildeleteroom.put("approved", "false");
	return faildeleteroom.toJSONString();
	}
public static String Message(String identity, String content){
	JSONObject message = new JSONObject();
	message.put("type", "message");
	message.put("identity", identity);
	message.put("content", content);
	return message.toJSONString();
}

public static String QuitTocient(String identity){
	JSONObject quitid = new JSONObject();
	quitid.put("type", "quit");
	quitid.put("identity", identity);
	return quitid.toJSONString();
}
public static String QuitToServer(String identity){
	JSONObject quitidServer = new JSONObject();
	quitidServer.put("type", "quit");
	quitidServer.put("identity", identity);
	return quitidServer.toJSONString();
}

public static String putJsonQuitSucss(String chatroom,String currentroom, String identity){
	JSONObject deleteoomsucc = new JSONObject();
	deleteoomsucc.put("type", "roomchange");
	deleteoomsucc.put("roomid", chatroom);
	deleteoomsucc.put("former", currentroom);
	deleteoomsucc.put("identity", identity);
	return deleteoomsucc.toJSONString();
	
}


public static String JoinOutSucss(String roomid,String server_Host, String server_portID){
	JSONObject JoinOut = new JSONObject();
	JoinOut.put("type", "route");
	JoinOut.put("roomid", roomid);
	JoinOut.put("host", server_Host);
	JoinOut.put("port", server_portID);
	return JoinOut.toJSONString();
	
}

public static String serverChange(String server_portID){
	JSONObject JoinOut = new JSONObject();
	JoinOut.put("type", "serverchange");
	JoinOut.put("approved","true");
	JoinOut.put("serverid",server_portID);
	return JoinOut.toJSONString();
	
}

public static String roomServerChange(String former,String roomid,String identityName){
	JSONObject JoinOut = new JSONObject();
	JoinOut.put("type", "roomchange");
	JoinOut.put("former",former);
	JoinOut.put("roomid", roomid);
	JoinOut.put("identity",identityName);
	return JoinOut.toJSONString();
	
}

}
