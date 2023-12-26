package com.touchfish.Tool;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * RSA加密解密工具类
 */
public class RSAUtils {
    /**
     * 常量字符串
     */
    private static final String RSA = "RSA";

    /**
     * 私钥
     */
    private static final String PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAItn9b5IvrTh1rnhzJJFNQ4UnJBL9kRitVHHklkz28ykYB5IWWM4lvZ7VzBP01AFG/I+otjr62Xx+gurInr22/F8FA4uKOmiYCPPf5h1mEvRK0y20ShYtDiwu9eM8hYkCqBvBHDKYjNAFmw3+HRzgCmGFEnmAG44bKCnZiMqUEf9AgMBAAECgYA05uCksypbrhA0PfHJ2CWIEF5Ri+IKlYLFY/yviTRx9jbbhw0U0BbJtoihtskz5pxyUz6tHuoXp7oBz5GoJCHWcvUGHTfJx8aq2IJwfJKGfi5V1I7ke3H6A8fI9lE8Y62lFSJmZITbUv3TsBiec/GhotYn3x5cu329MFX6miY7QQJBANwuq/jRM9RB+d8NUdO9XsWHVqCVp59J1KyKR7UHTgshH5ahHUGiSBtqM9yirKPnPHJLZpDsloNUDm7xhoLfULkCQQCiFWw7VZ7gKwY6q4nxBcb5BXDb3Fa5JKr7ggduvF8C90nue7TMbVMr0z8HHDvqhjzD06XTYG2P4+xPBwm5E2dlAkAMHkl6xVDb8tGk1B/Xzoljx8Idzn7ORor9ABNYRFGoTSdm6/EnRp4/XAYEs7NaxgROqhW4Dj1udvbgZkyn8VCJAkAJPV9mIoNkFA/O2GiMrN+i4oSEhBMNiuGUZN03mtVvvdkhFzw/Sxwqq2g0Z4+i1vQv1ajmW+DjCwM1nhkXy9thAkBPh46mKPQ58yURs9hlhLlUziUFjq83sSzXJkZQx7EWOud0U8Nriq+xsrVKJRkzntmIhLBacdwQSyImIWkStd7v";

    /**
     * 公钥
     */
    private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCLZ/W+SL604da54cySRTUOFJyQS/ZEYrVRx5JZM9vMpGAeSFljOJb2e1cwT9NQBRvyPqLY6+tl8foLqyJ69tvxfBQOLijpomAjz3+YdZhL0StMttEoWLQ4sLvXjPIWJAqgbwRwymIzQBZsN/h0c4AphhRJ5gBuOGygp2YjKlBH/QIDAQAB";

    /**
     * 获取密钥对象
     * @param keySize RSA算法模长(个人理解应该是模长越大加密安全性越高，但加密过程可能也越长)
     * @return List
     */
    public static List<Key> getRsaObject(int keySize) throws NoSuchAlgorithmException {
        //创建list用来接收公钥对象和私钥对象
        List<Key> keyList = new ArrayList<>();
        //创建RSA密钥生成器
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
        //设置密钥大小，RSA算法的模长=最大加密数据的大小
        keyPairGenerator.initialize(keySize);
        //调用函数生成公钥私钥对象（以对生成密钥）
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        //获取公钥放入list
        keyList.add(keyPair.getPublic());
        //获取私钥放入list
        keyList.add(keyPair.getPrivate());
        //返回list
        return keyList;
    }

    /**
     * 生成公钥私钥的字符串
     * @param keySize 模长
     * @return List
     */
    public static List<String> getRsaKeyString(int keySize) throws NoSuchAlgorithmException {
        //创建list用来接收公钥对象和私钥对象
        List<String> keyList = new ArrayList<>();
        //创建RSA密钥生成器
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
        //设置密钥大小，RSA算法的模长=最大加密数据的大小
        keyPairGenerator.initialize(keySize);
        //调用函数生成公钥私钥对象（以对生成密钥）
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        //将公钥对象转换为字符串通过base64加密
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        //将私钥对象转换为字符串通过base64加密
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        //获取公钥放入list
        keyList.add(publicKey);
        //获取私钥放入list
        keyList.add(privateKey);
        //返回list
        return keyList;
    }

