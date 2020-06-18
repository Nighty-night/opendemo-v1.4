package com.open.noticeTransAction;

import java.io.UnsupportedEncodingException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.open.util.Common;
import com.open.util.GuardUtil;

/**
 * 南京银行发起，调用商户接口，诸如各类通知接口的开发demo 
 * 该类以银联代付商户 通知接口[merzhantYLDF]为例
 * 
 * @author 南京银行互联网金融开放平台
 *
 */
public class MerzhantYLDFDemo {

	/**
	 * 通知解密验签方法demo，以及返回加密加签说明
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// 针对回调通知的解密验签处理
		// 南京银行模拟请求报文，也是商户接收到的报文
		String reqMsg = "{\"reqData\":\"RvHNGintug6sIxdwqyT5F0KEnrQ58G/3XnEQqArBY5PsYw2Rd1IJOvK4VQFaIQIZt+3932zQRls3CIy6dojBAM7GTMwOPfO06XRo9K32rJl7FblUKI8dpSGySJSfN2YPrIL60zQzChkvCE1Yq9KMbBE/NHO8vqCUaw7HeHAt4JK0YDyrBxHSx7MHLOsbunqVasZEET9wyRZV+aaImjvMy5/ahzRq5rdwOz4D71ARz/f7JwRL/QMBGYkh/BQKV8t7q32J0CEEHkdz3jedScND0w==\",\"reqHead\":{\"signData\":\"IQQlOEflL5GKFy++ugmjXxKqcdNIwY/fPWXHS37WDzv95F2UjRKpu8/7wVeWr8GYbw2Iy+mbSVs6KMyFGQ3HBKj4opyiSojYqSg0C4XhMRdCcpEelIv/+YDDB3G/RYU60SGAKzG1+jI5i+92yXxP6ePln2JWwnYdhYN9jDUqrJQ=\",\"transTime\":\"20190610\",\"seqNo\":\"20190610171504\",\"transDate\":\"171504\",\"encryptFlag\":\"1\",\"serviceID\":\"merzhantYLDF\",\"channelID\":\"OPEN\"},\"appID\":\"0f89cfb8_8bb9_4017_b54d_5e7c32f28a42\",\"key\":\"ftTkNO/wn7kkfeFc3fAgOXX7BRrXjVph4lkRMg3YiR3ZhAyOdHrnFH7u7QKDTO0iRa/ZeHhe2mKT71Ck32ZnzeSA7nSvc1Kqb4arAvAIhDtg8r4Q/VGhEOtI0ci3v6N8X4RHX2z5sBDpmyzvrEAJ8y0txVkBnvAEozLv2w4J9GI=\"}";
		System.out.println("收到的通知请求报文:\n" + reqMsg);

		// 将请求字符串转换成json
		JSONObject reqJson = JSON.parseObject(reqMsg, Feature.OrderedField);
		// 获取密文随机密钥key
		String key = (String) reqJson.get("key");
		System.out.println("随机密钥key密文:\n" + key);

		// 用私钥解密随机密钥
		String decryptKey = null;
		try {
			decryptKey = new String(GuardUtil.decryptByPrivateKey4Pkcs5(key.getBytes(), Common.PRIVATEKEY),
					Common.CHARSET);
			System.out.println("随机密钥key明文:\n" + decryptKey);
		} catch (UnsupportedEncodingException e) {
			//
			System.out.println("解密随机密钥失败，不支持的的字符集:\n" + e);
		}

		// 获取请求头
		JSONObject reqHead = reqJson.getJSONObject("reqHead");
		System.out.println("请求报文头reqHead:\n" + reqHead);

		// 获取请求体
		String reqData = reqJson.getString("reqData");
		System.out.println("请求报文体reqData密文:\n" + reqData);
		// 解密请求体
		String decryptReqData = null;
		try {
			decryptReqData = new String(GuardUtil.decrypt4Base64(reqData, decryptKey), Common.CHARSET);
			System.out.println("请求报文体reqData明文:\n" + decryptReqData);
		} catch (UnsupportedEncodingException e) {
			//
			System.out.println("解密密文失败，不支持的的字符集:\n" + e);
		}

		// 验证请求报文签名
		String signData = reqHead.getString("signData");
		boolean verify = false;
		try {
			verify = GuardUtil.verify(decryptReqData.getBytes(), Common.PUBLICKEY, signData);
		} catch (Exception e1) {
			// 
			System.out.println("验签异常:\n" + e1);
		}
		System.out.println("是否验签成功:\n" + verify);
		
		// 处理业务
		System.out.println("处理业务等操作...\n");

		
		
		
		
		// 返回通知结果
		JSONObject rspMsgJson = new JSONObject();

		// 通知的返回没有业务字段，但是要new一个json来用私钥加签、用请求的key来加密，否则我方无法处理
		JSONObject rspDataJson = new JSONObject();
		// 如果有业务字段，则需要添加到rspDataJson中
		// rspDataJson.put("xxx", "xxx");

		try {
			signData = GuardUtil.sign(rspDataJson.toJSONString().getBytes(), Common.PRIVATEKEY);
			System.out.println("返回报文签名值:\n" + signData);
		} catch (Exception e) {
			//
			System.out.println("返回报文加签失败:\n" + e);
		}
		
		// 返回的报文头
		String rspHead = "{\"returnCode\":\"000000\",\"transDate\":\"20180906\",\"seqNo\":\"20180906204003000002\",\"returnMsg\":\"交易成功\",\"serviceID\":\"merzhantYLDF\",\"transTime\":\"204003908\",\"channelID\":\"OPEN\"}";
		JSONObject rspHeadJson = JSON.parseObject(rspHead, Feature.OrderedField);
		System.out.println("返回报文头:\n" + rspHeadJson);
		// 把signData塞入返回的报文头
		rspHeadJson.put("signData", signData);
		
		// AES加密reqDataJson，用上述解密出来的随机密钥key来加密
		System.out.println("返回报文rspData明文:\n" + rspDataJson.toJSONString());
		String rspDataEncrypt = GuardUtil.encrypt4Base64(rspDataJson.toJSONString(), decryptKey);
		System.out.println("返回报文rspData密文:\n" + rspDataEncrypt);
		// 把rspHeadJson和rspDataEncrypt塞入返回的rspMsgJson
		rspMsgJson.put("rspHead", rspHeadJson);
		rspMsgJson.put("rspData", rspDataEncrypt);
		
		System.out.println("完整返回报文rspMsgJson:\n" + rspMsgJson.toJSONString());
	}
}
