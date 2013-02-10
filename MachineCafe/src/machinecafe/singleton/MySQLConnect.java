package machinecafe.singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe sur le modèle singleton qui permet de n'avoir qu'une seul connection à une base MySql
 * @author Chris
 */
public class MySQLConnect{
    private static MySQLConnect BASE = null;
    private String url;
    private String dbName;
    private String driver;
    private String userName;
    private String password;
    
    private final static String default_url = "jdbc:mysql://localhost:8889/";
    private final static String default_dbName = "MachineCafe";
    private final static String default_driver = "com.mysql.jdbc.Driver";
    private final static String default_userName = "root";
    private final static String default_password = "root";
    
    private Connection conn;
    
    /**
     * Connection par défaut à la base de données
     */
    private MySQLConnect(){
        this.conn = null;
        this.url = default_url;
        this.dbName = default_dbName;
        this.driver = default_driver;
        this.userName = default_userName; 
        this.password = default_password;
        try {
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(url+dbName,userName,password);
            System.out.println("Connected to the database");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Connection avec des attributs custom
     * @param url
     * @param dbName
     * @param driver
     * @param username
     * @param password 
     */
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
    
    /**
     * Permet de retourner l'objet Connection si une connection existe déjà, SINON ON L'A CREE
     * Qaund elle existe, on vérifie simplement qu'on souhaite accéder à la même connection avant de la renvoyer
     * Sinon on ferme l'ancienne et on ouvre une nouvelle avec les nouveaux paramètres
     * @return l'objet connection et non l'object MySQL
     */
    public static Connection getDefaultConnection(){
        if (BASE == null){
            BASE = new MySQLConnect();
        }else{
            if ((BASE.userName.compareTo(default_userName) != 0)&&(BASE.password.compareTo(default_password) != 0)){
                BASE.close();
                BASE = new MySQLConnect();
            }
        }
        return BASE.conn;
    }
    
    /**
     * Similaire à la connexion par dafaut mais en mode attribut custom
     * @param url
     * @param dbName
     * @param driver
     * @param userName
     * @param password
     * @return l'objet connection et non l'object MySQL
     */
    public static Connection getConnexion(String url,String dbName,String driver,String userName,String password){
        if (BASE == null){
            BASE = new MySQLConnect(url, dbName, driver, userName, password);
        }else{
            if ((BASE.userName.compareTo(userName) != 0)
                    &&(BASE.password.compareTo(password) != 0)
                    &&(BASE.dbName.compareTo(dbName) != 0)
                    &&(BASE.url.compareTo(url) != 0)
                    &&(BASE.driver.compareTo(driver) != 0)){
                BASE.close();
                BASE = new MySQLConnect(url,dbName,driver,userName,password);
            }
        }
        return BASE.conn;
    }
    
    /**
     * Permet de fermer une connexion
     */
    public void close(){
        try {
            this.conn.close();
            System.out.println("Disconnected from database");
        } catch (SQLException ex) {
            Logger.getLogger(MySQLConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}