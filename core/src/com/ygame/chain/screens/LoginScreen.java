package com.ygame.chain.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.ygame.chain.Client.GameClient;

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
    private Texture backgroundTexture;
    private Image backgroundImage1;
    private Image backgroundImage2;
    Game game;

    public LoginScreen(Game game) {
        this.game = game;
//        VisUI.load(Gdx.files.internal("uiresource/uiskin.json")); // 加载VisUI皮肤

        VisUI.load(VisUI.SkinScale.X2);
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);


        backgroundTexture = new Texture(Gdx.files.internal("./resourse/backgrounds/3.png"));
        backgroundImage1 = new Image(backgroundTexture);
        backgroundImage2 = new Image(backgroundTexture);
        backgroundImage1.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        backgroundImage2.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        backgroundImage2.setPosition(Gdx.graphics.getWidth(), 0);

        stage.addActor(backgroundImage1);
        stage.addActor(backgroundImage2);

        // 创建标题
        Label titleLabel = new Label("Chain Together", VisUI.getSkin());
        titleLabel.setColor(Color.BLACK);
        titleLabel.setFontScale(3);

        // 创建输入字段和按钮
        TextField idField = new TextField("", VisUI.getSkin());
        idField.setMessageText("Enter ID");

        Label idText = new Label("ID:", VisUI.getSkin());
        idText.setColor(Color.BLACK);

        TextField passwordField = new TextField("", VisUI.getSkin());
        passwordField.setMessageText("Enter Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        Label passwordText = new Label("Password:", VisUI.getSkin());
        passwordText.setColor(Color.BLACK);

        TextButton loginButton = new TextButton("Login", VisUI.getSkin());
        TextButton registerButton = new TextButton("Register", VisUI.getSkin());

        // 创建表格布局
        Table table = new Table();
        table.setFillParent(true);
        table.add(titleLabel).colspan(2).center().padBottom(50);
        table.row();
        table.add(idText).left();
        table.add(idField).fillX().uniformX().padBottom(10);
        table.row();
        table.add(passwordText).left();
        table.add(passwordField).fillX().uniformX().padBottom(20);
        table.row();
        table.add(loginButton).colspan(2).center().padBottom(10);
        table.row();
        table.add(registerButton).colspan(2).center();

        stage.addActor(table);

        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String id = idField.getText();
                String password = passwordField.getText();
//                Gdx.app.log("aaaa","aaa");
                // Send login request to server

                new Thread(new GameClient()).start();

//                game.setScreen(new Level1());
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

        // 动态背景效果
        float speed = 100 * delta; // 背景移动速度
        backgroundImage1.setX(backgroundImage1.getX() - speed);
        backgroundImage2.setX(backgroundImage2.getX() - speed);

        // 循环背景
        if (backgroundImage1.getX() + backgroundImage1.getWidth() <= 0) {
            backgroundImage1.setX(backgroundImage2.getX() + backgroundImage2.getWidth());
        }
        if (backgroundImage2.getX() + backgroundImage2.getWidth() <= 0) {
            backgroundImage2.setX(backgroundImage1.getX() + backgroundImage1.getWidth());
        }

        stage.act(Math.min(delta, 1 / 60f));
//        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        backgroundImage1.setSize(width, height);
        backgroundImage2.setSize(width, height);
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
        backgroundTexture.dispose();
        VisUI.dispose(); // 释放VisUI资源
    }
}