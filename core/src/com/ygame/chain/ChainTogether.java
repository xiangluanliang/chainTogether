package com.ygame.chain;

import com.badlogic.gdx.Game;
import com.kotcrab.vis.ui.VisUI;
import com.ygame.chain.screens.LoginScreen;

public class ChainTogether extends Game {

    @Override
    public void create() {
        VisUI.load(VisUI.SkinScale.X2);
        setScreen(new LoginScreen(this));
//        setScreen(new Level1());
    }

    @Override
    public void render() {
        super.render();
    }


    @Override
    public void dispose() {
    }
}

