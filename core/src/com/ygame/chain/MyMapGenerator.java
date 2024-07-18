package com.ygame.chain;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;


import static com.ygame.chain.ConstPool.PPM;

/**
 * ProjectName: chain_together_Yhr
 * ClassName: MapGenerator
 * Package : com.ygame.chain
 * Description:
 *
 * @Author Lxl
 * @Create 2024/7/16 15:19
 * @Version 1.0
 */
public class MyMapGenerator {
    World world;
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer mapRenderer;

    public MyMapGenerator(String path, World world) {
        this.world = world;
        map = new TmxMapLoader().load(path);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / PPM);
    }

    public OrthogonalTiledMapRenderer getMapRenderer() {
        return mapRenderer;
    }

    public void createTerrainFromTiled(String layerName) {
        MapLayer terrainLayer = map.getLayers().get(layerName);
        MapObjects objects = null;
        if (terrainLayer != null) {
            objects = terrainLayer.getObjects();
        }
        if (objects != null) {
            for (MapObject object : objects) {
                if (object instanceof PolylineMapObject) {
                    createChainShape((PolylineMapObject) object);
                } else if (object instanceof RectangleMapObject) {
                    createRectangleShape((RectangleMapObject) object);
                } else if (object instanceof PolygonMapObject) {
                    createPolygonShape((PolygonMapObject) object);
                } else if (object instanceof EllipseMapObject) {
                    createEllipseShape((EllipseMapObject) object);
                }
            }
        }

    }


    private void createChainShape(PolylineMapObject polylineObject) {
        float[] vertices = polylineObject.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; i++) {
            worldVertices[i] = new Vector2(vertices[i * 2] / PPM, vertices[i * 2 + 1] / PPM);
        }

        ChainShape chainShape = new ChainShape();
        chainShape.createChain(worldVertices);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = chainShape;
        fixtureDef.friction = 0.5f;
        body.createFixture(fixtureDef);

        chainShape.dispose();
    }

    private void createRectangleShape(RectangleMapObject rectangleObject) {
        Rectangle rectangle = rectangleObject.getRectangle();

        PolygonShape polygonShape = new PolygonShape();
        Vector2 center = new Vector2((rectangle.x + rectangle.width / 2) / PPM, (rectangle.y + rectangle.height / 2) / PPM);
        polygonShape.setAsBox(rectangle.width / 2 / PPM, rectangle.height / 2 / PPM, center, 0.0f);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.friction = 0.5f;
        body.createFixture(fixtureDef);

        polygonShape.dispose();
    }

    private void createPolygonShape(PolygonMapObject polygonObject) {
        float[] vertices = polygonObject.getPolygon().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; i++) {
            worldVertices[i] = new Vector2(vertices[i * 2] / PPM, vertices[i * 2 + 1] / PPM);
        }

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(worldVertices);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.friction = 0.5f;
        body.createFixture(fixtureDef);

        polygonShape.dispose();
    }

    private void createEllipseShape(EllipseMapObject ellipseObject) {
        float x = (ellipseObject.getEllipse().x + ellipseObject.getEllipse().width / 2) / PPM;
        float y = (ellipseObject.getEllipse().y + ellipseObject.getEllipse().height / 2) / PPM;
        float radius = ellipseObject.getEllipse().width / 2 / PPM;

        CircleShape circleShape = new CircleShape();
        circleShape.setPosition(new Vector2(x, y));
        circleShape.setRadius(radius);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.friction = 0.5f;
        body.createFixture(fixtureDef);

        circleShape.dispose();
    }
}
