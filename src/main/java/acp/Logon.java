package acp;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.*;
import javax.swing.border.LineBorder;

import acp.utils.*;
import acp.xml.*;

public class Logon extends MyInternalFrame {
  private static final long serialVersionUID = 1L;
  private int resultForm = RES_NONE;
  
  DbConfig dbConfig = null;
  ArrayList<String> listConfig = null;
  Properties currProp = null;

  JPanel pnlData = new JPanel();
  JTextField txtUser = new JTextField(20);
  JPasswordField txtPassword = new JPasswordField(20);
//  JTextField txtConnString = new JTextField(20);
//  JTextField txtDriver = new JTextField(20);

  JComboBox<String> cmbDatabase = new JComboBox<>();

  JPanel pnlButtons = new JPanel();
  JPanel pnlBtnRecord = new JPanel();
  JButton btnOk = new JButton(Messages.getString("Button.Ok"));
  JButton btnCancel = new JButton(Messages.getString("Button.Cancel"));

  public Logon() {
    desktop.add(this);
    Container cp = getContentPane();

    JLabel lblUser = new JLabel(Messages.getString("Column.User"),JLabel.TRAILING);
    JLabel lblPassword = new JLabel(Messages.getString("Column.Password"),JLabel.TRAILING);
//    JLabel lblConnString = new JLabel(Messages.getString("Column.ConnString"),JLabel.TRAILING);
//    JLabel lblDriver = new JLabel(Messages.getString("Column.Driver"),JLabel.TRAILING);
    JLabel lblDatabase = new JLabel(Messages.getString("Column.Database"),JLabel.TRAILING);

    pnlData.setLayout(new SpringLayout());
    pnlData.setBorder(new LineBorder(Color.BLACK));
    
    pnlData.add(lblUser);
    pnlData.add(txtUser);
    lblUser.setLabelFor(txtUser);

    pnlData.add(lblPassword);
    pnlData.add(txtPassword);
    lblPassword.setLabelFor(txtPassword);

//    pnlData.add(lblConnString);
//    pnlData.add(txtConnString);
//    lblConnString.setLabelFor(txtConnString);

//    pnlData.add(lblDriver);
//    pnlData.add(txtDriver);
//    lblDriver.setLabelFor(txtDriver);

    pnlData.add(lblDatabase);
    pnlData.add(cmbDatabase);
    lblDatabase.setLabelFor(cmbDatabase);

    SpringUtilities.makeCompactGrid(pnlData, 3, 2, 10, 10, 10, 10);

    pnlButtons.add(pnlBtnRecord);
    pnlBtnRecord.setLayout(new GridLayout(1, 2, 20, 0));
    pnlBtnRecord.add(btnOk);
    pnlBtnRecord.add(btnCancel);

    cp.add(pnlData, BorderLayout.CENTER);
    cp.add(pnlButtons, BorderLayout.SOUTH);

    pack();
    setToCenter();

    MyActionListener myActionListener = new MyActionListener();
    btnOk.addActionListener(myActionListener);
    btnCancel.addActionListener(myActionListener);

    initForm();
  }

  public boolean initForm() {
    this.resultForm = RES_NONE;
    setTitle(Messages.getString("Title.Logon"));
    // ------------------------
    clearForm();
    fillForm();
    // ------------------------
    return true;
  }

  private void clearForm() {
    txtUser.setText("");
    txtPassword.setText("");
//    txtConnString.setText("");
//    txtDriver.setText("");
    cmbDatabase.setSelectedIndex(-1);
  }

  private boolean fillForm() {
    // -------------------------------
    if (listConfig == null) {
      // -------------------------------------------      
      // dbConfig = new DbConfigProp();
      // dbConfig.loadConfigXml("oracle.conf");
      // -------------------------------------------      
      // dbConfig = new DbConfigDom();
      dbConfig = new DbConfigSax();
      dbConfig.loadConfigXml("config.xml");
      // -------------------------------------------      
      listConfig = dbConfig.getConfigNames();
    } 
    // -------------------------------
    cmbDatabase.removeAllItems();
    for (String conf : listConfig) {
      cmbDatabase.addItem(conf);
    }
    cmbDatabase.setMaximumRowCount(3);
    cmbDatabase.setSelectedIndex(-1);
    // -------------------------------
    cmbDatabase.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JComboBox<?> cmb = (JComboBox<?>) e.getSource();
        Properties props = dbConfig.getConfigProp(cmb.getSelectedIndex());
        setCurrProp(props);
      }
    });
    // -------------------------------
    Properties props = DbConnect.getParams();
    if (props != null) {
      String vIndex = props.getProperty(DbConst.DB_INDEX,"-1");
      int index = Integer.valueOf(vIndex);
      if (index>=0 && index < cmbDatabase.getItemCount()) {
        cmbDatabase.setSelectedIndex(index);
      }  
      setCurrProp(props);
    } else {
      if (cmbDatabase.getItemCount()>0 ) {
        cmbDatabase.setSelectedIndex(0);
      }
    }
    return true;
  }

  private void setCurrProp(Properties props) {
    if (props != null) {
      txtUser.setText(props.getProperty(DbConst.DB_USER));
      txtPassword.setText(props.getProperty(DbConst.DB_PASSWORD));
//      txtConnString.setText(props.getProperty(DbConst.DB_CONN_STRING));
//      txtDriver.setText(props.getProperty(DbConst.DB_DRIVER));
    } else {
      clearForm();
    }
    currProp = props;
  }
  
  private Properties getCurrProp() {
    String user = txtUser.getText();
    String passwd = new String(txtPassword.getPassword());
//    String connString = txtConnString.getText();
//    String driver = txtDriver.getText();
    // -------------------------------------------
    currProp.setProperty(DbConst.DB_USER, user);
    currProp.setProperty(DbConst.DB_PASSWORD, passwd);
//    currProp.setProperty(DbConst.DB_CONN_STRING, connString);
//    currProp.setProperty(DbConst.DB_DRIVER, driver);
    // -------------------------------------------
    return currProp; 
  }

  private class MyActionListener implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
      Object objSource = ae.getSource();
      if (objSource.equals(btnOk)) {
        // -----------------------------------------------
        Properties props = getCurrProp();  
        DbConnect.setParams(props);
        DbConnect.connect();
        //------------------
        if (DbConnect.testConnection()) {
          dispose();
          resultForm = RES_OK;
        } else {  
          DialogUtils.errorMsg(Messages.getString("Message.ConnectError"));
        }
        // -----------------------------------------------

      } else if (objSource.equals(btnCancel)) {
        dispose();
        resultForm = RES_CANCEL;
      }
    }
  }

  public int getResultForm() {
    return resultForm;
  }


}
