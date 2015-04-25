package com.lcsc.cs.lurkserver.game;

import org.eclipse.jetty.util.ajax.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jake on 4/19/2015.
 * This holds a logged in player's data.
 */
public class Player {
    private static final Logger _logger = LoggerFactory.getLogger(Player.class);
    public  final   int         MAX_STAT_POINTS = 100;
    private final   File        _playerFile;
    public  final   String      name;
    public          String      description;
    public          int         gold;
    public          int         attack;
    public          int         defense;
    public          int         regen;
    public          PlayerStatus status;
    public          String      location;
    public          int         health;
    public          boolean     started;

    /**
     * The constructor for the player. This will automatically load the player's data from its existing data file
     * or will load in default data and save the file.
     * @param playerName A unique player name.
     * @param playerDataDir The absolute path for the players' data files directory.
     */
    public Player(String playerName, String playerDataDir) {
        name            = playerName;
        _playerFile     = new File(playerDataDir, playerName+".pldat");

        if (_playerFile.exists()) {
            loadDataFromFile();
        }
        else {
            loadDefaultData();
        }
    }

    /**
     * This tells the pool if the player exists or not.
     * @return A boolean specifying if the player's data file exists already.
     */
    public static boolean playerExists(String playerName, String playerDataDir) {
        return new File(playerDataDir, playerName+".pldat").exists();
    }

    /**
     * This just sets some default data for the new player.
     */
    private void loadDefaultData() {
        description = null;
        gold        = 0;
        attack      = 0;
        defense     = 0;
        regen       = 0;
        status      = PlayerStatus.ALIVE;
        location    = "Purgatory";
        health      = 100;
        started     = false;
    }

    /**
     * This will load the player's data from a file.
     */
    private void loadDataFromFile() {
        FileReader reader = null;

        try {
            reader = new FileReader(_playerFile);
            Map<String, Object> data = (Map<String, Object>)JSON.parse(reader);

            description = (String)data.get("description");
            gold        = ((Long)data.get("gold")).intValue();
            attack      = ((Long)data.get("attack")).intValue();
            defense     = ((Long)data.get("defense")).intValue();
            regen       = ((Long)data.get("regen")).intValue();
            status      = PlayerStatus.fromString((String)data.get("status"));
            location    = (String)data.get("location");
            health      = ((Long)data.get("health")).intValue();;
            started     = ((Boolean)data.get("started")).booleanValue();
        } catch (FileNotFoundException e) {
            _logger.error("Problem loading the player data file", e);
        } catch (IOException e) {
            _logger.error("Problem loading the player data file", e);
        }
    }

    /**
     * This is used to save the player's current data to a file so it can be loaded next time that player joins
     * the game.
     */
    public void saveData() {
        Map<String, Object> data = new HashMap<String, Object>();

        data.put("description", description);
        data.put("gold", gold);
        data.put("attack", attack);
        data.put("defense", defense);
        data.put("regen", regen);
        data.put("status", status.getStatus());
        data.put("location", location);
        data.put("health", health);
        data.put("started", started);

        String jsonData = JSON.toString(data);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(_playerFile);
            out.write(jsonData.getBytes());
        } catch (FileNotFoundException e) {
            _logger.error("Player data file failed to save", e);
        } catch (IOException e) {
            _logger.error("Player data file failed to save", e);
        } finally {
            try {
                out.close();
            } catch (IOException e) {}
        }
    }

    public String getStats() {
        return  String.format("Name: %s\n",name)+
                String.format("Description: %s\n", description)+
                String.format("Gold: %d\n", gold)+
                String.format("Attack: %d\n", attack)+
                String.format("Defense: %d\n", defense)+
                String.format("Regen: %d\n", regen)+
                String.format("Status: %s\n", status.getStatus())+
                String.format("Location: %s\n", location)+
                String.format("Health: %d\n", health)+
                String.format("Started: %s\n", started ? "YES" : "NO");
    }
}
