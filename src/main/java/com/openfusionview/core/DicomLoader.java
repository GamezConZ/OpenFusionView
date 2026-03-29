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
            // Typical for CT: A folder full of individual 2D .dcm slices
            volume = FolderOpener.open(path);
        } else if (fileOrDir.isFile()) {
            // Typical for SPECT: A single multi-frame .dcm file
            Opener opener = new Opener();
            volume = opener.openImage(path);
        }

        if (volume != null) {
            System.out.println("--- Volume Loaded Successfully ---");
            System.out.println("Title: " + volume.getTitle());
            // Width x Height x Depth (Number of slices/frames)
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