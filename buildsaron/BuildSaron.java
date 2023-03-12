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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;


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

public class BuildSaron implements Constants {
    private final String versionPrefix; 
    private final String PREFIX = "VER_";
    
    public BuildSaron(){
        this.versionPrefix = getVersion();
    }
    
    // walk through the file-tree
    public void walk(Properties props, String path ) throws IOException {
        String dist_uri = props.getProperty(DIST_URI);

        File dir = new File( path );

        File[] list = dir.listFiles();
        if (list == null) 
            return;
        
        
        if(hasPrefixableFiles(dir))
            reCreateDist(props, dir);        
        

        for(File f : list) {
            if (f.isDirectory()) {
                if(!f.getName().endsWith(dist_uri)){
                    reCreateDist(props, f);
                    walk(props, f.getAbsolutePath());                    
                }
                else{
                }
            }
            else {
                if(isPrefixableFile(f)){
                    int endIndex = f.getAbsoluteFile().toString().length() - f.getName().length();
                    String pathString = f.getAbsoluteFile().toString().substring(0,endIndex) +  dist_uri + "/" + versionPrefix + f.getName();                  
                    Files.copy(f.toPath(), new File(pathString).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println( "Copy:" + f.getAbsoluteFile()  + " ---> " + pathString);
                }
                else
                    System.out.println("Ignore: " + f.getName());

            }
        }
    }
    
    
    
    private void printPhpPrefixFile(Properties props, File phpFile) throws IOException{
        String dev_root = props.getProperty(DEV_ROOT);
        String prefix_file_uri = props.getProperty(PREFIX_FILE_URI);
        String prefix_filename = props.getProperty(PREFIX_FILENAME);
        String saron_uri = props.getProperty(SARON_URI);

        StringBuilder sb = new StringBuilder();
        sb.append("<?php\n"); 
        sb.append("define(\"JS_VERSION_PREFIX\", \"");
        sb.append(versionPrefix);
        sb.append("\");");

        try (OutputStreamWriter fstream = new OutputStreamWriter(new FileOutputStream(phpFile), StandardCharsets.UTF_8)) {
            fstream.write(sb.toString());
        }
        
    }
    
    
    
    private void reCreateDist(Properties props, File f) throws IOException{
        String dist_uri = props.getProperty(DIST_URI);

        File distFile = new File(f.toString() + "/" + dist_uri);
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

    
    
    private boolean isPrefixableFile(File f){
        if(f.getName().endsWith(".js"))
            return true;
        if(f.getName().endsWith(".css"))
            return true;
        return false;
    }
    


    private boolean hasPrefixableFiles(File dir){
        File[] list = dir.listFiles();
        for(File f : list) 
            if (isPrefixableFile(f))
                return true;
        
        return false;
    }
    
    
    
    public static void main(String[] args) {
        BuildSaron bs = new BuildSaron();
        System.out.println("Start");
        try{
            SaronProperties sProp = new SaronProperties();
            Properties props = sProp.getPropertiesValue();
            String js_uri = props.getProperty(JS_URI);
            String saron_uri = props.getProperty(SARON_URI);
            String dev_root = props.getProperty(DEV_ROOT);
            String css_uri = props.getProperty(CSS_URI);
            String prefix_file_uri = props.getProperty(PREFIX_FILE_URI);
            String prefix_filename = props.getProperty(PREFIX_FILENAME);

            File phpFile = new File(dev_root + saron_uri + prefix_file_uri + prefix_filename);
            if(phpFile.delete())
                System.out.println("DELTED: " + phpFile);

            bs.walk(props, dev_root + saron_uri + js_uri );
            bs.walk(props, dev_root + saron_uri + css_uri );
            bs.printPhpPrefixFile(props, phpFile);

        }
        catch(IOException e){
            System.out.println(e.toString());
        }
    }

}
