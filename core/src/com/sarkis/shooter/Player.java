package com.sarkis.shooter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.sarkis.shooter.GameScreen;

import static com.sarkis.shooter.GameScreen.ScreenHeight;
import static com.sarkis.shooter.GameScreen.ScreenWidth;
import static com.sarkis.shooter.GameScreen.bullets;


public class Player {
    private Rectangle PlayerRect;
    private Texture PlayerImage;
    private Sound GunShot = Gdx.audio.newSound(Gdx.files.internal("gunshot.wav"));
    private Sound BulletHit = Gdx.audio.newSound(Gdx.files.internal("bullethit.mp3"));

    private float velocity;
    private double angle;

    public Rectangle getRect() {
        return PlayerRect;
    }
    public final String type; // can be either "friend" or "enemy"

    Player(float x, float y, Texture PlayerImage, String PlayerType, float PlayerWidth, float PlayerHeight) {

        this.PlayerRect = new Rectangle(x, y, PlayerWidth, PlayerHeight);
        this.PlayerImage = PlayerImage;
        this.type = PlayerType;
        this.velocity = 15;

    }

    // this updates the player’s rectangle’s coordinates
    public void update(float deltaTime, float FriendX, float FriendY) {
        // get angle
        this.angle = Math.atan2((FriendY-PlayerRect.getY()), (FriendX-PlayerRect.getX()));
        PlayerRect.setX(PlayerRect.getX() + (float) (velocity * deltaTime * Math.cos(angle)));
        PlayerRect.setY(PlayerRect.getY() + (float) (velocity * deltaTime * Math.sin(angle)));
    }

    public void setX(float x) {
        this.getRect().x += x * Gdx.graphics.getDeltaTime();

    }

    public void setY(float y) {
        this.getRect().y += y * Gdx.graphics.getDeltaTime();

    }

    public Texture getPlayerImage() {
        return PlayerImage;
    }

    // friendly Player fires
    // enemy Players do not fire
    public bullet fire(float x, float y) {
        GunShot.play();
        return new bullet(PlayerRect.x, PlayerRect.y, x, y, "bullet");
    }

    // enemy Player explodes when bullet collides with it
    // friendly player does not explode
    public void explode() {
        BulletHit.play();
        for(int i = 0; i < 5; i++) {
            float xPos = MathUtils.random(0, ScreenWidth);
            float yPos = MathUtils.random(0, ScreenHeight);
            bullet particle = new bullet( this.getRect().x, this.getRect().y, xPos, yPos, "particle" );
            bullets.add(particle);
        }

    }



}
