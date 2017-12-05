package com.mygdx.game.entities;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;

public abstract class InteractiveTileObject {

    private TiledMap map;
    private World world;
    protected Rectangle bounds;
    protected Body body;
    protected Fixture fixture;

    public  InteractiveTileObject(World world, TiledMap map, Rectangle bounds){
        this.world = world;
        this.map = map;
        this.bounds = bounds;

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((bounds.getX()+bounds.getWidth()/2),(bounds.getY()+bounds.getHeight()/2));

        body = world.createBody(bdef);

        shape.setAsBox(bounds.getWidth()/2,bounds.getHeight()/2);
        fdef.shape = shape;
        fixture = body.createFixture(fdef);
    }


    public void setCategoryFilter(short filterBit){
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }

    public void onPlayerContact(){
    }
}
