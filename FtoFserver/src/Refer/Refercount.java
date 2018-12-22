package Refer;

import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

public class Refercount extends HttpServlet {
    private static final long serialVersionUID = 1L;
    // JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/ftof?useSSL=false";

    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "root";
    static final String PASS = "a123456";


    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException {

        int count =0;
        String classes=null;
        Connection conn = null;
        Statement stmt = null;
        JSONObject jsonObj = new JSONObject();

        response.setContentType("text/html;charset=gbk");
        PrintWriter pw= response.getWriter();
        classes=  new String(request.getParameter("classes"));
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn= DriverManager.getConnection(DB_URL,USER,PASS);

            String sql = "select * from "+classes+";";

            stmt=conn.createStatement();


            ResultSet rs = stmt.executeQuery(sql);


            while(rs.next()){
                count++;
            }
            jsonObj.put("num",count);
            pw.write(jsonObj.toString());
            rs.close();
            stmt.close();
            conn.close();
        }catch(SQLException sqle){
            //处理JDBC错误
            sqle.printStackTrace();
        }catch (Exception e) {    //处理Class.forName
            e.printStackTrace();
        }finally {
            //最后用于关闭资源的块
            try{
                if(stmt!=null)
                    stmt.close();
            }catch (SQLException SE2){
            }
            try{
                if (conn!=null)
                    conn.close();
            }catch (SQLException SE){
                SE.printStackTrace();
            }
        }
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}


