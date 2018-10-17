
public class mainCliente {

	public static Cliente c;
	public static int PORT_SERVER = 8080;
	public static String IP_ADRESS = "157.253.205.64";//localhost
	
	public mainCliente() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		c = new Cliente(PORT_SERVER,IP_ADRESS);

	}

}
