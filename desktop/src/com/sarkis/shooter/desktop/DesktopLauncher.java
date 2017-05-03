package com.sarkis.shooter.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.sarkis.shooter.Shooter;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Shooter";
		config.width = 800;
		config.height = 480;
		new LwjglApplication(new Shooter(), config);
	}
}
