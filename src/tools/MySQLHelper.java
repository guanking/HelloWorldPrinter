package tools;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class MySQLHelper {
	public static MySQLHelper sbHelper = new MySQLHelper();
	private Statement state;
	private Connection connection;

	private MySQLHelper() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection("jdbc:mysql://121.42.158.80:3306/helloworld", "root",
					"caoguanjie");
			state = (Statement) connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public byte insert(String tableName, String[] objs, String[] values) {
		StringBuffer sql = new StringBuffer("insert into `" + tableName + "`(");
		for (String ele : objs)
			sql.append("`" + ele + "`,");
		sql.deleteCharAt(sql.lastIndexOf(","));
		sql.append(")values(");
		for (String ele : values)
			sql.append("'" + ele + "',");
		sql.deleteCharAt(sql.lastIndexOf(","));
		sql.append(")");
		try {
			System.out.println("MySQl :" + sql.toString());
			if (state.execute(sql.toString()))
				return 1;
			return 1;
		} catch (SQLException e) {
			String msg = e.getMessage();
			System.out.println(msg);
			if (msg.contains("Duplicate"))
				return 2;
			else if (objs.length != values.length)
				return 3;
			else
				return 4;
		}

	}

	public boolean delete(String tableName, String where) {
		try {
			return !state.execute("delete from `" + tableName + "` where " + where);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public int update(String tableName, String[] objs, String[] values, String where) {
		StringBuffer sql = new StringBuffer("update `" + tableName + "` set ");
		int len = objs.length;
		for (int i = 0; i < len; i++)
			sql.append("`" + objs[i] + "`='" + values[i] + "',");
		sql.deleteCharAt(sql.lastIndexOf(","));
		sql.append(" where " + where);
		try {
			System.out.println(sql.toString());
			return state.executeUpdate(sql.toString());
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public ResultSet select(String tableName, String[] objs, String where, String add, String order) {
		StringBuffer sb = new StringBuffer("select ");
		if (objs == null)
			sb.append("* ");
		else {
			for (String ele : objs)
				sb.append("`" + ele + "`,");
			sb.deleteCharAt(sb.lastIndexOf(","));
		}
		sb.append(" from `" + tableName + "` ");
		if (where != null)
			sb.append(" where " + where);
		if (order != null)
			sb.append("order by " + order);
		if (add != null)
			sb.append(" " + add);
		try {
			System.out.println("MySQL : " + sb.toString());
			return state.executeQuery(sb.toString());
		} catch (SQLException e) {
			System.out.println(sb.toString());
			e.printStackTrace();
			return null;
		}
	}

	public ResultSet select(String sql) {
		try {
			System.out.println(sql);
			return state.executeQuery(sql);
		} catch (SQLException e) {
			System.out.println(sql);
			e.printStackTrace();
			return null;
		}
	}

	public int count(String tableName, String where) {
		ResultSet r = null;
		try {
			if (where == null)
				r = state.executeQuery("select count(*) from `" + tableName + "`");
			else
				r = state.executeQuery("select count(*) from `" + tableName + "` where " + where);
			if (r.next())
				return r.getInt(1);
			return -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public ResultSet select(String tableName, String[] objs) {
		return select(tableName, objs, null, null, null);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		try {
			if (!connection.isClosed())
				connection.close();
			if (!state.isClosed())
				state.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] Args) throws SQLException {
		MySQLHelper db = new MySQLHelper();
		ResultSet r = db.select("order", null);
		while (r.next()) {
			System.out.println(r.getString(1) + ":" + r.getString(2) + ':' + r.getString(3));
		}
	}
}
