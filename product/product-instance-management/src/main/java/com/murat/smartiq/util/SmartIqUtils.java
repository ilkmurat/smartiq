package com.murat.smartiq.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmartIqUtils {

	public static Map<String, Object> errorResponse(String error, String message) {
		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("error", error);
		errorResponse.put("message", message);
		return errorResponse;
	}
	
	public static boolean isNullorEmpty(List<String> objectList) {
		try {
			for(String object:objectList) {
				if(object==null || object.isEmpty())
				{
					return true;
				}
			}
		} catch (Exception e) {
			// log
		}
		return false;
		
	}

}
