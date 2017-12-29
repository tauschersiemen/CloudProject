package be.howest.mariobros.Screen;

import be.howest.mariobros.MarioBros;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import be.howest.mariobros.Scenes.Hud;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Egon on 28/12/2017.
 */

public class MyTextInputListener implements Input.TextInputListener {
    private Hud hud;
    private int level;
    private MarioBros game;
    public MyTextInputListener(MarioBros game,Hud hud, int level){
        this.hud = hud;
        this.level = level;
        this.game = game;
    }
    @Override
    public void input (String text) {
        Gdx.app.log("Your name", text);
        Gdx.app.log("Your score",  hud.getScore()*level - hud.getTime() + "");
        postHighscore(text,hud.getScore()*level - hud.getTime());
    }

    @Override
    public void canceled () {
    }

    public void postHighscore(String username, int score){
        if(score < 0){
            score = 0;
        }
        try{

            String url = "https://us-central1-mariobros-187710.cloudfunctions.net/newScore";
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "mozilla/5.0");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            String urlParameters = "username="+username+"&score="+score;

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
