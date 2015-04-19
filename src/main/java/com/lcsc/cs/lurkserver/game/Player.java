package com.lcsc.cs.lurkserver.game;

/**
 * Created by Jake on 4/19/2015.
 * This holds a logged in player's data.
 */
public class Player {
    private static  String _playerDirectory;
    private         String _name;
    public Player(String playerName) {
        _name = playerName;
    }

    public static boolean playerExists(String playerName) {
        //TODO Search player dir for player files to see if a player exists.
        return false;
    }

    /**
     * This will either load in default data for a player or load in a player's data from a file.
     * This depends on if the player's file exists already or not.
     */
    public void loadData() {
        //TODO Load data for a player.
    }

    private void loadDefaultData() {

    }

    private void loadDataFromFile() {

    }

    /**
     * This is used to save the player's current data to a file so it can be loaded later on.
     */
    public void saveData() {
        //TODO Save player's data to corresponding file.
    }
}
