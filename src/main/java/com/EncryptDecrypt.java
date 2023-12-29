package com;

import java.io.IOException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class EncryptDecrypt {	
	
	private String cipher_AES = "AES";
	private static int size = 256;
	
	@Value("${ENCRYPTION_KEY}")
	private String ENCRYPTION_KEY;
	
	byte[] keyDecoder;

	@PostConstruct
	public void setUpDecoder() {
		keyDecoder = Base64.getDecoder().decode(ENCRYPTION_KEY.getBytes());
	}
	
	/**
	 * below functions are for encrpting and decrpting file based generated key
	 */
	
	public byte[] encryptFile(byte[] fileData) throws IOException {
        try {
            SecretKey secretKey = new SecretKeySpec(keyDecoder, cipher_AES);
            Cipher cipher = Cipher.getInstance(cipher_AES);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(fileData);
        } catch (Exception e) {
            throw new IOException("Encryption failed: " + e.getMessage());
        }
    }
	
	public byte[] decryptFile(byte[] encryptedData) throws IOException {
		try {
			SecretKey secretKey = new SecretKeySpec(keyDecoder, cipher_AES);
			Cipher cipher = Cipher.getInstance(cipher_AES);
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return cipher.doFinal(encryptedData);
		}catch (Exception e) {
            throw new IOException("Decryption failed: " + e.getMessage());
        }
	}
	
	
	
	/**
	 * 	//any of these methods can be used to get Key... keyGenerator is best option to go for
	 */
	
	/*
	 * public static void main(String[] ag) throws NoSuchAlgorithmException { String
	 * randomKey =
	 * Base64.getEncoder().encodeToString(getRandomKey(cipher,size).getEncoded());
	 * String secureRaondomKey =
	 * Base64.getEncoder().encodeToString(getSecureRandomKey(cipher,
	 * size).getEncoded()); String keyGeneratorKey =
	 * Base64.getEncoder().encodeToString(getKeyFromKeyGenerator(cipher,
	 * size).getEncoded()); }
	 */
	
	private static Key getRandomKey(String cipher, int keySize) {
	    byte[] randomKeyBytes = new byte[keySize / 8];
	    Random random = new Random();
	    random.nextBytes(randomKeyBytes);
	    return new SecretKeySpec(randomKeyBytes, cipher);
	}
	
	private static Key getSecureRandomKey(String cipher, int keySize) {
	    byte[] secureRandomKeyBytes = new byte[keySize / 8];
	    SecureRandom secureRandom = new SecureRandom();
	    secureRandom.nextBytes(secureRandomKeyBytes);
	    return new SecretKeySpec(secureRandomKeyBytes, cipher);
	}
	
	private static Key getKeyFromKeyGenerator(String cipher, int keySize) throws NoSuchAlgorithmException {
	    KeyGenerator keyGenerator = KeyGenerator.getInstance(cipher);
	    keyGenerator.init(keySize);
	    return keyGenerator.generateKey();
	}
	
}
