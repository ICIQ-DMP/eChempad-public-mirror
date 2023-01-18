package org.ICIQ.eChempad.web.ui;

import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("rawtypes")
public class FileUploadedListener implements EventListener {

    private static final Pattern filePathWithExtensionPattern = Pattern.compile("(.*)?(\\.\\S+)");

    private Main main = null;
    private String parameter = null;
    private Label filenameLabel	= null;
    private String tmpFolderPath = null;
    private boolean isRepeatable = false;


    public FileUploadedListener(Main main, String parameter, Label filenameLabel, boolean isRepeatable){
        this.main = main;
        this.parameter = parameter;
        this.filenameLabel = filenameLabel;
        this.tmpFolderPath = main.getTmpDir();
        this.isRepeatable = isRepeatable;
    }

    @Override
    public void onEvent(Event arg0) throws Exception {
        UploadEvent event = (UploadEvent)arg0;
        for(Media media : event.getMedias())
            loadFile(media);
    }

    private void loadFile(org.zkoss.util.media.Media media) throws IOException {
        if(media instanceof AMedia){
            AMedia m = (AMedia) media;
            String fileName = calculateValidFileName(media.getName());
            String fileNamePath =  tmpFolderPath + File.separatorChar + fileName;
            copyFile(m,fileNamePath, m.isBinary());
            setLabelText(fileName);
            main.addParameterFile(this.parameter, fileName, isRepeatable);
        }else{
            filenameLabel.setValue("");
            Messagebox.show("File type not correct. Must be a text file.", "Error", Messagebox.OK, Messagebox.ERROR);
        }
    }

    private void copyFile (AMedia m, String filePath, boolean isBinary) throws IOException {
        File f = new File(filePath);
        if(isBinary) {
            try(InputStream in = m.getStreamData()){
                Files.copy(in, f.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } else {
            try(FileOutputStream outFile = new java.io.FileOutputStream(f)){
                outFile.write(m.getStringData().getBytes("utf-8"));
            }
        }
    }


    // This function is used to avoid name collisions when uploading multiple files for a calculation, if we upload two files with the same name we'll
    // return this filename with a numeric suffix. Example : if we upload two energy.txt files resulting names will be: energy.txt, energy_2.txt and so on
    private String calculateValidFileName(String filename){
        String folderPath = tmpFolderPath + File.separatorChar;
        if(!new File(folderPath + filename).exists())
            return filename;
        else{
            String name = getFileName(filename);
            String extension = getFileExtension(filename);
            int inx = 2;
            do{
                String alternativeName = name + "_" + String.valueOf(inx) + extension;
                String alternativeFilePath = folderPath + alternativeName;
                if(!new File(alternativeFilePath).exists())
                    return alternativeName;
                inx++;
            }while(true);
        }
    }

    private String getFileName(String filename){
        Matcher matches = filePathWithExtensionPattern.matcher(filename);
        if(matches.matches()){
            return matches.group(1);
        }
        return filename;
    }

    private String getFileExtension(String filename){
        Matcher matches = filePathWithExtensionPattern.matcher(filename);
        if(matches.matches()){
            return matches.group(2);
        }
        return "";
    }

    private void setLabelText(String fileName){
        if(isRepeatable){
            String labelText = filenameLabel.getValue() + " " + fileName;
            if(labelText.length() > 20)
                labelText = labelText.substring(0,20) + "...";
            filenameLabel.setValue(labelText);
        }
        else
            filenameLabel.setValue(fileName);
    }
}