package com.openfusionview.ui;

import com.openfusionview.core.AppConfig;
import com.openfusionview.core.SeriesRecord;
import ij.ImagePlus;
import ij.io.Opener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class BrowserDialog extends JDialog {

    private JTable table;
    private DefaultTableModel tableModel;
    private MainFrame mainFrame;
    private JLabel statusLabel;
    private JButton btnScan; 
    private final ResourceBundle bundle;

    public BrowserDialog(MainFrame parent) {
        super(parent, false); 
        this.mainFrame = parent;
        
        Locale currentLocale = Locale.of(AppConfig.getLanguage());
        this.bundle = ResourceBundle.getBundle("messages", currentLocale);
        
        setTitle(bundle.getString("browser.title"));
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnScan = new JButton(bundle.getString("browser.scan")); 
        JButton btnLoadCT = new JButton(bundle.getString("browser.set_ct"));
        JButton btnLoadNM = new JButton(bundle.getString("browser.set_nm"));
        JButton btnLoadBoth = new JButton(bundle.getString("browser.load"));
        
        btnLoadBoth.setFont(new Font("Arial", Font.BOLD, 12));
        btnLoadBoth.setBackground(new Color(46, 204, 113));
        btnLoadBoth.setForeground(Color.WHITE);

        topPanel.add(btnScan);
        topPanel.add(new JLabel(" | "));
        topPanel.add(btnLoadCT);
        topPanel.add(btnLoadNM);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(btnLoadBoth);

        add(topPanel, BorderLayout.NORTH);

        String[] columns = {
            bundle.getString("browser.col.id"), 
            bundle.getString("browser.col.name"), 
            bundle.getString("browser.col.mod"), 
            bundle.getString("browser.col.date"), 
            bundle.getString("browser.col.path")
        };
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        statusLabel = new JLabel(" " + bundle.getString("browser.status.ready") + " " + AppConfig.getDicomRoot());
        
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblSelectedCT = new JLabel(bundle.getString("browser.ct_selected") + " " + bundle.getString("browser.none"));
        JLabel lblSelectedNM = new JLabel(bundle.getString("browser.nm_selected") + " " + bundle.getString("browser.none"));
        lblSelectedCT.setForeground(Color.CYAN);
        lblSelectedNM.setForeground(Color.ORANGE);
        selectionPanel.setBackground(Color.DARK_GRAY);
        selectionPanel.add(lblSelectedCT);
        selectionPanel.add(Box.createHorizontalStrut(30));
        selectionPanel.add(lblSelectedNM);

        bottomPanel.add(statusLabel);
        bottomPanel.add(selectionPanel);
        add(bottomPanel, BorderLayout.SOUTH);

        final String[] selectedCtPath = {null};
        final String[] selectedNmPath = {null};

        btnScan.addActionListener(e -> scanFolder());

        btnLoadCT.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                selectedCtPath[0] = (String) tableModel.getValueAt(row, 4);
                lblSelectedCT.setText(bundle.getString("browser.ct_selected") + " " + tableModel.getValueAt(row, 1) + " (" + tableModel.getValueAt(row, 2) + ")");
            }
        });

        btnLoadNM.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                selectedNmPath[0] = (String) tableModel.getValueAt(row, 4);
                lblSelectedNM.setText(bundle.getString("browser.nm_selected") + " " + tableModel.getValueAt(row, 1) + " (" + tableModel.getValueAt(row, 2) + ")");
            }
        });

        btnLoadBoth.addActionListener(e -> {
            if (selectedCtPath[0] == null && selectedNmPath[0] == null) {
                JOptionPane.showMessageDialog(this, bundle.getString("browser.msg.select"));
                return;
            }
            mainFrame.loadFromBrowser(selectedCtPath[0], selectedNmPath[0]);
        });

        if (!AppConfig.getDicomRoot().isEmpty()) {
            scanFolder();
        }
    }

    private void scanFolder() {
        String rootPath = AppConfig.getDicomRoot();
        if (rootPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, bundle.getString("browser.msg.noroot"));
            return;
        }

        File root = new File(rootPath);
        if (!root.exists() || !root.isDirectory()) {
            JOptionPane.showMessageDialog(this, bundle.getString("browser.msg.invalid"));
            return;
        }

        statusLabel.setText(" " + bundle.getString("browser.status.scanning"));
        tableModel.setRowCount(0); 
        btnScan.setEnabled(false);

        new SwingWorker<List<SeriesRecord>, Void>() {
            @Override
            protected List<SeriesRecord> doInBackground() {
                List<SeriesRecord> records = new ArrayList<>();
                crawlDirectory(root, records);
                return records;
            }

            @Override
            protected void done() {
                try {
                    List<SeriesRecord> records = get();
                    for (SeriesRecord r : records) {
                        tableModel.addRow(new Object[]{r.patientId, r.patientName, r.modality, r.date, r.folderPath});
                    }
                    statusLabel.setText(" " + bundle.getString("browser.status.complete") + " " + records.size());
                } catch (Exception ex) {
                    statusLabel.setText(" " + bundle.getString("browser.status.error"));
                    ex.printStackTrace();
                } finally {
                    btnScan.setEnabled(true); 
                }
            }
        }.execute();
    }

    private void crawlDirectory(File dir, List<SeriesRecord> records) {
        File[] files = dir.listFiles();
        if (files == null) return;

        boolean hasDicomFiles = false;
        File firstDicom = null;

        for (File f : files) {
            if (f.isDirectory()) {
                crawlDirectory(f, records); 
            } else if (!hasDicomFiles && (f.getName().toLowerCase().endsWith(".dcm") || f.getName().matches(".*\\d+.*"))) {
                hasDicomFiles = true;
                firstDicom = f;
            }
        }

        if (hasDicomFiles && firstDicom != null) {
            Opener opener = new Opener();
            ImagePlus imp = opener.openImage(firstDicom.getAbsolutePath());
            if (imp != null) {
                String info = (String) imp.getProperty("Info");
                if (info != null) {
                    String id = extractVal(info, "0010,0020");
                    String name = extractVal(info, "0010,0010");
                    String mod = extractVal(info, "0008,0060");
                    String date = extractVal(info, "0008,0022"); 
                    if (date.isEmpty()) date = extractVal(info, "0008,0020"); 
                    
                    if (!mod.isEmpty()) {
                        name = name.replace("^", " ").trim();
                        String path = (mod.equals("NM")) ? firstDicom.getAbsolutePath() : dir.getAbsolutePath();
                        records.add(new SeriesRecord(id, name, mod, date, path));
                    }
                }
            }
        }
    }

    private String extractVal(String info, String tag) {
        String[] lines = info.split("\n");
        for (String line : lines) {
            if (line.contains(tag)) {
                int idx = line.lastIndexOf(":");
                if (idx != -1 && idx < line.length() - 1) return line.substring(idx + 1).trim();
            }
        }
        return "";
    }
} 