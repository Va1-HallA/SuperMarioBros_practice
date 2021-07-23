package com.supermario.game.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.supermario.game.MarioBros;

public class Hud implements Disposable {
    public Stage stage; // stage = a box(?)
    private Viewport viewport;
    private Integer worldTimer;
    private float timeCount;
    private Integer score; //why not use int type?

    Label countdownLabel;
    Label scoreLabel;
    Label timeLabel;
    Label levelLabel;
    Label worldLabel;
    Label marioLabel;

    @SuppressWarnings("DefaultLocale")
    public Hud(MarioBros game){
        worldTimer = 300;
        timeCount = 0;
        score = 0;
        viewport = new FitViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, game.batch);

        Table table = new Table();
        table.top();
        table.setFillParent(true); // let the table to fill the stage

        countdownLabel = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(game.font, Color.WHITE));
        scoreLabel = new Label(String.format("%06d", score), new Label.LabelStyle(game.font, Color.WHITE));
        timeLabel = new Label("TIME", new Label.LabelStyle(game.font, Color.WHITE));
        levelLabel = new Label("1-1", new Label.LabelStyle(game.font, Color.WHITE));
        worldLabel = new Label("WORLD", new Label.LabelStyle(game.font, Color.WHITE));
        marioLabel = new Label("MARIO", new Label.LabelStyle(game.font, Color.WHITE));
        table.add(marioLabel).expandX().padTop(10);
        table.add(worldLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.row();
        table.add(scoreLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(countdownLabel).expandX();

        stage.addActor(table);
    }

    @SuppressWarnings("DefaultLocale")
    public void update(float deltaTime){
        timeCount += deltaTime;
        if(timeCount >= 1){
            worldTimer --;
            countdownLabel.setText(String.format(("%03d"), worldTimer));
            timeCount = 0;

        }
    }

    @SuppressWarnings("DefaultLocale")
    public void addScore(int value){
        score += value;
        scoreLabel.setText(String.format(("%06d"), score));
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
