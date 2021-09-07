package com.example.client;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {
	
	private static byte[] keyBytes; 		// Symetric key (Bytes)
	private static SecretKeySpec keySpec;	// Secret Key that is Generated for every Encryption
	private static Cipher cipher=null;		// Cipher for encryption
	private static IvParameterSpec ivSpec;	// Encryption Vector

	public static boolean setKey(byte[] keyB, byte[] nonce) {
		//in: symetric key
		//out: sets the key for the current
		keyBytes = keyB;
		keySpec = new SecretKeySpec(keyBytes, "AES");

		// Create key cipher
		byte[] iv = new byte[16];
		System.arraycopy(nonce, 0, iv, 0, nonce.length);
		ivSpec = new IvParameterSpec(iv);
		cipher=null;
		try {
			cipher = Cipher.getInstance("AES/CTR/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
		} catch (Exception e) {
			Log.d("Exception!!", ""+e.toString());
		}
		if(cipher==null)
			return false;
		return true;
	}
	public static byte[] encrypt(String msg, byte[] keyBytes) {
		//in: Message
		//out: returns the message Encrypted with

		//Generate Nonce:
		byte[] nonce = new byte[8];
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.nextBytes(nonce);

		// Create key
		if(!setKey(keyBytes, nonce))
			return null;

		//Encrypt
	    ByteArrayInputStream bIn = new ByteArrayInputStream(msg.getBytes());
	    CipherInputStream cIn = new CipherInputStream(bIn, cipher);
	    ByteArrayOutputStream bOut = new ByteArrayOutputStream();
	    int ch;
	    try {
			while ((ch = cIn.read()) >= 0)
			  bOut.write(ch);
		} catch (IOException e) {
			Log.d("Exception!!", ""+e.toString());
		}
	    byte[] cipherText = bOut.toByteArray();

	    //encrypted = nonce + encrypted;
  		byte[] encrypted = new byte[nonce.length + cipherText.length];
  		System.arraycopy(nonce, 0, encrypted, 0, nonce.length);
  		System.arraycopy(cipherText, 0, encrypted, nonce.length, cipherText.length);
  		return encrypted;
	}

	public static byte[] decrypt(byte[] cipherText, byte[] keyB) {
		//in: cipherText - encrypted bytes
		//out: returns the decrypted bytes
		//Turn encrypted byte[] into the encrypted string and the encryption nonce
		byte[] nonce=new byte[8], encrypted=new byte[cipherText.length-8];
		for(int i=0;i<8;i++)
			nonce[i] = cipherText[i];
		for(int i=8;i<cipherText.length;i++)
			encrypted[i-8]=cipherText[i];
		keyBytes = keyB;
		if(!setKey(keyB, nonce))
			return null;

		//Decrypt
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
	    CipherOutputStream cOut = new CipherOutputStream(bOut, cipher);
	    try {
			cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
			cOut.write(encrypted);
		    cOut.close();
		} catch (Exception e) {
			Log.d("Exception!!", ""+e.toString());
		}
	    return bOut.toByteArray();
	}
	public byte[] getKey() {
		//in: nothing
		//out: returns the Secret key (byte array)
		return this.keyBytes;
	}

}

