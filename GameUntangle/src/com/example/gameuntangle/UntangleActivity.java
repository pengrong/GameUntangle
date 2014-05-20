package com.example.gameuntangle;

import java.io.IOException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;

/**
 * 拖动蓝点，让所有线都不相交，看看你多久能解开?
 * 
 * @author think
 * 
 */
public class UntangleActivity extends Activity {

	public static final int WELCOME = 0;
	public static final int GAME = 1;
	public static final int HELP = 2;
	public static final int EXIT = 3;
	public static final String PREFERENCES_NAME = "untangle";
	private static int pointNum;

	private SharedPreferences sharePreferences;
	private WelcomeView welcomeView;
	private GameView gameView;
	private MediaPlayer gameMusic;
	private boolean isSound = true;// 是否播放声音
	private int width, height;
	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == WELCOME) {
				initWelcomeView();
			} else if (msg.what == GAME) {
				initGameView();
			} else if (msg.what == EXIT) {
				exitGameView();
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		width = getWindowManager().getDefaultDisplay().getWidth();
		height = getWindowManager().getDefaultDisplay().getHeight();
		sharePreferences = getSharedPreferences(PREFERENCES_NAME,
				Activity.MODE_PRIVATE);
		pointNum = sharePreferences.getInt("pointNum", 5);
		isSound = sharePreferences.getBoolean("isSound", false);
		gameMusic = MediaPlayer.create(this, R.raw.gamesound);
		gameMusic.setLooping(true);
		setSound(false, isSound);
		initWelcomeView();
	}

	public void onDestroy() {
		super.onDestroy();
		if (gameMusic.isPlaying())
			this.gameMusic.stop();
		gameMusic.release();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_preferences, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.item1:
			MessageDialog.showPreferencesDialog(this);
			break;
		case R.id.item2:
			initGameView();
			break;
		case R.id.item3:
			exitGameView();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	void initWelcomeView() {
		welcomeView = new WelcomeView(this, this);
		setContentView(welcomeView);
	}

	void initGameView() {
		if (gameView == null) {
			gameView = new GameView(this, this);
			setContentView(gameView);
		}
		if (pointNum > 3) {
			Rope rope = new Rope(pointNum, width, height);
			gameView.setRope(rope);
		}
	}

	void exitGameView() {
		System.exit(0);
	}

	public void setPointNum(int pointNum) {
		if (pointNum > 30) {
			pointNum = 5;
		}
		UntangleActivity.pointNum = pointNum;
		SharedPreferences.Editor editor = sharePreferences.edit();
		editor.putInt("pointNum", pointNum);
		editor.commit();
	}

	public int getPointNum() {
		return pointNum;
	}

	public boolean isSound() {
		return isSound;
	}

	/**
	 * MediaPlayer的State Diagram告诉我们， music播放完毕后，进入PlaybackComopleted状态，
	 * 同时onCompletion会被调用。 在这个状态下，只能调用stop来改变状态，然后才能prepare()，然后才能start()
	 * 
	 * @param isSound
	 */
	public void setSound(boolean isSetting, boolean isSound) {
		this.isSound = isSound;
		if (isSetting) {
			SharedPreferences.Editor editor = sharePreferences.edit();
			editor.putBoolean("isSound", isSound);
			editor.commit();
		}
		try {
			this.gameMusic.stop();
			this.gameMusic.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (isSound) {
			this.gameMusic.start();
		} else {
			this.gameMusic.stop();
		}
	}
}
