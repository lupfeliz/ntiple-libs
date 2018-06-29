package com.ntiple.libs.data;

public class DataConverter {
	
	public static final int BIT_TYPE_MSB = 0;
	public static final int BIT_TYPE_LSB = 1;
	
	public static final Class<?> CLASS_BYTE = Byte.class;
	public static final Class<?> CLASS_SHORT = Short.class;
	public static final Class<?> CLASS_INTEGER = Integer.class;
	public static final Class<?> CLASS_FLOAT = Float.class;
	public static final Class<?> CLASS_LONG= Long.class;
	public static final Class<?> CLASS_DOUBLE= Double.class;
	public static final Class<?> CLASS_STRING= String.class;
	
	public static Integer intValue(Object obj) { return intValue(obj, 0); }
	public static Integer intValue(Object obj, Integer defaults) {
		Integer ret = null;
		try {
			if(obj instanceof Byte) {
				ret = ((Byte) obj).intValue();
			} else if(obj instanceof Short) {
				ret = ((Short) obj).intValue();
			} else if(obj instanceof Integer) {
				ret = (Integer) obj;
			} else if(obj instanceof Long) {
				ret = ((Long) obj).intValue();
			} else if(obj instanceof Float) {
				ret = ((Float) obj).intValue();
			} else if(obj instanceof Double) {
				ret = ((Double) obj).intValue();
			} else if(obj instanceof byte[]) {
				ret = parseInt((byte[]) obj, BIT_TYPE_MSB, defaults);
			} else {
				ret = Integer.parseInt(String.valueOf(obj).trim());
			}
		} catch (Exception ignore) { }
		if(ret == null) { ret = defaults; }
		return ret;
	}
	public static Integer parseInt(byte[] buf, int bitType, Integer defaults) {
		Integer ret = null;
		
		if(ret == null) { ret = defaults; }
		return ret;
	}
	
	public static Long longValue(Object obj) { return longValue(obj, 0L); }
	public static Long longValue(Object obj, Long defaults) {
		Long ret = null;
		try {
			if(obj instanceof Byte) {
				ret = ((Byte) obj).longValue();
			} else if(obj instanceof Short) {
				ret = ((Short) obj).longValue();
			} else if(obj instanceof Integer) {
				ret = ((Integer) obj).longValue();
			} else if(obj instanceof Long) {
				ret = (Long) obj;
			} else if(obj instanceof Float) {
				ret = ((Float) obj).longValue();
			} else if(obj instanceof Double) {
				ret = ((Double) obj).longValue();
			} else if(obj instanceof byte[]) {
				ret = parseLong((byte[]) obj, BIT_TYPE_MSB, defaults);
			} else {
				ret = Long.parseLong(String.valueOf(obj).trim());
			}
		} catch (Exception ignore) { }
		if(ret == null) { ret = defaults; }
		return ret;
	}
	public static Long parseLong(byte[] buf, int bitType, Long defaults) {
		Long ret = null;
		
		if(ret == null) { ret = defaults; }
		return ret;
	}
	
	public static Float floatValue(Object obj) { return floatValue(obj, 0F); }
	public static Float floatValue(Object obj, Float defaults) {
		Float ret = null;
		try {
			if(obj instanceof Byte) {
				ret = ((Byte) obj).floatValue();
			} else if(obj instanceof Short) {
				ret = ((Short) obj).floatValue();
			} else if(obj instanceof Integer) {
				ret = ((Integer) obj).floatValue();
			} else if(obj instanceof Long) {
				ret = ((Long) obj).floatValue();
			} else if(obj instanceof Float) {
				ret = (Float) obj;
			} else if(obj instanceof Double) {
				ret = ((Double) obj).floatValue();
			} else if(obj instanceof byte[]) {
				ret = parseFloat((byte[]) obj, BIT_TYPE_MSB, defaults);
			} else {
				ret = Float.parseFloat(String.valueOf(obj).trim());
			}
		} catch (Exception ignore) { }
		if(ret == null) { ret = defaults; }
		return ret;
	}
	public static Float parseFloat(byte[] buf, int bitType, Float defaults) {
		Float ret = null;
		
		if(ret == null) { ret = defaults; }
		return ret;
	}
	
	public static Double doubleValue(Object obj) { return doubleValue(obj, 0D); }
	public static Double doubleValue(Object obj, Double defaults) {
		Double ret = null;
		try {
			if(obj instanceof Byte) {
				ret = ((Byte) obj).doubleValue();
			} else if(obj instanceof Short) {
				ret = ((Short) obj).doubleValue();
			} else if(obj instanceof Integer) {
				ret = ((Integer) obj).doubleValue();
			} else if(obj instanceof Long) {
				ret = ((Long) obj).doubleValue();
			} else if(obj instanceof Float) {
				ret = ((Float) obj).doubleValue();
			} else if(obj instanceof Double) {
				ret = (Double) obj;
			} else if(obj instanceof byte[]) {
				ret = parseDouble((byte[]) obj, BIT_TYPE_MSB, defaults);
			} else {
				ret = Double.parseDouble(String.valueOf(obj).trim());
			}
		} catch (Exception ignore) { }
		if(ret == null) { ret = defaults; }
		return ret;
	}
	public static Double parseDouble(byte[] buf, int bitType, Double defaults) {
		Double ret = null;
		
		if(ret == null) { ret = defaults; }
		return ret;
	}
	
	public static String strValue(Object obj, String defaults) {
		String ret = null;
		if(obj != null) {
			ret = String.valueOf(obj);
		}
		if(ret == null) { ret = defaults; }
		return ret;
	}
}
