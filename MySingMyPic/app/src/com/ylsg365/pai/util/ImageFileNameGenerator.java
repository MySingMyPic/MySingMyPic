package com.ylsg365.pai.util;

import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;

public class ImageFileNameGenerator implements FileNameGenerator {

	@Override
	public String generate(String url) {
	
		String[] str=url.split("/");
		StringBuilder sb=new StringBuilder();
		for (int i = 3; i < str.length; i++) {
			sb.append(str[i]);
		}

        String name =  sb.toString();

        if(name.contains("\\.")){
            name = name.split("\\.")[0];
        }
        if(name.contains("=")){
            name = name.split("=")[1];
        }
		return name;
	}

}
