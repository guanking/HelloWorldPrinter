package tools;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class PrintInfo {
	private String fileName;
	private long size;
	private String type;// doc,ppt,pdf
	private int printNumber;
	private boolean colorful, doubleFace;
	private static final String orderFormFileDir = "F:\\Competition\\worksapce\\HelloWorld\\WebContent\\orderForm";

	public static boolean writeToFile(String userID, LinkedList<PrintInfo> listInfo) throws IOException {
		FileWriter writer = new FileWriter(new File(orderFormFileDir, userID + ".txt"));
		for (PrintInfo info : listInfo) {
			writer.write(info.toString() + "\n");
		}
		writer.close();
		return true;
	}

	public static String readFileContent(String userID) throws Exception {
		File file = new File(orderFormFileDir, userID + ".txt");
		if (!file.exists()) {
			throw new Exception("no this user's file " + userID);
		}
		FileReader reader = new FileReader(file);
		int len = 0;
		char[] buf = new char[1024];
		StringBuffer sb = new StringBuffer();
		while ((len = reader.read(buf, 0, 1024)) != -1) {
			sb.append(buf, 0, len);
		}
		return sb.toString();
	}

	public PrintInfo(String fileName, long size, String type, int printNumber, boolean colorful, boolean doubleFace) {
		this.fileName = fileName;
		this.size = size;
		this.type = type;
		this.printNumber = printNumber;
		this.colorful = colorful;
		this.doubleFace = doubleFace;
	}

	public PrintInfo(String fileName, long size, String type) {
		this.fileName = fileName;
		this.size = size;
		this.type = type;
		this.printNumber = 1;
		this.colorful = false;
		this.doubleFace = false;
	}

	public String getFileName() {
		return fileName;
	}

	public long getSize() {
		return size;
	}

	public String getType() {
		return type;
	}

	public int getPrintNumber() {
		return printNumber;
	}

	public boolean isColorful() {
		return colorful;
	}

	public boolean isDoubleFace() {
		return doubleFace;
	}

	public void setPrintNumber(int printNumber) {
		this.printNumber = printNumber;
	}

	public void setColorful(boolean colorful) {
		this.colorful = colorful;
	}

	public void setDoubleFace(boolean doubleFace) {
		this.doubleFace = doubleFace;
	}

	@Override
	public String toString() {
		return "PrintInfo [fileName=" + fileName + ", size=" + size + ", type=" + type + ", printNumber=" + printNumber
				+ ", colorful=" + colorful + ", doubleFace=" + doubleFace + "]";
	}

}
