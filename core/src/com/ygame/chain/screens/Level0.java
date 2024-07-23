package com.ygame.chain.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.ygame.chain.Client.GameClient;
import com.ygame.chain.utils.GameMapGenerator;
import com.ygame.chain.utils.Player;
import com.ygame.chain.utils.SharedClasses;
import com.ygame.chain.utils.SmoothCamera;

import java.util.HashMap;
import java.util.Map;

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


    public Level0(String userID, GameClient gameClient) {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();
        this.gameClient = gameClient;
        this.userID = userID;

//         创建相机
        float forceLength = 100f;// 相机焦距（缩小倍率） -mark-> 后期考虑要不要把相机封装起来（感觉没必要？

        smoothCamera = new SmoothCamera(1);
        smoothCamera.setToOrtho(false,
                Gdx.graphics.getWidth() / forceLength,
                Gdx.graphics.getHeight() / forceLength);

//         载入地图（我是不是有选择困难证？先加载地图还是先加载人物都要想半天。。没差啦，按照自然顺序先加载地图吧

        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        rainMusic.setLooping(true);
//        rainMusic.play();

        // 创建世界
        world = new World(new Vector2(0, -9.8f), true);
        // 试调渲染
        debugRenderer = new Box2DDebugRenderer();

        // 加载所有碰撞箱
        mapGenerator = new GameMapGenerator("./chain_together_map/level-0.tmx", world);
        mapGenerator.createTerrainFromTiled("terrainObj");

        mapGenerator.createGround();

        Player red = new Player("./ball/smallRedBall.png", world, 5.0f, 5.0f);
        Player green = new Player("./ball/smallGreenBall.png", world, 5.1f, 5.1f);
        Player purple = new Player("./ball/smallPurpleBall.png", world, 5.2f, 5.2f);

        players = new HashMap<>();
//        new SharedClasses();
//        for (Map.Entry<String, SharedClasses.PlayerState> player : playerMap.entrySet()) {
//            System.out.println(player.getKey() + ":" + player.getValue());
//        }
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

//        gameClient.sendPlayerMap();

    }

    public Level0(String userID, GameClient gameClient, String roomCode) {
        this(userID, gameClient);
        Skin skin = VisUI.getSkin();

        // 创建标签控件
        Label roomNumberLabel = new Label("Room: " + roomCode, skin);
        roomNumberLabel.setColor(Color.BLACK);
        roomNumberLabel.setFontScale(2f);

        // 创建一个Table来布局控件
        Table table = new Table();
        table.top().right();
        table.setFillParent(true);
        table.add(roomNumberLabel).pad(100);

        // 添加Table到Stage
        stage.addActor(table);
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

//        players = gameClient.gameplayers;

        handleInput();
        playersUpdate();

        batch.begin();
        for (Player player : players.values()) {
            player.render(batch);
        }
        batch.end();

//        for (Map.Entry<String, SharedClasses.PlayerState> playerState : SharedClasses.playerMap.entrySet()) {
//            players.get(playerState.getKey()).movePlayerTo(playerState.getValue().getX(), playerState.getValue().getY());
////            System.out.println(playerState.getValue().x + "," + playerState.getValue().y);
//        }
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

//        for (Map.Entry<String, SharedClasses.PlayerState> player :
//                SharedClasses.playerMap.entrySet()) {
//            System.out.println(player.getValue().getX() + ", " + player.getValue().getY());
//        }


//        for (Map.Entry<String, Player> role : players.entrySet()) {
//            SharedClasses.playerMap.get(role.getKey()).update(role.getValue().getPosX(), role.getValue().getPosY());
//        }
        gameClient.sendPlayerMap();


        // 给Box2D世界里的物体绘制轮廓，正式游戏需要注释掉这个渲染
        debugRenderer.render(world, smoothCamera.combined);

        // 更新世界里的关系 这个要放在绘制之后，最好放最后面
        world.step(1 / 60f, 6, 2);
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            controlledPlayer.move(new Vector2(0.1f, 0));
            SharedClasses.playerMap.get(userID).linearImpulse = new Vector2(0.1f, 0);
//            controlledPlayer.getBody().getPosition().x += 2;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            controlledPlayer.move(new Vector2(-0.1f, 0));
            SharedClasses.playerMap.get(userID).linearImpulse = new Vector2(-0.1f, 0);

        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            controlledPlayer.jump(new Vector2(0, 6f));
            SharedClasses.playerMap.get(userID).linearImpulse = new Vector2(0, 6f);

        }

    }

    private void playersUpdate() {
        for (Map.Entry<String, Player> role : players.entrySet()) {
            if (!role.getKey().equals(userID)) {
                role.getValue().move(SharedClasses.playerMap.get(role.getKey()).linearImpulse);
                SharedClasses.playerMap.get(role.getKey()).linearImpulse = new Vector2(0, 0);
//                SharedClasses.playerMap.get(role.getKey()).update(role.getValue().getPosX(), role.getValue().getPosY());
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        batch.dispose();
        world.dispose();
        debugRenderer.dispose();
    }
}
