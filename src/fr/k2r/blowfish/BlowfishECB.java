/*
* BlowfishECB.java version 2.00.00
* 
* Code Written by k2r (k2r.contact@gmail.com)
* 
* Tks to Mouser for his precious help.
* Tks to Murx for correct Padding and Long key. 
* 
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*/
package fr.k2r.blowfish;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import javax.crypto.*;


/**
 * Blowfish ECB Class
 * @author k2r
 * Permit to crypt/decrypt string in ECB Blowfish mode
 */
public class BlowfishECB extends Blowfish {
	
	/**
	 * Base64 string
	 */
	private static String B64 = "./0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	/**
	 * OK begin script
	 */
	private static String beginCryptOK = "+OK ";
	
	/**
	 * MCPS begin string
	 */
	private static String beginCryptMCPS = "mcps ";
	
	/**
	 * Constructor
	 * @param key
	 */
	public BlowfishECB(String key) {
		super(key, "Blowfish/ECB/NoPadding");		
	}
	
	/**
	 * Encrypt the string
	 * @param notcryptedString
	 * @return String
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidKeyException 
	 */
	public String Encrypt(String notcryptedString) throws IllegalBlockSizeException,
	  BadPaddingException, InvalidKeyException {
		
		// Mode cypher in Encrypt mode
		getChiper().init(Cipher.ENCRYPT_MODE, getSkeySpec()); 
	
		// Paddind the String
		byte[] BEncrypt = notcryptedString.getBytes();
		int Taille = BEncrypt.length;
		int Limit = 8 - (BEncrypt.length % 8);
		byte[] buff = new byte[Taille + Limit];

		for (int i = 0; i < Taille; i++) {
			buff[i] = BEncrypt[i];
		}
		
		for (int i = Taille; i < Taille + Limit; i++) {
			buff[i] = 0x0;
		}
		
		// Encrypt the padding string
		byte[] encrypted = getChiper().doFinal(buff); 
		// B64 ENCRYPTION (mircryption needed)
		String REncrypt = bytetoB64(encrypted);
		REncrypt = beginCryptOK.concat(REncrypt);
		return REncrypt;
	}

	@Override
	public String Decrypt(String encryptedString, Boolean forceEncryption)
			throws IOException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException,
			BadPaddingException {
		return this.Decrypt(encryptedString);
	}
	
	/**
	 * Decrypt the string
	 * @param encryptedString
	 * @return String
	 * @throws UnsupportedEncodingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 */
	public String Decrypt(String encryptedString) throws UnsupportedEncodingException,
	  InvalidKeyException, IllegalBlockSizeException {
		
		if(encryptedString.startsWith(beginCryptOK)) {
			encryptedString = encryptedString.substring(beginCryptOK.length(), encryptedString.length());
		} else if(encryptedString.startsWith(beginCryptMCPS))	{
			encryptedString = encryptedString.substring(beginCryptMCPS.length(), encryptedString.length());
		} else {
			//Not correct encrypted string, return the source string
			return encryptedString;
		}
		
		// B64 DECRYPTION (mircryption needed)
		byte[] Again = B64tobyte(encryptedString); 	
		byte[] decrypted = null;
		
		try {
			// Mode cypher in Decrypt mode
			getChiper().init(Cipher.DECRYPT_MODE, getSkeySpec());
			decrypted = getChiper().doFinal(Again);
		
			// Recup exact length
			int leng = 0;
			while(decrypted[leng] != 0x0) {leng++;}
			byte[] Final = new byte[leng];
			// Format & Limit the Result String
			int i = 0;
			while(decrypted[i] != 0x0) {
				Final[i] = decrypted[i];
				i++;
			}			
			//Force again the encoding result string
			return new String(Final,"8859_1");
		} catch (BadPaddingException e) {
			// Exception, not necessary padding, return directly the decypted string
			return new String(decrypted,"8859_1");
		}
	}

	/**
	 * Base64 to byte function
	 * @param ec
	 * @return byte[]
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] B64tobyte(String ec) throws UnsupportedEncodingException {
		StringBuffer dc = new StringBuffer();	
		int k = -1;
		while (k < (ec.length() - 1)) {

			int right = 0;
			int left = 0;
			int v = 0;
			int w = 0;
			int z = 0;

			for (int i = 0; i < 6; i++) {
				k++;
				v = B64.indexOf(ec.charAt(k));
				right |= v << (i * 6);
			}

			for (int i = 0; i < 6; i++) {
				k++;
				v = B64.indexOf(ec.charAt(k));
				left |= v << (i * 6);
			}

			for (int i = 0; i < 4; i++) {
				w = ((left & (0xFF << ((3 - i) * 8))));
				z = w >> ((3 - i) * 8);
				if(z < 0) {z = z + 256;}
				dc.append((char)z);
			}
				
			for (int i = 0; i < 4; i++) { 
				w = ((right & (0xFF << ((3 - i) * 8)))); 
				z = w >> ((3 - i) * 8);
				if(z < 0) {z = z + 256;}
				dc.append((char)z);
			}
		}	
		// Force the encoding result string
		return dc.toString().getBytes("8859_1");	
	}

	/**
	 * byte to Base64 function
	 * @param ec
	 * @return String
	 */
	public static String bytetoB64(byte[] ec) {
		StringBuffer dc = new StringBuffer();

		int left = 0;
		int right = 0;
		int k = -1;
		int v;

		while (k < (ec.length - 1)) {
			k++;
			v=ec[k]; if (v<0) v+=256;
			left = v << 24;
			k++;
			v=ec[k]; if (v<0) v+=256;
			left += v << 16;
			k++;
			v=ec[k]; if (v<0) v+=256;
			left += v << 8;
			k++;
			v=ec[k]; if (v<0) v+=256;
			left += v;

			k++;
			v=ec[k]; if (v<0) v+=256;
			right = v << 24;
			k++;
			v=ec[k]; if (v<0) v+=256;
			right += v << 16;
			k++;
			v=ec[k]; if (v<0) v+=256;
			right += v << 8;
			k++;
			v=ec[k]; if (v<0) v+=256;
			right += v;

			for (int i = 0; i < 6; i++) {
				dc.append(B64.charAt(right & 0x3F));
				right = right >> 6;
			}

			for (int i = 0; i < 6; i++) {
				dc .append(B64.charAt(left & 0x3F));
				left = left >> 6;
			}
		}
		return dc.toString();
	}
}