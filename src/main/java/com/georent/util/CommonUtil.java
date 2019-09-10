package com.georent.util;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.IOException;
import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;

public class CommonUtil {

    public  static  String generatingRandomString() {
        int length = 10;
        boolean useLetters = true;
        boolean useNumbers = true;
        String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
        return generatedString;
    }

    public static void desktop(String fileName) {
        try {


            File htmlFile = new File(fileName);
            Desktop.getDesktop().browse(htmlFile.toURI());
            Runtime rt = Runtime.getRuntime();
            rt.exec("/home/nick/HillelProject/georent-back-repo/georent-back-59-Messeg-mail/src/main/resources/forgotInEmail.html");
//            rt.exec(myFile);
//            Desktop desktop = null;
            Desktop desktop = Desktop.getDesktop();
            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
            }
            File myFile = new File(fileName);
            desktop.open(myFile);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    public static void runBrowser(String path) {
        String url = "file://" + path;
        if(Desktop.isDesktopSupported()){
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else{
            Runtime runtime = Runtime.getRuntime();
            try {

                runtime.exec("xdg-open " + url);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
