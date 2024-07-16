package com.ygame.chain;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.HashMap;
import java.util.Map;

public class ChainTogether extends ApplicationAdapter {

    private static final float PPM = 100;
    private OrthographicCamera camera;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private Body body;

    SpriteBatch batch;
    Player player;
    TiledMap map;
    OrthogonalTiledMapRenderer renderer;

    Array<Rectangle> collidableRects;
    Array<RectangleMapObject> touchedPlats;

    Map<MapObject, MapLayer> platsMap;
    MapObjects plats;
    Array<MapLayer> platsLayer;


    public void create() {
        player = new Player(50,500,0.15f,"bochii.png");
        batch = new SpriteBatch();

        map = new TmxMapLoader().load("./chain_together_map/level-1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set((float) Gdx.graphics.getWidth() / 2, (float) Gdx.graphics.getHeight() / 2, 0);

        world = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();


        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(0, 10);  // 物体初始位置

        // 根据定义创建一个物体
        body = world.createBody(bodyDef);

        // 创建一个形状
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1, 1);  // 设置形状为1x1的正方形

        // 将形状添加到物体中
        body.createFixture(shape, 1.0f);
        shape.dispose();

        //------------导入所有碰撞块------------
        collidableRects = new Array<>();
        MapLayer terrainLayer = map.getLayers().get("terrainObj");  //地形层
        if (terrainLayer != null) {
            MapObjects objects = terrainLayer.getObjects();
            for (RectangleMapObject obj : objects.getByType(RectangleMapObject.class)) {
                Boolean isCollidable = (Boolean) obj.getProperties().get("isCollidable");
                if (isCollidable != null && isCollidable) {
                    collidableRects.add(obj.getRectangle());
                }
            }
        }

        touchedPlats = new Array<>();
        MapLayer platLayer = map.getLayers().get("plats");  //平台层
        if (platLayer != null) {
            MapObjects objects = platLayer.getObjects();
            for (RectangleMapObject obj : objects.getByType(RectangleMapObject.class)) {
                Boolean isCollidable = (Boolean) obj.getProperties().get("isCollidable");
                if (isCollidable != null && isCollidable) {
                    collidableRects.add(obj.getRectangle());
                }
            }
        }
// --------------------------上面的代码有些可以和下面合并，但是先分开实现---------------------
//        for(RectangleMapObject platss : touchedPlats){
//            platss.getRectangle();
//        }

        platsLayer = new Array<>();
        for (int i = 1; i < 10; i++) {
            platsLayer.add(map.getLayers().get("plat" + i));    //所有平台贴图
        }

        platsMap = new HashMap<>();
        plats = map.getLayers().get("plats").getObjects();
        for(MapLayer platlayer : platsLayer){
            Integer relatedObjID = (Integer) platlayer.getProperties().get("relatedObjID");
            for (MapObject platobj : plats.getByType(MapObject.class)){ //所有平台碰撞体
                if (relatedObjID.equals(platobj.getProperties().get("platID")))
                {
                    platsMap.put(platobj,platlayer);    //map绑定平台的碰撞体和贴图
                }
            }
        }

        System.out.println("this is a test");


    }

    @Override
    public void render() {
        handleInput();
        update(Gdx.graphics.getDeltaTime());
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        renderer.setView(camera);
        renderer.render();

//        world.step(1 / 60f, 6, 2);

//        debugRenderer.render(world,camera.combined);
//        player.updateBox(body,PPM);

        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        player.render(batch);
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            player.move(1);
        if (Gdx.input.isKeyPressed(Input.Keys.A))
            player.move(-1);
        if (Gdx.input.isKeyPressed(Input.Keys.J))
            player.jump();
    }


    private void update(float deltaTime) {

        player.applyGravity(deltaTime, collidableRects);
        player.moveUpdate(deltaTime, collidableRects);
        player.jumpUpdate(deltaTime, collidableRects);
        player.deadUpdate();
//        camera.position.set(sprite.getX() + sprite.getWidth() / 2, sprite.getY() + sprite.getHeight() / 2, 0);
//        camera.update();
    }

    @Override
    public void dispose() {
        world.dispose();
        debugRenderer.dispose();
    }
}
