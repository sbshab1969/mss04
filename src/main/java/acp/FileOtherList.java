package acp;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import acp.service.FileOtherManager;
import acp.ssb.table.TbPanel;
import acp.utils.*;

public class FileOtherList extends MyInternalFrame {
  private static final long serialVersionUID = 1L;

  private int fileId;
  private FileOtherManager tableManager;

  TbPanel tabPanel;
  JTable jTable;

  JPanel pnlButtons = new JPanel();
  JPanel pnlBtnExit = new JPanel();
  JButton btnClose = new JButton(Messages.getString("Button.Close"));

  public FileOtherList(int file_id) {
    fileId = file_id;

    desktop.add(this);
    if (fileId > 0) {
      setTitle(Messages.getString("Title.AdvFileInfo"));
      setSize(1000, 500);
    } else {
      setTitle(Messages.getString("Title.OtherLogs"));
      setSize(1200, 650);
    }
    setToCenter();
    setMaximizable(true);
    setResizable(true);

    tableManager = new FileOtherManager(dbConnection, fileId);

    // --- Table ---
    tabPanel = new TbPanel(dbConnection);
    String[] fieldnames = tableManager.getFieldnames();
    tabPanel.setHeaders(fieldnames);
    if (fileId > 0) {
      tabPanel.setQueryAllRecords(true);
    } else {
      tabPanel.setQueryAllRecords(false);
      tabPanel.setRecOnPage(30);
    }
    jTable = tabPanel.getTable();
    jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    // jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

    // Buttons ---
    pnlButtons.setLayout(new BorderLayout());
    pnlButtons.add(pnlBtnExit, BorderLayout.EAST);
    pnlBtnExit.add(btnClose);

    btnClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });

    // --- Layout ---
    Container cp = getContentPane();
    cp.setLayout(new BorderLayout());
    cp.add(tabPanel, BorderLayout.CENTER);
    cp.add(pnlButtons, BorderLayout.SOUTH);
  }

  public boolean initForm() {
    // -----------------------
    boolean res = initTable();
    // -----------------------
    return res;
  }

  private boolean initTable() {
    boolean res = false;
    String query = tableManager.selectList();
    String queryCnt = tableManager.selectCount();
    // --------------------------------
    tabPanel.setQuery(query, queryCnt);
    res = tabPanel.executeQuery();
    if (fileId == 0) {
      tabPanel.selectFirst();
    }
    // --------------------------------
    return res;
  }

}
