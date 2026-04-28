package util;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Tạo kết nối JDBC tới SQL Server.
 * <p>
 * Lưu ý: để đơn giản cho bài tập, cấu hình đang hard-code. Bạn có thể tách ra file config sau.
 */
public class DBConnection {

    private static final String URL =
            "jdbc:sqlserver://192.168.1.10:1433;"
                    + "databaseName=QuanLyNhaHang;"
                    + "encrypt=true;"
                    + "trustServerCertificate=true;"
                    + "sendStringParametersAsUnicode=true";

    private static final String USER = "sa";
    private static final String PASS = "123456";

    private DBConnection() {
    }

    public static Connection getConnection() throws Exception {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
