package machinecafe;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySQLConnect{
    private static MySQLConnect BASE = null;
    private String url;
    private String dbName;
    private String driver;
    private String userName;
    private String password;

    private Connection conn;
    
    private MySQLConnect(){
        this.conn = null;
        this.url = "jdbc:mysql://localhost:8889/";
        this.dbName = "MachineCafe";
        this.driver = "com.mysql.jdbc.Driver";
        this.userName = "root"; 
        this.password = "root";
        try {
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(url+dbName,userName,password);
            System.out.println("Connected to the database");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private MySQLConnect(String url, String dbName, String driver, String username, String password){
        this.url = url;
        this.dbName = dbName;
        this.driver = driver;
        this.userName = username; 
        this.password = password;
        this.conn = null;
        try {
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(url+dbName,userName,password);
            System.out.println("Connected to the database");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static Connection getDefaultConnection(){
        if (BASE == null){
            BASE = new MySQLConnect();
        }
        return BASE.conn;
    }
    
    public static Connection getBASE(String url,String dbName,String driver,String userName,String password){
        if (BASE == null){
            BASE = new MySQLConnect(url, dbName, driver, userName, password);
        }
        return BASE.conn;
    }
    
    public void close(){
        try {
            this.conn.close();
            System.out.println("Disconnected from database");
        } catch (SQLException ex) {
            Logger.getLogger(MySQLConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}