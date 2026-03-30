package com.openfusionview.core;

import ij.ImagePlus;
import ij.io.Opener;
import ij.plugin.FolderOpener;
import java.io.File;

/**
 * Handles the loading of DICOM files/directories using the ImageJ API.
 */
public class DicomLoader {

    /**
     * Loads either a directory of slices or a single multi-frame DICOM.
     * @param path The absolute path to the file or directory.
     * @return An ImagePlus object representing the 3D volume, or null.
     */
    public ImagePlus loadVolume(String path) {
        File fileOrDir = new File(path);
        
        if (!fileOrDir.exists()) {
            System.err.println("Error: The path does not exist: " + path);
            return null;
        }

        ImagePlus volume = null;

        if (fileOrDir.isDirectory()) {
            volume = ij.plugin.FolderOpener.open(path);
        } else if (fileOrDir.isFile()) {
            ij.io.Opener opener = new ij.io.Opener();
            volume = opener.openImage(path);
        }

        if (volume != null) {
            // Extracción segura de la modalidad
            String info = (String) volume.getProperty("Info");
            boolean isNM = false;
            
            if (info != null) {
                String[] lines = info.split("\n");
                for (String line : lines) {
                    if (line.contains("0008,0060")) {
                        if (line.contains("NM") || line.contains("PT")) {
                            isNM = true;
                        }
                        break; 
                    }
                }
            }

            // Si es NM, aplicamos la corrección espacial definitiva deducida de la prueba clínica
            if (isNM) {
                ij.ImageStack stack = volume.getStack();
                ij.ImageStack correctedStack = new ij.ImageStack(stack.getWidth(), stack.getHeight());
                
                // Invertir eje Z
                for (int i = stack.getSize(); i >= 1; i--) {
                    ij.process.ImageProcessor ip = stack.getProcessor(i).duplicate();
                    
                    ip.flipVertical();   // Corrección eje Y (Espejo Y)
                    ip.flipHorizontal(); // Corrección eje X (Espejo X) - NUEVO
                    
                    correctedStack.addSlice(stack.getSliceLabel(i), ip);
                }
                volume.setStack(correctedStack);
                System.out.println(">> ÉXITO: Orientación SPECT corregida (Espejo Z + Y + X).");
            }

            System.out.println("--- Volume Loaded Successfully ---");
            System.out.println("Title: " + volume.getTitle());
            System.out.println("Dimensions (WxHxD): " + 
                               volume.getWidth() + "x" + 
                               volume.getHeight() + "x" + 
                               volume.getStackSize());
            System.out.println("----------------------------------");
        } else {
            System.err.println("Error: ImageJ could not parse the DICOM at " + path);
        }

        return volume;
    }
} 