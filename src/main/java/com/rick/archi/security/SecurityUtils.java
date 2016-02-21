package com.rick.archi.security;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class SecurityUtils {
	
	private static String bytesToHexString(byte[] src){   
	    StringBuilder stringBuilder = new StringBuilder();
	    if (src == null || src.length <= 0) {   
	        return null;   
	    }   
	    for (int i = 0; i < src.length; i++) {   
	        int v = src[i] & 0xFF;   
	        String hv = Integer.toHexString(v);   
	        if (hv.length() < 2) {   
	            stringBuilder.append(0);   
	        }   
	        stringBuilder.append(hv);   
	    }   
	    return stringBuilder.toString();   
	}
	
	private static byte[] hexStringToBytes(String hexString) {   
	    if (hexString == null || hexString.equals("")) {   
	        return null;   
	    }   
	    hexString = hexString.toUpperCase();   
	    int length = hexString.length() / 2;   
	    char[] hexChars = hexString.toCharArray();   
	    byte[] d = new byte[length];   
	    for (int i = 0; i < length; i++) {   
	        int pos = i * 2;   
	        d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));   
	    }   
	    return d;   
	}  
	
	private static byte charToByte(char c) {   
	    return (byte) "0123456789ABCDEF".indexOf(c);   
	}


	//File type

	private static String getFileHeader(String filePath) throws IOException {
		byte[] b = new byte[28];
		InputStream inputStream = null;
		inputStream = new FileInputStream(filePath);
		inputStream.read(b, 0, 28);
		inputStream.close();
		return bytesToHexString(b);
	}

	/**
	 * check file type
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static FileType getType(String filePath) throws IOException {
		String fileHead = getFileHeader(filePath);
		if(fileHead == null || fileHead.length() == 0) {
			return null;
		}
		fileHead = fileHead.toUpperCase();
		FileType[] fileTypes = FileType.values();
		for(FileType type: fileTypes) {
			if(fileHead.startsWith(type.getValue())) {
				return type;
			}
		}
		return null;
	}

	//hash

	/**
	 * md5 hash
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public static String md5(String content) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] bytes = md.digest(content.getBytes("utf8"));
		return bytesToHexString(bytes);
	}

	/**
	 * sha1
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public static String sha1(String content) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		byte[] bytes = md.digest(content.getBytes("utf8"));
		return bytesToHexString(bytes);
	}

	/**
	 * sha512
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public static String sha512(String content) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		byte[] bytes = md.digest(content.getBytes("utf8"));
		return bytesToHexString(bytes);
	}

	/**
	 * base64 encode
	 * @param bytes
	 * @return
	 */
	public static String bytesToBase64(byte[] bytes) {
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(bytes);
	}

	/**
	 * base64 decode
	 * @param base64
	 * @return
	 * @throws IOException
	 */
	public static byte[] base64ToBytes(String base64) throws IOException {
		BASE64Decoder decoder = new BASE64Decoder();
		return decoder.decodeBuffer(base64);
	}


	//DES

	public static byte[] getKeyDES() throws NoSuchAlgorithmException {
		KeyGenerator keyGen = KeyGenerator.getInstance("DES");
		keyGen.init(56);
		SecretKey key = keyGen.generateKey();
		return key.getEncoded();
	}

	public static SecretKey loadKeyDES(byte[] bytes) throws IOException {
		SecretKey key = new SecretKeySpec(bytes, "DES");
		return key;
	}

	public static String encryptDES(String source, SecretKey key) throws Exception {
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] bytes = cipher.doFinal(source.getBytes());
		return bytesToBase64(bytes);
	}

	public static String decryptDES(String source, SecretKey key) throws Exception{
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] bytes = cipher.doFinal(base64ToBytes(source));
		return new String(bytes, "utf8");
	}

	//AES
	public static byte[] getKeyAES() throws NoSuchAlgorithmException {
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(128);
		SecretKey key = keyGen.generateKey();
		return key.getEncoded();
	}

	public static SecretKey loadKeyAES(byte[] bytes) throws IOException {
		SecretKey key = new SecretKeySpec(bytes, "AES");
		return key;
	}

	public static String encryptAES(String source, SecretKey key) throws Exception {
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] bytes = cipher.doFinal(source.getBytes());
		return bytesToBase64(bytes);
	}

	public static String decryptAES(String source, SecretKey key) throws Exception{
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] bytes = cipher.doFinal(base64ToBytes(source));
		return new String(bytes, "utf8");
	}


    //RSA

    public static KeyPair getKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(512);
        KeyPair keyPair = keyGen.generateKeyPair();
        return keyPair;
    }

    public static byte[] getPublicKey(KeyPair keyPair) {
        PublicKey publicKey = keyPair.getPublic();
        byte[] bytes = publicKey.getEncoded();
        return bytes;
    }

    public static byte[] getPrivateKey(KeyPair keyPair) {
        PrivateKey privateKey = keyPair.getPrivate();
        byte[] bytes = privateKey.getEncoded();
        return bytes;
    }

    public static PublicKey bytesToPublicKey(byte[] pubBytes) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    public static PrivateKey bytesToPrivateKey(byte[] priBytes) throws Exception {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(priBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    public static String publicEncrypt(String content, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] bytes = cipher.doFinal(content.getBytes());
        return bytesToBase64(bytes);
    }

    public static String privateDecrypt(String content, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] bytes = cipher.doFinal(base64ToBytes(content));
        return new String(bytes, "utf8");
    }


    //sign
    public static String sign(String content, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA1withRSA"); //MD5withRSA, SHA1withRSA
        signature.initSign(privateKey);
        signature.update(content.getBytes());
        return bytesToBase64(signature.sign());
    }

    public static boolean verify(String content, String sign, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initVerify(publicKey);
        signature.update(content.getBytes());
        return signature.verify(base64ToBytes(sign));
    }

	
	
}
