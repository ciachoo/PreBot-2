/*
* BlowfishCBC.java version 1.00.00
* 
* Code Written by k2r (k2r.contact@gmail.com)
* 
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*/
package fr.k2r.blowfish;


import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


/**
 * Blowfish CBC Class
 * @author k2r
 * Permit to crypt/decrypt string in CBC Blowfish mode
 */
public class BlowfishCBC extends Blowfish {

	/**
	 * Initial Vector
	 */
	private byte[] INIT_IV = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
	
	/**
	 * Begin string
	 */
	private static String beginCrypt = "+OK *";
	
	/**
	 * Constructor
	 * @param key
	 */
	public BlowfishCBC(String key) {
		super(key, "Blowfish/CBC/NoPadding");
	}
	
	@Override
	public String Decrypt(String encryptedString) throws IOException,
			InvalidKeyException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException {
		return this.Decrypt(encryptedString, true);
	}
	
	/**
	 * Decrypt the string
	 * @param encryptedString
	 * @param forceEncryption
	 * @return String
	 * @throws IOException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public String Decrypt(String encryptedString, Boolean forceEncryption) throws IOException, InvalidKeyException,
	  InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		
		if(encryptedString.startsWith(beginCrypt)) {
			encryptedString = encryptedString.substring(beginCrypt.length(), encryptedString.length());
		} else {
			//Not correct encrypted string, return the source string
			if (forceEncryption)
				return "";
			else
				return encryptedString;
		}
		
		//1- Decrypt with BASE64 decoder
		byte[] lbase64 = new BASE64Decoder().decodeBuffer(encryptedString);

		//2- Decrypt the string
		IvParameterSpec oIV = new IvParameterSpec(INIT_IV);
		getChiper().init(Cipher.DECRYPT_MODE, getSkeySpec(), oIV);
		byte[] lDecoded = getChiper().doFinal(lbase64);
		
		//3- Find the last 0x0 value in the decrypted string
		int lFinalIndex = findLast0byte(lDecoded) - 8;
		if(lFinalIndex <= 0) {
			lFinalIndex = lDecoded.length - 8;
		}

		//4- Delete the first 8 byte (IV) and the 0x0 value
		byte[] lFinalDecoded = new byte[lFinalIndex];
		System.arraycopy(lDecoded, 8, lFinalDecoded, 0, lFinalIndex);
		
		//5- Return the formated String
		return new String(lFinalDecoded, "8859_1");
	}

	/**
	 * Encrypt the string
	 * @param notcryptedString
	 * @return String
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public String Encrypt(String notcryptedString) throws InvalidKeyException,
	  InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		
		//1- Correct the padding
		byte[] lToDecrypt = null;
		if(notcryptedString.getBytes().length % 8 != 0) {
			lToDecrypt = correctPadding(notcryptedString.getBytes());
		} else {
			lToDecrypt = notcryptedString.getBytes();
		}

		//2- Load the vector (IV) in front of the string
		byte[] lFinalToDecrypt = new byte[lToDecrypt.length + 8];
		System.arraycopy(INIT_IV, 0, lFinalToDecrypt, 0, INIT_IV.length);
		System.arraycopy(lToDecrypt, 0, lFinalToDecrypt, 8, lToDecrypt.length);

		//3- Generate a vector (IV) and crypt the string
		IvParameterSpec oIV = generateIV();
		getChiper().init(Cipher.ENCRYPT_MODE, getSkeySpec(), oIV);
		byte[] lEncoded = getChiper().doFinal(lFinalToDecrypt);
		
		//4- Encode with BASE64 encoder
		String lbase64 = new BASE64Encoder().encodeBuffer(lEncoded);	

		//5- Return the result
		return beginCrypt.concat(lbase64);
	}
	
	/**
	 * Vector generator
	 * @return IvParameterSpec
	 */
    private IvParameterSpec generateIV() {
    	byte[] b = new byte[8];
    	SecureRandom sr = new SecureRandom();
    	sr.nextBytes(b);
    	return new IvParameterSpec(b);
    }
	
    /**
     * Function for correction of padding
     * @param pCorrect
     * @return byte[]
     */
	private byte[] correctPadding(byte[] pCorrect) {
		int lSize = pCorrect.length;
		int lIndex = 8 - (pCorrect.length % 8);
		byte[] buff = new byte[lSize + lIndex];

		for (int i = 0; i < lSize; i++) {
			buff[i] = pCorrect[i];
		}

		for (int i = lSize; i < lSize + lIndex; i++) {
			buff[i] = 0x0;
		}
		return buff;
	}
	
	/**
	 * Function that return the last index of 0x0 value in tab byte
	 * @param pString
	 * @return int
	 */
	private int findLast0byte(byte[] pString) {
		int lIndex = 0;
		for(int i = 0; i < pString.length; i++) {
			if(i > 8 && pString[i] == 0x0) {
				lIndex = i;
				break;
			}
		}
		return lIndex;
	}
}
