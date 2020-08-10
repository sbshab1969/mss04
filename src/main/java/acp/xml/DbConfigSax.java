package acp.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class DbConfigSax implements DbConfig {

  private ArrayList<Properties> configProps = new ArrayList<>();
  private ArrayList<String> configNames = new ArrayList<>();

  public void loadConfigXml(String fileName) {
//    System.out.println("SAX parser");
    configProps.clear();
    configNames.clear();
    try {
      //---------------------------------------
      XMLReader reader = XMLReaderFactory.createXMLReader();
      SaxHandler saxHandler = new SaxHandler();
      reader.setContentHandler(saxHandler);
      
      MyErrorHandler errorHandler = new MyErrorHandler(); 
      reader.setErrorHandler(errorHandler);  // !!!!!!

      reader.parse(fileName);
      //---------------------------------------
      configProps = saxHandler.getConfigs();
      for (int i=0; i<configProps.size(); i++) {
        Properties prop = configProps.get(i);
        prop.setProperty(DbConst.DB_INDEX, String.valueOf(i));
        String nameConf = prop.getProperty(DbConst.DB_NAME,"null");
        configNames.add(nameConf);
      }
      //---------------------------------------
    } catch (IOException e) {
      System.err.println("I/O exception reading XML document");
    } catch (SAXException e) {
//      System.err.println("XML exception reading document.");
    }
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

class SaxHandler extends DefaultHandler {
  private ArrayList<Properties> configs = new ArrayList<>();
  private Properties curr = null;
  private String currTag = null;

  public ArrayList<Properties> getConfigs() {
    return configs;
  }
  
  public void startDocument() throws SAXException {
    configs.clear();
    curr = null;
    currTag = null;
  }

  public void startElement(String uri, String localName, String qName, Attributes attrs) {
    currTag = qName;
    if (qName.equals("resource")) {
      curr = new Properties();
      for (int i=0; i<attrs.getLength(); i++) {
        String attrName = attrs.getQName(i);
        String attrValue = attrs.getValue(i);
        if (attrValue != null) {
          curr.setProperty(attrName, attrValue);
        }  
      }
    }
  }

  public void characters(char[] ch, int start, int length) {
    if (curr != null && currTag != null) {
      String s = new String(ch, start, length).trim();
      if (s.length()>0) {
        curr.setProperty(currTag, s);
      }
    }  
  }

  public void endElement(String uri, String localName, String qName) {
    currTag = null;
    if (qName.equals("resource")) {
      configs.add(curr);
    }  
  }

  public void endDocument() throws SAXException {
    
  }
  
}

