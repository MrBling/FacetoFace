package addRecord;

import java.sql.*;

public class RecordCount {
    private static final long serialVersionUID = 1L;
    // JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/ftof?useSSL=false";

    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "root";
    static final String PASS = "a123456";


    public static  int RecordCount(){

        int count =0;

        Connection conn = null;
        Statement stmt = null;

        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn= DriverManager.getConnection(DB_URL,USER,PASS);

            String sql = "select * from record;";

            stmt=conn.createStatement();


            ResultSet rs = stmt.executeQuery(sql);


            while(rs.next()){
                count++;
            }

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

        return count;
    }

}
