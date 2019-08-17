package com.benberi.cadesim.server.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.benberi.cadesim.server.util.RandomUtils;
import com.benberi.cadesim.server.config.Constants;

import java.io.File;
import java.nio.file.*;

public class Cli {
    private static final Logger log = Logger.getLogger(Cli.class.getName());
    private String[] args = null;
    private Options options = new Options();
    private String chosenMap;

    public Cli(String[] args) {
    	log.log(Level.INFO, "starting up " + Constants.name + ".");

        this.args = args;

        options.addOption("h", "help", false, "Show help");
        options.addOption("a", "amount", true, "Set amount of allowed players");
        options.addOption("p", "port", true, "Set port for server");
        options.addOption("t", "turn duration", true, "set turn duration (sec)");
        options.addOption("r", "round duration", true, "set round duration (sec)");
        options.addOption("d", "respawn delay", true, "set respawn delay after sinking (sec)");
        options.addOption("m", "map", true, "Set map name. (leave blank for random)");
    }

    public void parse() throws NumberFormatException, InterruptedException {
        CommandLineParser parser = new BasicParser();

        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("h"))
                help();

            if (!cmd.hasOption("a")) {
                log.log(Level.SEVERE, "Missing amount option");
                help();
            }

            if (!cmd.hasOption("p")) {
                log.log(Level.SEVERE, "Missing port option");
                help();
            }
            
            if (!cmd.hasOption("t")) {
            	log.log(Level.SEVERE, "Missing turnDuration option");
            }
            
            if (!cmd.hasOption("r")) {
            	log.log(Level.SEVERE, "Missing roundDuration option");
            }
            
            if (!cmd.hasOption("d")) {
            	log.log(Level.SEVERE, "Missing respawnDelay option");
            }

            if (!cmd.hasOption("m")) { // Chooses random map if no map chosen
                Path currentRelativePath = Paths.get("");
                Path mapFolderPath = currentRelativePath.resolveSibling("maps");
                File mapFolder = mapFolderPath.toFile();
                File[] mapList = mapFolder.listFiles();
                try {
                    File randomMap = mapList[RandomUtils.randInt(0, mapList.length-1)];
                    chosenMap = randomMap.getName();
                    chosenMap = chosenMap.substring(0, chosenMap.lastIndexOf("."));
                    log.log(Level.INFO, "no map specified, chose random map: " + chosenMap);
                } catch(NullPointerException e) {
                    log.log(Level.SEVERE, "failed to find random map folder. create a folder called \"maps\" in the same directory.");
                    help();
                }
            }
            else {
                chosenMap = cmd.getOptionValue("m");
                log.log(Level.INFO, "using user specified map:" + chosenMap);
            }

            // TODO - this is a really stupid way of handling args.
            // every time you want to add new args you need to modify...
            //	Cli.java
            //	GameServerBootstrap.java
            //	ServerConfiguration.java
            if(cmd.hasOption("p") && cmd.hasOption("a")) {
                GameServerBootstrap.initiateServerStart(
                		Integer.parseInt(cmd.getOptionValue("a")),
                		chosenMap, Integer.parseInt(cmd.getOptionValue("p")),
                		Integer.parseInt(cmd.getOptionValue("t")),
                		Integer.parseInt(cmd.getOptionValue("r")),
                		Integer.parseInt(cmd.getOptionValue("d"))
                );
            }

        } catch (ParseException e) {
            log.log(Level.SEVERE, "Failed to parse comand line properties", e);
            help();
        }
    }

    private void help() {
        // This prints out some help
        HelpFormatter formatter = new HelpFormatter();

        formatter.printHelp("Obsidio-Server", options);
        System.exit(0);
    }
}