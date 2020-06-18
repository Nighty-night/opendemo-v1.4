package com.open.transaction;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.http.conn.ConnectTimeoutException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.open.util.Common;
import com.open.util.GuardUtil;
import com.open.util.HttpClientUtil;
import com.open.util.JSONUtil;
import com.open.util.TimeUtil;

/**
 * 商户发起，调用南京银行接口的开发demo 
 * 该类以授信申请接口[creditApplyVIP]和查询电子账户[accountQuery]为例
 * @author 南京银行互联网金融开放平台
 *
 */
public class APIDemo {

	/**
	 * 此为南京银行开放平台通用的加密加签方法
	 * 根据需求自行调整请求报文和接口英文名
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Map<String,Object> parameterMap = new HashMap<String,Object>();
		 Map<String,Object> headMap = new LinkedHashMap<String, Object>();
        Map<String,Object> bodyMap = new HashMap<String,Object>();
        String nowStr =  DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        String nowTime =  DateFormatUtils.format(new Date(), "HH:mm:ss");

       //如果是API调用，则以下面头信息为准就可以了，如果是H5调用方式，则需要根据实际情况上送头信息
        headMap.put("channelID","SYTSC");
        headMap.put("serviceID","returnGoodsWKZF");
        headMap.put("seqNo","2222222");
        headMap.put("transDate",nowStr);
        headMap.put("transTime",nowTime);
        headMap.put("encryptFlag","1");

        //业务参数和加签参数一起进行加密
        bodyMap.put("merSeqNo","123");//订单号
        bodyMap.put("merId","999920191010T001407211");//商户号
        bodyMap.put("merDate",nowStr);//商户日期
        bodyMap.put("orgMerSeqNo","3333333");//原订单号
        bodyMap.put("orgMerDate","2019-01-01");//原商户日期
        bodyMap.put("transAmt","2000");//金额
        bodyMap.put("currency","156");//金额
        bodyMap.put("transTrmtp","08");//交易终端类型--08--手机
        parameterMap.put("appID","a436d816-bbb0-43a8-8686-c51dde2d4b04");
        parameterMap.put(Common.REQHEAD,headMap);
        parameterMap.put(Common.REQDATA,bodyMap);

		// 生成16位随机密钥key，例如：or7rp57jknwvts5g
		String key = GuardUtil.getRandom();
		System.out.println("随机密钥key明文:\n" + key);
		//证书地址
		String certPath = "D:\\IdeaProjects\\opendemo-v1.4\\conf\\cert\\test.pfx";
		//证书密钥
		String certKey = "111111";
		// 加密请求报文
		String encryReqData = encryptReqMsg(JSON.toJSONString(parameterMap), key, certPath, certKey, false);
		System.out.println("加密后的请求数据：" + encryReqData);

		// 示例接口2请求地址
		String url = "https://openapi-dev.njcb.com.cn:24443/openapi/OPEN/returnGoodsWKZF";//uat环境，上线前验证环境
        //String url = "https://openapi.njcb.com.cn/openapi/OPEN/returnGoodsWKZF";

		// 设置post中的content-Type
		String contentType = "application/json";

		// post发送数据
		new APIDemo().doPost(url, encryReqData, contentType, key);
	}

	/**
	 * 从文件里读取明文请求报文
	 * 
	 * @param path
	 * @return
	 */
	public static String readReqMsg(String path) {
		// 读取请求报文存放根路径
		String reqConfPath = System.getProperty(Common.ROOTPATH) + File.separator + Common.REQCONFPATH;
		File file = new File(reqConfPath + path);
		try {
			return FileUtils.readFileToString(file, Common.CHARSET);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 请求报文加签加密
	 * 
	 * @param reqData
	 * certPath 证书路径
	 * certKey 证书密码
	  * isH5 是H5调用还是API调用           
	 * @return
	 * @throws Exception
	 */
	public static String encryptReqMsg(String reqData, String key, String certPath, String certKey, boolean isH5) throws Exception {
		// 请求报文按照默认顺序转json
		JSONObject reqJson = JSON.parseObject(reqData, Feature.OrderedField);

		// 获取请求头
		JSONObject reqHeadJson = reqJson.getJSONObject(Common.REQHEAD);

		// 获取请求体
		JSONObject reqDataJson = reqJson.getJSONObject(Common.REQDATA);
		String data = reqDataJson.toJSONString();
		System.out.println("请求报文体reqData明文:\n" + data);

		// 使用格尔证书对请求体进行加签，生产证书在投产前另行提供
		System.out.println("格尔证书路径:\n" + certPath);
		System.out.println("格尔证书密码:\n" + certKey);
		String signDataAndSignCert = GuardUtil.SVSinitAPI(data, certPath, certKey, isH5);
		JSONObject signDataAndSignCertJson = JSON.parseObject(signDataAndSignCert, Feature.OrderedField);
		String signCert = signDataAndSignCertJson.getString("signCert");
		String signData = signDataAndSignCertJson.getString("signData");

		// 格尔加签后生成的两个参数放在请求头里
		reqHeadJson.put("signCert", signCert);
		reqHeadJson.put("signData", signData);

		// AES加密请求数据，用16位随机密钥key明文加密请求体reqData
		String AESreqData = GuardUtil.encrypt4Base64(reqDataJson.toString(), key);
		reqJson.put(Common.REQDATA, AESreqData);

		// 使用公钥加密16位随机密钥key
		String randomKey = GuardUtil.encryptByPublicKey4Pkcs5(key.getBytes(), Common.PUBLICKEY);
		reqJson.put(Common.KEY, randomKey);
		// 最终请求报文
		return reqJson.toString();
	}

	/**
	 * post发送方法
	 * 
	 * @param url
	 * @param reqMsg
	 * @param contentType
	 */
	public void doPost(String url, String reqMsg, String contentType, String key) {
		try {
			// 计算耗时
			TimeUtil t = new TimeUtil();
			t.startRec();
			// 获取响应，响应只需要解密，不需要加签，但是请求需要加签
			String rsp = HttpClientUtil.post(url, reqMsg, contentType, Common.CHARSET, 60000, 60000);
			t.endRec();
			System.out.println(t.output("交易"));
			System.out.println(JSONUtil.prettyPrint("响应报文:\n" + rsp));

			if (StringUtils.isNotBlank(rsp)) {
				// 提取响应报文中的密文字符串
				JSONObject rspJson = JSONObject.parseObject(rsp);
				String encryptRspData = rspJson.getString("rspData");

				if (StringUtils.isNotBlank(encryptRspData)) {
					// 解密请求报文，使用请求的明文key来解密
					String decryptRspStr = new String(GuardUtil.decrypt4Base64(encryptRspData, key));
					System.out.println("解密结果为：" + decryptRspStr);
				}
			}
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}