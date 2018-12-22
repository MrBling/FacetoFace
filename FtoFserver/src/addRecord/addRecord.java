package addRecord;

import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class addRecord extends HttpServlet {
    private static final long serialVersionUID = 1L;
    // JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/ftof?useSSL=false";

    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "root";
    static final String PASS = "a123456";
    boolean canLogin=false;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection conn=null;
        Statement stmt=null;

        int Id;
        String classes=null;
        String Record_class=null;
        int PId;
        String name=null;
        Boolean canadd = null;


        //设置相应类型
        response.setContentType("text/html;charset=gbk");
        PrintWriter pw= response.getWriter();
        JSONObject jsonObj = new JSONObject();
        classes= new String(request.getParameter("classes"));
        Record_class=new String(request.getParameter("Record_class"));
        PId=Integer.parseInt(request.getParameter("PId"));
        name= new String(request.getParameter("name"));
        try{
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();

            int Count=RecordCount.RecordCount();
            System.out.println(Count);

            String sql="insert into record values('" +
                    Count+ "','" +
                    classes + "','" +
                    Record_class + "','" +
                    PId+ "','" +
                    name + "');";


            System.out.println(sql);
            stmt.executeUpdate(sql);
            canLogin=true;
            jsonObj.put("canLogin",canLogin);
            pw.write(jsonObj.toString());

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

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}

