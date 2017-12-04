package nl.phanos.liteliveresultsclient;

import java.awt.FileDialog;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import nl.phanos.liteliveresultsclient.classes.*;
import nl.phanos.liteliveresultsclient.gui.Main;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author woutermkievit
 */
public class AtletiekNuPanel extends JPanel implements TableModelListener {

    private javax.swing.JSplitPane jSplitPane1 = new javax.swing.JSplitPane();
    public javax.swing.JTextPane jTextPane1 = new JTextPane();
    private javax.swing.JTable parFileNames = new JTable();
    private javax.swing.JScrollPane jScrollPane4 = new JScrollPane();
    private javax.swing.JScrollPane jScrollPane5 = new JScrollPane();
    private JTabbedPane tPane;
    private DonePanel doneView;
    public LoginHandler loginHandler;
    private UnzipUtility unzip;
    private String nuid;
    private File baseDir;
    public final static boolean test = false;
    public final static boolean live = false;
    public HashMap<String, ParFile> parFiles = new HashMap<String, ParFile>();

    public static AtletiekNuPanel panel;

    public static String indentifingColumnName;

    public HashMap<String, ParFile> GetparFiles() {
        return parFiles;
    }

    public static AtletiekNuPanel GetAtletiekNuPanel(final JTabbedPane pane, int nuid) {
        if (panel == null) {
            panel = new AtletiekNuPanel(pane, nuid);
            indentifingColumnName = "externalId";
        }
        return panel;
    }

    public static AtletiekNuPanel GetAtletiekNuPanel() {
        if (panel != null) {
            return panel;
        }
        return null;
    }

