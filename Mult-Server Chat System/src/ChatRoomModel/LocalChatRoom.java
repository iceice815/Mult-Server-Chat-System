package ChatRoomModel;

import java.util.HashSet;

public class LocalChatRoom extends ChatRoom {
	private String owner;
	private String serverid;
	private HashSet<String> memberSet;
	
	public LocalChatRoom(String roomid,String owner, HashSet<String> memberSet) {
		  super(roomid);
		this.setOwner(owner);
		this.setMemberSet(memberSet);
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getServerid() {
		return serverid;
	}
	public void setServerid(String serverid) {
		this.serverid = serverid;
	}
	public HashSet<String> getMemberSet() {
		return memberSet;
	}
	public void setMemberSet(HashSet<String> memberSet) {
		this.memberSet = memberSet;
	}
	

}
