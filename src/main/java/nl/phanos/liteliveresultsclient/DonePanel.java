/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.phanos.liteliveresultsclient;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import nl.phanos.liteliveresultsclient.classes.ParFile;

/**
 *
 * @author PhanosIT
 */
public class DonePanel extends JPanel implements TableModelListener {

    public javax.swing.JTable doneParFiles = new JTable();
    private javax.swing.JScrollPane jScrollPane4 = new JScrollPane();
    private JTabbedPane tPane;
    private AtletiekNuPanel atletiekNuPanel;

    public DonePanel(final JTabbedPane pane, AtletiekNuPanel atletiekNuPanel) {
        tPane=pane;
        this.atletiekNuPanel=atletiekNuPanel;
        doneParFiles.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "parfile", "onderdeel", "serie","atleten", "reupload"
                }
        ) {
            Class[] types = new Class[]{
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean[]{
                false, false, false,false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        doneParFiles.getModel().addTableModelListener(this);
        jScrollPane4.setViewportView(doneParFiles);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(this);
        this.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane4)
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
        );
        tPane.addTab("Done", this);
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if (e.getType() == TableModelEvent.UPDATE) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            TableModel model = (TableModel) e.getSource();
            String columnName = model.getColumnName(column);
            String name = (String) model.getValueAt(row, 0);
            ParFile entry = atletiekNuPanel.parFiles.get(name);
            entry.done = (boolean) model.getValueAt(row, 3);
        }
    }

}