    private AtletiekNuPanel(final JTabbedPane pane, int nuid) {

        System.setProperty("apple.awt.fileDialogForDirectories", "true");
        FileDialog dialog = new FileDialog(Main.mainObj);
        dialog.setMultipleMode(false);
        dialog.setVisible(true);
        System.setProperty("apple.awt.fileDialogForDirectories", "false");
        baseDir = dialog.getFiles()[0];
        if (!baseDir.isDirectory()) {
            baseDir = baseDir.getParentFile();
        }

        tPane = pane;
        this.nuid = nuid + "";
        parFileNames.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "parfile", "onderdeel", "serie", "Done"
                }
        ) {
            Class[] types = new Class[]{
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean[]{
                false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        parFileNames.getColumnModel().getColumn(2).setCellRenderer(new StatusColumnCellRenderer());
        parFileNames.getModel().addTableModelListener(this);
        jScrollPane4.setViewportView(parFileNames);

        jSplitPane1.setLeftComponent(jScrollPane4);

        jScrollPane5.setViewportView(jTextPane1);

        jSplitPane1.setRightComponent(jScrollPane5);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(this);
        this.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jSplitPane1)
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
        );

        tPane.addTab("AtletiekNu", this);
        doneView = new DonePanel(pane, this);

        reloadParFiles();
        Main.getWindow().addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                savePrefResults();
            }

        });
        unzip = new UnzipUtility();
        try {
            if (!AtletiekNuPanel.panel.test) {
                loginHandler = new LoginHandler(this.nuid);
                UpdateListRemote();
            } else {
                UpdateListFromLocal();
            }

        } catch (Exception ex) {
            Logger.getLogger(AtletiekNuPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        ResultsHandler handelr = new ResultsHandler(baseDir);
        handelr.start();
    }

    public void tableChanged(TableModelEvent e) {
        if (e.getType() == TableModelEvent.UPDATE) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            TableModel model = (TableModel) e.getSource();
            String name = (String) model.getValueAt(row, 0);
            ParFile entry = parFiles.get(name);
            entry.done = (boolean) model.getValueAt(row, 3);
        }
    }

    public void UpdateList() {
        try {
            if (!panel.test) {
                UpdateListRemote();
            } else {
                UpdateListFromLocal();
            }

        } catch (Exception ex) {
            Logger.getLogger(AtletiekNuPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        parFileNames.repaint();
        doneView.doneParFiles.repaint();
        
    }

    public void UpdateListRemote() {
        try {
            reloadParFiles();
            loginHandler.getZip();
            unzip.unzip("tmp.zip", baseDir.getPath());
            new File("tmp.zip").delete();
            ((DefaultTableModel) parFileNames.getModel()).setRowCount(0);
            ((DefaultTableModel) doneView.doneParFiles.getModel()).setRowCount(0);
            for (File fileEntry : baseDir.listFiles()) {
                if (fileEntry.getName().endsWith("par")) {
                    ParFile entry = null;
                    if (parFiles.containsKey(fileEntry.getName())) {
                        entry = parFiles.get(fileEntry.getName());
                        entry.getValuesFromFile(fileEntry);
                    } else {
                        entry = new ParFile(fileEntry);
                    }
                    //System.out.println("GotResults:" + entry.gotResults);
                    if (!entry.gotResults) {
                        ((DefaultTableModel) parFileNames.getModel()).addRow(new Object[]{entry.fileName, entry.onderdeel + " " + entry.startgroep, entry.serie+"("+entry.atleten.size()+")", entry.done});
                    } else {
                        ((DefaultTableModel) doneView.doneParFiles.getModel()).addRow(new Object[]{entry.fileName, entry.onderdeel + " " + entry.startgroep, entry.serie+"("+entry.atleten.size()+")", entry.UploadedAtleten, entry.forceUpload});
                    }
                    parFiles.put(fileEntry.getName(), entry);
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(AtletiekNuPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void UpdateListFromLocal() {
        try {
            ((DefaultTableModel) parFileNames.getModel()).setRowCount(0);
            for (File fileEntry : baseDir.listFiles()) {
                if (fileEntry.getName().endsWith("par")) {
                    ParFile entry = null;
                    if (parFiles.containsKey(fileEntry.getName())) {
                        entry = parFiles.get(fileEntry.getName());
                        entry.getValuesFromFile(fileEntry);
                    } else {
                        entry = new ParFile(fileEntry);
                    }
                    //System.out.println("GotResults:" + entry.gotResults);
                    if (!entry.gotResults) {
                        ((DefaultTableModel) parFileNames.getModel()).addRow(new Object[]{entry.fileName, entry.onderdeel + " " + entry.startgroep, entry.serie});
                    }
                    parFiles.put(fileEntry.getName(), entry);
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(AtletiekNuPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    void addText(String string) {
        String text = jTextPane1.getText();
        if (!Arrays.asList(text.split("\n")).contains(string)) {
            jTextPane1.setText(string + "\n" + text);
        }
    }

    void removeLine(String string) {
        String text = jTextPane1.getText();
        List<String> lines = Arrays.asList(text.split("\n"));
        String newText = "";
        for (String line : lines) {
            if (!line.equals(string)) {
                newText += "\n" + line;
            }
        }
        jTextPane1.setText(newText.substring(Math.min(1, newText.length())));
    }

    private void reloadParFiles() {
        File file = new File(baseDir.getAbsolutePath() + "/results.ser");
        try {
            FileInputStream streamIn = new FileInputStream(file);
            ObjectInputStream objectinputstream = null;
            try {
                
                objectinputstream = new ObjectInputStream(streamIn);
                HashMap<String, ParFile> readCase = (HashMap<String, ParFile>) objectinputstream.readObject();
                for (Map.Entry<String, ParFile> e : readCase.entrySet()) {
                    if (parFiles.containsKey(e.getKey())) {
                        ParFile parfile = parFiles.get(e.getKey());
                        if (parfile.uploadDate < e.getValue().uploadDate) {
                            parFiles.put(e.getKey(), e.getValue());
                            System.out.println("newer results");
                        }
                        parfile.done=e.getValue().done||parfile.done;
                    } else {
                        System.out.println("new result");
                        parFiles.put(e.getKey(), e.getValue());
                    }
                    if(Main.getWindow().resultsWindow!=null&&e.getValue().uploadDate>Main.getWindow().resultsWindow.currentDisplayDate){
                        System.out.println("new result for sb");
                        Main.getWindow().resultsWindow.setSerieResults(parFiles.get(e.getKey()).results);
                    }
                }
            } catch (Exception e) {
                System.out.println("Failed to load old results");
                System.out.println(e.toString());
            } finally {
                if (objectinputstream != null) {
                    try {
                        objectinputstream.close();
                        System.out.println("should have synced");
                    } catch (IOException ex) {
                        Logger.getLogger(AtletiekNuPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
           
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void savePrefResults() {
        File file = new File(baseDir.getAbsolutePath() + "/results.ser");
        try {


            // Use the file channel to create a lock on the file.
            // This method blocks until it can retrieve the lock.
            
            FileOutputStream fout = new FileOutputStream(file);
            FileLock lock = fout.getChannel().lock();
            ObjectOutputStream oos = null;
            try {
                oos = new ObjectOutputStream(fout);
                oos.writeObject(parFiles);
            } catch (Exception ex) {
                System.out.println("Failed to write results");
                ex.printStackTrace();
            } finally {
                if (oos != null) {
                    try {
                        oos.close();
                        System.out.println("saved for synced");
                    } catch (IOException ex) {
                        Logger.getLogger(AtletiekNuPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            if (lock != null) {
                lock.release();
            }
        } catch (Exception e) {
        }
    }

}
