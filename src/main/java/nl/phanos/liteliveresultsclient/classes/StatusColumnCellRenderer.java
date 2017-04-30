/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.phanos.liteliveresultsclient.classes;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import nl.phanos.liteliveresultsclient.AtletiekNuPanel;

public class StatusColumnCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

        //Cells are by default rendered as a JLabel.
        JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

        //Get the status for the current row.
        TableModel model = (TableModel) table.getModel();
        String name = (String) model.getValueAt(row, 0);
        ParFile entry = AtletiekNuPanel.GetAtletiekNuPanel().parFiles.get(name);
        if (entry.foundResult) {
            l.setBackground(Color.GREEN);
        } else if (isSelected) {
            l.setBackground(Color.BLUE);
        } else {
            l.setBackground(Color.white);
        }

        //Return the JLabel which renders the cell.
        return l;
    }
}
