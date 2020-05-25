package gred.nucleus.autocrop;

import gred.nucleus.FilesInputOutput.Directory;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.io.FileSaver;

import static ij.plugin.Orthogonal_Views.getImageID;

public class GenerateOverlay {

    String m_pathToZprojectionFolder;
    String m_pathToOverlayFolder;

    public GenerateOverlay(String pathToZprojectionFolder,String pathToOverlayFolder){
        this.m_pathToZprojectionFolder=pathToZprojectionFolder;
        this.m_pathToOverlayFolder=pathToOverlayFolder;
    }
    public void run() {
        Directory ZprojectionFolder = new Directory(this.m_pathToZprojectionFolder);
        ZprojectionFolder.listImageFiles(this.m_pathToZprojectionFolder);
        ZprojectionFolder.checkIfEmpty();
        Directory OverlayFolder = new Directory(this.m_pathToOverlayFolder);
        OverlayFolder.listImageFiles(this.m_pathToOverlayFolder);
        OverlayFolder.checkIfEmpty();
        for (short i = 0; i < ZprojectionFolder.getNumberFiles(); ++i) {
            ImagePlus overlay = IJ.openImage(OverlayFolder.getFile(i).getAbsolutePath());
            ImagePlus Zprojection = IJ.openImage(ZprojectionFolder.getFile(i).getAbsolutePath());

            IJ.run(Zprojection, "Fire", "");
            IJ.run(Zprojection, "Invert LUT", "");
            overlay.show();
            Zprojection.show();
            IJ.run("Add Image...", overlay +" x=0 y=0 opacity=50");

            saveFile(Zprojection,ZprojectionFolder.getFile(i).getParent()+ZprojectionFolder.getFile(i).separator+Zprojection.getTitle()+"MERGED.tiff");
            overlay.close();
            Zprojection.close();

        }
    }

    /**
     * Save output file
     * @param imagePlusInput image to save
     * @param pathFile path to save image
     */
    public static void saveFile ( ImagePlus imagePlusInput, String pathFile) {
        FileSaver fileSaver = new FileSaver(imagePlusInput);
        fileSaver.saveAsTiff(pathFile);
    }
}