    /**
     * 通过公钥字符串生成公钥对象（RSAPublicKey类型）
     * X509EncodeKeySpec方式（字符串公钥转为RSAPublicKey公钥）
     * @param publicKeyStr  公钥字符串
     * @return 返回RSAPublicKey类型的公钥对象
     */
    public static RSAPublicKey getRSAPublicKeyByX509(String publicKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //密钥工厂创建
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        //公钥字符解密为bytes数组
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyStr);
        //公钥字符串转x509
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        //x509转RSAPublicKey
        return (RSAPublicKey) keyFactory.generatePublic(x509EncodedKeySpec);
    }

    /**
     * 通过私钥字符串生成私钥对象（RSAPrivateKey类型）
     * PKCS8EncodedKeySpec方式（字符串私钥转为RSAPrivateKey公钥）
     * @param privateKey 私钥字符串
     * @return 返回RSAPrivateKey类型的私钥对象
     */
    public static RSAPrivateKey getRSAPrivateKeyByPKCS8(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //密钥工厂创建
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        //私钥字符串解密为bytes数组
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        //私钥字符串转pkcs8
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        //pkcs8转RSAPrivateKey
        return (RSAPrivateKey) keyFactory.generatePrivate(pkcs8EncodedKeySpec);
    }

    /**
     * 公钥加密
     * @param message 需要加密的信息
     * @param rsaPublicKey rsa公钥对象
     * @return 返回信息被加密后的字符串
     */
    public static String encryptByPublicKey(String message, RSAPublicKey rsaPublicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        //RSA加密实例
        Cipher cipher = Cipher.getInstance(RSA);
        //初始化公钥
        cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
        //模长转为字节数
        int modulusSize = rsaPublicKey.getModulus().bitLength()/8;
        //PKCS PADDING长度为11字节，解密数据是除去这11byte
        int maxSingleSize = modulusSize-11;
        //切分字节数，每段不大于maxSingleSize
        byte[][] dataArray = splitArray(message.getBytes(), maxSingleSize);
        //字节数组输出流
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //分组加密，加密后内容写入输出字节流
        for (byte[] s : dataArray){
            byteArrayOutputStream.write(cipher.doFinal(s));
        }
        //使用base64将字节数组转为string类型
        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
    }

    /**
     * 公钥加密
     * @param message 需要加密的信息
     * @param publicKey rsa公钥字符串
     * @return 返回信息被加密后的字符串
     */
    public static String encryptByPublicKey(String message, String publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException, InvalidKeySpecException {
        RSAPublicKey rsaPublicKeyByX509 = getRSAPublicKeyByX509(publicKey);
        return encryptByPublicKey(message,rsaPublicKeyByX509);
    }

    /**
     * 私钥解密密
     * @param encryptedMessage 信息加密后的字符串
     * @param rsaPrivateKey rsa私钥对象
     * @return 返回解密后的字符串
     */
    public static String decryptByPrivateKey(String encryptedMessage, RSAPrivateKey rsaPrivateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        //RSA加密实例
        Cipher cipher = Cipher.getInstance(RSA);
        //初始化公钥
        cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
        //加密算法模长
        int modulusSize = rsaPrivateKey.getModulus().bitLength()/8;
        byte[] dataBytes = encryptedMessage.getBytes();
        //加密做了转码，这里也要用base64转回来
        byte[] decodeData = Base64.getDecoder().decode(dataBytes);
        //切分字节数，每段不大于maxSingleSize
        byte[][] dataArray = splitArray(decodeData, modulusSize);
        //字节数组输出流
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //分组解密，解密后内容写入输出字节流
        for (byte[] s : dataArray){
            byteArrayOutputStream.write(cipher.doFinal(s));
        }
        //使用base64将字节数组转为string类型
        return byteArrayOutputStream.toString();
    }

    /**
     * 私钥解密密
     * @param encryptedMessage 信息加密后的字符串
     * @param privateKey rsa私钥字符串
     * @return 返回解密后的字符串
     */
    public static String decryptByPrivateKey(String encryptedMessage, String privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException, InvalidKeySpecException {
        RSAPrivateKey rsaPublicKeyByX509 = getRSAPrivateKeyByPKCS8(privateKey);
        return decryptByPrivateKey(encryptedMessage,rsaPublicKeyByX509);
    }

    /**
     * 私钥解密密
     * @param encryptedMessage 信息加密后的字符串
     * @return 返回解密后的字符串
     */
    public static String decryptByPrivateKey(String encryptedMessage) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException, InvalidKeySpecException {
        return decryptByPrivateKey(encryptedMessage,PRIVATE_KEY);
    }

    /**
     * 按指定长度切分数组
     * @param byteArrayInfo 需要切分的byte数组
     * @param maxSize 单个字节数组长度
     * @return byte[][]
     */
    private static byte[][] splitArray(byte[] byteArrayInfo, int maxSize){
        int dataLen = byteArrayInfo.length;
        if(dataLen<=maxSize){
            return new byte[][]{byteArrayInfo};
        }
        byte[][] result = new byte[(dataLen-1)/maxSize+1][];
        int resultLen = result.length;
        for (int i = 0; i < resultLen; i++) {
            if(i==resultLen-1){
                int sLen = dataLen-maxSize*i;
                byte[] single = new byte[sLen];
                System.arraycopy(byteArrayInfo, maxSize*i, single, 0, sLen);
                result[i] = single;
                break;
            }
            byte[] single = new byte[maxSize];
            System.arraycopy(byteArrayInfo, maxSize*i, single, 0, maxSize);
            result[i] = single;
        }
        return result;
    }

    /**
     * 返回公钥
     * @return String
     */
    public static String getPublicKey(){
        return PUBLIC_KEY;
    }


}
