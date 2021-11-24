package com.vnm.erp.cover;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import javax.xml.crypto.*;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Iterator;

public class JavaSignSHA256 {

    public static void main(String[] arg) {
        try {

            String publicPath = "tcb_mahoa.cer";

            String sSource = "AcctInqef4c7bc9-5a9d-43e1-851c-4a80392c7772VINFASTOWNER19144556699669";
            String signStr = signData_SHA256(sSource);
            System.out.println(">>>>>>>>>> Sign: " + signStr);

            
           //Verify MBCpay
            
            String signStr2 = "f4YRWK2LP3BXdCJ1edmXOiduMKraUFYTKR8zp+cCExN2WesUC8ra487UoP4n6qz8fqQ9TA5d35NMK8JboDKTv/7+w8lyvGsT+JXIZ+dusmhrNlL8o5oVrow8T7I/LMP7/QT12mj+uRwEcoou/EZRuUuFtC3YtNjcKLA8SNf/stU2qwT+lxc60oLhE9jhIRgwzkOb5EYpFRaCpTtytJVDVccGUAI9/6QFrhdwJW2zwbwWaCzy/LhR7IWfkCkjIb01zCI7Jv9gDLgUt/mQn9YeV441VO4dHOmt+jCcC/Q171QYo6OvRC0AAkgbTjA2xd4w48I1Dja9Zm7y0sibb/lAnDMCwgTCeXIOZ/LtEhU+dEY+bqATwMVF2OMK7gSYrL6lgU0MSl0ER5vefou1ZsjtBGwXQIJUE3pFeMaJU0FGpnmQwTKkizgbhitteL3DYUwar/4wZdct8JOyCyDClaBFSamlJacq0fCT/D3WTYudAqRx94UcJbQdeUD9YJFA75hteoXzuOMseH3K1MYVAQrcTipeFOvydGgPLTn2KBq7EwY5BI26ADSaYXsS0PFvP3BckPYQbh25LjUqImpoZd+8PKATXM9c7udgdqtEjL/vVZPEX5blK3Lbonzo292wuwAB63kzrA2zuUqTAopcw8Y1/UrzTAHyRFdGGiPO3WYz4pw=";
            boolean result2 = verifySign_SHA256(sSource, publicPath, signStr);
            System.out.println(">>>>>>>>>> verifySignMBCpay: " + result2);
            
            
            //encode MBCpay
            String encrpTest = encryptWithPublicKeyTCB("Test message");            
            System.out.println(">>>>>>>>>> encryptWithPublicKeyTCB: " + encrpTest);
    
          //decode MBCpay
            String decrpTest = decryptFtWithTCB("o1IoCyCTAhQVIMW+187k1AGK4YKkz1shRRAYgmvmFwFZgh7Eh4wo2A7xHhcIXm0BzjTgVfRjF4tvvVoI+cHY+xu+MWPUTtnGK5qFaZKyWuQsytVjyr3ihLaStyvG49BLNrhUaEA5xAtCMMSm7Yk/SSRJSvL9kzT/DVXiN5s7Ni42/1Kf0iN1abZLXtOyA4wgg7s7PDV3xYjP3e5wcfMS3iEHmLfKf5oU46fTzGy0YyBG7RUhiBszx3rGMKCJ3+qHdS17IicAf7FIvGhrCagwXmo6fweqP1FeNLTLkXiDmXSsQ/C09S9SirT53opX1hG6FFIPkQpjslO4G16o8I9ixuJt9+XgsUGpHlH+naVdD4BJbhZR+27XCrND4bsg0Pi1Z2fFb6tH1wLfKY3zByLOu3pSiGSDeNVkDazAkFGzGJRQ8rV6RsF8+rMx1mH0PsciqMQ2NQxdWiGZ9FHDso51DAofhFaybd65pjTArZM/q8hJs7FhQRsUe/Eys2aMidhNxgQ4TYi+/37oCjlZ5RtoGVp0ExWVyv9Yymwh9A4+00XbRwBAySAk8/+4kmW7znggFpHQJ59rhRVzjjr83dYkS7ZgeIPTfaB0oCcrY2LHIrHbFbuMyNBUXP/h5fZ47rWB3A0eDPHG62w4202UmbrRbXu8WvsARdPK8/UqvL/QnGI4zUfMxNuZ4lFxeZyJLlMYACFi3VUjvHc06NUNSvZUQg==");            
            System.out.println(">>>>>>>>>> decryptFtWithTCB: " + decrpTest);            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String signData_SHA256(Object sSource) {
        byte[] bSource = (byte[]) null;
        byte[] bSourceSha256 = (byte[]) null;
        byte[] bSign = (byte[]) null;
        String bSignBase64 = null;
        try {
            String privateKeyPath = "MBCpay.pfx";
            String keyStorePass = "12345678";
            String keyStoreType = "PKCS12";
            String alias = "le-7f0cf49a-2207-4799-94b5-35f5649631cc";
            String keyPass = "12345678";
            PrivateKey privateKey = getPrivateKey(privateKeyPath, keyStorePass, keyStoreType, alias, keyPass);
            bSource = sSource.toString().getBytes("UTF-8");
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            bSourceSha256 = digest.digest(bSource);
            Signature sha256_rsa = Signature.getInstance("SHA256withRSA");
            sha256_rsa.initSign(privateKey);
            sha256_rsa.update(bSourceSha256);
            bSign = sha256_rsa.sign();
            bSignBase64 = Base64.getEncoder().encodeToString(bSign);

        } catch (Exception var8) {
        }
        return bSignBase64;
    }

    public static boolean verifySign_SHA256(Object sSource, String _path, Object sSign) {
        byte[] bSign = (byte[]) null;
        byte[] bSource = (byte[]) null;
        byte[] bSourceSha256 = (byte[]) null;
        boolean bVerify = false;

        try {
            PublicKey publicKey = getPulicKey(_path);
            bSource = sSource.toString().getBytes("UTF-8");
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            bSourceSha256 = digest.digest(bSource);
            bSign = Base64.getDecoder().decode(sSign.toString());
            
            //System.out.println(">>>>>>>>>>signStr1: " + bSign.toString());
            Signature sha256_rsa = Signature.getInstance("SHA256withRSA");
            sha256_rsa.initVerify(publicKey);
            sha256_rsa.update(bSourceSha256);
            bVerify = sha256_rsa.verify(bSign);
        } catch (Exception var9) {
        }

        return bVerify;
    }
    public static boolean verifySign_SHA256_2(Object sSource, String _path, Object sSign) {
        byte[] bSign = (byte[]) null;
        byte[] bSource = (byte[]) null;
        byte[] bSourceSha256 = (byte[]) null;
        boolean bVerify = false;

        try {
            PublicKey publicKey = getPulicKey(_path);
            bSource = sSource.toString().getBytes("UTF-8");
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            bSourceSha256 = digest.digest(bSource);
            
            bSign = Base64.getDecoder().decode(sSign.toString());
            byte[] decrypted_bSign = JavaSignSHA256.decrypt2(bSign, publicKey);
            //System.out.println(">>>>>>>>>>signStr1: " + bSign.toString());
            String s1 = new String(bSourceSha256);
            String s2 = new String(decrypted_bSign);
            
            GlobalVariables.logger.info("S1:"+s1);
            GlobalVariables.logger.info("S1:"+s2);
            return (s1).equals(s2);
        } catch (Exception var9) {
            GlobalVariables.logger.info("S1:"+var9.getMessage());
        }

        return bVerify;
    }

    public static String encryptWithPublicKeyTCB(String _content) {
        String _pathPublic = "tcb_mahoa.cer";
        return encrypt_AES256(_content, _pathPublic);
    }

    public static String decryptFtWithTCB(String content) {
        String keyStorePath = "MBCpay.pfx";
        String keyStorePass = "12345678";
        String keyStoreType = "PKCS12";
        String alias = "le-7f0cf49a-2207-4799-94b5-35f5649631cc";
        String keyPass = "12345678";
        String _pathPublic = "tcb_mahoa.cer";
        return decrypt_AES256(content, keyStorePath, keyStorePass, keyStoreType, alias, keyPass, _pathPublic);
    }

    public static String verifySHA256(String msgInput, String inputType, String sign, String publicPath, String algorithm) {
        try {
            PublicKey publicKey = getPulicKey(publicPath);
            if (msgInput == null) {
                return "Error Message Input cannot null";
            } else if (inputType == null) {
                return "Error Input type cannot null";
            } else {
                String var6;
                switch ((var6 = inputType.toUpperCase()).hashCode()) {
                    case 87031:
                        if (var6.equals("XML")) {
                            if (verifyXML(msgInput, publicKey)) {
                                return "true";
                            }

                            return "false";
                        }
                        break;
                    case 2571565:
                        if (var6.equals("TEXT")) {
                            if (sign == null) {
                                return "Error Signature value cannot null";
                            }

                            if (verifyMsg(msgInput, publicKey, sign, algorithm)) {
                                return "true";
                            }

                            return "false";
                        }
                }

                return "Error Input type doesn't support";
            }
        } catch (Exception var7) {
            return "Error" + var7.getMessage();
        }
    }

    private static boolean verifing(byte[] bSource, PublicKey publicKey, byte[] bSign, String signAlgorithm) {
        Boolean bverify = false;

        try {
            Signature signature = Signature.getInstance(signAlgorithm);
            signature.initVerify(publicKey);
            signature.update(bSource);
            bverify = signature.verify(bSign);
        } catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException var7) {
            return false;
        }

        return bverify;
    }

    public static boolean verifyXML(String xmlInput, PublicKey publicKey) {
        boolean result = false;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            ByteArrayInputStream inputArr = new ByteArrayInputStream(xmlInput.getBytes("UTF-8"));
            Document doc = builder.parse(inputArr);
            NodeList nl = doc.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
            System.out.println("Number of signature tag : " + nl.getLength());
            if (nl.getLength() == 0) {
                return false;
            } else {
                int length = nl.getLength();

                for (int i = 0; i < length; ++i) {
                    DOMValidateContext valContext = new DOMValidateContext(new X509KeySelector(), nl.item(i));
                    XMLSignatureFactory xmlFactory = XMLSignatureFactory.getInstance("DOM");
                    XMLSignature signature = xmlFactory.unmarshalXMLSignature(valContext);
                    KeyInfo k = signature.getKeyInfo();
                    k.getContent();
                    result = signature.validate(valContext);
                    System.out.println("Number " + i + " " + result);
                }

                return result;
            }
        } catch (Exception var15) {
            return false;
        }
    }

