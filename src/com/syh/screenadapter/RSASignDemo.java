package com.syh.screenadapter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * 签名示例
 */
public class RSASignDemo {
	/**
	 * 算法名称
	 */
	public static final String ALGORITHM = "RSA";

	/**
	 * 用openssl产生的私钥文件
	 */
	public static final String PRIVATE_KEY_FILE = "/Users/yongheshen/Desktop/Rsakey/rsa_private_key.key";

	/**
	 * 用openssl产生的公钥文件
	 */
	public static final String PUBLIC_KEY_FILE = "/Users/yongheshen/Desktop/Rsakey/rsa_public_key.key";

	/**
	 * 用openssl产生的私钥签名
	 * 
	 * @param text
	 *            用于签名数据
	 * @param key
	 *            openssl产生的私钥
	 * @return Encrypted 签名
	 * @throws java.lang.Exception
	 */
	private static byte[] signatureBySHA1WithRSA(byte[] text, PrivateKey key)
	{
		byte[] signatureText = null;
		try
		{
			final Signature signature = Signature.getInstance("SHA1withRSA");
			signature.initSign(key);
			signature.update(text);
			signatureText = signature.sign();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return signatureText;
	}

	/**
	 * 用加密机产生的公钥验证签名
	 * 
	 * @param text
	 *            用于签名数据
	 * @param signMessage
	 *            签名
	 * @param key
	 *            加密机产生的公钥
	 * @return verifyResult 是否通过验证
	 * @throws java.lang.Exception
	 */
	private static Boolean verifySignature(byte[] text, byte[] signMessage,
			PublicKey key)
	{
		Boolean verifyResult = null;
		try
		{
			final Signature signature = Signature.getInstance("SHA1withRSA");
			signature.initVerify(key);
			signature.update(text);
			verifyResult = signature.verify(signMessage);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return verifyResult;
	}

	/**
	 * 加签方法
	 * 
	 * @param origText
	 *            待签原文
	 * @return
	 */
	public static String sign(String origText)
	{
		BufferedReader priBuf = null;
		String ret = "";
		try
		{
			// 从公私钥文件读取公私钥
			priBuf = new BufferedReader(new FileReader(PRIVATE_KEY_FILE));
			StringBuffer strPrivateKey = new StringBuffer();
			String line = "";
			while ((line = priBuf.readLine()) != null)
			{
				strPrivateKey.append(line);
			}
			priBuf.close();
			// 从读取的私钥中去除多余的头和尾信息
			String strPrivKey = strPrivateKey.toString()
					.replace("-----BEGIN PRIVATE KEY-----", "")
					.replace("-----END PRIVATE KEY-----", "");
			// 用base64解码
			byte[] privKeyByte = Base64.decodeBase64(strPrivKey.getBytes());
			// 私钥需要使用pkcs8格式的
			PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(
					privKeyByte);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PrivateKey privKey = kf.generatePrivate(privKeySpec);

			ret = bytes2HexStr(signatureBySHA1WithRSA(
					origText.getBytes(Charset.forName("UTF-8")), privKey));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 验证签名
	 * 
	 * @param sign
	 *            签名
	 * @param origText
	 *            待签原文
	 * @return
	 */
	public static Boolean verifySign(String sign, String origText)
	{
		BufferedReader pubBuf = null;
		Boolean result = Boolean.FALSE;
		try
		{
			// 从公钥文件读取公钥
			pubBuf = new BufferedReader(new FileReader(PUBLIC_KEY_FILE));
			StringBuffer strPublicKey = new StringBuffer();
			String line = "";
			while ((line = pubBuf.readLine()) != null)
			{
				strPublicKey.append(line);
			}
			pubBuf.close();
			// 从读取的公钥中去除多余的头和尾信息
			String strPubKey = strPublicKey.toString()
					.replace("-----BEGIN PUBLIC KEY-----", "")
					.replace("-----END PUBLIC KEY-----", "");
			byte[] pubKeyByte = Base64.decodeBase64(strPubKey.getBytes());
			// 公钥使用x509格式的
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pubKeyByte);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PublicKey pubKey = kf.generatePublic(pubKeySpec);

			result = verifySignature(
					origText.getBytes(Charset.forName("UTF-8")),
					hexStr2Bytes(sign), pubKey);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	public static void main(String[] args) throws UnsupportedEncodingException
	{
		String str = "renshetong shaoxing";
		String sign = sign(str);
		System.out.println("加签后密文： " + sign);
		Boolean result = verifySign(sign, str);
		System.out.println("验签结果： " + result);

	}

	private static String bytes2HexStr(byte[] b)
	{
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++)
		{
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase();
	}

	private static byte[] hexStr2Bytes(String src)
	{
		/* 对输入值进行规范化整理 */
		src = src.trim().replace(" ", "").toUpperCase();
		// 处理值初始化
		int m = 0, n = 0;
		int iLen = src.length() / 2; // 计算长度
		byte[] ret = new byte[iLen]; // 分配存储空间

		for (int i = 0; i < iLen; i++)
		{
			m = i * 2 + 1;
			n = m + 1;
			ret[i] = (byte) (Integer.decode("0x" + src.substring(i * 2, m)
					+ src.substring(m, n)) & 0xFF);
		}
		return ret;
	}

}
