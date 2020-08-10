package acp.xml;

import java.util.ArrayList;
import java.util.Properties;

public interface DbConfig {
  public void loadConfigXml(String fileName);
  public Properties getConfigProp(int index);
  public ArrayList<String> getConfigNames();
}
