package com.ygame.chain;

import com.badlogic.gdx.Game;
import com.ygame.chain.screens.LoginScreen;

public class ChainTogether extends Game {

    @Override
    public void create() {
        setScreen(new LoginScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }


    @Override
    public void dispose() {
    }
}

