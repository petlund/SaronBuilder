# SaronBuilder
This tool sets a random prefix to all javascripts in saron so the users always have the latest version of the application. 

This tool generate a prefixfile with a random prefix witch looks like this. The prefixf

define("JS_VERSION_PREFIX", "VER_7048_");

If the prefix file exists all js file moves to a dist folder and the prefix adds to the filenames. 
All paths in the Saron application also changes so they to point to the new locations

Output from the tool
Copy:/<xxx>/saron/app/css/saron.js ---> /<yyy>/saron/app/css/dist/VER_7048_saron.js
