package com.rick.archi.security;

import java.io.IOException;
import java.security.KeyPair;

import org.junit.Test;

import javax.crypto.SecretKey;

public class TestSecurity {

	@Test
	public void testFileType() throws IOException {
		System.out.println(SecurityUtils.getType("/home/eros/1.png").name());
	}

	@Test
	public void testMD5() throws Exception {
		System.out.println(SecurityUtils.md5("password"));
	}

    @Test
    public void testSHA1() throws Exception {
        System.out.println(SecurityUtils.sha1("password"));
    }

    @Test
    public void testSHA512() throws Exception {
        System.out.println(SecurityUtils.sha512("password"));
    }

    @Test
    public void testBase64Encode() {
        System.out.println(SecurityUtils.bytesToBase64("password".getBytes()));
    }

    @Test
    public void testBase64Decode() throws IOException {
        String s = new String(SecurityUtils.base64ToBytes("cGFzc3dvcmQ="),"utf8");
        System.out.println(s);
    }

    @Test
    public void testDES() throws Exception {
        SecretKey key = SecurityUtils.loadKeyDES(SecurityUtils.getKeyDES());
        String encrypted = SecurityUtils.encryptDES("password", key);
        System.out.println(encrypted);
        String decrypted = SecurityUtils.decryptDES(encrypted, key);
        System.out.println(decrypted);
    }

    @Test
    public void testAES() throws Exception {
        SecretKey key = SecurityUtils.loadKeyAES(SecurityUtils.getKeyAES());
        String encrypted = SecurityUtils.encryptAES("password", key);
        System.out.println(encrypted);
        String decrypted = SecurityUtils.decryptAES(encrypted, key);
        System.out.println(decrypted);
    }

    @Test
    public void testRSA() throws Exception {
        String content = "password";
        KeyPair keyPair = SecurityUtils.getKeyPair();
        byte[] publicKey = SecurityUtils.getPublicKey(keyPair);
        byte[] privateKey = SecurityUtils.getPrivateKey(keyPair);
        System.out.println("public key: " + SecurityUtils.bytesToBase64(publicKey));
        System.out.println("private key: " + SecurityUtils.bytesToBase64(privateKey));
        String result = SecurityUtils.publicEncrypt(content, SecurityUtils.bytesToPublicKey(publicKey));
        System.out.println("Encrypted: " + result);
        String decrypted = SecurityUtils.privateDecrypt(result, SecurityUtils.bytesToPrivateKey(privateKey));
        System.out.println("Decryped: " + decrypted);
    }

    @Test
    public void testSign() throws Exception {
        String content = "password";
        KeyPair keyPair = SecurityUtils.getKeyPair();
        byte[] publicKey = SecurityUtils.getPublicKey(keyPair);
        byte[] privateKey = SecurityUtils.getPrivateKey(keyPair);
        String sign = SecurityUtils.sign(content, SecurityUtils.bytesToPrivateKey(privateKey));
        System.out.println("sign: " + sign);
        System.out.println("result: " + SecurityUtils.verify(content, sign, SecurityUtils.bytesToPublicKey(publicKey)));
    }

}
