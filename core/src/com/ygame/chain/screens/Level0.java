package com.ygame.chain.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
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
import com.ygame.chain.utils.GameMapGenerator;
import com.ygame.chain.utils.Player;
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
    private static Player redBall;
    private static Player greenBall;
    private static Player purpleBall;
    private SmoothCamera smoothCamera;

    private World world;
    private Box2DDebugRenderer debugRenderer;

    GameMapGenerator mapGenerator;
    private Stage stage;


    public Level0(String userID, String texturePath) {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();

//         创建相机
        float forceLength = 100f;// 相机焦距（缩小倍率） -mark-> 后期考虑要不要把相机封装起来（感觉没必要？

        smoothCamera = new SmoothCamera(1);
        smoothCamera.setToOrtho(false,
                Gdx.graphics.getWidth() / forceLength,
                Gdx.graphics.getHeight() / forceLength);

//         载入地图（我是不是有选择困难证？先加载地图还是先加载人物都要想半天。。没差啦，按照自然顺序先加载地图吧

        // 创建世界
        world = new World(new Vector2(0, -9.8f), true);
        // 试调渲染
        debugRenderer = new Box2DDebugRenderer();

        // 加载所有碰撞箱
        mapGenerator = new GameMapGenerator("./chain_together_map/level-0.tmx", world);
        mapGenerator.createTerrainFromTiled("terrainObj");

        mapGenerator.createGround();

        players = new HashMap<>();
        // Initialize the controlled player
        controlledPlayer = new Player(texturePath, world, 5.1f, 5.1f);
        players.put(userID, controlledPlayer);


    }

    public Level0(String userID, String texturePath, String roomCode) {
        this(userID, texturePath);
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

//        smoothCamera.update(redBall);

        // 将绘制与相机投影绑定
        batch.setProjectionMatrix(smoothCamera.combined);

        batch.begin();
        for (Player player : players.values()) {
            player.render(batch);
        }
        batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        handleInput();

        // 给Box2D世界里的物体绘制轮廓，正式游戏需要注释掉这个渲染
        debugRenderer.render(world, smoothCamera.combined);

        // 更新世界里的关系 这个要放在绘制之后，最好放最后面
        world.step(1 / 60f, 6, 2);
    }

    public void addPlayer(String userID, String texturePath) {
        Player player = new Player(texturePath, world, 5.1f, 5.1f);
        players.put(userID, player);
    }

    public void updatePlayerPosition(String userID, Vector2 position) {
        Player player = players.get(userID);
        if (player != null) {
            player.setPosition(position.x, position.y);
        }
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            greenBall.move(0.1f, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.A))
            greenBall.move(-0.1f, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.W))
            greenBall.jump(0, 6);
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
