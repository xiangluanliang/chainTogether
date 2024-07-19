package com.ygame.chain.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;

/**
 * ProjectName: chain_together_Yhr
 * ClassName: LoginScreen
 * Package : com.ygame.chain.screens
 * Description:
 *
 * @Author Lxl
 * @Create 2024/7/18 17:00
 * @Version 1.0
 */
public class LoginScreen implements Screen {
    private Stage stage;
    private TextField idField;
    private TextField passwordField;
    private TextButton loginButton;
    private TextButton registerButton;
    Game game;

    public LoginScreen(Game game) {
//        VisUI.load(Gdx.files.internal("uiresource/uiskin.json")); // 加载VisUI皮肤

        this.game = game;
        VisUI.load(VisUI.SkinScale.X2);
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        idField = new TextField("", VisUI.getSkin());
        idField.setMessageText("Enter ID");
        passwordField = new TextField("", VisUI.getSkin());
        passwordField.setMessageText("Enter Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        loginButton = new TextButton("Login", VisUI.getSkin());
        registerButton = new TextButton("Register", VisUI.getSkin());

        table.add(new Label("ID:", VisUI.getSkin()));
        table.add(idField).fillX().uniformX();
        table.row().pad(10, 0, 10, 0);
        table.add(new Label("Password:", VisUI.getSkin()));
        table.add(passwordField).fillX().uniformX();
        table.row().pad(10, 0, 10, 0);
        table.add(loginButton).colspan(2).center();
        table.row().pad(10, 0, 10, 0);
        table.add(registerButton).colspan(2).center();

        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String id = idField.getText();
                String password = passwordField.getText();
                // Send login request to server

                game.setScreen(new Level1());
            }
        });

        registerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String id = idField.getText();
                String password = passwordField.getText();
                // Send register request to server


            }
        });
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        VisUI.dispose(); // 释放VisUI资源
    }
}