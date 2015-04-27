package com.lcsc.cs.lurkserver.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class  Room {
    private List<String>        _connections;
    private List<Monster>       _monsters;
    private Map<String, Player> _players;

    public Room(String roomName, Map<String, Object> roomData) {
        _connections            = new ArrayList<String>();
        Object[] connections    = (Object[])roomData.get("connections");

        for (Object connection : connections) {
            _connections.add((String)connection);
        }

        _monsters               = new ArrayList<Monster>();
        Object[] monsters       = (Object[])roomData.get("monsters");

        for (Object monsterObj : monsters) {
            Map<String, Object> monsterData = (Map<String, Object>)monsterObj;
            _monsters.add(new Monster(monsterData));
        }

        _players                = new HashMap<String, Player>();
    }

    /**
     * @param otherRoom This is the other room that may or may not be connected to this one.
     * @return A boolean informing if the given room is connected to this room or not.
     */
    public boolean isConnected(String otherRoom) {
        return _connections.contains(otherRoom);
    }

    /**
     * This will add a player to this room.
     * @param player This is the player that has entered the room.
     * @return The amount of gold that was picked up.
     */
    public int addPlayer(Player player) {
        int gold = 0;
        _players.put(player.name, player);
        return gold;
    }

    /**
     * This will remove a player from this room.
     * @param playerName This is the player that has left the room.
     */
    public void removePlayer(String playerName) {
        _players.remove(playerName);
    }
}