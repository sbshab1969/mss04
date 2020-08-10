package acp;

import java.sql.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.utils.DialogUtils;
import acp.utils.Messages;
import acp.xml.DbConst;

public class DbConnect {

  private static Properties dbProp;
  private static Connection dbConn;
 
  private static Logger logger = LoggerFactory.getLogger(DbConnect.class);

  public static Properties getParams() {
    return dbProp;
  }

  public static void setParams(Properties props) {
    dbProp = props;
  }

  public static Connection getConnection() {
    return dbConn;
  }

  public static void setConnection(Connection conn) {
    dbConn = conn;
  }

  public static boolean testConnection() {
    if (dbConn != null) {
      return true;
    }
    return false;
  }

  public static void connect() {
    dbConn = null;
    if (dbProp != null) {
      String user = dbProp.getProperty(DbConst.DB_USER);
      String passwd = dbProp.getProperty(DbConst.DB_PASSWORD);
      String connString = dbProp.getProperty(DbConst.DB_CONN_STRING);
      String driver = dbProp.getProperty(DbConst.DB_DRIVER);
      // -----------------------------------------------------
      try {
        Class.forName(driver).newInstance();
        dbConn = DriverManager.getConnection(connString, user, passwd);
      } catch (Exception e) {
//        e.printStackTrace();
//        DialogUtils.errorPrint(e);
//        System.err.println(e.toString());
        logger.error(e.getMessage());
      }
      // -----------------------------------------------------
    } else {
      DialogUtils.errorMsg(Messages.getString("Message.ConnectNoParams"));
    }
  }

  public static void disconnect() {
//    System.out.println("disconnect");
    if (dbConn == null) {
      return;
    }
    // ------------------------
    try {
      dbConn.close();
    } catch (SQLException e) {
//      e.printStackTrace();
//      DialogUtils.errorPrint(e);
//      System.err.println(e.toString());
      logger.error(e.getMessage());
    }
    // ------------------------
    dbConn = null;
  }

}
