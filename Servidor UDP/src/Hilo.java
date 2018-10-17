import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class Hilo extends Thread {	

	private ClienteConectado cliente;
	
	private int tamanio;
	
	private DatagramSocket ss;
	
	private byte[] video;
	
	
	public Hilo(ClienteConectado a, int tamanioPkt, DatagramSocket serverSoket, byte[] vide)  {
		video = vide;
		ss = serverSoket;
		tamanio = tamanioPkt;
		cliente = a;
	}

	@Override
	public void run() {

		for (int i = 0; i < video.length; i+=tamanio) {
			byte[] sendData = Arrays.copyOfRange(video,i,i+tamanio);
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, cliente.IPAddress, cliente.port);
			try {
				ss.send(sendPacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
