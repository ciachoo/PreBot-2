package fr.k2r;


import java.io.UnsupportedEncodingException;

import fr.k2r.blowfish.Blowfish;
import fr.k2r.blowfish.BlowfishCBC;
import fr.k2r.blowfish.BlowfishECB;


/**
 * 
 * @author k2r
 *
 */
public class Crypt {

	/**
	 * Key
	 */
	private static final String key = "keyTest";
	
	/**
	 * String to crypt
	 */
	private static final String initial = "java mircryption CBC implementation !";
	
	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("Initial : " + initial);
		
		System.out.println("ECB ENCRYPTION");
		Blowfish lBlowECB = new BlowfishECB(key);
		String encryptedECB = lBlowECB.Encrypt(initial);
		System.out.println("Encrypt : " +  encryptedECB);
		System.out.println("Decrypt : " + lBlowECB.Decrypt(encryptedECB));
		
		System.out.println("CBC ENCRYPTION");
		Blowfish lBlowCBC = new BlowfishCBC(key);
		String encryptedCBC = lBlowCBC.Encrypt(initial);
		System.out.println("Encrypt : " +  encryptedCBC);
		System.out.println("Decrypt : " + lBlowCBC.Decrypt(encryptedCBC));
	}
}
