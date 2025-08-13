package dev.sjnothekja.gittokubejs;

import net.fabricmc.api.ModInitializer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Gittokubejs implements ModInitializer {

    @Override
    public void onInitialize() {

        //Path modConfigFolder = Paths.get("../config/gittokubejs");
        Path modConfigFolder = Paths.get("../git2kubejs");
        if(Files.notExists(modConfigFolder)) {
            try {
                Files.createDirectory(modConfigFolder);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            GetAllFiles();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void GetAllFiles() throws IOException {

        URL allFiles = new URL("https://raw.githubusercontent.com/Snjothekja/GitToKubeJS/refs/heads/main/testFile.txt");
        Download(allFiles, new String("../KubeJSFilesAndDirectories.txt"));

        String[] tokens = new String(Files.readAllBytes(Paths.get("../KubeJSFilesAndDirectories.txt"))).split("\\R");


        String baseString = "https://raw.githubusercontent.com/Snjothekja/GitToKubeJS/refs/heads/main";

        for(String s : tokens){
            String[] curFile = s.split(" ");
            System.out.println(curFile[0] + "+" +  curFile[1]);
        }


    }

    public static void Download(URL curFileURl, String directory) throws IOException {
        try {

            ReadableByteChannel rbc = Channels.newChannel(curFileURl.openStream());
            FileOutputStream fos = new FileOutputStream(directory);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
