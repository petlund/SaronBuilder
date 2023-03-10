/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buildsaron;

/**
 *
 * @author peter
 */
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


/**
 * This program generate a fileprefix on every javascriptfile used in saron/app and copy all javascript files to a subdirectory /dist
 * It also generate a PHP file including a definition of the prefix constant. This definition is used by the browser to find js-files
 * This mechanism is used to avoid using old javascript in the application
 * 
 * If app/util/js_version_prefix.php not exist. Saron use the files without prefix
 * 
 * One example row of output from this program
 * Copy: xxxx/dev/project-saron/saron/app/js/tables/memberstate.js ---> xxxx/project-saron/saron/app/js/tables/dist/VER_1221_memberstate.js

 */

public class BuildSaron {
    //Absoule path to dev root folder
    private static final String DEV_ROOT = "/Users/peter/Dropbox/Peter/dev/project-saron/";
    
    //Same as in config.php
    private static final String PREFIX_FILE_URI = "app/util/";
    
    //The name of the file this program generate. Look at /app/util/distPath.php 
    private static final String PREFIX_FILENAME = "js_version_prefix.php";    
    
    //Same as in config.php
    private static final String SARON_URI = "saron/";

    //Same as in config.php
    private static final String JS_URI = "app/js/";

    //Same as in config.php
    private static final String CSS_URI = "app/css/";

    //Same as in config.php
    private static final String DIST_URI = "dist";

    private final String versionPrefix; 
    private final String PREFIX = "VER_";
    
    public BuildSaron(){
        this.versionPrefix = getVersion();
    }
    
    // walk through the file-tree
    public void walk( String path ) throws IOException {
        
        File dir = new File( path );

        File[] list = dir.listFiles();
        if (list == null) 
            return;
        
        
        boolean hasFiles = hasFiles(dir);
        if(hasFiles)
            reCreateDist(dir);        
        

        for(File f : list) {
            if (f.isDirectory()) {
                if(!f.getName().endsWith(DIST_URI)){
                    reCreateDist(f);
                    walk(f.getAbsolutePath());                    
                }
                else{
                }
            }
            else {
                int endIndex = f.getAbsoluteFile().toString().length() - f.getName().length();
                String pathString = f.getAbsoluteFile().toString().substring(0,endIndex) +  DIST_URI + "/" + versionPrefix + f.getName();  
                Files.copy(f.toPath(), new File(pathString).toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println( "Copy:" + f.getAbsoluteFile()  + " ---> " + pathString);
            }
        }
        printPhpPrefixFile();
    }
    
    
    
    private void printPhpPrefixFile() throws IOException{
        StringBuilder sb = new StringBuilder();
        sb.append("<?php\n"); 
        sb.append("define(\"JS_VERSION_PREFIX\", \"");
        sb.append(versionPrefix);
        sb.append("\");");
        
        try (FileWriter phpFile = new FileWriter(DEV_ROOT + SARON_URI + PREFIX_FILE_URI + PREFIX_FILENAME)) {
            phpFile.write(sb.toString());
        }        
        
    }
    
    
    
    private void reCreateDist(File f) throws IOException{
        File distFile = new File(f.toString() + "/" + DIST_URI);
        if(distFile.exists()){
            for(File file: distFile.listFiles()) 
                if (!file.isDirectory()) 
                    file.delete();
            distFile.delete();
        }
        distFile.mkdir();
    }
    
    
    
    private String getVersion(){
        return PREFIX + (int)(Math.random()* 9000 + 1000) + "_";
    }

    
    
    private void create(){
        StringBuilder sb = new StringBuilder();
        sb.append("<?php\r\n");
        sb.append("define(\"JS_VERSION_PREFIX\", \"");
        sb.append(getVersion());
        sb.append("\");");
        
        sb.toString();
        
    }


    private boolean hasFiles(File dir){
        File[] list = dir.listFiles();
        for(File f : list) 
            if (!f.isDirectory())
                return true;
        
        return false;
    }
    
    
    private void addDistDirectoryIfNotExist(File f){
        File distFile = new File(f.toString() + "/" + DIST_URI);
        if(!distFile.exists())
            distFile.mkdir();
    }

    
    public static void main(String[] args) {
        BuildSaron bs = new BuildSaron();
        System.out.println("Start");
        try{
            bs.walk(DEV_ROOT + SARON_URI + JS_URI );
            bs.walk(DEV_ROOT + SARON_URI + CSS_URI );
        }
        catch(IOException e){
            System.out.println(e.toString());
        }
    }

}
