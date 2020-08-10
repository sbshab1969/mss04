package acp.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

public class MyErrorHandler implements ErrorHandler {
  
  private static Logger logger = LoggerFactory.getLogger(MyErrorHandler.class);
  
  public MyErrorHandler() {
  }

  public void warning(SAXParseException e) {
    logger.warn(e.getMessage());
  }

  public void error(SAXParseException e) {
    logger.error(e.getMessage());
  }

  public void fatalError(SAXParseException e) {
    logger.error(e.getMessage());
  }

}
