package tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import models.ConfigManager;
import models.ConfigManager.Configer;

public class Log {
	private static String path = null;
	private static String errPath = null;
	static {
		Configer configer = new Configer(ConfigManager.LOG);
		ConfigManager.exeConfig(configer);
		path = configer.getResult();
		create(ConfigManager.LOG, path, ConfigManager.getWebContentPath() + "\\temp\\log.txt", configer);
		path = configer.getResult();
		configer.setKey(ConfigManager.ERRORLOG);
		ConfigManager.exeConfig(configer);
		errPath = configer.getResult();
		create(ConfigManager.ERRORLOG, errPath, ConfigManager.getWebContentPath() + "\\temp\\errlog.txt", configer);
		errPath = configer.getResult();
	}

	private static void create(String key, String value, String defaultvalue, Configer configer) {
		if (path == null) {
			configer.putConfiguration(key, defaultvalue);
			ConfigManager.exeConfig(configer);
			File file = new File(ConfigManager.getWebContentPath() + "\\temp");
			if (!file.exists() || file.isDirectory())
				file.mkdir();
			file = new File(configer.getResult());
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 将产生的错误添到日志信息
	 * 
	 * @param logText
	 *            错误信息
	 */
	public static void appendErrorLog(String logText) {
		File file = new File(errPath);
		append(file, logText);
	}

	/**
	 * 添加日志信息
	 * 
	 * @param logText
	 *            日志信息
	 */
	public static void appendLog(String logText) {
		File file = new File(path);
		append(file, logText);
	}

	private static void append(File file, String logText) {
		RandomAccessFile ac;
		try {
			ac = new RandomAccessFile(file, "rw");
			ac.seek(ac.length());
			ac.writeChars("\n" + logText);
			ac.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查看错误日志
	 * 
	 * @return 日志信息
	 */
	public static String getErrorLog() {
		return getlog(new File(errPath));
	}

	/**
	 * 查看日志
	 * 
	 * @return 日志信息
	 */
	public static String getLog() {
		return getlog(new File(path));
	}

	private static String getlog(File file) {
		RandomAccessFile ac;
		try {
			ac = new RandomAccessFile(file, "r");
			StringBuffer sb = new StringBuffer();
			String temp = null;
			do {
				temp = ac.readLine();
				if (temp == null)
					break;
				sb.append(temp + '\n');
			} while (true);
			ac.close();
			return sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 清除产生错误的日志
	 * 
	 * @return 成功返回true，否则false
	 */
	public static boolean clearErrorLog() {
		return clearLog(new File(errPath));
	}

	/**
	 * 清除日志信息
	 * 
	 * @return 成功返回true，否则false
	 */
	public static boolean clearLog() {
		return clearLog(new File(path));
	}

	/**
	 * 清除所有日志信息，包括错误日志
	 * 
	 * @return 成功返回true，否则false
	 */
	public static boolean clearAllLog() {
		return clearErrorLog() && clearLog();
	}

	private static boolean clearLog(File file) {
		if (file.exists())
			file.delete();
		try {
			file.createNewFile();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void main(String[] Args) {
		clearLog();
		System.out.println("log:");
		for (int i = 0; i < 10; i++) {
			Log.appendLog("number :" + i + "\n");
			System.out.println(Log.getLog());
		}
		System.out.println("errlog:");
		clearErrorLog();
		for (int i = 0; i < 10; i++) {
			Log.appendErrorLog("number :" + i);
			System.out.println(Log.getErrorLog());
		}
		clearAllLog();
	}
}
