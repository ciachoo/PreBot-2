/**
 * Utils
 *
 * Copyright (C) 2013 Sh1fT
 *
 * This file is part of PreBot.
 *
 * PreBot is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * PreBot is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PreBot; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package org.mind.prebot.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import fr.k2r.blowfish.BlowfishCBC;

/**
 * Manage a {@link Utils}
 * @author Sh1fT
 *
 */
public class Utils {
	public static String URLRegex = "https?://([-\\w\\.]+)+(:\\d+)?(/([\\w/_\\.]*(\\?\\S+)?)?)?";
	public static String TitleRegex = "(\\<title.*?\\>)(.+)(\\<\\/title.*?\\>)";
	public static String VDMFeed = "http://feeds.feedburner.com/viedemerde";
	public static String VDMRegex = "<content type=\"html\">.*VDM";
	public static String CNFPage = "http://www.chucknorrisfacts.fr/index.php?p=parcourir&tri=aleatoire";
	public static String CNFRegex = "<div class=\"fact\".*</div>";

	/**
	 * Return the category's color code corresponding to a category
	 * @param category
	 * @return
	 */
	public static String getCategoryCode(String category) {
		switch (category) {
			case "TV-FR":
	 			return "\00302"; // Blue
		 	case "TV-X264":
		 		return "\00303"; // Green
			case "TV-VOSTFR":
				return "\00304"; // Red
			case "TV-MULTi":
				return "\00305"; // Brown
			case "HD-X264":
				return "\00306"; // Purple
			case "DVDRIP":
				return "\00307"; // Olive
			default:
				return "\00315"; // Light Gray
		}
	}

	/**
	 * Return a list of matches for a specified regex and search and pattern
	 * @param regex
	 * @param search
	 * @param p
	 * @return
	 */
	public static List<String> getMatcher(String regex, String search, Integer p) {
		ArrayList<String> matches = new ArrayList<String>();
		Pattern pattern = Pattern.compile(regex, p);
		Matcher matcher = pattern.matcher(search);
		while (matcher.find())
			matches.add(matcher.group());
		return matches;
	}

	/**
	 * Return a list of page titles from a specified search
	 * @param search
	 * @return
	 */
	public static String getTitleMatcher(String search) {
		List<String> titleMatches = getMatcher(TitleRegex, search, Pattern.DOTALL);
		if (!titleMatches.isEmpty())
			return titleMatches.get(0);
		return null;
	}

	/**
	 * Return the HTML code from an URL
	 * @param url
	 * @return
	 */
	public static String getCode(String url) {
		String code = "";
		if(urlExists(url)) {
			BufferedReader in = null;
			try {
				URL site = new URL(url);
				in = new BufferedReader(new InputStreamReader(site.openStream()));
				String inputLine;
                while ((inputLine = in.readLine()) != null)
                    code = code + "\n" + (inputLine);
                in.close();
			} catch (IOException ex) {
				System.out.println("Erreur lors de l'ouverture de l'URL: " + ex.getLocalizedMessage());
			} finally {
				try {
					in.close();
				} catch (IOException ex) {
					System.out.println("Erreur lors de la fermeture du buffer: " + ex.getLocalizedMessage());
				}
			}
		} else
			System.out.println("Le site n'existe pas !");
		return code;
	}

	/**
	 * Check if the selected URL exist or not
	 * @param url
	 * @return
	 */
	public static boolean urlExists(String url) {
		try {
	        URL site = new URL(url);
	        try {
	            site.openStream();
	            return true;
	        } catch (IOException ex) {
	            return false;
	        }
	    } catch (MalformedURLException ex) {
	        return false;
	    }
	}

	/**
	 * Encrypt a password
	 * @param password
	 * @return
	 */
	public static String encryptPassword(String password) {
	    String sha1 = "";
	    try {
	        MessageDigest crypt = MessageDigest.getInstance("SHA-512");
	        crypt.reset();
	        crypt.update(password.getBytes("UTF-8"));
	        sha1 = byteToHex(crypt.digest());
	    } catch(NoSuchAlgorithmException ex) {
	    	System.out.println("Error: " + ex.getLocalizedMessage());
	    	System.exit(1);
	    } catch(UnsupportedEncodingException ex) {
	    	System.out.println("Error: " + ex.getLocalizedMessage());
	    	System.exit(1);
	    }
	    return sha1;
	}

	/**
	 * Convert bytes to hex
	 * @param hash
	 * @return
	 */
	public static String byteToHex(final byte[] hash) {
	    Formatter formatter = new Formatter();
	    for (byte b : hash)
	        formatter.format("%02x", b);
	    String result = formatter.toString();
	    formatter.close();
	    return result;
	}

	/**
	 * Encrypt data using Blowfish
	 * @param fish
	 * @param data
	 * @return
	 */
	public static String encryptData(BlowfishCBC fish, String data) {
		try {
			return fish.Encrypt(data).replace("\n", "");
		} catch (BadPaddingException | IllegalBlockSizeException |
				InvalidAlgorithmParameterException | InvalidKeyException ex) {
			System.out.println("Error: " + ex.getLocalizedMessage());
			System.exit(1);
		}
		return null;
	}

	/**
	 * Decrypt data using Blowfish
	 * @param fish
	 * @param data
	 * @param forceEncryption
	 * @return
	 */
	public static String decryptData(BlowfishCBC fish, String data, Boolean forceEncryption) {
		try {
			return fish.Decrypt(data, forceEncryption).replace("\n", "");
		} catch (BadPaddingException | IllegalBlockSizeException |
				InvalidAlgorithmParameterException | InvalidKeyException |
				IOException ex) {
			System.out.println("Error: " + ex.getLocalizedMessage());
			System.exit(1);
		}
		return null;
	}
}