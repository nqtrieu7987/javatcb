package com.vnm.erp.cover;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Base64;

public class JavaSecutityEncrypt_V2 {

    public static void main(String[] args) throws Exception {
        String _pathPublic = "d:\\TCB\\other\\Singnature\\keystore\\KeyTestGrap/mobivi.cer";
        String s = encrypt_AES256("This is the first string to test", _pathPublic);
        System.out.println(s);
        String x = decryptFtWithTCB(s);
        System.out.println(x);
    }
    public static String decryptFtWithTCB(String content) {
        String keyStorePath = "d:\\TCB\\other\\Singnature\\keystore\\KeyTestGrap\\mobivi.pfx";
        String keyStorePass = "1";
        String keyStoreType = "PKCS12";
        String alias = "le-7f0cf49a-2207-4799-94b5-35f5649631cc";
        String keyPass = "1";
        String _pathPublic = "d:\\TCB\\other\\Singnature\\keystore\\KeyTestGrap\\mobivi.cer";
        return decrypt_AES256(content, keyStorePath, keyStorePass, keyStoreType, alias, keyPass, _pathPublic);
        //return com.tcb.security.utils.Utils.asymmantricDecrypt(content, keyStorePath, keyStoreType, keyStorePass, keyPass, _pathPublic, "AES256RSA");
    }

    public static PublicKey getPulicKey(String certPath) {
        try {
            FileInputStream fis = new FileInputStream(certPath);
            if (fis == null) {
                System.out.println("+++++++++++++++++++++++++++++++++++++++++");
                System.out.println("TCB path PublicKey is Null!!!!!!!");
                System.out.println("+++++++++++++++++++++++++++++++++++++++++");
                return null;
            } else {
                CertificateFactory fact = CertificateFactory.getInstance("X.509");
                X509Certificate cer = (X509Certificate)fact.generateCertificate(fis);
                PublicKey publicKey = cer.getPublicKey();
                fis.close();
                return publicKey;
            }
        } catch (Exception var5) {
            System.out.println("+++++++++++++++++++++++++++++++++++++++++");
            System.out.println("Exception TCB Get Publickey :" + var5.toString());
            System.out.println("+++++++++++++++++++++++++++++++++++++++++");
            return null;
        }
    }

    public static PrivateKey getPrivateKey(String keyStorePath, String keyStorePass, String keyStoreType, String alias, String keyPass) {
        try {
            FileInputStream is = new FileInputStream(keyStorePath);
            KeyStore keystore = KeyStore.getInstance(keyStoreType);
            keystore.load(is, keyStorePass.toCharArray());
            if (keystore.isKeyEntry(alias)) {
                PrivateKey privateKey = (PrivateKey)keystore.getKey(alias, keyPass.toCharArray());
                is.close();
                return privateKey;
            } else {
                is.close();
                System.out.println("+++++++++++++++++++++++++++++++++++++++++");
                System.out.println("TCB path PrivateKey is Null!!!!!!!");
                System.out.println("+++++++++++++++++++++++++++++++++++++++++");
                return null;
            }
        } catch (Exception var8) {
            System.out.println("+++++++++++++++++++++++++++++++++++++++++");
            System.out.println("Exception TCB Get PrivateKey :" + var8.toString());
            System.out.println("+++++++++++++++++++++++++++++++++++++++++");
            return null;
        }
    }

    public static String encrypt_AES256(String _content, String _path) {
        String _result = null;

        try {
            SecureRandom rand = new SecureRandom();
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(256, rand);
            SecretKey secretKey = generator.generateKey();
            PublicKey publicKey = getPulicKey(_path);
            if (publicKey == null) {
                System.out.println("+++++++++++++++++++++++++++++++++++++++++");
                System.out.println("TCB Get PublicKey is Null!!!!!!!");
                System.out.println("+++++++++++++++++++++++++++++++++++++++++");
                throw new Exception();
            }

            _result = encrypt_AES_RSA(_content, secretKey.getEncoded(), publicKey);
        } catch (Exception var7) {
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println("Exception TCB Security Encrypt AES256:" + var7.toString());
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
        }

        return _result;
    }

    public static String decrypt_AES256(String _content, String keyStorePath, String keyStorePass, String keyStoreType, String alias, String keyPass, String _pathPublic) {
        String _result = null;

        try {
            PublicKey publicKey = getPulicKey(_pathPublic);
            PrivateKey privatekey = getPrivateKey(keyStorePath, keyStorePass, keyStoreType, alias, keyPass);
            if (privatekey == null) {
                System.out.println("+++++++++++++++++++++++++++++++++++++++++");
                System.out.println("TCB Get PrivateKey is Null!!!!!!!");
                System.out.println("+++++++++++++++++++++++++++++++++++++++++");
                throw new Exception();
            }

            _result = decrypt_AES_RSA(_content, privatekey, publicKey);
        } catch (Exception var10) {
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println("Exception TCB Security Decrypt AES256:" + var10.toString());
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
        }

        return _result;
    }

