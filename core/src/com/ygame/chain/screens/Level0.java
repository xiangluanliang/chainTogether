package com.ygame.chain.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.ygame.chain.Client.GameClient;
import com.ygame.chain.utils.*;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.ygame.chain.utils.ConstPool.PPM;

/**
 * ProjectName: chain_together_Yhr
 * ClassName: Level1
 * Package : com.ygame.chain.screens
 * Description:
 *
 * @Author Lxl
 * @Create 2024/7/19 10:20
 * @Version 1.0
 */
public class Level0 implements Screen {
    private SpriteBatch batch;
    private Map<String, Player> players;

    private Player controlledPlayer;
    private SmoothCamera smoothCamera;

    private World world;
    private Box2DDebugRenderer debugRenderer;

    GameMapGenerator mapGenerator;
    private Stage stage;
    String userID;
    private GameClient gameClient;
    private Music rainMusic;
    private Game game;
    private Array<Bullet> bullets;
    private float bulletTimer;
    private Texture bulletTexture;
    private LevelKey starKey;
    private float keyX;
    private float keyY;

    public Level0(Game game, String userID, GameClient gameClient) {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();
        this.gameClient = gameClient;
        this.userID = userID;
        this.game = game;
        keyX = 17f;
        keyY = 10f;

        bulletTexture = new Texture("drop.png");


//         创建相机
        float forceLength = 100f;// 相机焦距（缩小倍率） -mark-> 后期考虑要不要把相机封装起来（感觉没必要？

        smoothCamera = new SmoothCamera(1);
        smoothCamera.setToOrtho(false,
                Gdx.graphics.getWidth() / forceLength,
                Gdx.graphics.getHeight() / forceLength);

//         载入地图（我是不是有选择困难证？先加载地图还是先加载人物都要想半天。。没差啦，按照自然顺序先加载地图吧

        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("bgm.mp3"));
        rainMusic.setLooping(true);
        rainMusic.play();

        // 创建世界
        world = new World(new Vector2(0, -9.8f), true);
        // 试调渲染
        debugRenderer = new Box2DDebugRenderer();

        // 加载所有碰撞箱
        mapGenerator = new GameMapGenerator("./chain_together_map/level-1.tmx", world);
        mapGenerator.createTerrainFromTiled("terrainObj");

        mapGenerator.createGround();

        bullets = new Array<>();
        bulletTimer = 0;

        starKey = null;

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();

