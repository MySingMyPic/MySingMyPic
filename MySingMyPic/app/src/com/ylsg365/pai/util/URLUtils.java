package com.ylsg365.pai.util;

import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.web.dic.EnumAction;
import com.ylsg365.pai.web.dic.EnumController;
import com.ylsg365.pai.web.dic.EnumParameter;

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class URLUtils {
	/**
	 * @author ylsg365
	 * @param builder	
	 * @param controller
	 * @return 带controller的StringBuilder
	 */
	public static StringBuilder addController(StringBuilder builder, EnumController controller){
		builder.append(Constants.WEB_SERVER_DOMAIN);
		builder.append(controller.getDesc());
		builder.append("/");
		return builder;
	}
	
	/**
	 * @author ylsg365
	 * @param builder
	 * @param action
	 * @return 带action的StringBuilder
	 */
	public static StringBuilder addActionForGet(StringBuilder builder, EnumAction action){
		builder.append(action.getDesc());
		builder.append("?");
		return builder;
	}
	
	/**
	 * @author ylsg365
	 * @param builder
	 * @param action
	 * @return 带action的StringBuilder
	 */
	public static StringBuilder addActionForPost(StringBuilder builder, EnumAction action){
		builder.append(action.getDesc());
		return builder;
	}
	
	public static StringBuilder addParameter(StringBuilder builder, EnumParameter parameter, String paramValue){
		if(!builder.toString().endsWith("?")){
			builder.append("&");
		}

		builder.append(parameter.getDesc());
		builder.append("=");
		builder.append(paramValue == null ? "" : paramValue);
		
		return builder;
	}
	

	
	public static List<NameValuePair> addNameValuePair(List<NameValuePair> nameValuePairs, EnumParameter parameter, String paramValue){
		
		nameValuePairs.add(new BasicNameValuePair(parameter.getDesc(), paramValue));
		
		return nameValuePairs;
	}


}
