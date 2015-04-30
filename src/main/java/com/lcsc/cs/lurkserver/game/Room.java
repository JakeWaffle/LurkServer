package com.lcsc.cs.lurkserver.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class  Room {
    private Random              _randomGenerator;
    private String              _roomName;
    private String              _description;
    private String              _roomInfo;
    private List<String>        _connections;
    private List<Monster>       _monsters;
    private Map<String, Player> _players;

    public Room(String roomName, Map<String, Object> roomData) {
        _randomGenerator        = new Random();
        _roomName               = roomName;
        _description            = (String)roomData.get("description");
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

        //The room info will later be used when a player enters this room and needs its information.
        _roomInfo               = String.format("Name: %s\n", _roomName)+
                String.format("Description: %s\n", _description);

        for (int i=0; i<_connections.size(); i++) {
            if (i == _connections.size()-1 && _monsters.size() == 0)
                _roomInfo += String.format("Connection: %s", _connections.get(_connections.size()-1));
            else
                _roomInfo += String.format("Connection: %s\n", _connections.get(i));
        }

        for (int i = 0; i < _monsters.size(); i++) {
            if (i == _monsters.size()-1)
                _roomInfo += String.format("Monster: %s", _monsters.get(i).name);
            else
                _roomInfo += String.format("Monster: %s\n", _monsters.get(i).name);
        }
    }

    /**
     * @param otherRoom This is the other room that may or may not be connected to this one.
     * @return A boolean informing if the given room is connected to this room or not.
     */
    public boolean isConnected(String otherRoom) {
        return _connections.contains(otherRoom);
    }

    /**
     * This triggers players and monsters to battle each other.
     */
    public void fight() {
        if (!areMonstersDead()) {
            for (Player player : _players.values()) {
                Monster randomMonster = randomMonster();
                player.fight(randomMonster);
            }
        }
    }

    private boolean areMonstersDead() {
        boolean monstersAreDead = true;
        for (Monster monster : _monsters) {
            if (!monster.isDead()) {
                monstersAreDead = false;
                break;
            }
        }
        return monstersAreDead;
    }

    /**
     * @return A random monster in the room that isn't dead.
     */
    private Monster randomMonster() {
        Monster monster;
        do {
            int index   = _randomGenerator.nextInt(_monsters.size());
            monster     = _monsters.get(index);
        } while(monster.isDead());
        return monster;
    }

    /**
     * This is meant to get a list of information about this room, that will be sent back to the client. In addition
     * to the room's info it will include players and monsters in the room.
     * @return A list of strings that are meant to be sent to the client in separate INFOM messages.
     */
    public List<String> getRoomInfo() {
        List<String> infoList = new ArrayList<String>();

        for (Player player : _players.values()) {
            infoList.add(player.getInfo());
        }

        for (Monster monster : _monsters) {
            infoList.add(monster.getInfo());
        }



        infoList.add(_roomInfo);

        return infoList;
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