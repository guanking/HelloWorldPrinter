package models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.UUID;

import models.ConfigManager.Configer;
import sun.misc.BASE64Encoder;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;

public class FileConverter {
	private static String accessKey = "lEffaGScIsvX8QFIdouoZy03Crur7VtPfF8w1zDV";
	private static String secretKey = "yVXLvDXqIjthEIdjN5_tVsfB3B5ndBEFrLAjayLO";
	private static HashMap<String, String> domains = new HashMap<String, String>();
	private static Auth auth = null;
	private static UploadManager uploadManager = null;
	private static BucketManager bucketManager = null;
	static {
		auth = Auth.create(accessKey, secretKey);
		uploadManager = new UploadManager();
		bucketManager = new BucketManager(auth);
		domains.put("hello", "http://7xnjg3.com1.z0.glb.clouddn.com/");
		domains.put("hello2", "http://7xnm79.com1.z0.glb.clouddn.com/");
	}

	/**
	 * 根据空间名生成上传凭证
	 * 
	 * @param bucket
	 *            空间名
	 * @return 上传凭证
	 */
	private static String getUpToken(String bucket) {
		return auth.uploadToken(bucket);
	}

	/**
	 * 根据空间名，和文件名（key）生成上传凭证
	 * 
	 * @param bucket
	 *            空间名
	 * @param key
	 *            空间中文件的key值
	 * @return 上传凭证
	 */
	private static String getUpToken(String bucket, String key) {
		return auth.uploadToken(bucket, key);
	}

	/**
	 * 根据空间名和文件的key值生成可以转化为pdf的上传凭证
	 * 
	 * @param bucket
	 *            空间名
	 * @param key
	 *            文件的key值
	 * @param deadline
	 *            允许消耗的时间
	 * @return 上传凭证
	 * @throws UnsupportedEncodingException 
	 */
	private static String getTransToPDFUpToken(String bucket, String key, Long deadline) throws UnsupportedEncodingException {
		if (deadline == null)
			deadline = new Long(3600);
		String saveKey = key.replaceFirst("\\..+$", ".pdf");
		String encodedEntryUri = (new BASE64Encoder()).encode((bucket + ":" + saveKey).getBytes());
		return auth.uploadToken(
				bucket,
				null,
				3600,
				new StringMap().putNotEmpty("scope", bucket + ':' + key)
						.putNotEmpty("persistentOps", "yifangyun_preview|saveas/" + encodedEntryUri)
						.putNotEmpty("deadline", deadline.toString())
						.putNotEmpty("persistentNotifyUrl", "http://fake.com/qiniu/notify"));
	}

	public static class MyRet {
		public long fsize;
		public String key;
		public String hash;
		public int width;
		public int height;

		@Override
		public String toString() {
			return "MyRet [fsize=" + fsize + ", key=" + key + ", hash=" + hash + ", width=" + width + ", height="
					+ height + "]";
		}
	}

	/**
	 * 上传文件
	 * 
	 * @param file
	 *            需要上传的文件
	 * @param common
	 *            是否为共有资源
	 * @param key
	 *            文件的key值
	 * @param transToPDF
	 *            上传完成之后是否转化为PDF文件格式
	 * @param deadline
	 *            允许的上传时间
	 * @throws UnsupportedEncodingException 
	 */
	public static void upload(File file, boolean common, String key, boolean transToPDF, long deadline) throws UnsupportedEncodingException {
		try {
			Response res = null;
			String bucket = common ? "hello" : "hello2";
			if (transToPDF) {
				res = uploadManager.put(file, key, getTransToPDFUpToken(bucket, key, new Long(deadline)));
			} else {
				res = uploadManager.put(file, key, getUpToken(bucket, key));
			}
			if (res.isOK()) {
				System.out.println(res.toString() + ':' + res.bodyString());
			} else {
				System.out.println(res.bodyString());
			}
		} catch (QiniuException e) {
			Response r = e.response;
			System.out.println(r.toString());
			try {
				System.out.println(r.bodyString());
			} catch (QiniuException e1) {
				System.out.println(e1.toString());
			}
		}
	}

