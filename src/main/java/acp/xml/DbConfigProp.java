package acp.xml;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

public class DbConfigProp implements DbConfig {

  private ArrayList<Properties> configProps = new ArrayList<>();
  private ArrayList<String> configNames = new ArrayList<>();

  public void loadConfigXml(String fileName) {
    configProps.clear();
    configNames.clear();
    // ----------------------------------------
    Properties prop = loadXmlProps(fileName);
    // ----------------------------------------
    configProps.add(prop);
    configNames.add(fileName);  
    // ----------------------------------------
  }

  public Properties loadXmlProps(String fileName) {
    Properties props = new Properties();
    try {
      FileInputStream fis = new FileInputStream(fileName);
      props.loadFromXML(fis);
      fis.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    props.setProperty(DbConst.DB_INDEX, "0");
    props.setProperty(DbConst.DB_NAME, fileName);
    return props;
  }

  public Properties getConfigProp(int index) {
    if (index<0 || index>=configProps.size()) {
      return null;
    }
    return configProps.get(index);
  }

  public ArrayList<String> getConfigNames() {
    return configNames;
  }

}
