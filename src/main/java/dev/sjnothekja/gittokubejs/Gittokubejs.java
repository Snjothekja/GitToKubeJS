package dev.sjnothekja.gittokubejs;

import net.fabricmc.api.ModInitializer;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

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
            //GetAllFiles();
            ZipDownloader();
        } catch (IOException | URISyntaxException e) {
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

    public static void ZipDownloader() throws IOException, URISyntaxException {
        URL zipFileURL = new URL("https://github.com/Snjothekja/GitToKubeJS/archive/refs/heads/master.zip");
        Download(zipFileURL, "../KubeJSFilesAndDirectories.zip");

        ExtractZip("../KubeJSFilesAndDirectories.zip");

        //Files.deleteIfExists(Paths.get("../KubeJSFilesAndDirectories.zip"));
    }

    public static void ExtractZip(String zipFile) throws IOException {

        byte[] buffer = new byte[1024];
        File destDir = new File("../../kubejs");
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry ze = zis.getNextEntry();
        while (ze != null) {
            File newFile = newFile(destDir, ze);
            if(ze.isDirectory()) {
                if(!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Could not create directory " + newFile.getAbsolutePath());
                }
            } else {
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Could not create directory " + parent.getAbsolutePath());
                }

                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            ze = zis.getNextEntry();
        }
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }
}
