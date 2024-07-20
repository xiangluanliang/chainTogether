package com.ygame.chain.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.ygame.chain.Client.GameClient;


import javax.swing.*;
import java.net.InetAddress;
import java.sql.*;

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

        stage.addActor(createLoginTable());

    }

    private Table createLoginTable() {
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
        TextButton exitButton = new TextButton("Exit", VisUI.getSkin());

        // 创建表格布局
        Table loginTable = new Table();
        loginTable.setFillParent(true);
        loginTable.add(titleLabel).colspan(3).center().padBottom(50);

        loginTable.row();
        loginTable.add(idText).left();
        loginTable.add(idField).colspan(2).fillX().uniformX().padBottom(10);

        loginTable.row();
        loginTable.add(passwordText).left();
        loginTable.add(passwordField).colspan(2).fillX().uniformX().padBottom(20);

        loginTable.row();
        loginTable.add().uniformY().padBottom(60);
        loginTable.row();
        loginTable.add(loginButton).center();
        loginTable.add(registerButton).center();
        loginTable.add(exitButton).center();

        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String userID = idField.getText();
                String password = passwordField.getText();
                if (iflogin(userID, password)) {
                    JOptionPane.showMessageDialog(null, "Welcome！", "Message", -1);
                    loginTable.remove();
                    stage.addActor(createRoomTable());
                } else {
                    JOptionPane.showMessageDialog(null, "User not found！", "Message", -1);
                }


            }
        });

        registerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String userID = idField.getText();
                String password = passwordField.getText();
                actionPerformed(userID, password);
                // Send register request to server

            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        return loginTable;

    }

    private Table createRoomTable() {

        TextButton enterRoomButton = new TextButton("Enter Room", VisUI.getSkin());
        TextButton createRoomButton = new TextButton("Create Room", VisUI.getSkin());
        TextButton exitButton = new TextButton("Exit", VisUI.getSkin());
        enterRoomButton.setColor(Color.RED);
        createRoomButton.setColor(Color.RED);
        exitButton.setColor(Color.RED);

        Table createRoomTable = new Table();
        createRoomTable.setFillParent(true);

        createRoomTable.row();
        createRoomTable.add(createRoomButton).center().uniform().padBottom(50);

        createRoomTable.row();
        createRoomTable.add(enterRoomButton).center().uniform().padBottom(50);

        createRoomTable.row();
        createRoomTable.add(exitButton).center().uniform().padBottom(50);

        enterRoomButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // 显示输入对话框
                showInputDialog("Enter Something:", new InputListener() {
                    @Override
                    public void input(String input) {
                        System.out.println(input);
//                        Gdx.app.log("LoginScreen", "Input received: " + input);
//                        // 在这里处理输入逻辑
                    }
                });
            }
        });


        createRoomButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
//                new Thread(new GameClient()).start();
                game.setScreen(new Level1());
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        return createRoomTable;
    }

    private void showInputDialog(String message, final InputListener listener) {
        final TextField textField = new TextField("", VisUI.getSkin());
        final Dialog dialog = new Dialog("Input", VisUI.getSkin()) {
            @Override
            protected void result(Object object) {
                if (object.equals(true)) {
                    listener.input(textField.getText());
                }
            }
        };
        dialog.text(message);


        dialog.getContentTable().row();
        dialog.getContentTable().add(textField).width(200);

        dialog.button("OK", true);
        dialog.button("Cancel", false);

        dialog.show(stage);

    }

    public static boolean iflogin(String userID, String password) {
        if (userID == null && password == null) {
            JOptionPane.showMessageDialog(null, "账号或密码不能为空");
            return false;
        }
        Connection con;//代表数据库连接
        PreparedStatement pre;//用于执行预编译的SQL语句
        ResultSet resultSet;//存储了从数据库查询返回的结果集
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/userinfor", "root", "orange741216");
            System.out.println(con.isReadOnly());
            String sql = "SELECT * FROM user WHERE userID=? AND password=?";
            pre = con.prepareStatement(sql);//将SQL查询语句作为参数传入
            pre.setString(1, userID);
            pre.setString(2, password);
            resultSet = pre.executeQuery();//执行查询，并将返回的结果集赋值
            return resultSet.next();//判断结果集中是否有记录（如果有只会有一条）
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void actionPerformed(String userID, String password) {

        if (userID.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "账号或密码不能为空");
            return;
        }
        Connection con = null;
        PreparedStatement pre = null;
        ResultSet rs = null;//存储了从数据库查询返回的结果集
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/userinfor", "root", "orange741216");
            //检查用户名是否已存在在
            String sql = "SELECT * FROM user WHERE userID=? ";
            pre = con.prepareStatement(sql);
            pre.setString(1, userID);
            rs = pre.executeQuery();//执行查询，并将返回的结果集赋值

            if (rs.next()) {       //判断用户名是否重复
                JOptionPane.showMessageDialog(null, "用户名已经存在");
            } else {
                String sql2 = "INSERT INTO user(userId, password) VALUES (?, ?)";
                pre = con.prepareStatement(sql2);
                pre.setString(1, userID);
                pre.setString(2, password);
                pre.executeUpdate();
                JOptionPane.showMessageDialog(null, "注册成功！请再次登录以开始游戏.");
            }
        } catch (Exception e) {
            throw new RuntimeException("注册失败！", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pre != null) pre.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void show() {
    }

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

    public interface InputListener {
        void input(String input);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
        VisUI.dispose(); // 释放VisUI资源
    }
}