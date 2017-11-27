package com.mygdx.game.characters;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.mygdx.game.world.TiledGameMap;


import java.util.ArrayList;

import static com.mygdx.game.RasmusGame.player;

public class SpawnGenerator {
    public static void spawnPlayer(TiledGameMap map) {

        MapObjects objects = map.getLayers("spawnPoints").getObjects();

        for (MapObject object : objects) {
            if (object.getProperties().get("gid").equals(4916)) {
                player.createMovementAnimation();
                float x = object.getProperties().get("x", float.class);
                float y = object.getProperties().get("y", float.class);
                player.setPlayerLocation(x,y);
            }
        }
    }

    public static ArrayList<Enemy> spawnEnemy(TiledGameMap map){
        ArrayList<Enemy> enemies = new ArrayList<>();
        MapObjects objects  = map.getLayers("spawnPoints").getObjects();

        for(MapObject object : objects){

            if (object.getProperties().get("gid").equals(4917)){
                Enemy enemy = new Enemy();
                enemy.create(new Coordinates(object.getProperties().get("x",float.class),object.getProperties().get("y", float.class)));
                enemies.add(enemy);
            }
        }
        return enemies;
    }
}
