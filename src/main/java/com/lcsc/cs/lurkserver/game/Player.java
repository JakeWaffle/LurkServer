package com.lcsc.cs.lurkserver.game;

import java.io.File;

/**
 * Created by Jake on 4/19/2015.
 * This holds a logged in player's data.
 */
public class Player {
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

        if (playerExists(playerDataDir, playerName+".pldat")) {
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
        return  new File(playerDataDir, playerName+".pldat").exists();
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
        //TODO Load player data from file!
        loadDefaultData();
    }

    /**
     * This is used to save the player's current data to a file so it can be loaded next time that player joins
     * the game.
     */
    public void saveData() {
        //TODO Save player's data to corresponding file.
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
