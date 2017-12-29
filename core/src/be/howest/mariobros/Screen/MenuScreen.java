package be.howest.mariobros.Screen;

import be.howest.mariobros.MarioBros;
import be.howest.mariobros.Scenes.Hud;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;


import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class MenuScreen implements Screen {
    MarioBros game;
    Hud hud;

    Texture playButton;
    Texture background;
    Texture leaderbord;

    private static final int PLAY_BUTTON_WIDTH = 208;
    private static final int PLAY_BUTTON_HEIGHT = 116;
    private static final int PLAY_BUTTON_X = Gdx.graphics.getWidth()/2 - 106;
    private static final int PLAY_BUTTON_Y = Gdx.graphics.getHeight()/2 - 58;

    private static final int TROPHY_WIDTH = 127;
    private static final int TROPHY_HEIGHT = 98;
    private static final int TROPHY_X = Gdx.graphics.getWidth()/2-63;
    private static final int TROPHY_Y = Gdx.graphics.getHeight()/2 -150;


    public MenuScreen(MarioBros game) {
        this.game = game;
        playButton = new Texture("playbtn.png");
        leaderbord = new Texture("trophy.png");
        background = new Texture("Mario.png");


    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();

        game.batch.draw(background, 0 , 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.batch.draw(playButton, PLAY_BUTTON_X ,  PLAY_BUTTON_Y, PLAY_BUTTON_WIDTH, PLAY_BUTTON_HEIGHT);
        game.batch.draw(leaderbord, TROPHY_X ,  TROPHY_Y, TROPHY_WIDTH, TROPHY_HEIGHT);

        //check for play button click
        if(Gdx.input.getX() > PLAY_BUTTON_X && Gdx.input.getX() < PLAY_BUTTON_X + PLAY_BUTTON_WIDTH && Gdx.input.getY()
                > PLAY_BUTTON_Y && Gdx.input.getY()< PLAY_BUTTON_Y + PLAY_BUTTON_HEIGHT){
            if (Gdx.input.isTouched()) {
                game.setScreen(new PlayScreen(game,1,null));
            }
        }
        //check for trophy click
        if(Gdx.input.getX() > TROPHY_X && Gdx.input.getX() < TROPHY_X + TROPHY_WIDTH && Gdx.input.getY()
                > TROPHY_Y+PLAY_BUTTON_HEIGHT*2 && Gdx.input.getY()< TROPHY_Y + +PLAY_BUTTON_HEIGHT*2+TROPHY_HEIGHT){
            if (Gdx.input.isTouched()) {
                game.setScreen(new Leaderboard(game));
            }
        }

        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {

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

    public String getUsers(){
        String users = "";
        try{
            StringBuilder result = new StringBuilder();
            URL url = new URL("https://us-central1-mariobros-187710.cloudfunctions.net/getTopHighscores");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            users = result.toString();
        }catch(Exception e){

        }
        return users;

    }




}
