package com.example.client;

import android.util.Base64;
import android.util.Log;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSA {
    // Class used to encrypt via RSA key

    private RSAPublicKey public_key;    // Public key received from Server (used for encryption only)

    public RSA(String public_key_str) {
        //in: Public key received from server (String)
        //out: Constructor.
        public_key_str = public_key_str.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "").replace("\n", "");
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.decode(public_key_str, Base64.DEFAULT));
            this.public_key = (RSAPublicKey) kf.generatePublic(keySpecX509);
        } catch (Exception e) {
            this.public_key=null;
        }
    }
    public byte[] encrypt(byte[] data) {
        //in: Data to encrypt
        //out: returns the data encrypted with the public key. (bytes array)
        byte[] ret = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, public_key);
            ret = cipher.doFinal(data);
        }
        catch (Exception e) {
            Log.d("EXCEPTION1" ,"" + e.toString());
        }
        return ret;
    }

    public String encryptB64(byte[] data) {
        //in: data to Encrypt
        //out: returns the data encrypted with the public key (as base64 String)
        String strBase64 = "";
        byte[] encrypted_bytes = null;
        try {
            encrypted_bytes = this.encrypt(data);
        }
        catch (Exception e) {
            Log.d("EXCEPTION1" ,"" + e.toString());
        }
        if (encrypted_bytes != null)
            strBase64 = Base64.encodeToString(encrypted_bytes, Base64.DEFAULT);
        return strBase64;
    }
}
