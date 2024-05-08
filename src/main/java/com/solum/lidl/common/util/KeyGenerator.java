package com.solum.lidl.common.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class KeyGenerator {
	   private static int serialNo = 0;
	   
	   private static String prevTime = "";
	   
	   private static final int MAX_SERIAL_NO = 999;
	   
	   public static synchronized String getKeyByDateFormat() {
	     SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	     DecimalFormat decimalFormat = new DecimalFormat("000");
	     String currentTime = dateFormat.format(new Date());
	     if (serialNo == 0) {
	       while (prevTime.equals(currentTime))
	         currentTime = dateFormat.format(new Date()); 
	       prevTime = currentTime;
	     } 
	     String keyStr = currentTime + decimalFormat.format(serialNo);
	     serialNo = (serialNo >= 999) ? 0 : (serialNo + 1);
	     return keyStr;
	   }
	   
	   public static synchronized long getLongKeyByDateFormat() {
	     String key = getKeyByDateFormat();
	     return Long.parseLong(key.substring(2, key.length()));
	   }
	 }