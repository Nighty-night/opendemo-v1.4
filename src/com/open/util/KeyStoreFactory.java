package com.open.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.encrypt4.Base64Utils;

/**
 * 财付通定制化加密工厂类
 * 
 * @author Ezio
 * 
 */

public class KeyStoreFactory {

	private static PublicKey bankPublicKey;
	private static PrivateKey privateKey;
	private static String certPath = "D:/xxx/cert.pem";
	private static String privatekeyPath = "D:/xxx/cert_key.pem";

	static String PriKeyHeader = "-----BEGIN PRIVATE KEY-----";
	static String PriKeyFooter = "-----END PRIVATE KEY-----";
	static String PubKeyHeader = "-----BEGIN PUBLIC KEY-----";
	static String PubKeyFooter = "-----END PUBLIC KEY-----";
	static String CertHeader = "-----BEGIN CERTIFICATE-----";
	static String CertFooter = "-----END CERTIFICATE-----";

	public static final String KEY_ALGORITHM = "RSA";
	public static final String CERT_TYPE = "X.509";

	public static final String encoding = "UTF-8";
	private static final Log log = LogFactory.getLog(KeyStoreFactory.class);

	public static PublicKey getPublicKey(String path) {
		if (StringUtils.isNotBlank(path)) {
			certPath = path;
		}
		loadCertPublicKey();
		return bankPublicKey;
	}

	public static PrivateKey getPrivateKey(String path) {
		if (StringUtils.isNotBlank(path)) {
			privatekeyPath = path;
		}
		loadPrivateKey();
		return privateKey;
	}

	private static void loadCertPublicKey() {
		try {
			if (StringUtils.isBlank(certPath)) {
				log.error("certPath can not be null.");
				throw new Exception("certPath can not be null.");
			}
			BufferedReader br = new BufferedReader(new FileReader(certPath));
			if (log.isDebugEnabled()) {
				log.debug("读取公钥证书中" + certPath);
			}
			String readLine = null;
			StringBuffer sb = new StringBuffer();
			while ((readLine = br.readLine()) != null) {
				sb.append(readLine);
			}
			br.close();
			String certStr = sb.toString();
			certStr = certStr.replace(PubKeyHeader, "");
			certStr = certStr.replace(PubKeyFooter, "");
			if (log.isDebugEnabled()) {
				log.debug("公钥字符串:" + certStr);
			}

			byte[] keyBytes = Base64Utils.decode(certStr);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			bankPublicKey = keyFactory.generatePublic(keySpec);

		} catch (Exception e) {
			throw new RuntimeException(String.format(
					"get bank public key error[%s] ", certPath), e);
		}
	}

	private static void loadPrivateKey() {
		try {
			if (StringUtils.isBlank(privatekeyPath)) {
				log.error("privatekeyPath can not be null.");
				throw new Exception("privatekeyPath can not be null.");
			}
			if (log.isDebugEnabled()) {
				log.debug("读取私钥证书中" + privatekeyPath);
			}
			BufferedReader br = new BufferedReader(new FileReader(
					privatekeyPath));
			String readLine = null;
			StringBuffer sb = new StringBuffer();
			while ((readLine = br.readLine()) != null) {
				sb.append(readLine);
			}
			br.close();
			String certStr = sb.toString();
			certStr = certStr.replace(PriKeyHeader, "");
			certStr = certStr.replace(PriKeyFooter, "");

			byte[] keyBytes = Base64Utils.decode(certStr);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory keyF = KeyFactory.getInstance(KEY_ALGORITHM);
			privateKey = (RSAPrivateKey) keyF.generatePrivate(keySpec);

		} catch (Exception e) {
			throw new RuntimeException(String.format(
					"get private key error[%s] ", privatekeyPath), e);
		}
	}

	public static PublicKey getPublicKeyFromCert(String path) {
		if (StringUtils.isNotBlank(path)) {
			certPath = path;
		}
		loadCertPublicKeyFromCert();
		return bankPublicKey;
	}

	private static void loadCertPublicKeyFromCert() {
		try {
			if (StringUtils.isBlank(certPath)) {
				log.error("certPath can not be null.");
				throw new Exception("certPath can not be null.");
			}
			BufferedReader br = new BufferedReader(new FileReader(certPath));
			if (log.isDebugEnabled()) {
				log.debug("读取公钥证书中" + certPath);
			}
			String readLine = null;
			StringBuffer sb = new StringBuffer();
			while ((readLine = br.readLine()) != null) {
				sb.append(readLine);
			}
			br.close();
			String certStr = sb.toString();
			certStr = certStr.replace(CertHeader, "");
			certStr = certStr.replace(CertFooter, "");
			if (log.isDebugEnabled()) {
				log.debug("公钥字符串:" + certStr);
			}

			CertificateFactory cf = CertificateFactory.getInstance(CERT_TYPE);
			InputStream is = new ByteArrayInputStream(certStr.getBytes(Charset
					.forName("UTF-8")));
			Certificate c = cf.generateCertificate(is);
			bankPublicKey = c.getPublicKey();

		} catch (Exception e) {
			throw new RuntimeException(String.format(
					"get bank public key error[%s] ", certPath), e);
		}
	}

}