    public static String encrypt_AES_RSA(Object objSource, byte[] keyGen, PublicKey publicKey) {
        byte[] bSource = (byte[])null;
        String base64Return = null;

        try {
            bSource = objSource.toString().getBytes();
            int ivSize = 16;
            byte[] iv = new byte[ivSize];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            /*IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);*/
            GCMParameterSpec ivParameterSpec = new GCMParameterSpec(ivSize * Byte.SIZE, iv);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyGen, 0, keyGen.length, "AES");
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(1, secretKeySpec, ivParameterSpec);
            byte[] enSource = cipher.doFinal(bSource);
            byte[] comIVandEnSource = new byte[ivSize + enSource.length];
            System.arraycopy(iv, 0, comIVandEnSource, 0, ivSize);
            System.arraycopy(enSource, 0, comIVandEnSource, ivSize, enSource.length);
            Cipher rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(1, publicKey);
            byte[] enSecretKey = rsaCipher.doFinal(secretKeySpec.getEncoded());
            byte[] comReturn = new byte[enSecretKey.length + comIVandEnSource.length];
            System.arraycopy(enSecretKey, 0, comReturn, 0, enSecretKey.length);
            System.arraycopy(comIVandEnSource, 0, comReturn, enSecretKey.length, comIVandEnSource.length);
            //base64Return = DatatypeConverter.printBase64Binary(comReturn);
            base64Return = Base64.getEncoder().encodeToString(comReturn);
        } catch (Exception var16) {
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println("Exception Encrypt AES256 + RSA256(KEYGEN):" + var16.toString());
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
        }

        return base64Return;
    }

    public static String decrypt_AES_RSA(Object objSource, PrivateKey privateKey, PublicKey publicKey) {
        int ivSize = 16;
        byte[] bSource = (byte[])null;
        String deReturn = null;

        try {
            int keyLength = getKeyLength(publicKey) / 8;
            //bSource = DatatypeConverter.parseBase64Binary(objSource.toString());
            bSource = Base64.getDecoder().decode(objSource.toString());
            byte[] enSecretKey = new byte[keyLength];
            System.arraycopy(bSource, 0, enSecretKey, 0, keyLength);
            byte[] iv = new byte[ivSize];
            System.arraycopy(bSource, keyLength, iv, 0, iv.length);
            /*IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);*/
            GCMParameterSpec ivParameterSpec = new GCMParameterSpec(ivSize * Byte.SIZE, iv);
            int encryptedSize = bSource.length - ivSize - keyLength;
            byte[] encryptedBytes = new byte[encryptedSize];
            System.arraycopy(bSource, keyLength + ivSize, encryptedBytes, 0, encryptedSize);
            byte[] decryptedKey = decrypt(enSecretKey, privateKey);
            SecretKeySpec originalKey = new SecretKeySpec(decryptedKey, 0, decryptedKey.length, "AES");
            Cipher cipherDecrypt = Cipher.getInstance("AES/GCM/NoPadding");
            cipherDecrypt.init(2, originalKey, ivParameterSpec);
            byte[] decrypted = cipherDecrypt.doFinal(encryptedBytes);
            deReturn = new String(decrypted, "UTF-8");
        } catch (Exception var16) {
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println("Exception Decrypt AES256 + RSA256(KEYGEN):" + var16.toString());
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
        }

        return deReturn;
    }

    public static byte[] decrypt(byte[] enSecretKey, PrivateKey privateKey) {
        try {
            Cipher rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(2, privateKey);
            byte[] decodedKeyBytes = rsaCipher.doFinal(enSecretKey);
            return decodedKeyBytes;
        } catch (Exception var4) {
            return null;
        }
    }
    public static byte[] decrypt2(byte[] enSecretKey, PublicKey pubickey) {
            try {
                Cipher rsaCipher = Cipher.getInstance("RSA");
                rsaCipher.init(2, pubickey);
                byte[] decodedKeyBytes = rsaCipher.doFinal(enSecretKey);
                return decodedKeyBytes;
            } catch (Exception var4) {
                return null;
            }
        }
    public static int getKeyLength(PublicKey publicKey) {
        try {
            RSAPublicKey rsaPk = (RSAPublicKey)publicKey;
            int result = rsaPk.getModulus().bitLength();
            return result;
        } catch (Exception var3) {
            return 0;
        }
    }

    public byte[] encrypt(byte[] byteArray, PublicKey publicKey) throws Exception {
        Cipher rsaCipher = Cipher.getInstance("RSA");
        rsaCipher.init(1, publicKey);
        byte[] encodedKeyBytes = rsaCipher.doFinal(byteArray);
        return encodedKeyBytes;
    }
}
