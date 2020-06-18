/**
 * 
 */
package com.open.util;

/**
 * Util to record time cost.
 * 
 * @author chentao
 * 
 */
public class TimeUtil {
	private long start = -1;
	private long end = -1;

	public void startRec() {
		start = System.currentTimeMillis();
	}

	public void endRec() {
		end = System.currentTimeMillis();
	}

	public String output(String tag) {
		return tag + "花费时间为: " + (end - start) + "毫秒";
	}

}
