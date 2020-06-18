package com.open.util;

public class Common {
	public static final String ROOTPATH = "user.dir";// 工程根目录
	public static final String CHARSET = "UTF-8";// 字符集
	public static final String REQCONFPATH = "conf\\req";// 请求报文存放路径
	public static final String CERTPATH = "conf\\cert";//证书存放路径
	public static final String KEY = "key";// 16位随机密钥
	public static final String REQDATA = "reqData";// 请求体
	public static final String REQHEAD = "reqHead";// 请求头
	public static final String RSPDATA = "rspData";// 返回体
	public static final String RSPHEAD = "rspHead";// 返回头
	// 公钥
	public static final String PUBLICKEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC4ObfNuxe+HnqsWAk2EngyIcYtoU33BkjkCxC7Wd/LRhY+Tns/IVvgV3ZoF0A7qjNS4WvAtij8ypqcviuhsrpiQeqeMBbzY+hh7lrBf7dPM4XxSkx396I+a0FYYN5z1Qe40+IlRLp7+lNHil27h863WCK8wM0nzEEU3Lf0f0/CLQIDAQAB";
//	public static final String PUBLICKEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCfA80xmLfsHAIOaobPZOobe+blusrfBDd4IAd3dl1uRZDcBv6TNRqdblGkN9vA+G7poGOi19KlMZyayPOYBrokxedl+ldN0zY3/TasZTKsCtkhRVJja87H6NsNKV9TiXx6DdXPcn8K/y73FAyPRNRsXp2BJYl8buAfSuaUGZIJOQIDAQAB";
	
	// 私钥
	public static final String PRIVATEKEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBALg5t827F74eeqxYCTYSeDIhxi2hTfcGSOQLELtZ38tGFj5Oez8hW+BXdmgXQDuqM1Lha8C2KPzKmpy+K6GyumJB6p4wFvNj6GHuWsF/t08zhfFKTHf3oj5rQVhg3nPVB7jT4iVEunv6U0eKXbuHzrdYIrzAzSfMQRTct/R/T8ItAgMBAAECgYA6S3lQDn3q2TY0Rv9TLvD93BYb5kkHe+Og1aeLwVrBtgHf4XG6flHWYZbERWc7+hWXimFQb9P0eiAGrV6dIjM7zuLmlM0UOmmxBgjuA4aG2o4Nd5H1tz3QN7XnRePotlxiBYrmCSDJOLjW9vpozqUIk+9oXzxd4DQdDNLfNFzNeQJBAN/shg6AMKTEfghsjJKy1PqZtWbVBmzmsx28ciqwng9LcYXWRdxgm1jlF8THrP48vm1YKKTGSozwoCwN0Hs2xtcCQQDSnWs2mnne/W6Ht8qoq/Z3952Dbvvop8ARq+ue/my9/on9n35H5k3WBGXxEjhlma9VAOP4aotrx32XW//uH9KbAkAI1RKAlevVQ+7YtDdjPeyKqe9uy+5B82OTQ7CZIHhLkCU9KzN8Os9F1Vt3poybRpgYRgbsYOnl30PiGPVlNsbnAkB+/rawFRNZVx03rqX/cLlL+z26C8jPr57LyQtpMmalZX8VM/+0IPVcEAjcBK2G7dVf/wp3Nt9cnMxo4OuATSXzAkBOIRtM7ReaIszZnlm5HnZsfbMcjnRDGC/TuXSc0zdKYnEiCgGfHuOcvncl1joocwJGNjlhUzn1pIoFmOl6G3J0";
	
//	 public static final String PRIVATEKEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJ8DzTGYt+wcAg5qhs9k6ht75uW6yt8EN3ggB3d2XW5FkNwG/pM1Gp1uUaQ328D4bumgY6LX0qUxnJrI85gGuiTF52X6V03TNjf9NqxlMqwK2SFFUmNrzsfo2w0pX1OJfHoN1c9yfwr/LvcUDI9E1GxenYEliXxu4B9K5pQZkgk5AgMBAAECgYABINTG4k+DxFsbYRQdypiCo2lq/d1MocoG9ys2ZMea7bFubjpGLx1GOq8nnCHaB6k5zSKH2EItW+HBk2VvEbUmZMdv7PeEndRY2gVEXa6A/CqR0DXy55ROOo3YKjCudn1s7m2MNXOjLhbH5C5g47hRUHSWhsarIDN8uAHdGCJ0kQJBANKlCMt6Pge4axMNj9EEim6q+KCYdbVkvFX2eLY+MtN3A4bN83Pb4KsiRYeM4TVrpOwaEHwmt2K3OQsWiiHU8JUCQQDBQN/9EiSGJMn1qBKyiA1r+mthJB5rbOdQycIgFTT8jTa+6/tgKszjNf6lH9jyqtMiMFGhkiuE8MnmTg5ffNkVAkAYFjMJQXMyYs4roZNebUx/FyHTC1v6YAiBM+vduwMI10UZ9xbtmqj4KLUWqO9fsm1raheUesDhkt38/JjTAYx5AkBlu4UucBKSG2PF0kile0G1igplBOKOorHSsaNjyEYwfRZIw2l9YoqhQZdfzbgjZxHmeZE/gPcv8KIYfxbJ5atRAkBzmY0YIajPZDhc3wLfjTxr3r3Cph/PRK0XjpoG/mXfGRAQ0lt0P/g0UujSXo6PTzPCiXfFa8sI6ZNg7zXOTdiV";
	
	/**
	 * 银联无卡商户号（模拟） 2018042101
	 * 测试通用的appid 0f89cfb8_8bb9_4017_b54d_5e7c32f28a42
	 * 银联无卡渠道号（模拟） WKZF
	 */
	public static final String appID = "0f89cfb8_8bb9_4017_b54d_5e7c32f28a42";
}
