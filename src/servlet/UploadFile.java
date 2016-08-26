package servlet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.FileConverter;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileUpload;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.RequestContext;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;

import com.lowagie.text.DocumentException;

import tools.ImageTrans;
import tools.MySQL;
import tools.PrintInfo;

public class UploadFile extends HttpServlet {
	private String filePath;
	private String tempPath;
	private String[] types = new String[] { ".doc", ".ppt", ".pptx", ".docx", ".xls", ".xlsx" };
	private String[] imageTypes = new String[] { ".jpg", ".png", ".tif", ".bmp" };

	private boolean needTrans(String fileName) {
		String temp = fileName.substring(fileName.lastIndexOf("."));
		for (String ele : types) {
			if (temp.equalsIgnoreCase(ele))
				return true;
		}
		return false;
	}

	private boolean isImage(String name) {
		for (String ele : imageTypes) {
			if (name.toLowerCase().endsWith(ele))
				return true;
		}
		return false;
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		filePath = config.getInitParameter("filepath");
		tempPath = config.getInitParameter("temppath");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		System.out.println("receive......");
		RequestContext req = new ServletRequestContext(request);
		if (FileUpload.isMultipartContent(req)) {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setRepository(new File(tempPath));
			factory.setSizeThreshold(1024 * 1024 * 1024);
			ServletFileUpload fileUpload = new ServletFileUpload(factory);
			fileUpload.setHeaderEncoding("UTF-8");
			fileUpload.setFileSizeMax(1024 * 1024 * 1024);
			List<FileItem> items = new ArrayList<FileItem>();
			try {
				items = fileUpload.parseRequest(req);
			} catch (FileUploadException e) {
				e.printStackTrace();
			}
			Iterator<FileItem> it = items.iterator();
			LinkedList<PrintInfo> printInfos = new LinkedList<PrintInfo>();
			PrintInfo temp = null;
			String userID = null;
			JSONArray infos = new JSONArray(), paths = new JSONArray();
			JSONObject json = null, path = null;
			String info, fileName;
			long size = 0;
			while (it.hasNext()) {
				FileItem fileItem = (FileItem) it.next();
				if (fileItem.isFormField()) {
					info = new String(fileItem.getString().getBytes("ISO-8859-1"), "GBK");
					json = JSONObject.fromObject(info);
					infos.add(json);
				} else {
					temp = new PrintInfo(fileItem.getName(), fileItem.getSize(), fileItem.getContentType());
					System.out.println(fileItem.getName());
					printInfos.add(temp);
					size = fileItem.getSize();
					fileName = fileItem.getName();
					if (fileName != null && size != 0) {
						byte[] buf = fileItem.get();
						path = new JSONObject();
						String uuid = UUID.randomUUID().toString();
						if (needTrans(fileName)) {
							System.out.println("fileName is" + fileName);
							fileName = uuid + fileName.substring(fileName.lastIndexOf("."));
							path.put("path", uuid + ".pdf");
							paths.add(path);
							FileConverter.upload(buf, false, fileName, true, 3600);
						} else if (isImage(fileName)) {
							String dir = System.getProperty("user.dir");
							System.out.println(dir);
							File file = new File(dir, fileName);
							if (!file.exists())
								file.createNewFile();
							FileOutputStream out = new FileOutputStream(file);
							out.write(buf);
							out.close();
							File pdfFileTemp = new File(dir, uuid + ".pdf");
							try {
								ImageTrans.imageToPdf(new FileInputStream(file), pdfFileTemp);
							} catch (DocumentException e) {
								e.printStackTrace();
							}
							fileName = uuid + ".pdf";
							path.put("path", fileName);
							paths.add(path);
							System.out.println("need trans add path " + path.toString());
							FileConverter.upload(pdfFileTemp, false, fileName, false, 3600);
							pdfFileTemp.delete();
						} else {
							fileName = uuid + fileName.substring(fileName.lastIndexOf("."));
							path.put("path", fileName);
							paths.add(path);
							System.out.println("need trans add path " + path.toString());
							FileConverter.upload(buf, false, fileName, false, 3600);
						}
					} else {
						System.out.println("no file choosen or empty file");
					}
				}
			}
			MySQL.insertOrder(infos, paths);
			PrintInfo.writeToFile(userID, printInfos);
		} else {
			System.out.println("request isn't multipart content");
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
}
