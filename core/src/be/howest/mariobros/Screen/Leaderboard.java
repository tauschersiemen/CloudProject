package be.howest.mariobros.Screen;

import be.howest.mariobros.Highscore;
import be.howest.mariobros.MarioBros;
import be.howest.mariobros.Scenes.Hud;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class Leaderboard implements Screen{
    private Viewport viewport;
    private Stage stage;

    private MarioBros game;
    private Array<Highscore> highscores;

    public Leaderboard(MarioBros game){
        this.game = game;
        viewport = new FitViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((MarioBros)game).batch);

        this.game = game;
        viewport = new FitViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((MarioBros)game).batch);


        try{



            String url = "https://us-central1-mariobros-187710.cloudfunctions.net/getTopHighscores";
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "mozilla/5.0");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            String urlParameters = "number=10";

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);



            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;


            while ((inputLine = in.readLine()) != null) {
                JsonValue json = new JsonReader().parse(inputLine);
                highscores = new Array<Highscore>();
                JsonValue highscoresjson = json.get("highscores");
                for (JsonValue highscoresJson : highscoresjson.iterator()) // iterator() returns a list of children
                {
                    Highscore newHighscore = new Highscore();
                    newHighscore.setName(highscoresJson.getString("username"));
                    newHighscore.setScore(highscoresJson.getString("score"));
                    System.out.println(newHighscore);
                    highscores.add(newHighscore);

                }

            }
            in.close();

            //print result


        }catch(Exception e){

        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        int xName = 500;
        int xScore = 700;
        int y = 500;
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        BitmapFont font = new BitmapFont();
        game.batch.begin();
        font.draw(game.batch, "Leaderboard", 500, 525);
        font.draw(game.batch, "Click to go to back to the menu", 500, 200);
        for (Highscore highscore : highscores) {
            font.draw(game.batch, highscore.getName(), xName, y);
            font.draw(game.batch, highscore.getScore(), xScore, y);
            y -= 15;
        }

        game.batch.end();
        if(Gdx.input.justTouched()) {
            game.setScreen(new MenuScreen(game));
        }

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
        stage.dispose();
    }

    public void getHighscores(int number){
        try{



            String url = "https://us-central1-mariobros-187710.cloudfunctions.net/getTopHighscores";
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "mozilla/5.0");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            String urlParameters = "number=10";

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            System.out.println(response.toString());

        }catch(Exception e){

        }
    }
}