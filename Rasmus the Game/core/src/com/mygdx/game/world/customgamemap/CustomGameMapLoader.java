package com.mygdx.game.world.customgamemap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;


public class CustomGameMapLoader {

    private static Json json = new Json();

    public CustomGameMapData loadMap(String id, String name){
        Gdx.files.local("maps").file().mkdirs();
        FileHandle file = Gdx.files.local("maps/"+id+".map");
        if (file.exists()){
            return json.fromJson(CustomGameMapData.class,file.readString());
        }
        return null;
    }

    public CustomGameMapData saveMap(String id, String name, int[][][] map){
        CustomGameMapData data = new CustomGameMapData();
        data.id = id;
        data.name = name;
        data.map = map;

        Gdx.files.local("maps").file().mkdirs();
        FileHandle file = Gdx.files.local("maps/"+id+".map");
        file.writeString(json.prettyPrint(data),false);
        return data;
    }
}
