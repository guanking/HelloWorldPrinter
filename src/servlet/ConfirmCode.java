package servlet;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.CreateImage;

public class ConfirmCode extends HttpServlet {
	static HashMap<String, String> codes = new HashMap<String, String>();// <ip,confirmCode>

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setHeader("Pragma", "No-cache");
		resp.setHeader("Cache-Control", "No-cache");
		resp.setDateHeader("Expires", 0);
		resp.setContentType("image/jpeg");
		Object[] objs = CreateImage.create();
		codes.put(req.getRemoteAddr(), (String) objs[0]);
		ImageIO.write((BufferedImage) objs[1], "JPEG", resp.getOutputStream());
	}
}
