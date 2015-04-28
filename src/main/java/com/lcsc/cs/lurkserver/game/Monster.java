package com.lcsc.cs.lurkserver.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by Jake on 4/26/2015.
 */
public class Monster {
    private static final Logger _logger         = LoggerFactory.getLogger(Monster.class);
    public  final   String      name;
    private         String      _description;
    private         int         _gold;
    private         int         _attack;
    private         int         _defense;
    private         int         _regen;
    private         BeingStatus _status;
    private         int         _maxHealth;
    private         int         _health;
    private         boolean     _started;

    public Monster(Map<String, Object> monsterData) {
        name        = (String)monsterData.get("name");
        _description = (String)monsterData.get("description");
        _gold        = ((Long)monsterData.get("gold")).intValue();
        _attack      = ((Long)monsterData.get("attack")).intValue();
        _defense     = ((Long)monsterData.get("defense")).intValue();
        _regen       = ((Long)monsterData.get("regen")).intValue();
        _maxHealth  = ((Long)monsterData.get("max_health")).intValue();
        _health      = _maxHealth;
        _status      = BeingStatus.ALIVE;
    }

    /**
     * This returns the information for the monster so it can be sent to the client.
     * @return
     */
    public String getInfo() {
        /*
        Name:  Glog
        Description: A slimy and toothy character
        Gold: 50
        Attack: 20
        Defense: 30
        Regen: 100
        Monster
        Health: 100
        */

        String info = String.format("Name: %s\n", name)+
                String.format("Description: %s\n", _description)+
                String.format("Gold: %s\n", _gold)+
                String.format("Attack: %s\n", _attack)+
                String.format("Defense: %s\n", _defense)+
                String.format("Regen: %s\n", _regen);

        if (_status == BeingStatus.ALIVE)
            info += "Monster\n";
        //TODO Make sure the 'dead monster' part of the info is correct!
        else
            info += "Dead Monster\n";

        info += String.format("Health: %s", _health);

        return info;
    }
}
