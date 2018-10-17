import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;




public class sevidor {
	private final static int TAMANIO_PKT = 64000;

	private String path1 = "data/250.txt";
	private String path2 = "data/500.txt";
	private int hash;

	private DatagramSocket serverSoket;

	private byte[] receiveData;
	private byte[] sendData;
	private byte[] video;
	
	private int port;
	
	private String prueba;
	private int peso;

	private int cantidadClientes;

	private ArrayList<ClienteConectado> clientes;

	public sevidor(int port, int cantidadC) throws Exception {
		this.port = port;
		recivirEnviar();
	}

	private void cargar(String path) {
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader(new File(path)));
			String txt = br.readLine();

			video = txt.getBytes();
			byte temp[] = new byte[video.length - video.length%TAMANIO_PKT];
			for (int i = 0; i < temp.length; i++) {
				temp[i]=video[i];
			}
			video = temp;
			//video = new String("hola1234567890123412hola1234567890123412hola1234567890123412").getBytes();
		}catch (IOException ioe)
		{
			ioe.printStackTrace();
		}

		hash = new String(video).hashCode();
		System.out.println("hash: " + hash);
	}

	private void recivirEnviar() throws Exception {
		serverSoket = new DatagramSocket(port);
		
		while(true) {
			prep();
			while(clientes.size()<cantidadClientes)
			{
				recibirMensajePreparado();
			}
			System.out.println("empece a enviar");

			enviar();
			clientes = new ArrayList<>();
			System.out.println("acabe de enviar");
			System.out.println("recibo informes");
			int x = 1;
			PrintWriter writer = new PrintWriter("logs/UDP_cl_"+cantidadClientes+"_pba_"+prueba+"_peso_"+peso+".txt", "UTF-8");
			writer.println("fecha: " + new Date().toString());
			writer.println(peso + ".txt , cantidad de paquetes enviados:"+(int) video.length/TAMANIO_PKT + " , tamanio del paquete (con encabezado): 64KB");
			writer.println("Funciono, cantidad de paquetes recibidos, tiempo, cliente");
			while(x <= cantidadClientes) {
				recibirInforme(x,writer);
				x++;
			}
			writer.close();
			System.out.println("acabe de escribir el informe");
		}

	}

	private void prep() throws Exception {
		System.out.println("1. 250 MiB");
		System.out.println("2. 500 MiB");
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		if(inFromUser.readLine().equals("1"))
		{
			cargar(path1);
			peso = 250;
		}else {
			cargar(path2);
			peso = 500;
		}
		System.out.println("Numero de clientes: ");
		inFromUser = new BufferedReader(new InputStreamReader(System.in));
		
		clientes = new ArrayList<>();
		cantidadClientes = Integer.parseInt(inFromUser.readLine()); 
		System.out.println("numero de la prueba");
		inFromUser = new BufferedReader(new InputStreamReader(System.in));
		prueba = inFromUser.readLine();
		
		receiveData = new byte[1024];
		sendData = new byte[TAMANIO_PKT];
		System.out.println("empece a escuchar");
		
	}

	private void recibirInforme(int x, PrintWriter writer) throws IOException {
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		serverSoket.receive(receivePacket);
		String datos = new String(receivePacket.getData()).trim();
		datos += "," + x;
		System.out.println(datos);
		writer.println(datos);
	}

	private void enviar() throws Exception {
		// Envio el hash
		sendData = new String(hash+"").getBytes();
		for (ClienteConectado cc : clientes) {
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, cc.IPAddress, cc.port);
			serverSoket.send(sendPacket);
		}
		// envio cantidad de paquetes
		byte temp[] = new byte[64000];
		System.out.println(video.length/TAMANIO_PKT+"");
		temp = new String(video.length/TAMANIO_PKT+"").getBytes();
		for (ClienteConectado cc : clientes) {
			DatagramPacket sendPacket = new DatagramPacket(temp, temp.length, cc.IPAddress, cc.port);
			serverSoket.send(sendPacket);
		}
		// envio paquetes
		for(ClienteConectado cc: clientes) {
			new Hilo(cc, TAMANIO_PKT,serverSoket,video).start();
		}

	}

	private ClienteConectado recibirMensajePreparado() throws IOException {
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		serverSoket.receive(receivePacket);
		InetAddress IPAddress = receivePacket.getAddress();
		int port = receivePacket.getPort();
		ClienteConectado c = new ClienteConectado(port, IPAddress);
		clientes.add(c);
		String sentence = new String( receivePacket.getData());
		System.out.println("cliente : "+ IPAddress.toString() +" port: " +port + " " + sentence);
		return c;
	}

}
