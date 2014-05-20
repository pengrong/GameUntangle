package com.example.gameuntangle;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class GameView extends RelativeLayout {
	public static int backgroundColor = Color.rgb(22, 56, 98);
	private UntangleActivity activity;
	private Paint paint_intersect = new Paint();//相交线段画笔
	private Paint paint_disjoint = new Paint();//相离线段画笔
	private Paint paint_start = new Paint();
	private Paint paint_choose = new Paint();
	private Rope rope;
	private ImageButton musicBtn;

	public void setRope(Rope rope) {
		this.rope = rope;
		invalidate();
	}
	public GameView(Context context,final UntangleActivity activity) {
		super(context);
		this.activity = activity;
		this.setBackgroundColor(backgroundColor);
		paint_intersect.setColor(Color.WHITE);
		paint_intersect.setStyle(Paint.Style.STROKE);
		paint_intersect.setStrokeWidth(2);
		paint_disjoint.setColor(Color.rgb(100, 100, 100));
		paint_disjoint.setStyle(Paint.Style.STROKE);
		paint_disjoint.setStrokeWidth(2);
		paint_start.setColor(Color.rgb(245, 247, 150));
		paint_start.setStyle(Paint.Style.FILL_AND_STROKE);
		paint_start.setStrokeWidth(2);
		paint_choose.setColor(Color.rgb(255, 255, 0));
		paint_choose.setStyle(Paint.Style.FILL_AND_STROKE);
		paint_choose.setStrokeWidth(2);
		//控制背景音乐的按钮
		musicBtn = new MusicButton(activity);
		this.addView(musicBtn); 
		setWillNotDraw(false);//RelativeLayout需要加上这个设置才能调用onDraw，View不需要
	}

	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (rope != null) {
			canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
			canvas.drawColor(backgroundColor);
			ArrayList<Rope.Line> lines = rope.getLines();
			PointF[] allPoints = rope.getAllPoints();
			for (Rope.Line l1 : lines) {
				boolean intersect = true;
				for (Rope.Line l2 : lines) {
					if (!l1.equals(l2)) {
						boolean b = rope.intersect(l1.pointIndex1,
								l1.pointIndex2, l2.pointIndex1, l2.pointIndex2);
						if (!b) {
							intersect = false;
						} else if ((l1.pointIndex1 == l2.pointIndex1 && !rope.online(l1.pointIndex1, l1.pointIndex2, l2.pointIndex2))
								|| (l1.pointIndex1 == l2.pointIndex2 && !rope.online(l1.pointIndex1, l1.pointIndex2, l2.pointIndex1))
								|| (l1.pointIndex2 == l2.pointIndex1 && !rope.online(l1.pointIndex1, l1.pointIndex2, l2.pointIndex2))
								|| (l1.pointIndex2 == l2.pointIndex2 && !rope.online(l1.pointIndex1, l1.pointIndex2, l2.pointIndex1))) {
							intersect = false;
						} else {
							intersect = true;
							break;
						}
					}
				}
				if (intersect) {
					canvas.drawLine(allPoints[l1.pointIndex1].x,
							allPoints[l1.pointIndex1].y,
							allPoints[l1.pointIndex2].x,
							allPoints[l1.pointIndex2].y, paint_intersect);
				} else {
					canvas.drawLine(allPoints[l1.pointIndex1].x,
							allPoints[l1.pointIndex1].y,
							allPoints[l1.pointIndex2].x,
							allPoints[l1.pointIndex2].y, paint_disjoint);
				}
			}
			int radius = 3;
			for (int i = 0; i < rope.getPointNum(); i++) {
				if (rope.getSelectIndex() == i) {
					canvas.drawCircle(allPoints[i].x, allPoints[i].y, radius,
							paint_choose);
				} else {
					canvas.drawCircle(allPoints[i].x, allPoints[i].y, radius,
							paint_start);
				}
			}
		}
	}

	public boolean onTouchEvent(MotionEvent event) {

		// Toast.makeText(getContext(), String.valueOf(event.getAction()),
		// Toast.LENGTH_SHORT).show();
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			float x = event.getX(), y = event.getY();
			rope.setSelectIndex(x, y);
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if (rope.getSelectIndex() > -1) {
				PointF[] points = rope.getAllPoints();
				points[rope.getSelectIndex()].x = event.getX();
				points[rope.getSelectIndex()].y = event.getY();
				this.invalidate();
			}
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			if (rope.getSelectIndex() > -1) {
				PointF[] points = rope.getAllPoints();
				points[rope.getSelectIndex()].x = event.getX();
				points[rope.getSelectIndex()].y = event.getY();
				this.invalidate();
				if (rope.win()) {
//					Toast.makeText(getContext(), "win", Toast.LENGTH_SHORT).show();
					MessageDialog.showWinDialog(activity);
				}
			} else {
				float x = event.getX(), y = event.getY();
				rope.setSelectIndex(x, y);
			}
		}
		return true;
	}
	public class MusicButton extends ImageButton{
		public MusicButton(final UntangleActivity activity) {
			super(activity);
			this.setBackgroundColor(GameView.backgroundColor);
			this.setImageResource(R.drawable.music);

			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.TRUE);
			params.width = 32;
			params.height = 32;
			this.setLayoutParams(params);
			this.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					activity.setSound(false, !activity.isSound());
				}
			});
		}
	}
}
