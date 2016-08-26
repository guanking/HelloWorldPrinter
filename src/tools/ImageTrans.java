package tools;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

@SuppressWarnings("unused")
public class ImageTrans {
	static int fileHeight = 1123;
	static int fileWidth = 842;
	static int padding = 100;
	public static void main(String[] args) throws Exception {
		ImageTrans.imageToPdf(new FileInputStream("E:\\Image\\cartoon\\3.jpg"), new File("E:\\Image\\cartoon\\my2.pdf"));
		System.out.println("finish!");
	}

	private static void listOrder() {

		File[] listFiles = new File("F:\\temp\\Project\\数据\\dfdsfds\\巴黎公社活动家传略_img").listFiles();
		TreeMap<Integer, File> tree = new TreeMap<Integer, File>();
		for (File f : listFiles) {
			tree.put(Integer.parseInt(f.getName().replaceAll(".jpg$", "")), f);
		}
		for (Entry<Integer, File> eif : tree.entrySet()) {
			System.out.println(eif.getKey() + "=" + eif.getValue().toString());
		}
	}

	public static boolean imageToPdf(InputStream imageIn, File pdfFile) throws IOException, DocumentException {
		if(pdfFile.exists())
			pdfFile.delete();
		pdfFile.createNewFile();
		ByteArrayOutputStream baos = new ByteArrayOutputStream(2048 * 3);
		BufferedImage bi = ImageIO.read(imageIn);
		java.awt.Image image = bi.getScaledInstance(100, 100, bi.SCALE_SMOOTH);
		double widthScal = (fileWidth - 2.0 * padding) / bi.getWidth();
		double heightScal = (fileHeight - 2.0 * padding) / bi.getHeight();
		AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(
				widthScal < heightScal ? widthScal : heightScal, widthScal < heightScal ? widthScal : heightScal), null);
		image = op.filter(bi, null);
		ImageIO.write((BufferedImage) image, "jpg", baos);
		Image img = Image.getInstance(baos.toByteArray());
		float width = img.width();
		float height = img.height();
		Document document = new Document(new Rectangle(fileWidth, fileHeight));
		PdfWriter pdfWr = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
		document.open();
		document.add(img);
		try {
			pdfWr.close();
		} catch (Exception e) {
		}
		return true;
	}

	public static boolean imgMerageToPdf(File[] list, File file) throws Exception {
		Map<Integer, File> mif = new TreeMap<Integer, File>();
		int len = list.length;
		for (int i = 0; i < len; i++)
			mif.put(i + 1, list[i]);
		ByteArrayOutputStream baos = new ByteArrayOutputStream(2048 * 3);
		InputStream is = new FileInputStream(mif.get(1));
		while ((len = is.read()) != -1)
			baos.write(len);
		baos.flush();
		Image image = Image.getInstance(baos.toByteArray());
		// image.scaleAbsolute(0.5f, 0.5f);
		float width = image.width();
		float height = image.height();
		is.close();
		baos.close();
		Document document = new Document(new Rectangle(width, height));
		System.out.println(document.setMargins(10, 10, 10, 10));
		PdfWriter pdfWr = PdfWriter.getInstance(document, new FileOutputStream(file));
		document.open();
		for (Entry<Integer, File> eif : mif.entrySet()) {
			baos = new ByteArrayOutputStream(2048 * 3);
			is = new FileInputStream(eif.getValue());
			while ((len = is.read()) != -1)
				baos.write(len);
			baos.flush();
			image = Image.getInstance(baos.toByteArray());
			Image.getInstance(baos.toByteArray());
			// image.scaleAbsolute(0.5f, 0.5f);
			image.setAbsolutePosition(1.0f, 1.0f);
			document.add(image);
			document.newPage();
			baos.close();
		}
		document.close();
		pdfWr.close();
		return true;
	}

	private static void pdfToJpg(String source, String target, int x) throws Exception {
		RandomAccessFile rea = new RandomAccessFile(new File(source), "r");
		FileChannel channel = rea.getChannel();
		ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
		PDFFile pdfFile = new PDFFile(buf);
		PDFPage page = pdfFile.getPage(x);
		java.awt.Rectangle rect = new java.awt.Rectangle(0, 0, (int) page.getBBox().getWidth(), (int) page.getBBox()
				.getHeight());
		java.awt.Image img = page.getImage(rect.width, rect.height, // width &
				rect, null, true, true);
		BufferedImage tag = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_RGB);
		tag.getGraphics().drawImage(img, 0, 0, rect.width, rect.height, null);
		FileOutputStream out = new FileOutputStream(target); // 输出到文件流
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
		encoder.encode(tag); // JPEG编码
		out.close();
	}

	/**
	 * @param source
	 *            源PDF文件路径
	 * @param target
	 *            保存PDF文件路径
	 * @param pageNum
	 *            提取PDF中第pageNum页
	 * @throws Exception
	 */
	private static void pdfExtraction(String source, String target, int pageNum) throws Exception {
		// 1：创建PDF读取对象
		PdfReader pr = new PdfReader(source);
		System.out.println("this document " + pr.getNumberOfPages() + " page");

		// 2：将第page页转为提取，创建document对象
		Document doc = new Document(pr.getPageSize(pageNum));

		// 3：通过PdfCopy转其单独存储
		PdfCopy copy = new PdfCopy(doc, new FileOutputStream(new File(target)));
		doc.open();
		doc.newPage();

		// 4：获取第1页，装载到document中。
		PdfImportedPage page = copy.getImportedPage(pr, pageNum);
		copy.addPage(page);

		// 5：释放资源
		copy.close();
		doc.close();
		pr.close();
	}

	/**
	 * @param pdfFile
	 *            源PDF文件
	 * @param imgFile
	 *            图片文件
	 */
	private static void jpgToPdf(File pdfFile, File imgFile) throws Exception {
		// 文件转img
		InputStream is = new FileInputStream(pdfFile);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (int i; (i = is.read()) != -1;) {
			baos.write(i);
		}
		baos.flush();

		// 取得图像的宽和高。
		Image img = Image.getInstance(baos.toByteArray());
		float width = img.width();
		float height = img.height();
		img.setAbsolutePosition(0.0F, 0.0F);// 取消偏移
		System.out.println("width = " + width + "\theight" + height);

		// img转pdf
		Document doc = new Document(new Rectangle(width, height));
		PdfWriter pw = PdfWriter.getInstance(doc, new FileOutputStream(imgFile));
		doc.open();
		doc.add(img);

		// 释放资源
		System.out.println(doc.newPage());
		pw.flush();
		baos.close();
		doc.close();
		pw.close();
	}

}
