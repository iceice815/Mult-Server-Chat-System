package ServerModel;

public class ServerInfo {
	private String serverid;
	private String serverAddress;
	private int clientPort;
	private int coordinationPort;
	
	/*constructor*/
	public ServerInfo(String serverid, String server_address, int clientportFromFile, int coordinationportFromFile) {
		
		this.serverid = serverid;
		this.serverAddress = server_address;
		this.clientPort = clientportFromFile;
		this.coordinationPort = coordinationportFromFile;
	}

	public String getServerid() {
		return serverid;
	}

	public void setServerid(String serverid) {
		this.serverid = serverid;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String server_address) {
		this.serverAddress = server_address;
	}

	public int getClientsPort() {
		return clientPort;
	}

	public void setClientPort(int clientsPort) {
		this.clientPort = clientsPort;
	}

	public int getCoordinationPort() {
		return coordinationPort;
	}

	public void setCoordinationPort(int coordinationPort) {
		this.coordinationPort = coordinationPort;
	}
	
	
	

}
