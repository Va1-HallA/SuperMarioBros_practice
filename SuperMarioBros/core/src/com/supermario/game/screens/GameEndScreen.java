package com.supermario.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.supermario.game.MarioBros;

public class GameEndScreen implements Screen {

    private MarioBros game;
    private Viewport gameViewPort;
    private Stage stage;
    private OrthographicCamera gameCam;

    public GameEndScreen(MarioBros game){
        this.game = game;
        gameCam = new OrthographicCamera();
        gameCam.setToOrtho(false, 800, 480);
        gameViewPort = new FitViewport(MarioBros.V_WIDTH / MarioBros.PPM, MarioBros.V_HEIGHT / MarioBros.PPM, gameCam);
        stage = new Stage();
//        gameCam.position.set(gameViewPort.getWorldWidth() / 2, gameViewPort.getWorldHeight() / 2, 0);
        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        Label gameoverLabel = new Label("Game Over", font);
        Label playAgainLabel = new Label("Click to play again", font);
        table.add(gameoverLabel).expandX();
        table.row();
        table.add(playAgainLabel).expandX();

        stage.addActor(table);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if(Gdx.input.justTouched()){
            game.setScreen(new PlayScreen(game));
            dispose();
        }
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        ScreenUtils.clear(0, 0, 0, 1);
        game.batch.setProjectionMatrix(gameCam.combined);
        stage.draw();
        //game.batch.begin();
//        game.font.draw(game.batch, "gg", 385, 240);
        //game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
//        gameViewPort.update(width, height);
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

    }
}
