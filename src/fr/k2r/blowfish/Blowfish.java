/*
* Important information :
* To Use Key > 16 char, you must update two jar files in your jre or jdk.
* 		Java Cryptography Extension (JCE)
* 		Unlimited Strength Jurisdiction Policy Files 1.4.2
* 		http://java.sun.com/j2se/1.4.2/download.html#docs
* Update the two files in jre\lib\security
* 		-> local_policy.jar
* 		-> US_export_policy.jar
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

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;


/**
 * Blowfish abstract class
 * @author k2r
 * The blowfish abstract class
 */
public abstract class Blowfish {

	/**
	 * Crypto cipher
	 */
	private Cipher ecipher;
	
	/**
	 * Keyspec for crypto
	 */
	private SecretKeySpec skeySpec;
	
	/**
	 * Constructor
	 * @param key
	 * @param mode
	 */
	public Blowfish(String key, String mode) {
		skeySpec = new SecretKeySpec(key.getBytes(), "Blowfish");
		try { 
			ecipher = Cipher.getInstance(mode);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * Decrypt the string
	 * @param encryptedString
	 * @return
	 * @throws IOException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public abstract String Decrypt(String encryptedString) throws IOException, InvalidKeyException,
	  InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException;

	/**
	 * Decrypt the string
	 * @param encryptedString
	 * @param forceEncryption
	 * @return
	 * @throws IOException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public abstract String Decrypt(String encryptedString, Boolean forceEncryption) throws IOException, InvalidKeyException,
	  InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException;
	
	/**
	 * Encrypt the string
	 * @param notcryptedString
	 * @return String
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public abstract String Encrypt(String notcryptedString) throws InvalidKeyException,
	  InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException;
	
	/**
	 * skeySpec getter
	 * @return SecretKeySpec
	 */
	public SecretKeySpec getSkeySpec() {
		return this.skeySpec;
	}
	
	/**
	 * ecipher getter
	 * @return Cipher
	 */
	public Cipher getChiper() {
		return this.ecipher;
	}
}
