package io.github.gamezconz.ui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.FlatDarkLaf;
import io.github.gamezconz.core.AppConfig;
import io.github.gamezconz.core.DicomLoader;
import io.github.gamezconz.core.ViewPlane;
import ij.ImagePlus;
import ij.process.LUT;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class MainFrame extends JFrame {

    private final ResourceBundle bundle;
    private JPanel centerContainer;
    private final DicomLoader dicomLoader;

    private ImagePlus ctVolume;
    private ImagePlus spectVolume;

    // Arreglo de porcentajes para el SPECT (100%, 75%, 50%, 33%, 20%, 10%)
    private final double[] SPECT_PRESETS = {1.0, 0.75, 0.5, 0.33, 0.2, 0.1};
    private int currentSpectPresetIndex = 0;

    private Map<String, SlicePanel> viewPanels;
    
    private LUT spectLut;      
    private LUT invGrayLut;    

    private JToggleButton btnCrosshair;
    private JToggleButton btnWindowLevel;
    
    private SlicePanel.CrosshairStyle currentCrosshairStyle = SlicePanel.CrosshairStyle.LARGE_GAP;
    private boolean is3x3 = false; 

    private String CT_MOD;
    private String NM_MOD;
    private String FUS_MOD;

    public MainFrame() {
        Locale currentLocale = Locale.of(AppConfig.getLanguage());
        this.bundle = ResourceBundle.getBundle("messages", currentLocale);
        
        this.CT_MOD = bundle.getString("modality.ct");
        this.NM_MOD = bundle.getString("modality.spect");
        this.FUS_MOD = bundle.getString("modality.fusion");

        this.dicomLoader = new DicomLoader();
        this.viewPanels = new HashMap<>();

        byte[] rJet = new byte[256], gJet = new byte[256], bJet = new byte[256];
        for (int i = 0; i < 256; i++) {
            double x = i / 255.0;
            rJet[i] = (byte) (Math.max(0.0, Math.min(1.0, 1.5 - Math.abs(4.0 * x - 3.0))) * 255);
            gJet[i] = (byte) (Math.max(0.0, Math.min(1.0, 1.5 - Math.abs(4.0 * x - 2.0))) * 255);
            bJet[i] = (byte) (Math.max(0.0, Math.min(1.0, 1.5 - Math.abs(4.0 * x - 1.0))) * 255);
        }
        rJet[0] = 0; gJet[0] = 0; bJet[0] = 0; 
        this.spectLut = new LUT(8, 256, rJet, gJet, bJet);

        byte[] inv = new byte[256];
        for (int i = 0; i < 256; i++) inv[i] = (byte) (255 - i); 
        this.invGrayLut = new LUT(8, 256, inv, inv, inv);

        setTitle("OpenFusion View - v0.0.5");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 900);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        setupToolBar();

        centerContainer = new JPanel(new BorderLayout());
        centerContainer.setBackground(Color.BLACK);
        add(centerContainer, BorderLayout.CENTER);
        
        setupKeyBindings();

        buildGrid(false); 
    }

    private void setupToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false); 
        toolBar.setMargin(new Insets(5, 10, 5, 10));

        btnCrosshair = new JToggleButton(new FlatSVGIcon("icons/crosshair.svg", 24, 24), true); 
        btnWindowLevel = new JToggleButton(new FlatSVGIcon("icons/wl.svg", 24, 24), false);
        
        JButton btnLayout2x3 = new JButton(new FlatSVGIcon("icons/layout2x3.svg", 24, 24));
        JButton btnLayout3x3 = new JButton(new FlatSVGIcon("icons/layout3x3.svg", 24, 24));
        JButton btnExport = new JButton(new FlatSVGIcon("icons/export.svg", 24, 24));
        JButton btnBrowser = new JButton(new FlatSVGIcon("icons/browser.svg", 24, 24));
        JButton btnConfig = new JButton(new FlatSVGIcon("icons/settings.svg", 24, 24));
        JButton btnRotateNM = new JButton(new FlatSVGIcon("icons/sync.svg", 24, 24));
        JButton btnCycleNM = new JButton(new FlatSVGIcon("icons/speed.svg", 24, 24)); // Asegúrate de tener este ícono o usa otro existente
        btnCycleNM.setToolTipText("Ciclar Intensidad SPECT (S)");
        btnCycleNM.addActionListener(e -> cycleSpectPresets());
        btnRotateNM.setToolTipText(bundle.getString("toolbar.rotate_nm"));

        btnRotateNM.addActionListener(e -> rotateSpectVolume());

        JButton btnTiltNM = new JButton(new FlatSVGIcon("icons/tilt.svg", 24, 24));
        btnTiltNM.setToolTipText("Rotar 3D Eje Z (T)");
        btnTiltNM.addActionListener(e -> tiltSpectVolume());
        JButton btnFlipX = new JButton(new FlatSVGIcon("icons/arrows_output.svg", 24, 24));

        JButton btnPreset1 = new JButton(new FlatSVGIcon("icons/bone.svg", 24, 24));
        JButton btnPreset2 = new JButton(new FlatSVGIcon("icons/soft_tissue.svg", 24, 24));
        JButton btnPreset3 = new JButton(new FlatSVGIcon("icons/lung.svg", 24, 24));
        JButton btnPreset4 = new JButton(new FlatSVGIcon("icons/brain.svg", 24, 24));


        JButton btnAbout = new JButton(bundle.getString("toolbar.about")); 

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(btnCrosshair);
        modeGroup.add(btnWindowLevel);


        btnBrowser.setToolTipText(bundle.getString("toolbar.browser"));
        btnConfig.setToolTipText(bundle.getString("toolbar.settings"));
        btnCrosshair.setToolTipText(bundle.getString("toolbar.crosshair"));
        btnWindowLevel.setToolTipText(bundle.getString("toolbar.windowlevel"));
        btnLayout2x3.setToolTipText(bundle.getString("toolbar.view2x3"));
        btnLayout3x3.setToolTipText(bundle.getString("toolbar.view3x3"));
        btnExport.setToolTipText(bundle.getString("toolbar.export"));
        
        btnPreset1.setToolTipText(bundle.getString("preset.bone"));
        btnPreset2.setToolTipText(bundle.getString("preset.soft_tissue"));
        btnPreset3.setToolTipText(bundle.getString("preset.lung"));
        btnPreset4.setToolTipText(bundle.getString("preset.brain"));


        btnLayout2x3.addActionListener(e -> buildGrid(false));
        btnLayout3x3.addActionListener(e -> buildGrid(true));
        btnExport.addActionListener(e -> exportToJPEG());
        
        btnPreset1.addActionListener(e -> applyCTWindowLevel(-500, 1500));  // W:2000 L:500
        btnPreset2.addActionListener(e -> applyCTWindowLevel(-150, 250));   // W:400 L:50
        btnPreset3.addActionListener(e -> applyCTWindowLevel(-1350, 150));  // W:1500 L:-600
        btnPreset4.addActionListener(e -> applyCTWindowLevel(0, 80));       // W:80 L:40

        btnBrowser.addActionListener(e -> {
            BrowserDialog browser = new BrowserDialog(this);
            browser.setVisible(true);
        });

        btnConfig.addActionListener(e -> {
            SettingsDialog settings = new SettingsDialog(this);
            settings.setVisible(true);
        });

        btnAbout.addActionListener(e -> {
            String aboutHtml = "<html><body style='font-family: Arial; text-align: center; color: white; width: 350px;'>"
                             + "<h2>OpenFusion View - v0.0.3</h2>"
                             + "<p>An open-source medical imaging viewer for CT and SPECT fusion.</p>"
                             + "<br>"
                             + "<p>Created by: <b>Carlos Gamez</b></p>"
                             + "<p>GitHub: <a href='https://github.com/GamezConZ' style='color: #4da6ff;'>github.com/GamezConZ</a></p>"
                             + "<br>"
                             + "<div style='font-size: 9px; text-align: left; background-color: #2b2b2b; padding: 10px; border-radius: 5px; color: #b3b3b3;'>"
                             + "<b>MIT License</b><br><br>"
                             + "Copyright (c) 2026 Carlos Gamez<br><br>"
                             + "Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the \"Software\"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:<br><br>"
                             + "The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.<br><br>"
                             + "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT."
                             + "</div>"
                             + "<br><br>Icons provided by Google (Material Symbols) under Apache License 2.0.<br><br>"
                             + "</body></html>";
                             
            JEditorPane ep = new JEditorPane("text/html", aboutHtml);
            ep.setEditable(false);
            ep.setOpaque(false);
            ep.addHyperlinkListener(hle -> {
                if (javax.swing.event.HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
                    try {
                        Desktop.getDesktop().browse(new URI(hle.getURL().toString()));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            JOptionPane.showMessageDialog(this, ep, bundle.getString("toolbar.about"), JOptionPane.INFORMATION_MESSAGE);
        });

        btnCrosshair.addActionListener(e -> updateInteractionModes());
        btnWindowLevel.addActionListener(e -> updateInteractionModes());

        btnFlipX.setToolTipText("Espejar X (H)");
        btnFlipX.addActionListener(e -> flipSpectX());

        toolBar.add(btnBrowser);
        toolBar.add(Box.createHorizontalStrut(5));
        toolBar.add(btnConfig);
        toolBar.addSeparator(new Dimension(20, 0));
        
        toolBar.add(btnCrosshair);
        toolBar.add(Box.createHorizontalStrut(5));
        toolBar.add(btnWindowLevel);
        toolBar.addSeparator(new Dimension(20, 0));
        
        toolBar.add(btnPreset1);
        toolBar.add(Box.createHorizontalStrut(5));
        toolBar.add(btnPreset2);
        toolBar.add(Box.createHorizontalStrut(5));
        toolBar.add(btnPreset3);
        toolBar.add(Box.createHorizontalStrut(5));
        toolBar.add(btnPreset4);
        toolBar.addSeparator(new Dimension(20, 0));
        toolBar.add(btnPreset4);
        toolBar.addSeparator(new Dimension(20, 0));
        toolBar.add(btnCycleNM); // <--- Tu nuevo botón aquí
        toolBar.add(btnLayout2x3);
        toolBar.add(Box.createHorizontalStrut(5));
        toolBar.add(btnLayout3x3);
        toolBar.addSeparator(new Dimension(20, 0));
        
        toolBar.add(btnExport);
        
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(btnAbout);

        toolBar.add(btnRotateNM);
        toolBar.add(Box.createHorizontalStrut(5));

        toolBar.add(btnTiltNM);
        toolBar.add(Box.createHorizontalStrut(5));

        toolBar.add(btnFlipX);
        toolBar.add(Box.createHorizontalStrut(5));

        add(toolBar, BorderLayout.NORTH);
    }

    private void setupKeyBindings() {
        InputMap im = centerContainer.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = centerContainer.getActionMap();

        im.put(KeyStroke.getKeyStroke("Z"), "modeCrosshair");
        am.put("modeCrosshair", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnCrosshair.setSelected(true);
                updateInteractionModes();
            }
        });

        im.put(KeyStroke.getKeyStroke("T"), "tiltNM");
        am.put("tiltNM", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tiltSpectVolume();
            }
        });

        im.put(KeyStroke.getKeyStroke("S"), "cycleSpect");
        am.put("cycleSpect", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cycleSpectPresets();
            }
        });

        im.put(KeyStroke.getKeyStroke("R"), "rotateNM");
        am.put("rotateNM", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rotateSpectVolume();
            }
        });

        im.put(KeyStroke.getKeyStroke("H"), "flipX");
        am.put("flipX", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                flipSpectX();
            }
        });

        im.put(KeyStroke.getKeyStroke("W"), "modeWL");
        am.put("modeWL", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnWindowLevel.setSelected(true);
                updateInteractionModes();
            }
        });

        im.put(KeyStroke.getKeyStroke("C"), "cycleCrosshair");
        am.put("cycleCrosshair", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (currentCrosshairStyle) {
                    case FULL: currentCrosshairStyle = SlicePanel.CrosshairStyle.SMALL_GAP; break;
                    case SMALL_GAP: currentCrosshairStyle = SlicePanel.CrosshairStyle.LARGE_GAP; break;
                    case LARGE_GAP: currentCrosshairStyle = SlicePanel.CrosshairStyle.LONG_TICKS; break;
                    case LONG_TICKS: currentCrosshairStyle = SlicePanel.CrosshairStyle.SHORT_TICKS; break;
                    case SHORT_TICKS: currentCrosshairStyle = SlicePanel.CrosshairStyle.FULL; break;
                }
                for (SlicePanel panel : viewPanels.values()) {
                    panel.setCrosshairStyle(currentCrosshairStyle);
                }
            }
        });

        bindPreset(im, am, "1", 2000, 500);  
        bindPreset(im, am, "2", 400, 50);    
        bindPreset(im, am, "3", 1500, -600); 
        bindPreset(im, am, "4", 80, 40);     
    }

    private void bindPreset(InputMap im, ActionMap am, String key, double width, double level) {
        im.put(KeyStroke.getKeyStroke(key), "preset" + key);
        am.put("preset" + key, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double min = level - (width / 2.0);
                double max = level + (width / 2.0);
                applyCTWindowLevel(min, max);
            }
        });
    }

    private void applyCTWindowLevel(double min, double max) {
        for (Map.Entry<String, SlicePanel> entry : viewPanels.entrySet()) {
            if (entry.getKey().startsWith(CT_MOD) || entry.getKey().startsWith(FUS_MOD)) {
                entry.getValue().setWindowLevel(min, max);
            }
        }
    }

    private void updateInteractionModes() {
        SlicePanel.InteractionMode mode = btnCrosshair.isSelected() ? 
            SlicePanel.InteractionMode.CROSSHAIR : SlicePanel.InteractionMode.WINDOW_LEVEL;
        for (SlicePanel panel : viewPanels.values()) {
            panel.setInteractionMode(mode);
        }
    }

    public void loadFromBrowser(String ctPath, String nmPath) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                if (ctPath != null) ctVolume = dicomLoader.loadVolume(ctPath);
                if (nmPath != null) spectVolume = dicomLoader.loadVolume(nmPath);
                return null;
            }
            @Override
            protected void done() {
                buildGrid(is3x3);
                JOptionPane.showMessageDialog(MainFrame.this, bundle.getString("msg.studies_loaded"));
            }
        }.execute();
    }

    private void exportToJPEG() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(bundle.getString("msg.export_title"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("JPEG Images", "jpg", "jpeg"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".jpg") && !file.getName().toLowerCase().endsWith(".jpeg")) {
                file = new File(file.getAbsolutePath() + ".jpg");
            }

            for (SlicePanel panel : viewPanels.values()) panel.setExporting(true);
            centerContainer.paintImmediately(0, 0, centerContainer.getWidth(), centerContainer.getHeight());

            BufferedImage image = new BufferedImage(centerContainer.getWidth(), centerContainer.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            centerContainer.paint(g2d);
            g2d.dispose();

            for (SlicePanel panel : viewPanels.values()) panel.setExporting(false);

            try {
                ImageIO.write(image, "jpg", file);
                JOptionPane.showMessageDialog(this, bundle.getString("msg.export_success"), "Export", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, bundle.getString("msg.export_error"), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void dispatchVolumeToPanels() {
        String[] planes = { bundle.getString("plane.axial"), bundle.getString("plane.coronal"), bundle.getString("plane.sagittal") };

        for (String planeStr : planes) {
            String ctKey = CT_MOD + " " + planeStr;
            String spectKey = NM_MOD + " " + planeStr;
            String fusionKey = FUS_MOD + " " + planeStr;

            if (ctVolume != null && viewPanels.containsKey(ctKey)) viewPanels.get(ctKey).setVolume(ctVolume);
            
            if (spectVolume != null && viewPanels.containsKey(spectKey)) {
                viewPanels.get(spectKey).setVolume(spectVolume);
                viewPanels.get(spectKey).setLut(invGrayLut);
            }

            if (viewPanels.containsKey(fusionKey)) {
                if (ctVolume != null) viewPanels.get(fusionKey).setVolume(ctVolume);
                if (spectVolume != null) {
                    viewPanels.get(fusionKey).setOverlayVolume(spectVolume);
                    viewPanels.get(fusionKey).setLut(spectLut);
                }
            }
        }
    }

    private void buildGrid(boolean includeSpectRow) {
        double savedNormX = 0.5, savedNormY = 0.5, savedNormZ = 0.5;
        double savedCtMin = Double.NaN, savedCtMax = Double.NaN;
        double savedSpectMin = Double.NaN, savedSpectMax = Double.NaN;

        if (!viewPanels.isEmpty()) {
            SlicePanel first = viewPanels.values().iterator().next();
            savedNormX = first.getNormX(); 
            savedNormY = first.getNormY(); 
            savedNormZ = first.getNormZ();

            String ctAxial = CT_MOD + " " + bundle.getString("plane.axial");
            String spectAxial = NM_MOD + " " + bundle.getString("plane.axial");
            String fusionAxial = FUS_MOD + " " + bundle.getString("plane.axial");

            if (viewPanels.containsKey(ctAxial)) {
                savedCtMin = viewPanels.get(ctAxial).getDisplayMin();
                savedCtMax = viewPanels.get(ctAxial).getDisplayMax();
            }

            if (this.is3x3 && viewPanels.containsKey(spectAxial)) {
                savedSpectMin = viewPanels.get(spectAxial).getDisplayMin();
                savedSpectMax = viewPanels.get(spectAxial).getDisplayMax();
            } else if (!this.is3x3 && viewPanels.containsKey(fusionAxial)) {
                savedSpectMin = viewPanels.get(fusionAxial).getOverlayMin();
                savedSpectMax = viewPanels.get(fusionAxial).getOverlayMax();
            }
        }

        this.is3x3 = includeSpectRow; 
        centerContainer.removeAll(); 

        int rows = is3x3 ? 3 : 2;
        JPanel gridPanel = new JPanel(new GridLayout(rows, 3, 5, 5));
        gridPanel.setBackground(Color.BLACK);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        String[] modalities = is3x3 ? new String[]{ CT_MOD, NM_MOD, FUS_MOD } : new String[]{ CT_MOD, FUS_MOD };
        String[] planes = { bundle.getString("plane.axial"), bundle.getString("plane.coronal"), bundle.getString("plane.sagittal") };

        SlicePanel.InteractionMode currentMode = btnCrosshair.isSelected() ? 
            SlicePanel.InteractionMode.CROSSHAIR : SlicePanel.InteractionMode.WINDOW_LEVEL;

        for (String mod : modalities) {
            for (String planeStr : planes) {
                
                ViewPlane vp = ViewPlane.AXIAL;
                if (planeStr.equals(bundle.getString("plane.coronal"))) vp = ViewPlane.CORONAL;
                else if (planeStr.equals(bundle.getString("plane.sagittal"))) vp = ViewPlane.SAGITTAL;
                
                SlicePanel panel = new SlicePanel(mod, vp);
                panel.setInteractionMode(currentMode); 
                panel.setCrosshairStyle(currentCrosshairStyle);

                if (mod.equals(NM_MOD) && is3x3) {
                    panel.setCrosshairColor(new Color(0, 0, 0, 180)); 
                } else {
                    panel.setCrosshairColor(new Color(255, 255, 255, 180)); 
                }

                if (mod.equals(FUS_MOD) && !is3x3) {
                    panel.setWlTarget(SlicePanel.WLTarget.OVERLAY);
                } else {
                    panel.setWlTarget(SlicePanel.WLTarget.BASE);
                }

                viewPanels.put(mod + " " + planeStr, panel); 
                
                panel.setCrosshairListener((nx, ny, nz) -> {
                    for (SlicePanel otherPanel : viewPanels.values()) {
                        if (otherPanel != panel) otherPanel.setNormalizedCrosshair(nx, ny, nz);
                    }
                });

                panel.setWindowLevelListener((min, max) -> {
                    for (String p : planes) {
                        if (mod.equals(CT_MOD)) {
                            String targetCt = CT_MOD + " " + p;
                            String targetFusion = FUS_MOD + " " + p;
                            if (viewPanels.containsKey(targetCt) && viewPanels.get(targetCt) != panel) viewPanels.get(targetCt).setWindowLevel(min, max);
                            if (viewPanels.containsKey(targetFusion)) viewPanels.get(targetFusion).setWindowLevel(min, max);
                        } else if (mod.equals(NM_MOD)) {
                            String targetSpect = NM_MOD + " " + p;
                            String targetFusion = FUS_MOD + " " + p;
                            if (viewPanels.containsKey(targetSpect) && viewPanels.get(targetSpect) != panel) viewPanels.get(targetSpect).setWindowLevel(min, max);
                            if (viewPanels.containsKey(targetFusion)) viewPanels.get(targetFusion).setOverlayWindowLevel(min, max);
                        } else if (mod.equals(FUS_MOD)) {
                            if (!is3x3) {
                                String targetFusion = FUS_MOD + " " + p;
                                if (viewPanels.containsKey(targetFusion) && viewPanels.get(targetFusion) != panel) viewPanels.get(targetFusion).setOverlayWindowLevel(min, max);
                            } else {
                                String targetCt = CT_MOD + " " + p;
                                String targetFusion = FUS_MOD + " " + p;
                                if (viewPanels.containsKey(targetCt)) viewPanels.get(targetCt).setWindowLevel(min, max);
                                if (viewPanels.containsKey(targetFusion) && viewPanels.get(targetFusion) != panel) viewPanels.get(targetFusion).setWindowLevel(min, max);
                            }
                        }
                    }
                });
                gridPanel.add(panel);
            }
        }

        dispatchVolumeToPanels();

        for (SlicePanel panel : viewPanels.values()) {
            panel.setNormalizedCrosshair(savedNormX, savedNormY, savedNormZ);
        }

        if (!Double.isNaN(savedCtMin)) {
            for (String p : planes) {
                if (viewPanels.containsKey(CT_MOD + " " + p)) viewPanels.get(CT_MOD + " " + p).setWindowLevel(savedCtMin, savedCtMax);
                if (viewPanels.containsKey(FUS_MOD + " " + p)) viewPanels.get(FUS_MOD + " " + p).setWindowLevel(savedCtMin, savedCtMax);
            }
        }

        if (!Double.isNaN(savedSpectMin)) {
            for (String p : planes) {
                if (viewPanels.containsKey(NM_MOD + " " + p)) viewPanels.get(NM_MOD + " " + p).setWindowLevel(savedSpectMin, savedSpectMax);
                if (viewPanels.containsKey(FUS_MOD + " " + p)) viewPanels.get(FUS_MOD + " " + p).setOverlayWindowLevel(savedSpectMin, savedSpectMax);
            }
        }

        centerContainer.add(gridPanel, BorderLayout.CENTER);
        centerContainer.revalidate();
        centerContainer.repaint();
    }

    private void rotateSpectVolume() {
        if (spectVolume == null) return;
        ij.ImageStack stack = spectVolume.getStack();
        ij.ImageStack rotatedStack = new ij.ImageStack(stack.getHeight(), stack.getWidth());
        for (int i = 1; i <= stack.getSize(); i++) {
                ij.process.ImageProcessor ip = stack.getProcessor(i);
                rotatedStack.addSlice(stack.getSliceLabel(i), ip.rotateLeft());
            }

        spectVolume.setStack(rotatedStack);
        
 
        buildGrid(is3x3); 
        System.out.println(">> Rotación manual aplicada al volumen SPECT.");
    }

    private void tiltSpectVolume() {
        if (spectVolume == null) return;

        ij.ImageStack stack = spectVolume.getStack();
        int w = stack.getWidth();
        int h = stack.getHeight();
        int d = stack.getSize();

        ij.ImageStack reslicedStack = new ij.ImageStack(w, d);

        for (int y = 0; y < h; y++) {
            ij.process.FloatProcessor newSlice = new ij.process.FloatProcessor(w, d);
            for (int z = 1; z <= d; z++) {
                ij.process.ImageProcessor oldSlice = stack.getProcessor(z);
                for (int x = 0; x < w; x++) {
                    newSlice.putPixelValue(x, z - 1, oldSlice.getPixelValue(x, y));
                }
            }
            reslicedStack.addSlice("", newSlice);
        }

        spectVolume.setStack(reslicedStack);
        buildGrid(is3x3);
        System.out.println(">> Rotación 3D (Reslice Y-Z) aplicada al volumen SPECT.");
    }

    private void flipSpectX() {
        if (spectVolume == null) return;
        ij.ImageStack stack = spectVolume.getStack();
        for (int i = 1; i <= stack.getSize(); i++) {
            stack.getProcessor(i).flipHorizontal();
        }
        buildGrid(is3x3);
        System.out.println(">> Espejo X aplicado al volumen SPECT.");
    }

    private void cycleSpectPresets() {
        if (spectVolume == null) return;

        // 1. Obtener el máximo de cuentas real del estudio
        double maxCounts = spectVolume.getStatistics().max;
        
        // 2. Calcular el porcentaje correspondiente
        double percent = SPECT_PRESETS[currentSpectPresetIndex];
        double newMax = maxCounts * percent;
        
        // 3. Preparar el índice para el próximo clic
        currentSpectPresetIndex = (currentSpectPresetIndex + 1) % SPECT_PRESETS.length;

        // 4. Aplicar el nuevo rango a los paneles correspondientes
        for (SlicePanel panel : viewPanels.values()) {
            String title = panel.getModalityTitle(); 
            
            if (title != null) {
                if (title.equals(FUS_MOD)) {
                    // En Fusión, solo alteramos la capa superpuesta (Overlay)
                    panel.setOverlayWindowLevel(0, newMax);
                } else if (title.equals(NM_MOD)) {
                    // En SPECT puro, alteramos la imagen base
                    panel.setWindowLevel(0, newMax);
                }
            }
        }

        System.out.println(">> Preset SPECT: " + (int)(percent * 100) + "% (Max Counts: " + (int)newMax + ")");
    }
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
} 