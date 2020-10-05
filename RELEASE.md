# Building for release

## Server http share
* Export server as jar ("Extract required libraries" option in Eclipse)
* Create a directory containing the following:
** cadesim-server.jar
** getdown.jar
** getdown.txt (rename getdown.txt.server to getdown.txt, set your http url after appbase = )
** getdown-bg.png
** getdown-core-1.8.6.jar
** growup.png
* cd "DirectoryName"
* ```java -classpath getdown-core-1.8.6.jar com.threerings.getdown.tools.Digester .```
* Copy the files into the http share.

## Server directory
Add the following files:
* maps (containing your maps. These are not auto downloaded)
* cadesim-server.jar
* getdown.jar
* getdown.txt
* digest.txt
* digest2.txt
* getdown-bg.png
* growup.png


## Client http share
As with Server http share, but specify a different url with the following files:
* bg.png
* cadesim.jar
* digest.txt
* digest2.txt
* getdown.jar
* getdown.txt
* growup.ico
* growup.png
* user.config
* version.txt

## Client directory
Same list of files as the client http share.