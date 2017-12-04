/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.phanos.liteliveresultsclient.gui;

import java.awt.Color;
import java.awt.Label;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import jmapps.jmstudio.AboutDialog;
import nl.phanos.liteliveresultsclient.classes.*;

/**
 *
 * @author woutermkievit
 */
public class ResultsWindows extends javax.swing.JFrame {
    public long currentDisplayDate=0;
    /**
     * Creates new form ResultsWindows
     */
    public ResultsWindows() {
        initComponents();
        initCustumComponents();
        setSerieResults();
        com.apple.eawt.FullScreenUtilities.setWindowCanFullScreen(this, true);
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE))
        );

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
        logoLabel.setBounds(this.getWidth() - icon.getIconWidth(),
                this.getHeight() - icon.getIconHeight(),
                icon.getIconWidth(),
                icon.getIconHeight());
    }//GEN-LAST:event_formComponentResized

//SUMS 100%
    float[] columnWidthPercentage = {75.0f, 10.0f, 15.0f,};
    private void jTable1ComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jTable1ComponentResized
        // TODO add your handling code here:
        int tW = jTable1.getWidth();
        TableColumn column;
        TableColumnModel jTableColumnModel = jTable1.getColumnModel();
        int cantCols = jTableColumnModel.getColumnCount();
        if (jTable1.getModel().getColumnCount() > 0) {
            for (int i = 0; i < cantCols; i++) {
                column = jTableColumnModel.getColumn(i);
                int pWidth = Math.round(columnWidthPercentage[i] * tW);
                column.setPreferredWidth(pWidth);
            }
        }
    }//GEN-LAST:event_jTable1ComponentResized
    public void setSerieResults() {
        ((DefaultTableModel) jTable1.getModel()).setRowCount(0);
        ((DefaultTableModel) jTable1.getModel()).addRow(new Object[]{"jan test", 1, 10.23});
    }

    public void setSerieResults(ResultFile resultFile) {
        SerieLabel.setText(resultFile.BelongsTo.onderdeel + " Serie " + resultFile.BelongsTo.serie + " " + resultFile.wind);
        currentDisplayDate=resultFile.BelongsTo.uploadDate;
        ((DefaultTableModel) jTable1.getModel()).setRowCount(0);
        for (ResultFileEntry entry : resultFile.atleten.values()) {
            ((DefaultTableModel) jTable1.getModel()).addRow(new Object[]{entry.naam, entry.baan, entry.tijd});
        }

    }

    private void initCustumComponents() {
        logoLabel = new javax.swing.JLabel();
        icon = new ImageIcon(getClass().getResource("/phanosLogo.png"));
        logoLabel.setIcon(icon); // NOI18N
        logoLabel.setBounds(this.getWidth() - icon.getIconWidth(),
                this.getHeight() - icon.getIconHeight(),
                icon.getIconWidth(),
                icon.getIconHeight());
        LayerdPane.add(logoLabel, JLayeredPane.PALETTE_LAYER);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Atleet", "Baan", "Tijd"
                }
        ) {
            Class[] types = new Class[]{
                java.lang.String.class, java.lang.Integer.class, java.lang.String.class
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

        jTable1.setFont(new java.awt.Font("Lucida Grande", 0, 80)); // NOI18N
        SerieLabel.setFont(new java.awt.Font("Lucida Grande", 0, 80)); // NOI18N
        jTable1.setRowHeight(85);
        jScrollPane1.getViewport().setBackground(Color.black);
        this.setBackground(Color.black);
        JTableHeader header = jTable1.getTableHeader();
        header.setOpaque(false);
        jPanel1.setBackground(Color.black);
        header.setBackground(Color.black);
        header.setForeground(Color.YELLOW);
        header.setFont(new java.awt.Font("Lucida Grande", 0, 48)); // NOI18N
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(Color.BLACK);

        for (int i = 0; i < jTable1.getModel().getColumnCount(); i++) {
            if (jTable1.getModel().getColumnCount() > 0) {
                jTable1.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(ResultsWindows.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(ResultsWindows.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(ResultsWindows.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(ResultsWindows.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ResultsWindows().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane LayerdPane;
    private java.awt.Label SerieLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
    private JLabel logoLabel;
    private ImageIcon icon;
}
