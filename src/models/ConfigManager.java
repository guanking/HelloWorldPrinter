package models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.CharBuffer;

import net.sf.json.JSONObject;

public class ConfigManager {
	public static final String TEMPDIR = "temp";
	public static final String FILEDIR = "file";
	public static final String LOG = "log";
	public static final String ERRORLOG = "errorlog";

	private interface ExecuteConfig {
		void exe(JSONObject obj);
	}

	private static String dir;

	static {
		dir = System.getProperty("user.dir") + File.separator + "WebContent" + File.separator + "config"
				+ File.separator + "config.ini";
	}

	public static String getWebContentPath() {
		return System.getProperty("user.dir") + File.separator + "WebContent";
	}

	private static JSONObject getConfig() throws IOException {
		File file = new File(dir);
		if (!file.exists())
			file.createNewFile();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuffer sb = new StringBuffer();
		char buf[] = new char[1024];
		int len = 0;
		while ((len = reader.read(buf, 0, 1024)) != -1) {
			sb.append(buf, 0, len);
		}
		reader.close();
		JSONObject obj;
		if (sb.length() == 0) {
			obj = new JSONObject();
		} else {
			obj = JSONObject.fromObject(sb.toString());
		}
		return obj;
	}

	public static void exeConfig(ExecuteConfig exe) {
		JSONObject obj;
		try {
			obj = getConfig();
			exe.exe(obj);
			writeConfig(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void writeConfig(JSONObject obj) throws IOException {
		File file = new File(dir);
		if (!file.exists())
			file.createNewFile();
		FileWriter writer = new FileWriter(file);
		writer.write(obj.toString());
		writer.flush();
		writer.close();
	}

	public void setCustomDBPath(final String path) throws IOException {
		exeConfig(new ExecuteConfig() {
			public void exe(JSONObject obj) {
				obj.put("customDBPath", path);
			}
		});
	}

	public static class Configer implements ExecuteConfig {
		private String key = null, value = null;

		@Override
		public void exe(JSONObject obj) {
			if (value == null)
				value = (String) obj.get(key);
			else
				obj.put(key, value);
		}

		public Configer(String key) {
			this.key = key;
		}

		public Configer(String key, String value) {
			this.key = key;
			this.value = value;
		}

		public void setKey(String key) {
			this.key = key;
			this.value = null;
		}

		public void putConfiguration(String key, String value) {
			this.key = key;
			this.value = value;
		}

		public String getResult() {
			return value;
		}
	}

	public static void main(String[] Args) throws IOException {
		Configer configer = new Configer(ConfigManager.ERRORLOG, ConfigManager.getWebContentPath()
				+ "\\temp\\errlog.txt");
		ConfigManager.exeConfig(configer);
		configer.setKey(ConfigManager.ERRORLOG);
		ConfigManager.exeConfig(configer);
		System.out.println(configer.getResult());
	}
}
