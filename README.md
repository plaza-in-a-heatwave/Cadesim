# Obsidio Blockade Server

This is a server used to run the Blockade Simulator that recently was named as "Obsidio".  
The game client repository: https://github.com/BenBeri/Obsidio  

Dependencies:  
[Netty](https://netty.io/)  
[Apache Commons CLI](http://commons.apache.org/proper/commons-cli/)

E.g.:
* netty-4.0.56.Final/jar/all-in-one/netty-all-4.0.56.Final-sources.jar
* netty-4.0.56.Final/jar/all-in-one/netty-all-4.0.56.Final.jar
* commons-cli-1.4/commons-cli-1.4.jar
* commons-cli-1.4/commons-cli-1.4-javadoc.jar
* commons-cli-1.4/commons-cli-1.4-sources.jar
* commons-cli-1.4/commons-cli-1.4-tests.jar
* commons-cli-1.4/commons-cli-1.4-test-sources.jar

# Notes on Eclipse build
Change .classpath entries to where you installed Netty/Commons CLI

Set src as source dir and clean,rebuild

Couldnt get jars exporting from eclipse
* you can use export-to-jar.sh in terminal
* add netty/commons paths to "jar.deps" and run "export-to-jar.sh"