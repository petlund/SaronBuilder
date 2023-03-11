# SaronBuilder
This tool sets a random prefix to all javascripts in Saron application so the users always have the latest version of the application. 

This tool generate a prefixfile with a random prefix witch looks like this. 

define("JS_VERSION_PREFIX", "VER_7048_");

The php-file is a one liner.

If the prefix file exists all js file moves to a dist folder and the prefix adds to the js-filenames. 
All paths in the Saron application also changes so they to point to the new locations

Output from the tool
Copy:/uri/saron/app/css/saron.js ---> /uri/saron/app/css/dist/VER_7048_saron.js

The first time you run the tool a file named config.props generates. You have to update with paths from your installation of Saron.  

#config.props
#Sat Mar 11 22:23:39 CET 2023
PREFIX_FILENAME=The name of the file this program generate. Look at /app/util/distPath.php 
SARON_URI=See config.php in saron
CSS_URI=See config.php in saron
JS_URI=See config.php in saron
DEV_ROOT=Absolute path to dev root folder
DIST_URI=See config.php in saron
PREFIX_FILE_URI=app/util/
  
