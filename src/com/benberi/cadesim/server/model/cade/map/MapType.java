package com.benberi.cadesim.server.model.cade.map;

import com.benberi.cadesim.server.config.ServerConfiguration;
import com.benberi.cadesim.server.model.cade.map.flag.Flag;
import com.benberi.cadesim.server.model.cade.map.flag.FlagSize;

import java.io.*;

import static com.benberi.cadesim.server.model.cade.map.BlockadeMap.FLAG_1;
import static com.benberi.cadesim.server.model.cade.map.BlockadeMap.FLAG_2;
import static com.benberi.cadesim.server.model.cade.map.BlockadeMap.FLAG_3;

public enum MapType {

    DEFAULT("default.txt");

    private String name;

    MapType(String name) {
        this.name = name;
    }

    public int[][] load(BlockadeMap bmap) {
        int[][] map = new int[BlockadeMap.MAP_WIDTH][BlockadeMap.MAP_HEIGHT];

       // File file = new File(CadeServer.class.getResource("resources/maps/default.txt").getPath());
        File file = new File("maps/" + ServerConfiguration.getMapName() + ".txt");

        int x = 0;
        int y = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(",");
                for (String tile : split) {
                    map[x][y] = Integer.parseInt(tile);
                    x++;
                }
                x = 0;
                y++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int[][] finalMap = new int[BlockadeMap.MAP_WIDTH][BlockadeMap.MAP_HEIGHT];

        int x1 = 0;
        int y1 = 0;

        for (int i = 0; i < map.length; i++) {
            for (int j = map[i].length - 1; j > -1; j--) {
                finalMap[x1][y1] = map[i][j];
                y1++;
            }
            y1 = 0;
            x1++;
        }

        for(int i = 0; i < map.length; i++) {
            for (int j = 0; j < finalMap[i].length; j++) {
                if (isFlag(finalMap[i][j])) {
                    Flag flag = new Flag(FlagSize.forTile(finalMap[i][j]));
                    flag.set(i, j);
                    bmap.addFlag(flag);
                    finalMap[i][j] = 0;
                }
            }
        }

        return finalMap;
    }

    private boolean isFlag(int tile) {
        return tile == FLAG_1 || tile == FLAG_2 || tile == FLAG_3;
    }
}
