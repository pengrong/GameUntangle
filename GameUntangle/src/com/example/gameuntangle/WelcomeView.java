package com.example.gameuntangle;

import android.content.Context;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class WelcomeView extends RelativeLayout {

	private ImageButton playNow;
	private UntangleActivity activity;
	private LayoutInflater inflater;
	
	public WelcomeView(Context context,final UntangleActivity activity) {
		super(context, null);
		this.activity = activity;
		inflater = LayoutInflater.from(context);
		View child = (View)inflater.inflate(R.layout.actvity_welcome, null);
		this.addView(child, RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
		playNow = (ImageButton) findViewById(R.id.play_now);
		playNow.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionevent) {
				if (motionevent.getAction() == MotionEvent.ACTION_DOWN)
					playNow.setImageResource(R.drawable.play_now1);
				else if (motionevent.getAction() == MotionEvent.ACTION_UP){
					playNow.setImageResource(R.drawable.play_now0);
					activity.handler.sendEmptyMessage(UntangleActivity.GAME);
				}
				return false;
			}
		});
	}

	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
}
