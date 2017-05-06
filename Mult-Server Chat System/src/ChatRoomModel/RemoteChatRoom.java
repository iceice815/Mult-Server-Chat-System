package ChatRoomModel;

public class RemoteChatRoom extends ChatRoom{

	String managedServer;

	public RemoteChatRoom(String roomId,String managedServer) {
		super(roomId);
		this.managedServer=managedServer;
		// TODO Auto-generated constructor stub
	}

	
	public String getManagedServer() {
		return managedServer;
	}

	public void setManagedServer(String managedServer) {
		this.managedServer = managedServer;
	}

}
