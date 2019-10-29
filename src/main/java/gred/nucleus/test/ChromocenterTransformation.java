package gred.nucleus.test;

import gred.nucleus.FilesInputOutput.Directory;
import gred.nucleus.FilesInputOutput.FilesNames;
import gred.nucleus.core.NucleusSegmentation;
import gred.nucleus.exceptions.fileInOut;
import gred.nucleus.segmentation.SegmentationParameters;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.ChannelSplitter;
import loci.formats.FormatException;
import loci.plugins.BF;

import java.io.File;
import java.io.IOException;

public class ChromocenterTransformation {

    public static void main(String[] args) throws IOException, FormatException, fileInOut,Exception {
        String input="/home/titus/Bureau/data/CCsegmented/raw";
        String output="/home/titus/Bureau/data/CCsegmented/transformed";


        SegmentationParameters segmentationParameters = new SegmentationParameters(input,output);


        Directory directoryInput = new Directory(input);
        directoryInput.listFiles(input);
        directoryInput.checkIfEmpty();
        for (short i = 0; i < directoryInput.getNumberFiles(); ++i) {
            File currentFile = directoryInput.getFile(i);
            String fileImg = currentFile.toString();
            FilesNames outPutFilesNames = new FilesNames(fileImg);
            String prefix = outPutFilesNames.PrefixeNameFile();
            NucleusSegmentation nucleusSegmentation = new NucleusSegmentation(currentFile ,prefix,segmentationParameters);
            ImagePlus[] currentImage = BF.openImagePlus(currentFile.getAbsolutePath());
            ChannelSplitter splitter = new ChannelSplitter();
            currentImage = splitter.split(currentImage[0]);
            ImagePlus toto=currentImage[0];
           ImagePlus out= nucleusSegmentation.generateSegmentedImage(toto,0);
            saveFile( out,output+prefix);



        }


    }
    public static void saveFile ( ImagePlus imagePlusInput, String pathFile) {
        FileSaver fileSaver = new FileSaver(imagePlusInput);
        fileSaver.saveAsTiffStack(pathFile);
    }
}
