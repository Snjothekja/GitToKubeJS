package dev.sjnothekja.gittokubejs;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Gittokubejs implements PreLaunchEntrypoint {

    public static Path githubLinkFile;

    @Override
    public void onPreLaunch() {
        config();
    }

    public static void config() {

        //Path modConfigFolder = Paths.get("../config/gittokubejs");
        System.out.println("Starting GitToKubeonPreJs");
        File configFolder = new File("config");
        File gittokubeFolder = new File("config/gittokubejs");
        File configFile = new File("config/gittokubejs/config.txt");
        try {
            configFolder.mkdir();
        }
        catch (Exception ignored) {}
        try {
            gittokubeFolder.mkdir();
        }
        catch (Exception ignored) {}
        try {
            configFile.createNewFile();
        } catch (IOException ignored) {}
        //System.out.println("Config file created or found");
        try {
            //GetAllFiles();
            githubLinkFile = Path.of("config/gittokubejs/config.txt");
            ZipDownloader();
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    /* ||DEPRECATED||
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

     */

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
        //URL zipFileURL = new URL("https://github.com/Snjothekja/GitToKubeJS/archive/refs/heads/master.zip");
        String link;
        try {
            //System.out.println("Getting link from: " + githubLinkFile.toString());
            link = Files.readString(githubLinkFile);
            //System.out.println("Got this link: " + link);
        } catch (Exception e) {
            return;
        }
        if(link.isEmpty()){
            System.out.println("No link");
            return;
        }
        URL zipFileURL = new URL(link);
        //System.out.println("Zip Downloading from: " + link);
        Download(zipFileURL, "config/gittokubejs/KubeJSFilesAndDirectories.zip");

        ExtractZip("config/gittokubejs/KubeJSFilesAndDirectories.zip");

        //Files.deleteIfExists(Paths.get("../KubeJSFilesAndDirectories.zip"));
    }

    public static void ExtractZip(String zipFile) throws IOException {

        byte[] buffer = new byte[1024];
        File destDir = new File("");
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry ze = zis.getNextEntry();
        while (ze != null) {
            File newFile;
            if (ze.isDirectory() && ze.toString().contains("main")) {
                newFile = new File(destDir, ze.getName());
                ze = zis.getNextEntry();
            }
            newFile = new File(destDir, System.getProperty("user.dir").substring(3) + "/kubejs/" + ze.getName().substring(ze.getName().indexOf("main") + 4));
            //System.out.println("System Path: " + System.getProperty("user.dir").substring(3));
            //System.out.println("Extracting " + ze.getName() + " to " + newFile.getAbsolutePath());
            if (ze.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {}
            } else {
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {}

                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            ze = zis.getNextEntry();
        }
        zis.close();
    }


}
