package com.lcsc.cs.lurkserver.game;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jake on 4/19/2015.
 * This class will be stored within Game and will hold the player objects that are logged into the game. If a
 * player has just logged in it will have a new player object with defaults assigned to it. If an existing player
 * logs in, its data will be loaded from its player file and its information will be used again.
 */
public class PlayerPool {
    //These are the players that are logged in.
    private         Map<String, Player> _players;

    //This directory is relative to the jar's directory.
    private final   String _playerDirectory;

    public PlayerPool() {
        _players = new HashMap<String, Player>();
        _playerDirectory = "player_data";
    }

    /**
     * This is for fetching data of players that are in the game currently.
     * @param playerName The player name that the Player object belongs to. It should be unique.
     * @return If the player exists a Player object will be returned, otherwise a null will be returned.
     */
    public Player getPlayer(String playerName) {
        return _players.get(playerName);
    }

    /**
     * This checks the PlayerPool _players map to see if the player is logged in currently.
     * @return false if player isn't logged in, true if the player is logged in.
     */
    public boolean playerLoggedIn(String playerName) {
        return _players.containsKey(playerName);
    }

    /**
     * This is called when a client logs in as a player. This playerName might have been used before. If it has
     * and that player isn't logged in currently, the players data will be loaded from a file. If it hasn't been used
     * before then the player will be given default data.
     * @param playerName A unique player name that may or may not already exist.
     * @return If the player's name is already logged in, then false will be returned. Else true will be returned.
     */
    public boolean loadPlayer(String playerName) {
        boolean success = false;
        if (!playerLoggedIn(playerName)) {
            Player newPlayer = new Player(playerName);
            newPlayer.loadData();
            _players.put(playerName, newPlayer);

            success = true;
        }
        return success;
    }

    /**
     * This should be called periodically and will inform the Player objects to save their data to their files.
     */
    public void savePlayers() {
        for (Player player : _players.values()) {
            player.saveData();
        }
    }
}
