package com.murat.smartiq.util;

import java.util.HashMap;
import java.util.Map;

public class SmartIqUtils {

	public static Map<String, Object> errorResponse(String error, String message) {
		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("error", error);
		errorResponse.put("message", message);
		return errorResponse;
	}

}
