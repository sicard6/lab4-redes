package generador;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.SecureRandom;

public class generador {

	static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	static SecureRandom rnd = new SecureRandom();

	static String randomString( int len ){
		
		StringBuilder sb = new StringBuilder( len );
		for( int i = 0; i < len; i++ ) 
			sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
		return sb.toString();
	}

	public static void main(String[] args) throws IOException {
		System.out.println("introdusca cantidad");
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		String a = inFromUser.readLine();
		int aa = Integer.parseInt(a);
		String aimp = randomString(aa);
		PrintWriter writer = new PrintWriter("data/500.txt", "UTF-8");
		writer.print(aimp);
		writer.close();
	}

}
