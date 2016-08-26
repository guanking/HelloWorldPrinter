package tools;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;

public class CreateImage {
	private static String imagePath = "F:\\myImg.png";
	private static Random r = new Random();
	private static int width = 100;
	private static int height = 30;
	private static Random random = new Random();
	private static String eles = null;
	private static Vector<Color> colors = new Vector<Color>();
	static {
		StringBuffer sb = new StringBuffer();
		for (char i = 'a'; i < 'z'; i++) {
			sb.append(i);
		}
		for (char i = 'A'; i < 'Z'; i++) {
			sb.append(i);
		}
		for (char i = '0'; i < '9'; i++) {
			sb.append(i);
		}
		eles = sb.toString();
		colors.add(Color.black);
		colors.add(Color.blue);
		colors.add(Color.cyan);
		colors.add(Color.darkGray);
		colors.add(Color.green);
		colors.add(Color.lightGray);
		colors.add(Color.magenta);
		colors.add(Color.orange);
		colors.add(Color.pink);
		colors.add(Color.red);
		colors.add(Color.white);
		colors.add(Color.yellow);
	}

	public static String drawRandomString(int length, Graphics2D g, Vector<Color> tcs) {
		StringBuffer sb = new StringBuffer();
		String ele = null;
		int index = 0;
		int eleWidth = width / length;
		for (int i = 0; i < length; i++) {
			index = r.nextInt(eles.length());
			ele = eles.substring(index, index + 1);
			index = r.nextInt(tcs.size());
			Color color = tcs.get(index);
			tcs.remove(index);
			g.setColor(color);
			AffineTransform trans = new AffineTransform();
			trans.rotate((random.nextInt(90) - 45) * 3.14 / 180, eleWidth * i + eleWidth / 2 + 5, (height - 5) / 2);
			float scaleSize = random.nextFloat() + 0.8f;
			if (scaleSize > 1f)
				scaleSize = 1f;
			trans.scale(scaleSize, scaleSize);
			g.setTransform(trans);
			index = r.nextInt(tcs.size());
			color = tcs.get(index);
			tcs.remove(index);
			g.setColor(color);
			g.drawString(ele, eleWidth * i + 5, height - 5);
			sb.append(ele);
		}
		g.dispose();
		return sb.toString();
	}

	public static Object[] create() throws IOException {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
		Graphics2D g = image.createGraphics();
		Font myFont = new Font("黑体", Font.BOLD, 30);
		Vector<Color> tcs = (Vector<Color>) colors.clone();
		g.setFont(myFont);
		int index = r.nextInt(tcs.size());
		g.setBackground(tcs.get(index));
		tcs.remove(index);
		g.clearRect(0, 0, width, height);
		index = r.nextInt(tcs.size());
		g.setColor(tcs.get(index));
		tcs.remove(index);
		int count = 10;
		int beginX, beginY, endX, endY;
		while (count-- > 0) {
			beginX = r.nextInt(width);
			beginY = r.nextInt(height);
			endX = r.nextInt(width);
			endY = r.nextInt(height);
			g.drawLine(beginX, beginY, endX, endY);
		}
		String code=drawRandomString(4, g, tcs);
		g.dispose();
		File file = new File(imagePath);
		if (!file.exists()) {
			file.createNewFile();
		}
		ImageIO.write(image, "PNG", file);
		return new Object[]{code,image};
	}

	public static void main(String[] Args) throws IOException {
		CreateImage.create();
	}
}
