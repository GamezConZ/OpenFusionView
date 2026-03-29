package com.openfusionview.ui;

import com.openfusionview.core.AppConfig;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class SettingsDialog extends JDialog {
    private JTextField txtPath;
    private JComboBox<String> cbLanguage;
    private Map<String, String> langMap;
    private final ResourceBundle bundle;

    public SettingsDialog(JFrame parent) {
        super(parent, true); 
        
        // Cargar idioma actual
        Locale currentLocale = Locale.of(AppConfig.getLanguage());
        this.bundle = ResourceBundle.getBundle("messages", currentLocale);
        
        setTitle(bundle.getString("settings.title"));
        setSize(550, 200);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridLayout(2, 2, 10, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Ruta
        mainPanel.add(new JLabel(bundle.getString("settings.root_folder")));
        JPanel pathPanel = new JPanel(new BorderLayout(5, 0));
        txtPath = new JTextField(AppConfig.getDicomRoot());
        JButton btnBrowse = new JButton(bundle.getString("settings.browse"));
        btnBrowse.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(txtPath.getText());
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                txtPath.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });
        pathPanel.add(txtPath, BorderLayout.CENTER);
        pathPanel.add(btnBrowse, BorderLayout.EAST);
        mainPanel.add(pathPanel);

        // Idioma
        mainPanel.add(new JLabel(bundle.getString("settings.language")));
        langMap = new LinkedHashMap<>();
        langMap.put("English", "en");
        langMap.put("Español", "es");
        langMap.put("Português", "pt");
        langMap.put("Français", "fr");
        langMap.put("中文 (Chinese)", "zh");
        langMap.put("日本語 (Japanese)", "ja");

        cbLanguage = new JComboBox<>(langMap.keySet().toArray(new String[0]));
        
        String currentLang = AppConfig.getLanguage();
        for (Map.Entry<String, String> entry : langMap.entrySet()) {
            if (entry.getValue().equals(currentLang)) {
                cbLanguage.setSelectedItem(entry.getKey());
                break;
            }
        }
        mainPanel.add(cbLanguage);

        add(mainPanel, BorderLayout.CENTER);

        // Botones
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton(bundle.getString("settings.save"));
        JButton btnCancel = new JButton(bundle.getString("settings.cancel"));

        btnSave.addActionListener(e -> {
            AppConfig.setDicomRoot(txtPath.getText());
            
            String selectedLang = langMap.get((String) cbLanguage.getSelectedItem());
            boolean langChanged = !selectedLang.equals(AppConfig.getLanguage());
            AppConfig.setLanguage(selectedLang);
            
            if (langChanged) {
                JOptionPane.showMessageDialog(this, 
                    bundle.getString("settings.msg.restart"), 
                    bundle.getString("settings.msg.restart_title"), 
                    JOptionPane.INFORMATION_MESSAGE);
            }
            dispose();
        });
        
        btnCancel.addActionListener(e -> dispose());

        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        add(btnPanel, BorderLayout.SOUTH);
    }
}