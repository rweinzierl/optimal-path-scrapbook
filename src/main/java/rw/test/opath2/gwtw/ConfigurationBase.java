package rw.test.opath2.gwtw;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

class ConfigurationBase {
	Map<String, String> argMap;

	public ConfigurationBase(String[] args) {
		argMap = new TreeMap<String, String>();
		for (String arg : args) {
			String[] keyValue = arg.split("=");
			if (keyValue.length == 2)
				argMap.put(keyValue[0], keyValue[1]);
		}
	}

	protected String getValue(Map<String, String> argMap, String key, String defaultt) {
		String value = argMap.get(key);
		if (value != null && value.trim().length() > 0) {
			return value;
		} else
			return defaultt;
	}

	protected String getValue(Map<String, String> argMap, String key) {
		return getValue(argMap, key, null);
	}

	protected int getIntValue(Map<String, String> argMap, String key, String defaultt) {
		return Integer.parseInt(getValue(argMap, key, defaultt));
	}

	protected long getLongValue(Map<String, String> argMap, String key) {
		return Long.parseLong(getValue(argMap, key, null));
	}

	protected double getDoubleValue(Map<String, String> argMap, String key, String defaultt) {
		return Double.parseDouble(getValue(argMap, key, defaultt));
	}

	protected Long getLongValue2(Map<String, String> argMap, String key) {
		String value = getValue(argMap, key, null);
		if (value != null)
			return new Long(value);
		else
			return null;
	}

	protected boolean getBooleanValue(Map<String, String> argMap, String key, String defaultt) {
		return Boolean.parseBoolean(getValue(argMap, key, defaultt));
	}

	protected Date getDateValue(Map<String, String> argMap, String key) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		return df.parse(getValue(argMap, key, null));
	}

	protected File getFileValue(Map<String, String> argMap, String key) {
		return new File(getValue(argMap, key, null));
	}
}