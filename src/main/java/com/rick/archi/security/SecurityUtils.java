package com.rick.archi.security;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SecurityUtils {
	
	private static String bytesToHexString(byte[] src){   
	    StringBuilder stringBuilder = new StringBuilder("");   
	    if (src == null || src.length <= 0) {   
	        return null;   
	    }   
	    for (int i = 0; i < src.length; i++) {   
	        int v = src[i] & 0xFF;   
	        String hv = Integer.toHexString(v);   
	        if (hv.length() < 2) {   
	            stringBuilder.append(0);   
	        }   
	        stringBuilder.append(hv);   
	    }   
	    return stringBuilder.toString();   
	}
	
	private static byte[] hexStringToBytes(String hexString) {   
	    if (hexString == null || hexString.equals("")) {   
	        return null;   
	    }   
	    hexString = hexString.toUpperCase();   
	    int length = hexString.length() / 2;   
	    char[] hexChars = hexString.toCharArray();   
	    byte[] d = new byte[length];   
	    for (int i = 0; i < length; i++) {   
	        int pos = i * 2;   
	        d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));   
	    }   
	    return d;   
	}  
	
	private static byte charToByte(char c) {   
	    return (byte) "0123456789ABCDEF".indexOf(c);   
	}

	private static String getFileHeader(String filePath) throws IOException {
		byte[] b = new byte[28];
		InputStream inputStream = null;
		inputStream = new FileInputStream(filePath);
		inputStream.read(b, 0, 28);
		inputStream.close();
		return bytesToHexString(b);
	}
	
	public static FileType getType(String filePath) throws IOException {
		String fileHead = getFileHeader(filePath);
		if(fileHead == null || fileHead.length() == 0) {
			return null;
		}
		fileHead = fileHead.toUpperCase();
		FileType[] fileTypes = FileType.values();
		for(FileType type: fileTypes) {
			if(fileHead.startsWith(type.getValue())) {
				return type;
			}
		}
		return null;
	}
	
	
}
