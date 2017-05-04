package com.sarkis.shooter;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;


public class GameScreen implements Screen {
    final Shooter game;

    static int ScreenWidth = 800;
    static int ScreenHeight = 480;
    int PlayerWidth = 64;
    int PlayerHeight = 64;
    Texture FriendImage;
    Texture EnemyImage;
    Sound BulletHit;
    Music BackgroundMusic;
    OrthographicCamera camera;
    static Array<Player> Players;
    static Array<bullet> bullets;
    Vector3 touchPos;
    Player FriendPlayer;
    final int maxEnemies = 10;
    int numEnemies = 10; // contains number of enemies on screen
    int EnemiesKilled = 0;

    public GameScreen(final Shooter gam) {
        this.game = gam;

        // load the images for the friend and enemy
        FriendImage = new Texture(Gdx.files.internal("friend.png"));
        EnemyImage = new Texture(Gdx.files.internal("enemy.png"));

        // load the background music and put it on loop
        BackgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("background_music.wav"));
        BackgroundMusic.setLooping(true);

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // initialize bullets array
        bullets = new Array<bullet>();

        // initialize Players array
        Players = new Array<Player>();

        // create friend Player, set him to middle of screen
        FriendPlayer = new Player(ScreenWidth/2 - PlayerWidth/2, ScreenHeight/2 - PlayerHeight/2, FriendImage, "friend", PlayerWidth, PlayerHeight);
        Players.add(FriendPlayer);

        // create enemy Players, set them around friend Player
        for(int i = 0; i < maxEnemies; i++) {
            // make sure they don't overlap with friend Player
            int x = MathUtils.random(0, ScreenWidth - PlayerWidth);
            int y = MathUtils.random(0, ScreenHeight - PlayerHeight);
            Rectangle z = new Rectangle(x, y, PlayerWidth, PlayerHeight);
            while( z.overlaps(FriendPlayer.getRect()) ) {
                x = MathUtils.random(0, ScreenWidth - PlayerWidth);
                y = MathUtils.random(0, ScreenHeight - PlayerHeight);
                z.setX(x);
                z.setY(y);
            }
            Player EnemyPlayer = new Player(x, y, EnemyImage, "enemy", PlayerWidth, PlayerHeight);
            Players.add(EnemyPlayer);
        }

    }

    @Override
    public void render(float delta) {
        // clear the screen with a white color. The
        // arguments to glClearColor are the red, green
        // blue and alpha component in the range [0,1]
        // of the color to be used to clear the screen.
        Gdx.gl.glClearColor(1, 1, 1.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);


        // begin a new batch and draw the bucket and
        // all Shooters
        game.batch.begin();
        game.font.draw(game.batch, "Enemies Killed: " + EnemiesKilled, 0, ScreenHeight);
        for (Player somePlayer : Players) {
            game.batch.draw(somePlayer.getPlayerImage(), somePlayer.getRect().x, somePlayer.getRect().y);
        }
        for (bullet someBullet : bullets) {
            game.batch.draw(someBullet.getBulletImage(), someBullet.getRect().x, someBullet.getRect().y);
        }
        game.batch.end();

        // spawn more enemies
        if(numEnemies < 5) {
            for(int i = numEnemies; i < maxEnemies; i++) {
                // make sure they don't overlap with friend Player
                int x = MathUtils.random(0, ScreenWidth - PlayerWidth);
                int y = MathUtils.random(0, ScreenHeight - PlayerHeight);
                Rectangle z = new Rectangle(x, y, PlayerWidth, PlayerHeight);
                while( z.overlaps(FriendPlayer.getRect()) ) {
                    x = MathUtils.random(0, ScreenWidth - PlayerWidth);
                    y = MathUtils.random(0, ScreenHeight - PlayerHeight);
                    z.setX(x);
                    z.setY(y);
                }
                Player EnemyPlayer = new Player(x, y, EnemyImage, "enemy", PlayerWidth, PlayerHeight);
                Players.add(EnemyPlayer);
                numEnemies++;
            }

        }

        // process user input
        if (Gdx.input.isTouched()) {
            touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bullets.add(FriendPlayer.fire(touchPos.x, touchPos.y));
        }

        // keyboard input
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) FriendPlayer.setX(-200);
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) FriendPlayer.setX(200);
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) FriendPlayer.setY(-200);
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) FriendPlayer.setY(200);

        // make sure the Friend stays within the screen bounds
        if(FriendPlayer.getRect().x < 0) FriendPlayer.getRect().x = 0;
        if(FriendPlayer.getRect().x > ScreenWidth - PlayerWidth) FriendPlayer.getRect().x = ScreenWidth - PlayerWidth;
        if(FriendPlayer.getRect().y < 0) FriendPlayer.getRect().y = 0;
        if(FriendPlayer.getRect().y > ScreenHeight - PlayerHeight) FriendPlayer.getRect().y = ScreenHeight - PlayerHeight;

        // checking for bullets colliding with enemies
        Iterator<bullet> iter = bullets.iterator();
        while (iter.hasNext()) {
            bullet curBullet = iter.next();
            curBullet.update(delta);
            if (curBullet.getRect().y + 32 < 0 || curBullet.getRect().y > ScreenHeight || curBullet.getRect().x + 32 < 0 || curBullet.getRect().x  > ScreenWidth )
                iter.remove();
            Iterator<Player> iter2 = Players.iterator();
            while (iter2.hasNext()){
                Player somePlayer = iter2.next();
                if (curBullet.getRect().overlaps(somePlayer.getRect()) && somePlayer.type == "enemy" && curBullet.type == "bullet") {
                    EnemiesKilled++;
                    numEnemies--;
                    somePlayer.explode();
                    // iter.remove();
                    iter2.remove();
                }
            }
        }

        // updating enemies
        // checking for Friendly player colliding with enemies (restart game if so)
        boolean gameOver = false;

        Iterator<Player> iter3 = Players.iterator();
        while (iter3.hasNext()) {
            Player somePlayer = iter3.next();
            if (somePlayer.type == "enemy") {
                somePlayer.update(delta, FriendPlayer.getRect().x, FriendPlayer.getRect().y);
            }
            if (somePlayer.getRect().overlaps(FriendPlayer.getRect()) && somePlayer.type == "enemy") {
                EnemiesKilled = 0;
                gameOver = true;
            }
        }

        // restart the game
        if (gameOver == true) {
            Iterator<Player> iter4 = Players.iterator();
            FriendPlayer.getRect().x = ScreenWidth/2 - PlayerWidth/2;
            FriendPlayer.getRect().y = ScreenHeight/2 - PlayerHeight/2;
            while (iter4.hasNext()) {
                Player somePlayer = iter4.next();
                if (somePlayer.type == "enemy") {
                    iter4.remove();
                    numEnemies--;
                }
            }
        }

    }


    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        // start the playback of the background music
        // when the screen is shown
        BackgroundMusic.play();
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {

    }

}