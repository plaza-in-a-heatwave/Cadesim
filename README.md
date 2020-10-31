# Cadesim server

## Dependencies:
- [Netty](https://netty.io/)
- [Apache Commons CLI](http://commons.apache.org/proper/commons-cli/)

## Eclipse config
- Use the gradle import wizard and folder Cadesim

Otherwise manually:
- Import from filesystem (not existing project), use folder "Cadesim\Server"
- Entry point: Cadesim\Server\src\com\benberi\cadesim\server\service\GameServerBootstrap.java
- Change .classpath entries to where you installed Netty/Commons CLI
- Set src as source dir and clean,rebuild

## Exporting
- Export as jar through your favorite IDE, extract dependencies.
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

### Scripts
Start and stop scripts (**start_servers.py** and **stop_servers.py**) are provided.

These can be used to start a group of several servers at once.

Change the commandline arguments in **start_servers.py** as needed. You shouldn't have to change anything in **stop_servers.py**. 

## Releasing
- modify any desired files before release
- use gradle (>6) to release: ```gradle release``` (also builds client).
    - this requires you to have bash in ```%PATH%``` if building on windows.
- For more details on the process see ```build.gradle``` and ```build-release.bash```.

# Cadesim client

## Dependencies
- [Netty](https://netty.io/)
- [Apache Commons IO](http://commons.apache.org/proper/commons-io/)
- [Google GSON](https://github.com/google/gson)

## Eclipse config
- Use the gradle import wizard and folder Cadesim

Otherwise manually:
- Use the Gradle import wizard to import the "Client" folder
- build.gradle -> gradle -> refresh gradle project
- Set JRE System Libraries to JavaSE-1.8 if not already
- Entry point: "Cadesim\Client\desktop\src\com\benberi\cadesim\desktop\DesktopLauncher.java"
- Update the build path dependencies:
    - Core depends on Core/src
    - BlockadeSimulator-Desktop depends on itself (with /src excluded)
    - BlockadeSimulator-Desktop depends on /src

## Exporting
- Export as jar through your favorite IDE, extract dependencies.
- or use gradle to export as jar: ```gradle :server:dist```

## Running
double-click on the .jar file.

If there are errors, check that ```user.config``` exists.

Additional developer options can be added at compile-time.

To disable automatic updates: add ```autoupdate=no``` to user.config

## Releasing
- modify any desired files before release, including:
    - user.config file (client only); for instance set your http downloader url after ```appbase = ```.
- use gradle (>6) to create the release: ```gradle release``` (also builds server)
    - this requires you to have bash in ```%PATH%``` if building on windows.
- For more details on the process see ```build.gradle``` and ```build-release.bash```.

# Releases
See https://github.com/plaza-in-a-heatwave/Cadesim/tree/release for prebuilt jars.

# Deploying
- Make changes to the code until happy with the feature/bugfix
- Run build-release.bash to create the release versions
- Run ```deploy.bash flavorname``` when happy with the release versions, where flavorname is the specific flavor of Cadesim. (e.g. different flagnames). This is to support each flavor having its own branch in future.
