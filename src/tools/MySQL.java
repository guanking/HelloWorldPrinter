package tools;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import models.FileConverter;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class MySQL {
	public static boolean insertOrder(JSONArray infos, JSONArray pathes) {
		Calendar cal = Calendar.getInstance();
		String time = String.format("%04d%02d%02d%02d%2d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
		MySQLHelper.sbHelper.insert("order", new String[] { "orderid", "paths", "message" }, new String[] { time,
				pathes.toString(), infos.toString() });
		return true;
	}

	public static JSONObject selectOrder() {
		ResultSet r = MySQLHelper.sbHelper.select("select "
				+ "`userphone`,`sellerphone`,`orderid`,`paths`,`price`,`message`"
				+ " from `helloworld`.`order` limit 1");
		JSONObject json = new JSONObject();
		String orderID=null;
		try {
			if(r.next()){
				json.put("state", 200);
				json.put("userPhone", r.getString(1));
				json.put("sellerPhone", r.getString(2));
				orderID=r.getString(3);
				json.put("orderID", orderID);
				JSONArray jsons=JSONArray.fromObject(r.getString(4));
				JSONObject temp=null;
				int len=jsons.size();
				for(int i=0;i<len;i++){
					temp=(JSONObject) jsons.get(i);
					temp.put("path",FileConverter.getDownloadURL(temp.getString("path"), false));
				}
				json.put("path",jsons.toString());
				json.put("price", r.getString(5));
				json.put("message", r.getString(6));
				deleteOrder(orderID);
			}else{
				json.put("state", 404);
				json.put("message","no order");
			}
			return json;
		} catch (SQLException e) {
			json.put("state", 417);
			json.put("message",e.getMessage());
			return json;
		}
	}
	public static void deleteOrder(String orderID){
		MySQLHelper.sbHelper.delete("order","`orderid`='"+orderID+"'");
	}
	public static void main(String[] Args) {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		System.out.println(String.format("%04d%02d%02d%02d%2d", year, month, day, hour, minute));
		JSONObject json=selectOrder();
		System.out.println(json.toString());
	}
}
