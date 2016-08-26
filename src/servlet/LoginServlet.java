package servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

public class LoginServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		String name=req.getParameter("username");
		String password=req.getParameter("password");
		OutputStreamWriter writer=new OutputStreamWriter(resp.getOutputStream());
		JSONObject json=new JSONObject();
		json.put("state", 200);
		json.put("doc", "http://192.168.137.1:8080/HelloWorld/file/123.pdf");
		writer.write(json.toString());
		writer.flush();
		writer.close();
		req.getRequestDispatcher("index.html").forward(req,resp);
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
}