    public static boolean verifyMsg(String sSource, PublicKey publicKey, String sSign, String signAlgorithm) {
        boolean bVerify = false;

        try {
            byte[] bSign = new byte[sSign.length()];
            byte[] bSource = new byte[sSource.length()];
            bSource = sSource.getBytes("UTF-8");
            bSign = DatatypeConverter.parseBase64Binary(sSign);
            bVerify = verifing(bSource, publicKey, bSign, signAlgorithm);
            return bVerify;
        } catch (Exception var8) {
            return false;
        }
    }

    public static PrivateKey getPrivateKey(String keyStorePath, String keyStorePass, String keyStoreType, String alias, String keyPass) {
        try {
            FileInputStream is = new FileInputStream(keyStorePath);
            KeyStore keystore = KeyStore.getInstance(keyStoreType);
            keystore.load(is, keyStorePass.toCharArray());
            if (keystore.isKeyEntry(alias)) {
                PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, keyPass.toCharArray());
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
                X509Certificate cer = (X509Certificate) fact.generateCertificate(fis);
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

    public static String getDataFt(String pathData) throws Exception {
        String _sSource = "FundTransfer";
        Document doc = convertXmlFileToDocument(pathData);
        String value = getValueByPath(doc, "/XferReq/ReqGnlInf/Id");
        _sSource += value;
        value = getValueByPath(doc, "/XferReq/ReqInf/ToAcct/AcctId");
        _sSource += value;
        value = getValueByPath(doc, "/XferReq/ReqInf/ToAcct/AcctTitl");
        _sSource += value;
        value = getValueByPath(doc, "/XferReq/ReqInf/TxDt");
        _sSource += value;
        value = getValueByPath(doc, "/XferReq/ReqInf/TxAmt");
        _sSource += value;
        return _sSource;
    }

    public static String getDataAcctInfo(String pathData) throws Exception {
        String _sSource = "AcctInq";
        Document doc = convertXmlFileToDocument(pathData);
        String value = getValueByPath(doc, "/AcctInqRq/RqUID");
        _sSource += value;
        value = getValueByPath(doc, "/AcctInqRq/MsgRqHdr/SvcIdent/SvcName");
        _sSource += value;
        value = getValueByPath(doc, "/AcctInqRq/MsgRqHdr/ContextRqHdr/ClientApp/Name");
        _sSource += value;
        value = getValueByPath(doc, "/AcctInqRq/MsgRqHdr/CredentialsRqHdr/SubjectRole");
        _sSource += value;
        value = getValueByPath(doc, "/AcctInqRq/MsgRqHdr/CredentialsRqHdr/OvrdExceptionCode");
        _sSource += value;
        value = getValueByPath(doc, "/AcctInqRq/AcctSel/AcctKeys/AcctId");
        _sSource += value;
        return _sSource;
    }

    public static String getDataInquiryOversea(String pathData) throws Exception {
        String _sSource = "InqTransactionStatus";
        Document doc = convertXmlFileToDocument(pathData);
        String value = getValueByPath(doc, "/InqTxStsReq/ReqGnlInf/Id");
        _sSource += value;
        value = getValueByPath(doc, "/InqTxStsReq/ReqInf/ReqTxId");
        _sSource += value;
        return _sSource;
    }

    public static Document convertXmlFileToDocument(String fileUrl) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document doc = null;
        try {
            File xmlFile = new File(fileUrl);
            builder = factory.newDocumentBuilder();
            doc = builder.parse(xmlFile);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return doc;
    }

    public static String getValueByPath(Document doc, String path, String[]... nameSpace) {
        String result = "";
        try {
            Node node = getNodeByPath(doc, path, nameSpace);
            if (node != null) {
                result = node.getTextContent();
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
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

    public static String encrypt_AES_RSA(Object objSource, byte[] keyGen, PublicKey publicKey) {
        byte[] bSource = (byte[]) null;
        String base64Return = null;

        try {
            bSource = objSource.toString().getBytes();
            int ivSize = 16;
            byte[] iv = new byte[ivSize];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyGen, 0, keyGen.length, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
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
            base64Return = DatatypeConverter.printBase64Binary(comReturn);
        } catch (Exception var16) {
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println("Exception Encrypt AES256 + RSA256(KEYGEN):" + var16.toString());
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
        }

        return base64Return;
    }

    public static Node getNodeByPath(Document doc, String path, String[]... nameSpace) {
        Node node = null;
        try {
            if (path == null || path.length() == 0) return null;
            XPath xPath = XPathFactory.newInstance().newXPath();
            if (nameSpace != null && nameSpace.length > 0) {
                NamespaceContext nsContext = new NamespaceContext() {
                    @Override
                    public String getNamespaceURI(String prefix) {
                        for (String[] _ns : nameSpace) {
                            if (prefix.equals(_ns[0]))
                                return _ns[1];
                        }
                        return null;
                    }

                    @Override
                    public String getPrefix(String namespaceURI) {
                        for (String[] _ns : nameSpace) {
                            if (namespaceURI.equals(_ns[1]))
                                return _ns[0];
                        }
                        return null;
                    }

                    @Override
                    public Iterator getPrefixes(String namespaceURI) {
                        return null;
                    }
                };
                xPath.setNamespaceContext(nsContext);

            }
            NodeList nodeList = (NodeList) xPath.compile(path).evaluate(
                    doc, XPathConstants.NODESET);
            if (nodeList != null && nodeList.getLength() > 0) {
                node = nodeList.item(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return node;
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

    public static String decrypt_AES_RSA(Object objSource, PrivateKey privateKey, PublicKey publicKey) {
        int ivSize = 16;
        byte[] bSource = (byte[]) null;
        String deReturn = null;

        try {
            int keyLength = getKeyLength(publicKey) / 8;
            bSource = DatatypeConverter.parseBase64Binary(objSource.toString());
            byte[] enSecretKey = new byte[keyLength];
            System.arraycopy(bSource, 0, enSecretKey, 0, keyLength);
            byte[] iv = new byte[ivSize];
            System.arraycopy(bSource, keyLength, iv, 0, iv.length);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            int encryptedSize = bSource.length - ivSize - keyLength;
            byte[] encryptedBytes = new byte[encryptedSize];
            System.arraycopy(bSource, keyLength + ivSize, encryptedBytes, 0, encryptedSize);
            byte[] decryptedKey = decrypt(enSecretKey, privateKey);
            SecretKeySpec originalKey = new SecretKeySpec(decryptedKey, 0, decryptedKey.length, "AES");
            Cipher cipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
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
    public static byte[] decrypt2(byte[] enSecretKey, PublicKey privateKey) {
        try {
            Cipher rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(2, privateKey);
            byte[] decodedKeyBytes = rsaCipher.doFinal(enSecretKey);
            return decodedKeyBytes;
        } catch (Exception var4) {
            return null;
        }
    }

    public static int getKeyLength(PublicKey publicKey) {
        try {
            RSAPublicKey rsaPk = (RSAPublicKey) publicKey;
            int result = rsaPk.getModulus().bitLength();
            return result;
        } catch (Exception var3) {
            return 0;
        }
    }

    public static String readFileFrPath(String path) {

        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(path))) {

            // read line by line
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }

        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }

        // System.out.println(sb);
        return sb.toString();
    }
}

class X509KeySelector extends KeySelector {
    public X509KeySelector() {
    }

    public KeySelectorResult select(KeyInfo keyInfo, Purpose purpose, AlgorithmMethod method, XMLCryptoContext context) throws KeySelectorException {
        Iterator ki = keyInfo.getContent().iterator();

        while(true) {
            XMLStructure info;
            do {
                if (!ki.hasNext()) {
                    throw new KeySelectorException("No key found!");
                }

                info = (XMLStructure)ki.next();
            } while(!(info instanceof X509Data));

            X509Data x509Data = (X509Data)info;
            Iterator xi = x509Data.getContent().iterator();

            while(xi.hasNext()) {
                Object o = xi.next();
                if (o instanceof X509Certificate) {
                    final PublicKey key = ((X509Certificate)o).getPublicKey();
                    System.out.println(key.toString());
                    if (this.algEquals(method.getAlgorithm(), key.getAlgorithm())) {
                        return new KeySelectorResult() {
                            public Key getKey() {
                                return key;
                            }
                        };
                    }
                }
            }
        }
    }

    boolean algEquals(String algURI, String algName) {
        return algName.equalsIgnoreCase("DSA") && algURI.equalsIgnoreCase("http://www.w3.org/2000/09/xmldsig#dsa-sha1") || algName.equalsIgnoreCase("RSA") && algURI.equalsIgnoreCase("http://www.w3.org/2000/09/xmldsig#rsa-sha1");
    }
}