package nl.phanos.liteliveresultsclient;

import java.awt.FileDialog;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
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
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.util.Util;

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
    public boolean live = true;
    public boolean slave = false;
    public HashMap<String, ParFile> parFiles = new HashMap<String, ParFile>();

    public static AtletiekNuPanel panel;

    public static String indentifingColumnName;
    private ResultScreenClient RSC;
    JChannel channel;
    public String club;

    public HashMap<String, ParFile> GetparFiles() {
        return parFiles;
    }

    public static AtletiekNuPanel GetAtletiekNuPanel(JTabbedPane JTabbedPane, Object s) {
        if (panel == null) {
            if (s instanceof String) {
                panel = new AtletiekNuPanel(JTabbedPane, Integer.parseInt((String) s));
            } else if (s instanceof Wedstrijd) {
                panel = new AtletiekNuPanel(JTabbedPane, Integer.parseInt(((Wedstrijd) s).id));
                panel.club=((Wedstrijd) s).club;
            } else if (s instanceof Integer) {
                panel = new AtletiekNuPanel(JTabbedPane, (int) s);
            }
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
                loginHandler = LoginHandler.get();
                loginHandler.setNuid(this.nuid);
                UpdateListRemote();
            } else {
                UpdateListFromLocal();
            }

        } catch (Exception ex) {
            Logger.getLogger(AtletiekNuPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        InitChannel();
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

    }

    public void UpdateListRemote() {
        try {
            //reloadParFiles();
            if (!slave) {
                loginHandler.getZip();
                unzip.unzip("tmp.zip", baseDir.getPath());
                new File("tmp.zip").delete();
            }
            for (File fileEntry : baseDir.listFiles()) {
                if (fileEntry.getName().endsWith("par")) {
                    ParFile entry = null;
                    if (parFiles.containsKey(fileEntry.getName())) {
                        entry = parFiles.get(fileEntry.getName());
                        entry.getValuesFromFile(fileEntry);
                    } else {
                        entry = new ParFile(fileEntry);
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
                        //((DefaultTableModel) parFileNames.getModel()).addRow(new Object[]{entry.fileName, entry.onderdeel + " " + entry.startgroep, entry.serie});
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

    public void reloadParFiles() {
        if (slave) {
            File file = new File(baseDir.getAbsolutePath() + "/results.ser");
            readSerialized(file);
        } else {
            File file = new File(baseDir.getAbsolutePath() + "/results.ser");
            readSerialized(file);
//            File[] files = new File(baseDir.getAbsolutePath()).listFiles();
//            for (File file : files) {
//                if (file.isFile() && file.getName().endsWith(".ser")) {
//                    readSerialized(file);
//                }
//            }
        }
        UpdateView();
    }

    public void readSerialized(File file) {
        try {
            FileInputStream streamIn = new FileInputStream(file);
            ObjectInputStream objectinputstream=null;
        try {
            
            objectinputstream = new ObjectInputStream(streamIn);
            HashMap<String, ParFile> readCase = (HashMap<String, ParFile>) objectinputstream.readObject();
            setParFiles(readCase);
        } catch (Exception e) {
            System.out.println("Failed to load old results");
            System.out.println(e.toString());
        } finally {
            if (objectinputstream != null) {
                try {
                    objectinputstream.close();
                    //System.out.println("should have synced");
                } catch (IOException ex) {
                    Logger.getLogger(AtletiekNuPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setParFiles(HashMap<String, ParFile> newParfiles) {
        System.out.println("merge parfiles");
        for (Map.Entry<String, ParFile> e : newParfiles.entrySet()) {
            if (parFiles.containsKey(e.getKey())) {
                ParFile parfile = parFiles.get(e.getKey());
                //if (parfile.uploadDate < e.getValue().uploadDate) {
                    parFiles.put(e.getKey(), e.getValue());
                    //System.out.println("newer results");
                //}
                parfile.done = e.getValue().done || parfile.done;
            } else {
                //System.out.println("new result");
                parFiles.put(e.getKey(), e.getValue());
            }
            if (Main.getWindow().resultsWindow != null && e.getValue().uploadDate > Main.getWindow().resultsWindow.currentDisplayDate) {
                System.out.println("new result for sb");
                Main.getWindow().resultsWindow.setSerieResults(parFiles.get(e.getKey()).results);
            }
        }
    }

    public void savePrefResults() {
        String key = "";
        if (slave) {
            try {
                InetAddress ip = InetAddress.getLocalHost();

                NetworkInterface network = NetworkInterface.getByInetAddress(ip);

                byte[] mac = network.getHardwareAddress();
                StringBuilder sb = new StringBuilder();
                for (byte b : mac) {
                    sb.append(String.format("%02X", b));
                }
                key = new String(sb.toString());
            } catch (SocketException ex) {
                System.out.println("error");
                Logger.getLogger(AtletiekNuPanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnknownHostException ex) {
                System.out.println("error");
                Logger.getLogger(AtletiekNuPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = null;
            try {
//                out = new ObjectOutputStream(bos);
//                out.writeObject(parFiles);
//                out.flush();
//                byte[] yourBytes = bos.toByteArray();
                if (channel != null) {
                    byte[] dataBytes = bos.toByteArray();
                    Message message = new Message(null, parFiles);
                    channel.send(message);
                }
            } catch (Exception ex) {
                System.out.println("error send parfiles");
                Logger.getLogger(AtletiekNuPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
//            catch (IOException ex) {
//                System.out.println("error send 2");
//                System.out.println(ex.toString());
//            } finally {
//                try {
//                    bos.close();
//                } catch (IOException ex) {
//                    // ignore close exception
//                }
//            }
        }
        File file = new File(baseDir.getAbsolutePath() + "/results" + key + ".ser");
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
                //System.out.println("Failed to write results");
                ex.printStackTrace();
            } finally {
                if (oos != null) {
                    try {
                        oos.close();
                        //System.out.println("saved for synced");
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

    public void ChangeSlaveState() {
        slave = !slave;
        if (slave) {
            channel = null;;
        }else{
            channel = RSC.channel;
        }
    }

    public void InitChannel() {
        try {
            RSC = new ResultScreenClient();
            channel = RSC.channel;
        } catch (Exception ex) {
            Logger.getLogger(AtletiekNuPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void UpdateView() {
        //((DefaultTableModel) parFileNames.getModel()).setRowCount(0);
        //((DefaultTableModel) doneView.doneParFiles.getModel()).setRowCount(0);
        int iToDo = 0;
        int iDone = 0;
        DefaultTableModel model = (DefaultTableModel) parFileNames.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (((ParFile) parFiles.get(model.getValueAt(i, 0))).gotResults) {
                model.removeRow(i);
            } else {

            }
        }
        TreeSet<String> treeSet = new TreeSet<String>(new Comparator<String>() {
            public int compare(String o1, String o2) {
                if (o1.matches("\\d+\\.txt") && o2.matches("\\d+\\.txt")) {
                    int n1 = Integer.parseInt(o1.replace(".txt", ""));
                    int n2 = Integer.parseInt(o2.replace(".txt", ""));
                    return n1 - n2;
                } else {
                    return o1.compareTo(o2);
                }
            }
        });
        treeSet.addAll(parFiles.keySet());
        for (String key : treeSet) {
            ParFile entry = parFiles.get(key);
            if (!entry.gotResults) {
                if (model.getRowCount() > iToDo && ((String) model.getValueAt(iToDo, 0)).equals(entry.fileName)) {
                    model.setValueAt(entry.onderdeel + " " + entry.startgroep, iToDo, 1);
                    model.setValueAt(entry.serie + "(" + entry.atleten.size() + ")", iToDo, 2);
                    model.setValueAt(entry.done, iToDo, 3);
                } else {
                    model.insertRow(iToDo, new Object[]{entry.fileName, entry.onderdeel + " " + entry.startgroep, entry.serie + "(" + entry.atleten.size() + ")", entry.done});
                }
                iToDo++;
            } else {
                DefaultTableModel modelDone = (DefaultTableModel) doneView.doneParFiles.getModel();
                if (modelDone.getRowCount() > iDone && ((String) modelDone.getValueAt(iDone, 0)).equals(entry.fileName)) {
                    modelDone.setValueAt(entry.onderdeel + " " + entry.startgroep, iDone, 1);
                    modelDone.setValueAt(entry.serie + "(" + entry.atleten.size() + ")", iDone, 2);
                    modelDone.setValueAt(entry.UploadedAtleten, iDone, 3);
                    modelDone.setValueAt(entry.forceUpload, iDone, 4);
                } else {
                    modelDone.insertRow(iDone, new Object[]{entry.fileName, entry.onderdeel + " " + entry.startgroep, entry.serie + "(" + entry.atleten.size() + ")", entry.UploadedAtleten, entry.forceUpload});
                }
                iDone++;
            }
        }
        parFileNames.repaint();
        doneView.doneParFiles.repaint();
    }
}
