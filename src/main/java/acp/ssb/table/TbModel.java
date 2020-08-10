package acp.ssb.table;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import acp.utils.DbUtils;

class TbModel extends AbstractTableModel {
  private static final long serialVersionUID = 1L;

  Connection db;
  Statement statement;
  ResultSet rs;

  String strQuery;
  String strQueryCnt;

  String[] headers;

  int colCount;
  int rowCount;
  ArrayList<String[]> cache;

  int recCount;
  int recStart;

  int recOnPage = 20;
  int currPage;
  int pageCount;

  public TbModel(Connection conn) {
    db = conn;
    cache = new ArrayList<>();
    // cache = new ArrayList<String[]>();
  }

  public String getColumnName(int i) {
    return headers[i];
  }

  public int getColumnCount() {
    return colCount;
  }

  public int getRowCount() {
    return cache.size();
  }

  public Object getValueAt(int row, int col) {
    return cache.get(row)[col];
  }

  public int getRecOnPage() {
    return recOnPage;
  }

  public void setRecOnPage(int recOnPage) {
    this.recOnPage = recOnPage;
  }

  public int getPageCount() {
    return pageCount;
  }

  public int getCurrPage() {
    return currPage;
  }

  public void calcPages() {
    recCount = DbUtils.getValueN(db, strQueryCnt);
    if (recCount > 0) {
      int fullPageCount = recCount / recOnPage;
      int tail = recCount - fullPageCount * recOnPage;
      if (tail == 0) {
        pageCount = fullPageCount;
      } else {
        pageCount = fullPageCount + 1;
      }
    } else {
      pageCount = 0;
    }
  }

  public void calcRecStart() {
    if (currPage > 0) {
      recStart = (currPage - 1) * recOnPage + 1;
    } else {
      recStart = 0;
    }
  }

  public void setHeaders(String[] headers) {
    this.headers = headers;
    if (headers == null) {
      colCount = 0;
    } else {
      colCount = headers.length;
    }
  }

  public void fillHeaders() {
    try {
      ResultSetMetaData meta = rs.getMetaData();
      colCount = meta.getColumnCount();
      headers = new String[colCount];
      for (int h = 1; h <= colCount; h++) {
        headers[h - 1] = meta.getColumnName(h);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void setQuery(String strQry, String strQryCnt) {
    strQuery = strQry;
    if (strQryCnt == null) {
      strQueryCnt = "select count(*) cnt from (" + strQry + ")";
    } else {
      strQueryCnt = strQryCnt;
    }
  }

  public boolean executeQuery(boolean queryAll) {
    boolean res = false;
    try {
      statement = db.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
          ResultSet.CONCUR_UPDATABLE);
      rs = statement.executeQuery(strQuery);
      if (headers == null) {
        fillHeaders();
      }
      if (queryAll) {
        res = fetchAll();
      } else {
        res = startPage();
      }
    } catch (SQLException e) {
      cache = new ArrayList<>();
      e.printStackTrace();
    }
    return res;
  }

  public boolean startPage() {
    if (rs == null) {
      return true;
    }
    boolean res = false;
    calcPages();
    if (pageCount > 0) {
      currPage = 1;
    } else {
      currPage = 0;
    }
    res = fetchPage();
    return res;
  }

  public boolean firstPage() {
    if (rs == null) {
      return true;
    }
    boolean res = false;
    calcPages();
    if (currPage > 1) {
      currPage = 1;
    }
    res = fetchPage();
    return res;
  }

  public boolean previousPage() {
    if (rs == null) {
      return true;
    }
    boolean res = false;
    calcPages();
    if (currPage > 1) {
      currPage--;
    }
    res = fetchPage();
    return res;
  }

  public boolean nextPage() {
    if (rs == null) {
      return true;
    }
    boolean res = false;
    calcPages();
    if (currPage < pageCount) {
      currPage++;
    }
    res = fetchPage();
    return res;
  }

  public boolean lastPage() {
    if (rs == null) {
      return true;
    }
    boolean res = false;
    calcPages();
    if (currPage < pageCount) {
      currPage = pageCount;
    }
    res = fetchPage();
    return res;
  }

  public boolean fetchPage() {
    cache = new ArrayList<>();
    calcRecStart();
    // -----------------
    if (recStart <= 0 || recStart > recCount) {
      fireTableChanged(null);
      return true;
    }
    // -----------------
    boolean res = false;
    try {
      rs.absolute(recStart);
      int recCnt = 0;
      do {
        recCnt++;
        String[] record = new String[colCount];
        for (int i = 0; i < colCount; i++) {
          record[i] = rs.getString(i + 1);
        }
        cache.add(record);
      } while (rs.next() && recCnt < recOnPage);
      fireTableChanged(null);
      res = true;
    } catch (SQLException e) {
      cache = new ArrayList<>();
      e.printStackTrace();
    }
    return res;
  }

  public boolean fetchAll() {
    boolean res = false;
    cache = new ArrayList<>();
    try {
      int recCnt = 0;
      while (rs.next()) {
        recCnt++;
        String[] record = new String[colCount];
        for (int i = 0; i < colCount; i++) {
          record[i] = rs.getString(i + 1);
        }
        cache.add(record);
      }
      rowCount = recCnt;
      fireTableChanged(null);
      res = true;
    } catch (SQLException e) {
      cache = new ArrayList<>();
      e.printStackTrace();
    }
    return res;
  }

}
