package com.openfusionview.ui;

import com.openfusionview.core.ViewPlane;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.LUT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class SlicePanel extends JPanel {

    public enum InteractionMode { CROSSHAIR, WINDOW_LEVEL }
    public enum CrosshairStyle { FULL, SMALL_GAP, LARGE_GAP, LONG_TICKS, SHORT_TICKS }
    public enum WLTarget { BASE, OVERLAY }

    public interface CrosshairListener { void onCrosshairMoved(double normX, double normY, double normZ); }
    public interface WindowLevelListener { void onWindowLevelChanged(double min, double max); }

    private InteractionMode mode = InteractionMode.CROSSHAIR; 
    private CrosshairStyle crosshairStyle = CrosshairStyle.LARGE_GAP; 
    private WLTarget wlTarget = WLTarget.BASE;
    private boolean isExporting = false; 

    private ImagePlus volume;
    private ImagePlus overlayVolume; 
    
    private int currentSlice = 1; 
    private int maxSlices = 1;
    private String modalityTitle; // CT, NM o CT/NM
    private ViewPlane plane;

    // Metadatos Clínicos
    private String patientId = "ID: -";
    private String acqDate = "Date: -";

    private double displayMin;
    private double displayMax;
    private double overlayMin = Double.NaN;
    private double overlayMax = Double.NaN;
    
    private int lastMouseX, lastMouseY;
    private int lastDrawX, lastDrawY, lastDrawWidth, lastDrawHeight;
    private double normX = 0.5, normY = 0.5, normZ = 0.5;
    
    private LUT customLut = null; 
    private float alpha = 0.5f; 
    private Color crosshairColor = new Color(255, 255, 255, 180); 
    
    private CrosshairListener crosshairListener; 
    private WindowLevelListener wlListener; 

    public SlicePanel(String modalityTitle, ViewPlane plane) {
        this.modalityTitle = modalityTitle;
        this.plane = plane;
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY)); 

        this.addMouseWheelListener(e -> {
            if (volume != null) {
                int rotation = e.getWheelRotation(); 
                if (rotation < 0) currentSlice--; 
                else currentSlice++; 

                if (currentSlice < 1) currentSlice = 1;
                else if (currentSlice > maxSlices) currentSlice = maxSlices;

                double newDepth = (maxSlices > 1) ? (double)(currentSlice - 1) / (maxSlices - 1) : 0.5;
                if (plane == ViewPlane.AXIAL) normZ = newDepth;
                else if (plane == ViewPlane.CORONAL) normY = newDepth;
                else if (plane == ViewPlane.SAGITTAL) normX = newDepth;

                repaint();
                if (crosshairListener != null) crosshairListener.onCrosshairMoved(normX, normY, normZ);
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMouseX = e.getX(); lastMouseY = e.getY();
                if (mode == InteractionMode.CROSSHAIR && volume != null) handleCrosshairUpdate(e.getX(), e.getY());
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (volume == null) return;
                if (wlTarget == WLTarget.OVERLAY && overlayVolume == null) return;

                if (mode == InteractionMode.WINDOW_LEVEL) {
                    int dx = e.getX() - lastMouseX;
                    int dy = e.getY() - lastMouseY;
                    lastMouseX = e.getX(); lastMouseY = e.getY();

                    double targetMin = (wlTarget == WLTarget.BASE) ? displayMin : overlayMin;
                    double targetMax = (wlTarget == WLTarget.BASE) ? displayMax : overlayMax;

                    double windowWidth = targetMax - targetMin;
                    double windowLevel = (targetMax + targetMin) / 2.0;
                    double sensitivity = Math.max(1.0, windowWidth * 0.005);

                    windowWidth += (dx * sensitivity);
                    windowLevel -= (dy * sensitivity);
                    if (windowWidth < 1) windowWidth = 1;

                    targetMin = windowLevel - (windowWidth / 2.0);
                    targetMax = windowLevel + (windowWidth / 2.0);

                    if (wlTarget == WLTarget.BASE) { displayMin = targetMin; displayMax = targetMax; } 
                    else { overlayMin = targetMin; overlayMax = targetMax; }

                    repaint(); 
                    if (wlListener != null) wlListener.onWindowLevelChanged(targetMin, targetMax);
                } else if (mode == InteractionMode.CROSSHAIR) {
                    handleCrosshairUpdate(e.getX(), e.getY());
                }
            }
        });
    }

    private void handleCrosshairUpdate(int mouseX, int mouseY) {
        if (lastDrawWidth <= 0 || lastDrawHeight <= 0) return;

        double cx = (double) (mouseX - lastDrawX) / lastDrawWidth;
        double cy = (double) (mouseY - lastDrawY) / lastDrawHeight;
        cx = Math.max(0.0, Math.min(1.0, cx)); cy = Math.max(0.0, Math.min(1.0, cy));

        if (plane == ViewPlane.AXIAL) { normX = cx; normY = cy; } 
        else if (plane == ViewPlane.CORONAL) { normX = cx; normZ = cy; } 
        else if (plane == ViewPlane.SAGITTAL) { normY = cx; normZ = cy; }

        setNormalizedCrosshair(normX, normY, normZ);
        if (crosshairListener != null) crosshairListener.onCrosshairMoved(normX, normY, normZ);
    }

    // --- NUEVO: Extraer Metadatos DICOM ---
    private void parseMetadata(ImagePlus img) {
        if (img == null) return;
        String info = (String) img.getProperty("Info");
        if (info != null) {
            String[] lines = info.split("\n");
            for (String line : lines) {
                if (line.contains("0010,0020")) { // Patient ID
                    patientId = "ID: " + extractDicomValue(line);
                } else if (line.contains("0008,0022") || line.contains("0008,0023")) { // Acquisition/Content Date
                    String rawDate = extractDicomValue(line);
                    // Formato habitual DICOM es YYYYMMDD
                    if (rawDate.length() == 8) {
                        acqDate = "Date: " + rawDate.substring(0,4) + "-" + rawDate.substring(4,6) + "-" + rawDate.substring(6,8);
                    } else {
                        acqDate = "Date: " + rawDate;
                    }
                }
            }
        }
    }

    private String extractDicomValue(String line) {
        int idx = line.lastIndexOf(":");
        if (idx != -1 && idx < line.length() - 1) {
            return line.substring(idx + 1).trim();
        }
        return "";
    }


    private double getPixelValueAtCrosshair(ImagePlus vol) {
        if (vol == null) return 0.0;
        int x = (int) Math.round(normX * (vol.getWidth() - 1));
        int y = (int) Math.round(normY * (vol.getHeight() - 1));
        int z = (int) Math.round(normZ * (vol.getStackSize() - 1)) + 1;
        
        x = Math.max(0, Math.min(x, vol.getWidth() - 1));
        y = Math.max(0, Math.min(y, vol.getHeight() - 1));
        z = Math.max(1, Math.min(z, vol.getStackSize()));
        
        return vol.getStack().getProcessor(z).getPixelValue(x, y);
    }

    public double getNormX() { return normX; }
    public double getNormY() { return normY; }
    public double getNormZ() { return normZ; }
    public double getDisplayMin() { return displayMin; }
    public double getDisplayMax() { return displayMax; }
    public double getOverlayMin() { return overlayMin; }
    public double getOverlayMax() { return overlayMax; }
    public ViewPlane getPlane() { return this.plane; }

    public void setCrosshairColor(Color c) { this.crosshairColor = c; repaint(); }
    public void setInteractionMode(InteractionMode mode) { this.mode = mode; repaint(); }
    public void setCrosshairStyle(CrosshairStyle style) { this.crosshairStyle = style; repaint(); }
    public void setWlTarget(WLTarget target) { this.wlTarget = target; }
    public void setExporting(boolean exporting) { this.isExporting = exporting; repaint(); }
    public void setCrosshairListener(CrosshairListener listener) { this.crosshairListener = listener; }
    public void setWindowLevelListener(WindowLevelListener listener) { this.wlListener = listener; }

    public void setNormalizedCrosshair(double nx, double ny, double nz) {
        this.normX = nx; this.normY = ny; this.normZ = nz;
        if (maxSlices > 1) {
            double depth = 0;
            if (plane == ViewPlane.AXIAL) depth = nz;
            else if (plane == ViewPlane.CORONAL) depth = ny;
            else if (plane == ViewPlane.SAGITTAL) depth = nx;
            
            this.currentSlice = (int) Math.round(depth * (maxSlices - 1)) + 1;
            if (this.currentSlice < 1) this.currentSlice = 1;
            if (this.currentSlice > maxSlices) this.currentSlice = maxSlices;
        }
        repaint();
    }

    public void setWindowLevel(double min, double max) { this.displayMin = min; this.displayMax = max; repaint(); }
    public void setOverlayWindowLevel(double min, double max) { this.overlayMin = min; this.overlayMax = max; repaint(); }

    public void setVolume(ImagePlus volume) {
        this.volume = volume;
        if (volume != null) {
            parseMetadata(volume); // Extraer datos clínicos
            maxSlices = getMaxSlices(volume, plane);
            setNormalizedCrosshair(0.5, 0.5, 0.5);

            this.displayMin = volume.getDisplayRangeMin();
            this.displayMax = volume.getDisplayRangeMax();
            if (this.displayMin == this.displayMax) { this.displayMin = -1000.0; this.displayMax = 1000.0; }
        }
        repaint(); 
    }

    public void setOverlayVolume(ImagePlus overlayVolume) {
        this.overlayVolume = overlayVolume;
        if (overlayVolume != null) {
            this.overlayMin = overlayVolume.getDisplayRangeMin();
            this.overlayMax = overlayVolume.getDisplayRangeMax();
            if (this.overlayMin == this.overlayMax) { this.overlayMin = 0.0; this.overlayMax = 255.0; }
        }
        repaint();
    }

    public void setLut(LUT lut) { this.customLut = lut; repaint(); }

    private int getMaxSlices(ImagePlus img, ViewPlane vp) {
        if (vp == ViewPlane.AXIAL) return img.getStackSize();
        if (vp == ViewPlane.CORONAL) return img.getHeight();
        return img.getWidth(); 
    }

    private ImageProcessor extractSlice(ImagePlus img, int targetSlice) {
        int w = img.getWidth(), h = img.getHeight(), d = img.getStackSize();
        ImageStack stack = img.getStack();

        if (plane == ViewPlane.AXIAL) {
            FloatProcessor fp = new FloatProcessor(w, h);
            ImageProcessor ip = stack.getProcessor(targetSlice);
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    fp.putPixelValue(x, y, ip.getPixelValue(x, y));
                }
            }
            return fp;
            
        } else if (plane == ViewPlane.CORONAL) {
            FloatProcessor fp = new FloatProcessor(w, d);
            int y = targetSlice - 1; 
            for (int z = 1; z <= d; z++) {
                ImageProcessor ip = stack.getProcessor(z);
                for (int x = 0; x < w; x++) {
                    fp.putPixelValue(x, z - 1, ip.getPixelValue(x, y));
                }
            }
            return fp;
            
        } else { // SAGITTAL
            FloatProcessor fp = new FloatProcessor(h, d);
            int x = targetSlice - 1;
            for (int z = 1; z <= d; z++) {
                ImageProcessor ip = stack.getProcessor(z);
                for (int y = 0; y < h; y++) {
                    fp.putPixelValue(y, z - 1, ip.getPixelValue(x, y));
                }
            }
            return fp;
        }
    }

    private void drawShadowText(Graphics2D g2d, String text, int x, int y) {
        g2d.setColor(Color.BLACK);
        g2d.drawString(text, x + 1, y + 1);
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (volume == null) {
            if (!isExporting) {
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.setFont(new Font("Arial", Font.BOLD, 18));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(modalityTitle)) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(modalityTitle, x, y);
            }
            return;
        }

        ImageProcessor baseProcessor = extractSlice(volume, currentSlice);
        baseProcessor.setMinAndMax(displayMin, displayMax);
        if (customLut != null && overlayVolume == null) baseProcessor.setLut(customLut);
        Image baseImage = baseProcessor.createImage();

        double pWidth = 1.0, pHeight = 1.0, pDepth = 1.0;
        Calibration cal = volume.getCalibration();
        if (cal != null) {
            pWidth = cal.pixelWidth != 0 ? Math.abs(cal.pixelWidth) : 1.0;
            pHeight = cal.pixelHeight != 0 ? Math.abs(cal.pixelHeight) : 1.0;
            pDepth = cal.pixelDepth != 0 ? Math.abs(cal.pixelDepth) : 1.0;
        }

        double physicalWidth = 1.0, physicalHeight = 1.0;
        if (plane == ViewPlane.AXIAL) {
            physicalWidth = baseImage.getWidth(null) * pWidth; physicalHeight = baseImage.getHeight(null) * pHeight;
        } else if (plane == ViewPlane.CORONAL) {
            physicalWidth = baseImage.getWidth(null) * pWidth; physicalHeight = baseImage.getHeight(null) * pDepth;
        } else if (plane == ViewPlane.SAGITTAL) {
            physicalWidth = baseImage.getWidth(null) * pHeight; physicalHeight = baseImage.getHeight(null) * pDepth;
        }

        int panelWidth = getWidth(), panelHeight = getHeight();
        double scale = Math.min(panelWidth / physicalWidth, panelHeight / physicalHeight);

        lastDrawWidth = (int) (physicalWidth * scale);
        lastDrawHeight = (int) (physicalHeight * scale);
        lastDrawX = (panelWidth - lastDrawWidth) / 2;
        lastDrawY = (panelHeight - lastDrawHeight) / 2;

        g2d.drawImage(baseImage, lastDrawX, lastDrawY, lastDrawWidth, lastDrawHeight, null);

        if (overlayVolume != null) {
            int maxOverlaySlices = getMaxSlices(overlayVolume, plane);
            int overlaySlice = (int) Math.round((double) currentSlice / maxSlices * maxOverlaySlices);
            if (overlaySlice < 1) overlaySlice = 1;
            if (overlaySlice > maxOverlaySlices) overlaySlice = maxOverlaySlices;

            ImageProcessor overlayProcessor = extractSlice(overlayVolume, overlaySlice);
            if (!Double.isNaN(overlayMin) && !Double.isNaN(overlayMax)) overlayProcessor.setMinAndMax(overlayMin, overlayMax);
            else overlayProcessor.resetMinAndMax(); 
            
            if (customLut != null) overlayProcessor.setLut(customLut);
            
            Image overlayImage = overlayProcessor.createImage();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2d.drawImage(overlayImage, lastDrawX, lastDrawY, lastDrawWidth, lastDrawHeight, null);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }

        // --- Crosshair ---
        if (mode == InteractionMode.CROSSHAIR) {
            double cx = 0, cy = 0;
            if (plane == ViewPlane.AXIAL) { cx = normX; cy = normY; }
            else if (plane == ViewPlane.CORONAL) { cx = normX; cy = normZ; }
            else if (plane == ViewPlane.SAGITTAL) { cx = normY; cy = normZ; }

            int screenX = lastDrawX + (int)(cx * lastDrawWidth);
            int screenY = lastDrawY + (int)(cy * lastDrawHeight);

            g2d.setColor(crosshairColor); 
            g2d.setStroke(new BasicStroke(1.5f));

            if (screenX >= lastDrawX && screenX <= lastDrawX + lastDrawWidth &&
                screenY >= lastDrawY && screenY <= lastDrawY + lastDrawHeight) {
                
                if (crosshairStyle == CrosshairStyle.FULL) {
                    g2d.drawLine(lastDrawX, screenY, lastDrawX + lastDrawWidth, screenY); 
                    g2d.drawLine(screenX, lastDrawY, screenX, lastDrawY + lastDrawHeight); 
                } else if (crosshairStyle == CrosshairStyle.SMALL_GAP || crosshairStyle == CrosshairStyle.LARGE_GAP) {
                    int gap = (crosshairStyle == CrosshairStyle.SMALL_GAP) ? 15 : 45;
                    g2d.drawLine(lastDrawX, screenY, screenX - gap, screenY);
                    g2d.drawLine(screenX + gap, screenY, lastDrawX + lastDrawWidth, screenY);
                    g2d.drawLine(screenX, lastDrawY, screenX, screenY - gap);
                    g2d.drawLine(screenX, screenY + gap, screenX, lastDrawY + lastDrawHeight);
                } else if (crosshairStyle == CrosshairStyle.LONG_TICKS || crosshairStyle == CrosshairStyle.SHORT_TICKS) {
                    int tick = (crosshairStyle == CrosshairStyle.LONG_TICKS) ? 40 : 15;
                    g2d.drawLine(lastDrawX, screenY, lastDrawX + tick, screenY);
                    g2d.drawLine(lastDrawX + lastDrawWidth - tick, screenY, lastDrawX + lastDrawWidth, screenY);
                    g2d.drawLine(screenX, lastDrawY, screenX, lastDrawY + tick);
                    g2d.drawLine(screenX, lastDrawY + lastDrawHeight - tick, screenX, lastDrawY + lastDrawHeight);
                }
            }
        }

        // --- NUEVO: Textos Limpios Estilo Clínico ---
        if (!isExporting) {
            g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
            
            // Arriba Izquierda: Solo la modalidad
            drawShadowText(g2d, modalityTitle, 10, 25);

            // Abajo Izquierda: Valores triangulados + ID + Fecha
            String bottomText = "";
            double baseVal = getPixelValueAtCrosshair(volume);
            
            if (modalityTitle.equals("CT")) {
                bottomText = "HU: " + String.format("%.0f", baseVal);
            } else if (modalityTitle.equals("NM")) {
                bottomText = "Counts: " + String.format("%.0f", baseVal);
            } else if (modalityTitle.equals("CT/NM")) {
                double overVal = getPixelValueAtCrosshair(overlayVolume);
                bottomText = "HU: " + String.format("%.0f", baseVal) + " | Counts: " + String.format("%.0f", overVal);
            }

            bottomText += "  |  " + patientId + "  |  " + acqDate;
            
            drawShadowText(g2d, bottomText, 10, panelHeight - 15);
        }
    }
} 