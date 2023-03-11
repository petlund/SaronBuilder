
package buildsaron;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author peter
 */

public class SaronProperties implements Constants{
    
    public Properties getPropertiesValue() throws IOException{
        Properties props = new Properties();
        File file = new File("config.props2");

        
        if(file.exists()){
            InputStream is = new FileInputStream(file);
            props.load(is);
        }
        else{
            System.out.println("new propsfile created: " + file.getAbsolutePath());
            props.setProperty(DEV_ROOT, "Absolute path to dev root folder");
            props.setProperty(PREFIX_FILE_URI, "app/util/");
            props.setProperty(PREFIX_FILENAME, "The name of the file this program generate. Look at /app/util/distPath.php ");
            props.setProperty(SARON_URI, "See config.php in saron");
            props.setProperty(JS_URI, "See config.php in saron");
            props.setProperty(CSS_URI, "See config.php in saron");
            props.setProperty(DIST_URI, "See config.php in saron");
    
            FileOutputStream fos = new FileOutputStream(file);
            props.store(fos, file.getName());
        }
    
        return props;
    }
}
