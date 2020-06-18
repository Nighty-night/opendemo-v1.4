package com.open.transaction;

import com.open.util.GuardUtil;

public class decrypt {

	/**
	 * 报文密文解密方法
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String req = null;
		// 密文字符串
		req = "b3UnuALeNFVowbXd/kbbx1imPSCrKZgypxUM22uAlNWo9RuwKCQD0v1uYFeU0wGNSNGUaSMWSz14HRGkNds2+NM00SWFb90psM2bIdWZ7Bs=";
		GuardUtil guardUtil = new GuardUtil();
		// 16位随机密钥明文
		String key = "e8stfhinjh4j6gid";
		String decryptStr = new String(guardUtil.decrypt4Base64(req, key));
		System.out.println("解密结果为：" + decryptStr);
	}
}