	public static void upload(byte[] data, boolean common, String key, boolean transToPDF, long deadline) throws UnsupportedEncodingException {
		try {
			Response res = null;
			String bucket = common ? "hello" : "hello2";
			if (transToPDF) {
				res = uploadManager.put(data, key, getTransToPDFUpToken(bucket, key, new Long(deadline)));
			} else {
				res = uploadManager.put(data, key, getUpToken(bucket, key));
			}
			if (res.isOK()) {
				System.out.println(res.toString() + ':' + res.bodyString());
			} else {
				System.out.println(res.bodyString());
			}
		} catch (QiniuException e) {
			Response r = e.response;
			System.out.println(r.toString());
			try {
				System.out.println(r.bodyString());
			} catch (QiniuException e1) {
				System.out.println(e1.toString());
			}
		}
	}

	/**
	 * 下载文件
	 * 
	 * @param bucket
	 *            空间名
	 * @param key
	 *            文件key值
	 * @param common
	 *            是否是公有资源
	 * @param fileName
	 *            要存为的文件名
	 * @return 返回是否成功
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static boolean download(String key, boolean common, String fileName) {
		String bucket = common ? "hello" : "hello2";
		String url = domains.get(bucket) + key;
		if (!common) {
			url = auth.privateDownloadUrl(url);
		}
		Configer configer = new Configer(ConfigManager.FILEDIR);
		ConfigManager.exeConfig(configer);
		File file = new File(configer.getResult());
		if (!file.exists() || !file.isDirectory()) {
			file.mkdir();
		}
		file = new File(file, fileName + ".pdf");
		try {
			FileOutputStream out = new FileOutputStream(file);
			InputStream in = new URL(url).openStream();
			int len = 0;
			byte[] bytes = new byte[1024];
			while ((len = in.read(bytes, 0, 1024)) != -1) {
				out.write(bytes, 0, len);
			}
			in.close();
			out.close();
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
		return true;
	}

	public static String getDownloadURL(String key, boolean common) {
		String bucket = common ? "hello" : "hello2";
		String url = domains.get(bucket) + key;
		if (!common) {
			url = auth.privateDownloadUrl(url);
		}
		return url;
	}

	/**
	 * 获取所有的空间名
	 * 
	 * @return 所有空间名
	 * @throws QiniuException
	 */
	public String[] getBuckets() throws QiniuException {
		return bucketManager.buckets();
	}

	/**
	 * 删除指定的文件
	 * 
	 * @param bucket
	 *            空间名
	 * @param key
	 *            文件的key值
	 * @return true表示成功，否则失败
	 * @throws QiniuException
	 */
	public boolean delete(String bucket, String key) throws QiniuException {
		boolean success = true;
		try {
			bucketManager.delete(bucket, key);
			return true;
		} catch (QiniuException e) {
			success = false;
			throw e;
		} finally {
			return success;
		}
	}

	/**
	 * 移动文件
	 * 
	 * @param srcBucket
	 *            原来文件所在空间的名字
	 * @param srcKey
	 *            原文件的key值
	 * @param desBucket
	 *            目标空间的名字
	 * @param desKey
	 *            目标key值
	 * @return true表示成功，否则失败
	 */
	public static boolean move(String srcBucket, String srcKey, String desBucket, String desKey) {
		boolean success = true;
		try {
			bucketManager.move(srcBucket, srcKey, desBucket, desKey);
		} catch (QiniuException e) {
			success = false;
			e.printStackTrace();
		} finally {
			return success;
		}
	}

	/**
	 * 获取文件信息
	 * 
	 * @param bucket
	 *            空间名
	 * @param key
	 *            文件key值
	 * @return 返回FileInfo，如果失败返回null
	 */
	public static FileInfo scanState(String bucket, String key) {
		FileInfo info = null;
		try {
			info = bucketManager.stat(bucket, key);
		} catch (QiniuException e) {
			throw e;
		} finally {
			return info;
		}
	}

	public static void main(String[] args) throws IOException {
		System.out.println(UUID.randomUUID().toString());
//		File file=new File("F:\\Competition\\worksapce\\HelloWorld\\WebContent\\file\\9aabaab1-edd9-47b4-bc09-2833a11a2e7a.doc");
//		FileConverter.upload(file, false,file.getName(), true, new Long(3600));
		/*File file1 = new File("E:\\勤工助学\\杭电勤工助学申请表.doc");
		long deadline = 10000;
		FileConverter.upload(file1, true, file1.getName(), true, deadline);
		System.out.println("file 1 "
				+ FileConverter.download(file1.getName(), true,
						file1.getName().substring(0, file1.getName().indexOf("."))));*/
	}
}
