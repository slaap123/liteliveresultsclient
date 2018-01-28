/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.phanos.liteliveresultsclient.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Label;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import nl.phanos.liteliveresultsclient.AtletiekNuPanel;
import nl.phanos.liteliveresultsclient.classes.*;
import org.apache.http.conn.util.InetAddressUtils;

/**
 *
 * @author woutermkievit
 */
public class ResultsWindows extends javax.swing.JFrame {

    public long currentDisplayDate = 0;
    //a reference to the GraphicsDevice for changing resolution and making 
    //this window fullscreen.
    private GraphicsDevice device = null;

    //the original resolution before our program is run.
    private DisplayMode dispModeOld = null;

    //variable used to toggle between windowed and fullscreen.
    protected boolean fullscreen = false;
    private ClockServer clockServer;

    public int currentRow = 0;
    private TimerTask tt;

    /**
     * Creates new form ResultsWindows
     */
    public ResultsWindows() {
        super();
        System.setProperty("apple.awt.application.name", "ResultWindow");
        //get a reference to the device.
        GraphicsDevice[] ScreenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        this.device = ScreenDevices[ScreenDevices.length - 1];
        //save the old display mode before changing it.
        dispModeOld = device.getDisplayMode();

        initComponents();
        try {
            Class util = Class.forName("com.apple.eawt.FullScreenUtilities");
            Class params[] = new Class[]{Window.class, Boolean.TYPE};
            Method method = util.getMethod("setWindowCanFullScreen", params);
            method.invoke(util, this, true);
        } catch (Exception e) {
            //System.out.println("OS X Fullscreen FAIL" + e.toString());
            jCheckBoxMenuItem1.setEnabled(true);
        }
        if (!jCheckBoxMenuItem1.isEnabled()) {
            jMenuBar1.remove(jCheckBoxMenuItem1);
        }
        initCustumComponents();
        initClock();
        setSerieResults();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        LayerdPane = new javax.swing.JLayeredPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        SerieLabel = new java.awt.Label();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        SeparateClock = new javax.swing.JCheckBoxMenuItem();
        ChangeIp = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1280, 720));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        jTable1.setBackground(new java.awt.Color(0, 0, 0));
        jTable1.setFont(new java.awt.Font("Lucida Grande", 0, 48)); // NOI18N
        jTable1.setForeground(java.awt.Color.yellow);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jTable1MouseMoved(evt);
            }
        });
        jTable1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jTable1ComponentResized(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        SerieLabel.setBackground(new java.awt.Color(0, 0, 0));
        SerieLabel.setFont(new java.awt.Font("Lucida Grande", 0, 48)); // NOI18N
        SerieLabel.setForeground(java.awt.Color.yellow);
        SerieLabel.setText("10m serie 1");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SerieLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SerieLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        SerieLabel.getAccessibleContext().setAccessibleName("SerieLabel");

        LayerdPane.setLayer(jScrollPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        LayerdPane.setLayer(jPanel1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout LayerdPaneLayout = new javax.swing.GroupLayout(LayerdPane);
        LayerdPane.setLayout(LayerdPaneLayout);
        LayerdPaneLayout.setHorizontalGroup(
            LayerdPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LayerdPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(LayerdPaneLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 722, Short.MAX_VALUE)
                .addContainerGap())
        );
        LayerdPaneLayout.setVerticalGroup(
            LayerdPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LayerdPaneLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE))
        );

        jMenu1.setText("Options");

        jCheckBoxMenuItem1.setText("Fullscreen");
        jCheckBoxMenuItem1.setEnabled(false);
        jCheckBoxMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jCheckBoxMenuItem1);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_EQUALS, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.META_MASK));
        jMenuItem1.setText("Larger Text");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_MINUS, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.META_MASK));
        jMenuItem2.setText("Smaler Text");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Clock");

        SeparateClock.setSelected(true);
        SeparateClock.setText("Separate clock");
        SeparateClock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SeparateClockActionPerformed(evt);
            }
        });
        jMenu2.add(SeparateClock);

        ChangeIp.setText("Change Ip");
        ChangeIp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChangeIpActionPerformed(evt);
            }
        });
        jMenu2.add(ChangeIp);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LayerdPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LayerdPane)
        );

        LayerdPane.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        // TODO add your handling code here:
        ChangeFont(fontSize);
    }//GEN-LAST:event_formComponentResized

    private void jTable1ComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jTable1ComponentResized
        resizeColumns();
    }//GEN-LAST:event_jTable1ComponentResized

    public void resizeColumns() {
        // TODO add your handling code here:
        int tW = jTable1.getWidth();
        TableColumnModel jTableColumnModel = jTable1.getColumnModel();
        int cantCols = jTableColumnModel.getColumnCount();
        if (jTable1.getModel().getColumnCount() > 0) {
            jTableColumnModel.getColumn(0).setPreferredWidth((int) (fontSize * 3.0));
            jTableColumnModel.getColumn(1).setPreferredWidth((int) Math.round(tW - (fontSize * 2.5) - (fontSize * 3.5)));
            jTableColumnModel.getColumn(2).setPreferredWidth((int) (fontSize * 3.5));
        }
    }

    private void jTable1MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseMoved
        // TODO add your handling code here:
        jMenuBar1.setVisible(evt.getY() < 50);
    }//GEN-LAST:event_jTable1MouseMoved

    private void jCheckBoxMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem1ActionPerformed
        //change modes.
        this.fullscreen = !this.fullscreen;
        jCheckBoxMenuItem1.setState(fullscreen);
        // toggle fullscreen mode
        if (!fullscreen) { //change to windowed mode.

            //set the display mode back to the what it was when
            //the program was launched.
            //device.setDisplayMode(dispModeOld);
            //hide the frame so we can change it.
            setVisible(false);

            //remove the frame from being displayable.
            dispose();

            //put the borders back on the frame.
            setUndecorated(false);

            //needed to unset this window as the fullscreen window.
            device.setFullScreenWindow(null);

            //make sure the size of the window is correct.
            setSize(800, 600);

            //recenter window
            setLocationRelativeTo(null);

            //reset the display mode to what it was before 
            //we changed it.
            setVisible(true);

        } else { //change to fullscreen.
            //hide everything
            setVisible(false);

            //remove the frame from being displayable.
            dispose();

            //remove borders around the frame
            setUndecorated(true);

            //make the window fullscreen.
            device.setFullScreenWindow(this);

            //attempt to change the screen resolution.
            //device.setDisplayMode(dispMode);
            //show the frame
            setVisible(true);

        } // end if

        //make sure that the screen is refreshed.
        repaint();
    }//GEN-LAST:event_jCheckBoxMenuItem1ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        fontSize++;
        resizeColumns();
        ChangeFont(fontSize);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        fontSize--;
        resizeColumns();
        ChangeFont(fontSize);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void SeparateClockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SeparateClockActionPerformed
        // TODO add your handling code here
        clockServer.clock.setVisible(SeparateClock.getState());
    }//GEN-LAST:event_SeparateClockActionPerformed

    private void ChangeIpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChangeIpActionPerformed
        // TODO add your handling code here:

        Object s = "";
        do {
            s = JOptionPane.showInputDialog(
                    this,
                    "Geef Ip van MacFinish op",
                    "ip",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "");
        } while (!InetAddressUtils.isIPv4Address((String) s));
        clockServer.changeIp((String) s);
    }//GEN-LAST:event_ChangeIpActionPerformed
    public void setSerieResults() {
        ((DefaultTableModel) jTable1.getModel()).setRowCount(0);
        ((DefaultTableModel) jTable1.getModel()).addRow(new Object[]{1, "Giulia Kuhn", 5.13});
    }

    public void setSerieResults(ResultFile resultFile) {
        SerieLabel.setText(resultFile.BelongsTo.onderdeel + " Serie " + resultFile.BelongsTo.serie + " " + resultFile.wind);
        currentDisplayDate = resultFile.BelongsTo.uploadDate;
        ((DefaultTableModel) jTable1.getModel()).setRowCount(0);
        for (ResultFileEntry entry : resultFile.atleten.values()) {
            ((DefaultTableModel) jTable1.getModel()).addRow(new Object[]{entry.plaats, entry.naam, entry.tijd});
        }
        ((DefaultTableModel) jTable1.getModel()).addRow(new Object[]{jTable1.getRowCount(), "", ""});
        //if (isCellVisible(jTable1, jTable1.getRowCount() - 1, jTable1.getColumnCount())) {
            Timer timer = new Timer();
            tt = new TimerTask() {

                @Override
                public void run() {
                    jTable1.scrollRectToVisible(jTable1.getCellRect(currentRow, jTable1.getColumnCount(), true));
                        System.out.println("currentRow:"+currentRow);
                    if (currentRow < jTable1.getRowCount()) {
                        currentRow++;
                    } else {
                        currentRow=0;
                        //tt.cancel();
                    }
                }
            };
            timer.schedule(tt, 0, 3000);
        //}
        //
    }

    

    private void initCustumComponents() {
        logoLabel = new javax.swing.JLabel();
        icon = new ImageIcon(getCLub());
        logoLabel.setIcon(icon); // NOI18N
        logoLabel.setBounds(this.getWidth() - icon.getIconWidth(),
                this.getHeight() - icon.getIconHeight(),
                icon.getIconWidth(),
                icon.getIconHeight());
        LayerdPane.add(logoLabel, JLayeredPane.PALETTE_LAYER);
        clockLabel = new javax.swing.JLabel();
        clockLabel.setFont(new java.awt.Font("Lucida Grande", 0, fontSize)); // NOI18N
        clockLabel.setText("");
        clockLabel.setForeground(Color.YELLOW);
        LayerdPane.add(clockLabel, JLayeredPane.PALETTE_LAYER);
        jTable1.setRowSelectionAllowed(false);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Plaats", "Atleet", "Tijd"
                }
        ) {
            Class[] types = new Class[]{
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean[]{
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });

        jScrollPane1.getViewport().setBackground(Color.black);
        this.setBackground(Color.black);
        JTableHeader header = jTable1.getTableHeader();
        header.setOpaque(false);
        jPanel1.setBackground(Color.black);
        header.setBackground(Color.black);
        header.setForeground(Color.YELLOW);
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(Color.BLACK);

        for (int i = 0; i < jTable1.getModel().getColumnCount(); i++) {
            if (jTable1.getModel().getColumnCount() > 0) {
                jTable1.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
            }
        }
        DefaultTableCellRenderer LEFTRenderer = new DefaultTableCellRenderer();
        LEFTRenderer.setHorizontalAlignment(JLabel.LEFT);
        jTable1.getColumnModel().getColumn(0).setCellRenderer(LEFTRenderer);
        ChangeFont(fontSize);
    }

    public void ChangeFont(int fontSize) {
        JTableHeader header = jTable1.getTableHeader();
        header.setSize(header.getWidth(), fontSize + 5);
        jTable1.setFont(new java.awt.Font("Lucida Grande", 0, fontSize)); // NOI18N
        SerieLabel.setFont(new java.awt.Font("Lucida Grande", 0, fontSize)); // NOI18N
        SerieLabel.setSize(SerieLabel.getWidth(), fontSize + 5);
        jPanel1.setSize(SerieLabel.getWidth(), fontSize + 5);
        jTable1.setRowHeight(fontSize + 5);
        logoLabel.setBounds(this.getWidth() - icon.getIconWidth(),
                this.getHeight() - icon.getIconHeight(),
                icon.getIconWidth(),
                icon.getIconHeight());
        header.setFont(new java.awt.Font("Lucida Grande", 0, fontSize)); // NOI18N
        clockLabel.setFont(new java.awt.Font("Lucida Grande", 0, fontSize)); // NOI18N
        clockLabel.setBounds(0,
                this.getHeight() - (fontSize + 15),
                fontSize * 30,
                (fontSize + 5));
        repaint();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem ChangeIp;
    private javax.swing.JLayeredPane LayerdPane;
    private javax.swing.JCheckBoxMenuItem SeparateClock;
    private java.awt.Label SerieLabel;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
    private JLabel logoLabel;
    public JLabel clockLabel;
    private ImageIcon icon;
    private int fontSize = 80;

    private void initClock() {
        clockServer = new ClockServer(this);
        (clockServer).start();
    }

    private URL getCLub() {
        String club = AtletiekNuPanel.GetAtletiekNuPanel().club;
        System.out.println(club);
        if (club == null) {
            club = "PhanosAmsterdam";
        }
        URL iconLoc = getClass().getResource("/" + club + "Logo.png");
        if (iconLoc == null) {
            iconLoc = getClass().getResource("/PhanosAmsterdamLogo.png");
        }
        return iconLoc;
    }
}
