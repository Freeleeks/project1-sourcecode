package com.mygdx.game.controllers;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

public class KeyboardController  implements InputProcessor {
    public boolean left, right, up, down, p;
    public boolean isMouse1Down, isMouse2Down, isMouse3Down;
    public boolean isDragged;
   // public Vector2 mouseLocation = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    @Override
    public boolean keyDown(int keycode) {
        boolean keyProcessed = false;
        switch (keycode) // switch code base on the variable keycode
        {
            case Keys.LEFT:     // if keycode is the same as Keys.LEFT a.k.a 21
                left = true;    // do this
                keyProcessed = true;    // we have reacted to a keypress
                break;
            case Keys.RIGHT:    // if keycode is the same as Keys.LEFT a.k.a 22
                right = true;   // do this
                keyProcessed = true;    // we have reacted to a keypress
                break;
            case Keys.UP:       // if keycode is the same as Keys.LEFT a.k.a 19
                up = true;      // do this
                keyProcessed = true;    // we have reacted to a keypress
                break;
            case Keys.DOWN:     // if keycode is the same as Keys.LEFT a.k.a 20
                down = true;    // do this
                keyProcessed = true;
                break;// we have reacted to a keypress
            case Keys.P:     // if keycode is the same as Keys.LEFT a.k.a 21
                p = true;    // do this
                keyProcessed = true;    // we have reacted to a keypress
                break;
        }
        return keyProcessed;    //  return our peyProcessed flag
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean keyProcessed = false;
        switch (keycode) // switch code base on the variable keycode
        {
            case Keys.LEFT:     // if keycode is the same as Keys.LEFT a.k.a 21
                left = false;   // do this
                keyProcessed = true;    // we have reacted to a keypress
                break;
            case Keys.RIGHT:    // if keycode is the same as Keys.LEFT a.k.a 22
                right = false;  // do this
                keyProcessed = true;    // we have reacted to a keypress
                break;
            case Keys.UP:       // if keycode is the same as Keys.LEFT a.k.a 19
                up = false;     // do this
                keyProcessed = true;    // we have reacted to a keypress
                break;
            case Keys.DOWN:     // if keycode is the same as Keys.LEFT a.k.a 20
                down = false;   // do this
                keyProcessed = true;    // we have reacted to a keypress
                break;
            case Keys.P:     // if keycode is the same as Keys.LEFT a.k.a 21
                p = false;    // do this
                keyProcessed = true;    // we have reacted to a keypress
                break;
        }
        return keyProcessed;    //  return our peyProcessed flag
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}