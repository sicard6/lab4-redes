import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Cliente {
	private final static int TAMANIO_PKT = 64000;

	private DatagramSocket clientSoket;

	private byte[] receiveData;
	private byte[] sendData;
	private int port;
	private byte[] video;
	private int cant;
	private InetAddress adress;

	public Cliente(int pPort, String IP_ADRESS) throws Exception {
		port = pPort;
		clientSoket = new DatagramSocket();
		sendData = new byte[1024];
		receiveData = new byte[TAMANIO_PKT];
		adress = InetAddress.getByName(IP_ADRESS);
		
		
		enviarRecibir();
		
	}
	public void enviarRecibir() throws Exception {
		enviarMensajeListo();
		recibir();
	}
	
	private void recibir() throws IOException {
		
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		clientSoket.receive(receivePacket);
		// recibo el hash
		String pktss = new String(receivePacket.getData());
		String hash = pktss;
		System.out.println("Hash: " + hash);
		
		// recibo la cantidad de paquetes
		byte [] temp = new byte[TAMANIO_PKT];
		DatagramPacket receivePacket3 = new DatagramPacket(temp, temp.length);
		clientSoket.receive(receivePacket3);
		String cantidad = new String(receivePacket3.getData());
		System.out.println("paquetes: " + cantidad);
		cant = Integer.parseInt(cantidad.trim());
		
		video = new byte[cant*TAMANIO_PKT];
		
		// recibo los paquetes
		int cantP = 0;
		long inicio = System.currentTimeMillis();
		try {
			clientSoket.setSoTimeout(500);
			DatagramPacket receivePacket2 = new DatagramPacket(new byte[TAMANIO_PKT],TAMANIO_PKT);
			for (int j = 0; j < cant; j++) {
				clientSoket.receive(receivePacket2);
				byte temp2[] = receivePacket2.getData();
				cantP = j; 
				for (int i = 0; i < temp2.length; i++) {
					int z =(int) (j*TAMANIO_PKT+i);
					video[z] = temp2[i];
				}				
			}
			
		}catch(Exception e) {
			System.err.println("no se recibieron todos los paquetes: "+cantP);
			
		}finally {
			long tiempo = System.currentTimeMillis() - inicio;
			int hashCalc = new String(video).trim().hashCode();
			String xx = hash.trim();
			int hashI = Integer.parseInt(xx);
			System.out.println("Hash Calculado: " + hashCalc);
			System.out.println("Hash Entregado: " + hash);
			System.out.println("cantidad de paquetes recividos: " + cantP);
			System.out.println("Son iguales : " + (hashI == hashCalc));
			// Envio Resultados
			enviarResultados(hashI == hashCalc,cantP, tiempo);
			clientSoket.close();
		}
		
			
	}
	
	private void enviarResultados(boolean b, int cantP, long tiempo) throws IOException {
		String sentence = b +","+ cantP +","+ tiempo;
		sendData = sentence.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, adress, port);
		clientSoket.send(sendPacket);
		
	}
	private void enviarMensajeListo() throws IOException {
		String sentence = "listo";
		sendData = sentence.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, adress, port);
		clientSoket.send(sendPacket);
		
	}

}
