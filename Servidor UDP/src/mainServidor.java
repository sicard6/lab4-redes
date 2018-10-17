
public class mainServidor {
	
	public static sevidor s;
	public static int PUERTO = 8080;
	public static int CANTIDAD_CLIENTES = 2;
	public mainServidor() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		try {
			s = new sevidor(PUERTO,CANTIDAD_CLIENTES);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
