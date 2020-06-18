package com.open.util;

import java.net.SocketTimeoutException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpClientUtil {

	private static Log logger = LogFactory.getLog(HttpClientUtil.class);

	public static final int CONNTIMEOUT = 10000;
	public static final int READTIMEOUT = 10000;
	public static final String CHARSET = "UTF-8";

	private static HttpClient client = null;

	static {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(128);
		cm.setDefaultMaxPerRoute(128);
		client = HttpClients.custom().setConnectionManager(cm).build();
	}

	/**
	 * 发送一个 Post 请求，使用指定的字符集编码
	 * 
	 * @param url
	 * @param body
	 *            RequestBody
	 * @param mimeType
	 *            例如 application/xml "application/x-www-form-urlencoded"
	 *            a=1&b=2&c=3 application/json
	 * @param CHARSET
	 *            编码
	 * @param CONNTIMEOUT
	 *            建立链接超时时间（毫秒）
	 * @param READTIMEOUT
	 *            响应超时时间（毫秒）
	 * @return ResponseBody 使用指定的字符集编码
	 * @throws ConnectTimeoutException
	 *             建立链接超时异常
	 * @throws SocketTimeoutException
	 *             响应超时
	 * @throws Exception
	 */
	public static String post(String url, String body, String mimeType,
			String CHARSET, Integer CONNTIMEOUT, Integer READTIMEOUT)
			throws ConnectTimeoutException, SocketTimeoutException, Exception {
		logger.info("发送HTTP(S) POST 请求：" + url + " | " + mimeType + " | "
				+ CHARSET + " | " + CONNTIMEOUT + " | " + READTIMEOUT);
		HttpClient client = null;
		HttpResponse res = null;
		HttpPost post = new HttpPost(url);
		String result = null;
		try {
			if (StringUtils.isNotBlank(body)) {
				HttpEntity entity = new StringEntity(body, ContentType.create(
						mimeType, CHARSET));
				post.setEntity(entity);
			}
			// 设置参数
			Builder customReqConf = RequestConfig.custom();
			if (CONNTIMEOUT != null) {
				customReqConf.setConnectTimeout(CONNTIMEOUT);
			}
			if (READTIMEOUT != null) {
				customReqConf.setSocketTimeout(READTIMEOUT);
			}
			post.setConfig(customReqConf.build());

			if (url.startsWith("https")) {
				// 执行 Https 请求.
				//测试环境忽略ssl证书
				client = getHttps();
				
				//生产环境校验ssl证书
				//client = createSSLInsecureClient();
				res = client.execute(post);
			} else {
				// 执行 Http 请求.
				client = HttpClientUtil.client;
				res = client.execute(post);
			}
			logger.info("response headers : "
					+ Arrays.toString(res.getAllHeaders()));
			if (res.getStatusLine().getStatusCode() == 200) {
				result = IOUtils
						.toString(res.getEntity().getContent(), CHARSET);
			} else {
				logger.error("HTTP(S) POST 请求，状态异常："
						+ res.getStatusLine().getStatusCode() + " | " + url);
				result = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.abort();
			if (null != res) {
				EntityUtils.consumeQuietly(res.getEntity());
			}
			if (url.startsWith("https") && client != null
					&& client instanceof CloseableHttpClient) {
				((CloseableHttpClient) client).close();
			}
		}
		return result;
	}

	/**
	 * 提交Form表单
	 * 
	 * @param url
	 * @param params
	 * @param CONNTIMEOUT
	 * @param READTIMEOUT
	 * @return ResponseBody 使用指定的字符集编码
	 * @throws ConnectTimeoutException
	 * @throws SocketTimeoutException
	 * @throws Exception
	 */
	public static String postForm(String url, Map<String, String> params,
			Map<String, String> headers, Integer CONNTIMEOUT,
			Integer READTIMEOUT) throws ConnectTimeoutException,
			SocketTimeoutException, Exception {
		// logger.info("发送HTTP(S) POST 请求：" + url + " | " + CONNTIMEOUT + " | "
		// + READTIMEOUT);
		HttpClient client = null;
		HttpResponse res = null;
		HttpPost post = new HttpPost(url);
		String result = null;
		try {
			if (params != null && !params.isEmpty()) {
				List<NameValuePair> formParams = new ArrayList<NameValuePair>();
				Set<Entry<String, String>> entrySet = params.entrySet();
				for (Entry<String, String> entry : entrySet) {
					formParams.add(new BasicNameValuePair(entry.getKey(), entry
							.getValue()));
				}
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
						formParams, Consts.UTF_8);
				post.setEntity(entity);
			}
			if (headers != null && !headers.isEmpty()) {
				for (Entry<String, String> entry : headers.entrySet()) {
					post.addHeader(entry.getKey(), entry.getValue());
				}
			}
			// 设置参数
			Builder customReqConf = RequestConfig.custom();
			if (CONNTIMEOUT != null) {
				customReqConf.setConnectTimeout(CONNTIMEOUT);
			}
			if (READTIMEOUT != null) {
				customReqConf.setSocketTimeout(READTIMEOUT);
			}
			post.setConfig(customReqConf.build());

			if (url.startsWith("https")) {
				// 执行 Https 请求.
				
				//测试环境忽略ssl证书
				client = getHttps();
				
				//生产环境校验ssl证书
				//client = createSSLInsecureClient();
//				client = createSSLInsecureClient();
				res = client.execute(post);
			} else {
				// 执行 Http 请求.
				client = HttpClientUtil.client;
				res = client.execute(post);
			}
			if (res.getStatusLine().getStatusCode() == 200) {
				result = IOUtils
						.toString(res.getEntity().getContent(), CHARSET);
			} else {
				logger.info("HTTP(S) POST 请求，状态异常："
						+ res.getStatusLine().getStatusCode() + " | " + url);
				result = null;
			}
		} finally {
			post.abort();
			if (null != res) {
				EntityUtils.consumeQuietly(res.getEntity());
			}
			if (url.startsWith("https") && client != null
					&& client instanceof CloseableHttpClient) {
				((CloseableHttpClient) client).close();
			}
		}
		return result;
	}

	/**
	 * 发送一个 GET 请求
	 * 
	 * @param url
	 * @param CHARSET
	 * @param CONNTIMEOUT
	 *            建立链接超时时间（毫秒）
	 * @param READTIMEOUT
	 *            响应超时时间（毫秒）
	 * @return
	 * @throws ConnectTimeoutException
	 *             建立链接超时
	 * @throws SocketTimeoutException
	 *             响应超时
	 * @throws Exception
	 */
	public static String get(String url, String CHARSET, Integer CONNTIMEOUT,
			Integer READTIMEOUT) throws ConnectTimeoutException,
			SocketTimeoutException, Exception {
		logger.info("发送HTTP(S) GET 请求：" + url + " | " + CHARSET + " | "
				+ CONNTIMEOUT + " | " + READTIMEOUT);
		HttpClient client = null;
		HttpResponse res = null;
		HttpGet get = new HttpGet(url);
		String result = null;
		try {
			// 设置参数
			Builder customReqConf = RequestConfig.custom();
			if (CONNTIMEOUT != null) {
				customReqConf.setConnectTimeout(CONNTIMEOUT);
			}
			if (READTIMEOUT != null) {
				customReqConf.setSocketTimeout(READTIMEOUT);
			}
			get.setConfig(customReqConf.build());

			if (url.startsWith("https")) {
				// 执行 Https 请求.
				//测试环境忽略ssl证书
				client = getHttps();
				
				//生产环境校验ssl证书
//				client = createSSLInsecureClient();
				res = client.execute(get);
			} else {
				// 执行 Http 请求.
				client = HttpClientUtil.client;
				res = client.execute(get);
			}
			if (res.getStatusLine().getStatusCode() == 200) {
				result = IOUtils
						.toString(res.getEntity().getContent(), CHARSET);
			} else {
				logger.info("HTTP(S) GET 请求，状态异常："
						+ res.getStatusLine().getStatusCode() + " | " + url);
				result = null;
			}
		} finally {
			get.abort();
			if (null != res) {
				EntityUtils.consumeQuietly(res.getEntity());
			}
			if (url.startsWith("https") && client != null
					&& client instanceof CloseableHttpClient) {
				((CloseableHttpClient) client).close();
			}
		}
		return result;
	}

	/**
	 * 创建 SSL连接
	 * 
	 * @return
	 * @throws GeneralSecurityException
	 */
	private static CloseableHttpClient createSSLInsecureClient()
			throws GeneralSecurityException {
		Builder requestBuilder = RequestConfig.custom();
		requestBuilder = requestBuilder.setSocketTimeout(CONNTIMEOUT);
		SSLContext sslContext = null;
		try {
			sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, new TrustManager[] { new X509TrustManager() {
				@Override
				public void checkClientTrusted(
						X509Certificate[] x509Certificates, String s)
						throws CertificateException {
				}

				@Override
				public void checkServerTrusted(
						X509Certificate[] x509Certificates, String s)
						throws CertificateException {
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			} }, new java.security.SecureRandom());
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
//		CloseableHttpClient client = HttpClientBuilder
//				.create()
//				.setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext))
//				.setDefaultRequestConfig(requestBuilder.build())
//				.setSSLHostnameVerifier(new HostnameVerifier() {
//					public boolean verify(String hostname, SSLSession session) {
//						return true;
//					}
//				}).build();
		
		CloseableHttpClient client = HttpClientBuilder
				.create()
				.setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext))
				.setDefaultRequestConfig(requestBuilder.build())
				.setSSLHostnameVerifier(new DefaultHostnameVerifier())
				.build();
		
		return client;
	}
	
	/**
	 * 忽略HTTPS请求的SSL证书，必须在openConnection之前调用
	 * 
	 * @throws Exception
	 */
	private static void ignoreSsl() throws Exception {
		HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String urlHostName, SSLSession session) {
				return true;
			}
		};
		trustAllHttpsCertificates();
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
	}
	
	private static void trustAllHttpsCertificates() throws Exception {
		TrustManager[] trustAllCerts = new TrustManager[1];
		TrustManager tm = new myTrustManager();
		trustAllCerts[0] = tm;
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}
	
	static class myTrustManager implements TrustManager, X509TrustManager {

		@Override
		public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws java.security.cert.CertificateException {
			return;
		}

		@Override
		public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws java.security.cert.CertificateException {
			return;
		}

		@Override
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

	}
	
	private static CloseableHttpClient getHttps() {
		HttpClient httpClient = new DefaultHttpClient();
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkServerTrusted(X509Certificate[] arg0,
						String arg1) throws CertificateException {

				}

				@Override
				public void checkClientTrusted(X509Certificate[] arg0,
						String arg1) throws CertificateException {

				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			@SuppressWarnings("deprecation")
			SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = httpClient.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", 443, ssf));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		return (CloseableHttpClient) httpClient;
	}
}
