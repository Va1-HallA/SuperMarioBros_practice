package com.supermario.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.supermario.game.MarioBros;
import com.supermario.game.sprites.FireBall;
import com.supermario.game.sprites.item.Item;
import com.supermario.game.sprites.item.ItemDef;
import com.supermario.game.sprites.item.Mushroom;
import com.supermario.game.scenes.Hud;
import com.supermario.game.sprites.Enemy;
import com.supermario.game.sprites.Mario;
import com.supermario.game.tools.B2WorldCreater;
import com.supermario.game.tools.WorldContactListener;

import java.util.concurrent.LinkedBlockingQueue;

public class PlayScreen implements Screen {
    private MarioBros game;
    public OrthographicCamera gameCam;
    public Mario mario;
    public B2WorldCreater creator;

    private Viewport gameViewPort;
    Hud hud;
    private TextureAtlas atlas;

    // tiled map variables
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    // box2d variables
    private World world;
    private Box2DDebugRenderer b2dr;

    private Music music;

    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;

    public Array<FireBall> fireBalls;

    public PlayScreen(MarioBros game){
        atlas = new TextureAtlas("Mario_And_Enemies.pack");

        this.game = game;
        gameCam = new OrthographicCamera();
        gameViewPort = new FitViewport(MarioBros.V_WIDTH / MarioBros.PPM, MarioBros.V_HEIGHT / MarioBros.PPM, gameCam); //original 800, 480
        hud = new Hud(game);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("World_1-1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MarioBros.PPM);
        gameCam.position.set(gameViewPort.getWorldWidth() / 2, gameViewPort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();

        mario = new Mario(this);

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<>();

        fireBalls = new Array<FireBall>();

        creator = new B2WorldCreater(this, game);
        world.setContactListener(new WorldContactListener(this));

        music = game.assetManager.get("audio/music/music.ogg", Music.class);
        music.setLooping(true);
        music.play();
    }

    public void spawnItem(ItemDef idef){
        itemsToSpawn.add(idef);
    }

    public void handleSpawningItems(){
        if(!itemsToSpawn.isEmpty()){
            ItemDef idef = itemsToSpawn.poll();
            if(idef.type == Mushroom.class){
                items.add(new Mushroom(this, idef.position.x, idef.position.y));
            }
        }
    }

    public void addScore(int value){
        hud.addScore(value);
    }

    public MarioBros getGame(){return game;}

    public TextureAtlas getAtlas(){
        return atlas;
    }

    public TiledMap getMap(){return map;}

    public World getWorld(){return world;}

    @Override
    public void show() {

    }

    public void update(float deltaTime){
        mario.handleInput(deltaTime);
        handleSpawningItems();
        mario.update(deltaTime);
        for(Enemy enemy: creator.getEnemies()){
            enemy.update(deltaTime);
            if(enemy.getX()< mario.getX()+ 350 / MarioBros.PPM) enemy.b2body.setActive(true);
        }
        for(Item item:items) item.update(deltaTime);
        for(FireBall fireBall: fireBalls) fireBall.update(deltaTime);
        hud.update(deltaTime);
        world.step(1/60f, 6, 2);
        gameCam.position.x = mario.b2body.getPosition().x;
        if(gameCam.position.x < (MarioBros.V_WIDTH / 2) / MarioBros.PPM){
            gameCam.position.x = (MarioBros.V_WIDTH / 2) / MarioBros.PPM;
        }
        if(gameCam.position.x > ((3840 - MarioBros.V_WIDTH / 2)) / MarioBros.PPM){
            gameCam.position.x = ((3840 - MarioBros.V_WIDTH / 2) / MarioBros.PPM);
        }
        if(mario.b2body.getPosition().y < -2){

        }
        gameCam.update();
        renderer.setView(gameCam);
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.render();

        b2dr.render(world, gameCam.combined);
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        mario.draw(game.batch);
        for(Enemy enemy: creator.getEnemies()){
            enemy.draw(game.batch);
        }
        for(Item item:items)item.draw(game.batch);
        for(FireBall fireBall: fireBalls) fireBall.draw(game.batch);
        game.batch.end();
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if(mario.currentState == Mario.State.DEAD && mario.getStateTimer() > 3){
            game.setScreen(new GameEndScreen(game));
            dispose();
        }

    }

    @Override
    public void resize(int width, int height) {
        gameViewPort.update(width, height);
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
        map.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