                if ((fixtureA.getUserData() != null && fixtureA.getUserData() instanceof Player) ||
                        (fixtureB.getUserData() != null && fixtureB.getUserData() instanceof Player)) {
                    if ((fixtureA.getUserData() != null && fixtureA.getUserData() instanceof Bullet) ||
                            (fixtureB.getUserData() != null && fixtureB.getUserData() instanceof Bullet)) {
                        if (SharedClasses.gameStart) {
                            pause();
                        }
                    } else if ((fixtureA.getUserData() != null && fixtureA.getUserData() instanceof LevelKey) ||
                            (fixtureB.getUserData() != null && fixtureB.getUserData() instanceof LevelKey)) {
                        SharedClasses.gameStart = false;
                        gameClient.startGame();
                        JOptionPane.showMessageDialog(null, "You Win!", "Message", -1);
//                        gameClient.close();
//                        game.setScreen(new LoginScreen(game));
//                        for (Player player :
//                                players.values()) {
//                            player.movePlayerTo(2f, 5f, 0);
//                        }
//                        mapGenerator = new GameMapGenerator("./chain_together_map/level-1.tmx", world);
//                        mapGenerator.createTerrainFromTiled("terrainObj");
//                        keyY = 10f;
//                        game.setScreen(new Level1(game, userID, gameClient));
                    }
                }
            }

            @Override
            public void endContact(Contact contact) {
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        });

        Player red = new Player("red", world, 2.0f, 5.0f);
        Player green = new Player("green", world, 2.1f, 5.1f);
        Player purple = new Player("purple", world, 2.2f, 5.2f);

        createDistanceJoint(red.getBody(), green.getBody(), 200 / PPM);
        createDistanceJoint(green.getBody(), purple.getBody(), 200 / PPM);

        players = new HashMap<>();

        players.put("red", red);
        players.put("green", green);
        players.put("purple", purple);


        switch (userID) {
            case "red":
                controlledPlayer = red;
                break;
            case "green":
                controlledPlayer = green;
                break;
            case "purple":
                controlledPlayer = purple;
                break;
            default:
                controlledPlayer = red;
                break;
        }


    }

    public Level0(Game game, String userID, GameClient gameClient, String roomCode) {
        this(game, userID, gameClient);
        Skin skin = VisUI.getSkin();

        // 创建标签控件
        Label roomNumberLabel = new Label("Room: " + roomCode, skin);
        roomNumberLabel.setColor(Color.BLACK);
        roomNumberLabel.setFontScale(2f);

        TextButton level0Button = new TextButton("Start Level0", VisUI.getSkin());
        TextButton exitButton = new TextButton("Exit", VisUI.getSkin());

        // 创建一个Table来布局控件
        Table table = new Table();
        table.top().right();
        table.setFillParent(true);
        table.add(roomNumberLabel).pad(100);

        table.row();
        table.add(level0Button).colspan(1).center().padBottom(30);

        table.row();
        table.add(exitButton).colspan(1).center().padBottom(30);

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
//                players.remove(userID);
                gameClient.close();
                game.setScreen(new LoginScreen(game));
            }
        });

        level0Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                starKey = new LevelKey(world, keyX, keyY);
                roomNumberLabel.setVisible(false);
                SharedClasses.gameStart = true;
                gameClient.startGame();
            }
        });
        stage.addActor(table);
    }

    private void createDistanceJoint(Body bodyA, Body bodyB, float length) {
        RopeJointDef jointDef = new RopeJointDef();
        jointDef.bodyA = bodyA;
        jointDef.bodyB = bodyB;
        jointDef.maxLength = length;
        jointDef.localAnchorA.set(0, 0);
        jointDef.localAnchorB.set(0, 0);

        world.createJoint(jointDef);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mapGenerator.getMapRenderer().setView(smoothCamera);
        mapGenerator.getMapRenderer().render();

//        smoothCamera.update(controlledPlayer);

        // 将绘制与相机投影绑定
        batch.setProjectionMatrix(smoothCamera.combined);

        handleInput();
        for (Map.Entry<String, SharedClasses.PlayerState> playerState : SharedClasses.playerMap.entrySet()) {
            if (!playerState.getKey().equals(userID)) {
                players.get(playerState.getKey()).movePlayerTo(playerState.getValue().getX(), playerState.getValue().getY(), playerState.getValue().angle);
            }
        }

        batch.begin();
        for (Player player : players.values()) {
            player.render(batch);
        }
        if (SharedClasses.gameStart) {
            if (bullets != null) {
                for (Bullet bullet : bullets) {
                    bullet.render(batch);
                }
            }
            if (starKey != null) {
                starKey.render(batch);
            }
        }
        batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        SharedClasses.playerMap.get(userID).update(controlledPlayer.getPosX(), controlledPlayer.getPosY(), controlledPlayer.getBody().getAngle());
        gameClient.sendPlayerMap(SharedClasses.playerMap.get(userID));

        // 给物体绘制轮廓，正式游戏需要注释掉这个渲染
        debugRenderer.render(world, smoothCamera.combined);

        if (SharedClasses.gameStart) {
            updateBullet(delta);
        }

        // 更新世界里的关系 这个要放在绘制之后，最好放最后面
        world.step(1 / 30f, 6, 2);


    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            controlledPlayer.move(new Vector2(0.1f, 0));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            controlledPlayer.move(new Vector2(-0.1f, 0));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            controlledPlayer.jump(new Vector2(0, 6f));

        }

    }

    public void updateBullet(float deltaTime) {

        bulletTimer += deltaTime;
        if (bulletTimer >= 2f) {
            bulletTimer = 0;
            bullets.add(new Bullet(bulletTexture, Gdx.graphics.getWidth() / PPM, new Random().nextInt(10) % (10) + 1, world)); // 从右下角发射
        }
        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update(deltaTime);
            // 移除离开屏幕的子弹
            if (bullet.isOffScreen()) {
                bullets.removeIndex(i);
                world.destroyBody(bullet.getBody());
            }
        }

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
//        SharedClasses.gameStart = false;
        int result = JOptionPane.showOptionDialog(null, "请选择重新开始或退出。", "游戏结束",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"重开", "退出"}, "重开");

        if (result == JOptionPane.YES_OPTION) {
//            players.remove(userID);
            gameClient.close();
            game.setScreen(new LoginScreen(game));
        } else if (result == JOptionPane.NO_OPTION) {
            gameClient.close();
            Gdx.app.exit();
        }
    }

    public void winTable() {

    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        world.dispose();
        debugRenderer.dispose();
    }
}
