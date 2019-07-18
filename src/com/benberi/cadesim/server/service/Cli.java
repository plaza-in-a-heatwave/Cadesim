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

import java.io.File;
import java.nio.file.*;

public class Cli {
    private static final Logger log = Logger.getLogger(Cli.class.getName());
    private String[] args = null;
    private Options options = new Options();
    private String chosenMap;

    public Cli(String[] args) {

        this.args = args;

        options.addOption("h", "help", false, "Show overview of functions available");
        options.addOption("a", "amount", true, "Set amount of allowed players");
        options.addOption("p", "port", true, "Set port for server");
        options.addOption("m", "map", true, "Set name of map. If you don't set this, make sure there is a maps folder.");
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
                    log.log(Level.SEVERE, "failed to find random map folder");
                    help();
                }
            }
            else {
                chosenMap = cmd.getOptionValue("m");
                log.log(Level.INFO, "using user specified map:" + chosenMap);
            }

            if(cmd.hasOption("p") && cmd.hasOption("a")) {
                GameServerBootstrap.initiateServerStart(Integer.parseInt(cmd.getOptionValue("a")), chosenMap, Integer.parseInt(cmd.getOptionValue("p")));
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