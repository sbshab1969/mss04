package acp.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DbConfigDom implements DbConfig {

  private ArrayList<Properties> configProps = new ArrayList<>();
  private ArrayList<String> configNames = new ArrayList<>();

  public void loadConfigXml(String fileName) {
//    System.out.println("DOM parser");
    configProps.clear();
    configNames.clear();
    // ----------------------------------------
    DocumentBuilderFactory dbf = null;
    DocumentBuilder db = null;
    Document docum = null;
    try {
      dbf = DocumentBuilderFactory.newInstance();
      db = dbf.newDocumentBuilder();

      MyErrorHandler errorHandler = new MyErrorHandler(); 
      db.setErrorHandler(errorHandler);
      
      docum = db.parse(fileName);
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (IOException e) {
      System.err.println("I/O exception reading XML document");
    } catch (SAXException e) {
//      System.err.println("XML exception reading document.");
    }
    // ----------------------------------------
    if (docum != null) {
      Element root = docum.getDocumentElement();
      NodeList configNodes = root.getElementsByTagName(DbConst.DB_RESOURCE);
      for (int i = 0; i < configNodes.getLength(); i++) {
        Properties prop = getProp(configNodes.item(i));
        prop.setProperty(DbConst.DB_INDEX, String.valueOf(i));
        configProps.add(prop);
        configNames.add(prop.getProperty(DbConst.DB_NAME, "null"));
      }
    }  
    // ----------------------------------------
  }

  private Properties getProp(Node pNode) {
    Properties props = new Properties();
    // ----------------------------------------
    NamedNodeMap attrs = pNode.getAttributes();
    for (int i = 0; i < attrs.getLength(); i++) {
      Node itemNode = attrs.item(i);
      String nodeName = itemNode.getNodeName();
      String nodeValue = itemNode.getNodeValue();
      if (nodeValue != null) {
        props.setProperty(nodeName, nodeValue);
      }  
    }
    // ----------------------------------------
    NodeList listNodes = pNode.getChildNodes();
    for (int i = 0; i < listNodes.getLength(); i++) {
      Node itemNode = listNodes.item(i);
      if (itemNode instanceof Element) {
        String nodeName = itemNode.getNodeName();
        String nodeValue = itemNode.getTextContent();
        if (nodeValue != null) {
          props.setProperty(nodeName, nodeValue);
        }
      }  
    }  
    // ----------------------------------------
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
