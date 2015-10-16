package com.syh.screenadapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
/**
 * DES加密解密操作
 * @author chenxu
 */
public class DESUtil {
	/**
	 * 将私钥保存到指定文件中
	 * @param keyFile 保存私钥的文件
	 */
	public void savePriveKey(String keyFile){
		FileOutputStream fos=null;
		try {
			KeyGenerator keyGen=KeyGenerator.getInstance("DES");
			SecureRandom sr=new SecureRandom();
			keyGen.init(sr);
			SecretKey key=keyGen.generateKey();
			byte[] rawKeyData=key.getEncoded();
			fos=new FileOutputStream(keyFile);
			fos.write(rawKeyData);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}finally{
			if(fos!=null){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 加密字符串
	 * @param text 要加密的字符串
	 * @param keyFile 密钥文件
	 */
	public void encryptionFile(String file,String keyFile){
		SecureRandom sr=new SecureRandom();
		SecretKey key=getPrivetKey(keyFile);
		FileInputStream fis=null;
		FileOutputStream fos=null;
		try {
			Cipher cipher=Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, key,sr);
			fis=new FileInputStream(new File(file));
			byte[] data=new byte[fis.available()];
			fis.read(data);
			byte[] encryptedData=cipher.doFinal(data);
			fos=new FileOutputStream(new File(file));
			fos.write(encryptedData);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(fis!=null){
					fis.close();
				}
				if(fos!=null){
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 对已加密的文件进行解密
	 * @param file 已加密的文件
	 * @param keyFile 密钥文件
	 */
	public void decryptionFile(String file,String keyFile){
		SecretKey key=getPrivetKey(keyFile);
		SecureRandom sr=new SecureRandom();
		FileInputStream fis=null;
		FileOutputStream fos=null;
		try {
			Cipher cipher=Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE,key,sr);
			fis=new FileInputStream(new File(file));
			byte[] encryptedData=new byte[fis.available()];
			fis.read(encryptedData);
			byte[] decryptedData=cipher.doFinal(encryptedData);
			fos=new FileOutputStream(new File(file));
			fos.write(decryptedData);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(fis!=null){
					fis.close();
				}
				if(fos!=null){
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 获取私钥
	 * @param keyFile 私钥文件
	 * @return 个人私钥
	 */
	private SecretKey getPrivetKey(String keyFile) {
		FileInputStream fis=null;
		SecretKey key=null;
		try {
			fis=new FileInputStream(new File(keyFile));
			byte[] rawKeyData=new byte[fis.available()];
			fis.read(rawKeyData);
			DESKeySpec dks=new DESKeySpec(rawKeyData);
			key=SecretKeyFactory.getInstance("DES").generateSecret(dks);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(fis!=null){
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return key;
	}
	public static void main(String[] args){
		DESUtil desUtil=new DESUtil();
//		desUtil.savePriveKey("key.txt");
//		desUtil.encryptionFile("superid.txt", "key.txt");
		desUtil.decryptionFile("superid.txt", "key.txt");
	}
}