package com.open.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Stack;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JSONUtil {
	
	public static void main(String[] args) {
		String json = "{\"appAccessToken\":\"f89cb5353cd5918b3c14cdf6fb72f4d9\",\"appID\":\"test-app-appkey\",\"key\":\"162987\",\"reqData\":{\"IdNo\":\"6228480323085446116\",\"MchannelId\":\"AP201\",\"UserName\":\"大王\"}}";
		System.out.println(prettyPrint(json));
//		System.out.println(prettyWrite(json, "D:\\ZYC\\workspace1\\HttpDemo\\config\\req\\test\\test002"));
	}
	
	public static boolean prettyWrite(String json, String path) {
		System.out.println("start to write: " + json);
		json = prettyPrint(json);
		OutputStream out = null;
		try {
			out = new FileOutputStream(path);
			out.write(json.getBytes("UTF-8"));
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(out != null)	out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public static String prettyPrint(String json){
		StringBuilder result = new StringBuilder();
		char[] chars = json.toCharArray();
		int level = 0;
		for(char c : chars) {
			switch(c) {
				case '{' :
				case '[' :
					level++;
					result.append(c).append("\n");
					appendTab(result, level);
					break;
				case '}' :
				case ']' :
					level--;
					result.append("\n");
					appendTab(result, level);
					result.append(c);
					break;
				case ',' :
					result.append(c).append("\n");
					appendTab(result, level);
					break;
				default :
					result.append(c);
			}
		}
		return result.toString();
	}
	
	public static void appendTab(StringBuilder sb, int level){
		for(int i=0;i<level;i++) {
			sb.append("\t");
		}
	}
	
	public static String stack2String(Stack<String> stack) {
		String[] strs = stack.toArray(new String[]{});
		StringBuilder sb = new StringBuilder();
		for(String s : strs) {
			sb.append(s);
		}
		return sb.toString();
	}

	/**
	 * @param msg 
	 * @param key 
	 * @return 获取json中指定key对应的value
	 */
	public static String handleJson(JSONObject msg, String key) {
		if(key == null || "".equals(key)){
			return null;
		}
		String value = null;
		Object[] set = msg.keySet().toArray();
		for (int i = 0; i < set.length; ++i) {

			Object object = msg.get(set[i]);
			if (value == null && object instanceof JSONObject) {
				value = handleJson((JSONObject) object, key);
			}

			if (value == null && object instanceof JSONArray) {
				JSONArray array = (JSONArray) object;
				for (int j = 0; j < array.size(); ++j) {
					value = handleJson(array.getJSONObject(j), key);
					if(value != null) break;
				}
			}

			if (value == null && object instanceof String) {
				if (key.equals(set[i].toString())) {
					return (String)object;
				}
			}
		}
		return value;
	}
}
