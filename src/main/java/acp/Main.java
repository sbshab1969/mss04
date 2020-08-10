package acp;

import javax.swing.*;

import java.awt.event.*;

import acp.utils.*;

public class Main extends JFrame {
  private static final long serialVersionUID = 1L;
  
  private static Main mainFrame = null;
  private static JDesktopPane desktop = new JDesktopPane();

  public Main() {
    super(Messages.getString("Title.Main") + " (4)");
    setContentPane(desktop);
    setSize(1200, 700);
    setLocationRelativeTo(null); // размещение по центру экрана
    setExtendedState(MAXIMIZED_BOTH);
    // desktop.setDragMode(JDesktopPane.LIVE_DRAG_MODE);
    // desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);

    MainMenu mainMenu = new MainMenu();
    setJMenuBar(mainMenu.createMenuBar());

    setDefaultCloseOperation(EXIT_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent evt) {
        formWindowClosing(evt);
      }
    });
  }

  private void formWindowClosing(WindowEvent evt) {
    DbConnect.disconnect();
  }

  public static void setTitle() {
    mainFrame.setTitle(Messages.getString("Title.Main"));
  }

  public static JDesktopPane getDesktop() {
    return desktop;
  }

  private static void createAndShowGUI() {
    // --- Установка L&F перед созданием формы ---
    // JFrame.setDefaultLookAndFeelDecorated(true);
    // JDialog.setDefaultLookAndFeelDecorated(true);

    // Установка Look and Feel
    //try {
    //  UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel"); // default
    //  // UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    //  // UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
    //  // UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
    //  // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    //} catch (Exception e) {
    //  e.printStackTrace();
    //}
    // System.out.println(UIManager.getSystemLookAndFeelClassName());

    mainFrame = new Main();
    
    // --- Установка L&F после создания формы ---
    // mainFrame.setUndecorated(true);
    // mainFrame.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);

    // Если есть установки после создания формы, то setVisible после
    mainFrame.setVisible(true);
  }

  private static void logon() {
    Logon logonObj = new Logon();
    logonObj.showModal(true);
    logonObj = null;
  }

  public static void main(String[] args) {
    // java.util.Locale.setDefault(java.util.Locale.US);
    // ----------------
    createAndShowGUI();
    // ----------------
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        logon();
      }
    });
    // ----------------
  }
  
}
