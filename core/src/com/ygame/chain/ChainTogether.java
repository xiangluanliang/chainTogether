package com.ygame.chain;

import com.badlogic.gdx.Game;
import com.ygame.chain.screens.LoginScreen;

public class ChainTogether extends Game {

    @Override
    public void create() {
        setScreen(new LoginScreen(this));
//        setScreen(new Level0());
//        System.out.println(new Timestamp(System.currentTimeMillis()));
    }

    @Override
    public void render() {
        super.render();
    }


    @Override
    public void dispose() {
    }
}

