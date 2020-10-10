# Cadesim server

## Dependencies:  
* [Netty](https://netty.io/)
* [Apache Commons CLI](http://commons.apache.org/proper/commons-cli/)

## Eclipse config
* Use the gradle import wizard and folder Cadesim

Otherwise manually:
* Import from filesystem (not existing project), use folder "Cadesim\Server"
* Entry point: Cadesim\Server\src\com\benberi\cadesim\server\service\GameServerBootstrap.java
* Change .classpath entries to where you installed Netty/Commons CLI
* Set src as source dir and clean,rebuild

## Exporting
- Export as jar, extract dependencies.
- or use gradle to export as jar: ```gradle :server:dist```

## Running
start with commandline: ```java -jar cadesim-server.jar <args>```

E.g.: to start several servers with different ports via commandline, and schedule automatic in-place updates:
```
java -jar cadesim-server.jar --schedule-updates 04:00 -p 2345
java -jar cadesim-server.jar --schedule-updates 04:00 -p 2346
java -jar cadesim-server.jar --schedule-updates 04:00 -p 2347
```

Specify round and turn times too:
```
# 30s turn, 10m round
java -jar cadesim-server.jar --schedule-updates 04:00 -p 2345 -t 30 -r 900
```

For full usage call with ```--help```.

Additional developer options can be added at compile-time.

## Releasing
- modify any desired files before release
- use gradle to release: ```gradle release``` (also builds client).
    - this requires you to have bash in %PATH% if building on windows.
- For more details on the process see RELEASE.md and build.gradle.

# Cadesim client

## Dependencies
* [Netty](https://netty.io/)
* [Apache Commons IO](http://commons.apache.org/proper/commons-io/)
* [Google GSON](https://github.com/google/gson)

## Eclipse config
* Use the gradle import wizard and folder Cadesim

Otherwise manually:
* Use the Gradle import wizard to import the "Client" folder
* build.gradle -> gradle -> refresh gradle project
* Set JRE System Libraries to JavaSE-1.8 if not already
* Entry point: "Cadesim\Client\desktop\src\com\benberi\cadesim\desktop\DesktopLauncher.java"
* Update the build path dependencies:
** Core depends on Core/src
** BlockadeSimulator-Desktop depends on itself (with /src excluded)
** BlockadeSimulator-Desktop depends on /src

## Exporting
- Export as jar, extract dependencies.
- or use gradle to export as jar: ```gradle :server:dist```

## Running
double-click on the .jar file.

If there are errors, check that ```user.config``` exists.

Additional developer options can be added at compile-time.

To disable automatic updates: add ```autoupdate=no``` to user.config

## Releasing
- modify any desired files before release, including:
    - user.config file (client only)
- use gradle to create the release: ```gradle release``` (also builds server)
    - this requires you to have bash in %PATH% if building on windows.
- For more details on the process see RELEASE.md and build.gradle.