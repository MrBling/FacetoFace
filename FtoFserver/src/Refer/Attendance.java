package Refer;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;

public class Attendance extends HttpServlet {
    static final String JDBC_DRIVER="com.mysql.jdbc.Driver";
    static final String DB_URL="jdbc:mysql://localhost:3306/ftof?useSSL=false";

    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER="root";
    static final String PASS="a123456";

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException {
        Connection conn = null;
        Statement stmt = null;

        String classes=null;
        String Record_class=null;

        response.setContentType("text/html;charset=gbk");
        PrintWriter pw= response.getWriter();
        System.out.println(request.getRequestURI());


        classes=  new String(request.getParameter("classes"));
        Record_class=new String(request.getParameter("Record_class"));
        JSONObject jsonObjlast = new JSONObject();
        try{
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();

            String sqlall="select PId,name from ruan2;";

            String sql="select PId,name "+
                    "from record "+
                    " where Record_class =" +"'"+Record_class+"'"+"and "+
                    "classes = "+"'"  +classes  +"';";
            System.out.println(sql);

            ResultSet rs=stmt.executeQuery(sql);
            ResultSetMetaData metaData=rs.getMetaData();
            int columnCount=metaData.getColumnCount();


            ArrayList objArray = new ArrayList();
            while (rs.next()) {
                JSONObject jsonObj = new JSONObject();
                String PId=rs.getString("PId");
                String name=rs.getString("name");
                jsonObj.put(PId,name);
                objArray.add(jsonObj.toString());

            }
            System.out.println(objArray);


            pw.write(objArray.toString());
            stmt.close();
            conn.close();


        }catch (Exception e) {    //处理Class.forName
            e.printStackTrace();
        } finally {
            //最后用于关闭资源的块
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException SE2) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException SE) {
                SE.printStackTrace();
            }

        }

    }

}
