# Cadesim server

## Dependencies:  
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

## Eclipse config
* Import from filesystem (not existing project), use folder "Cadesim\Server"
* Entry point: Cadesim\Server\src\com\benberi\cadesim\server\service\GameServerBootstrap.java
* Change .classpath entries to where you installed Netty/Commons CLI
* Set src as source dir and clean,rebuild

## Exporting
Use eclipse export to jar. Alternatively there is a helper script to export jars:
* export-to-jar.sh in terminal
* add netty/commons paths to "jar.deps" and run "export-to-jar.sh"

# Cadesim client

## Dependencies
[Netty](https://netty.io/)
[Apache Commons IO](http://commons.apache.org/proper/commons-io/)
[Google GSON](https://github.com/google/gson)

## Eclipse config
* Use the Gradle import wizard to import the "Client" folder
* build.gradle -> gradle -> refresh gradle project
* Set JRE System Libraries to JavaSE-1.8 if not already
* Entry point: "Cadesim\Client\desktop\src\com\benberi\cadesim\desktop\DesktopLauncher.java"
* Update the build path dependencies:
** Core depends on Core/src
** BlockadeSimulator-Desktop depends on itself (with /src excluded)
** BlockadeSimulator-Desktop depends on /src

## Exporting
* Export as jar, make sure there is user.config in the same directory
* java -jar jarjar.jar