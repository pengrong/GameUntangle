package com.example.gameuntangle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MessageDialog {

	/**
	 * 显示游戏设置对话框
	 * 
	 * @param activity
	 */
	public static void showPreferencesDialog(final UntangleActivity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setIcon(R.drawable.preferences);
		builder.setTitle("游戏设置");
		RelativeLayout view = (RelativeLayout)LayoutInflater.from(activity).inflate(
				R.layout.preferences_dialog, null);
		builder.setView(view);
		final EditText pointNumEditText = (EditText) view.findViewById(R.id.pointNumEdit);
		pointNumEditText.setText(""+activity.getPointNum());
		final RadioGroup radioGroup = (RadioGroup) view
				.findViewById(R.id.radioGroup1);
		RadioButton b1 = (RadioButton)view.findViewById(R.id.radioButton1);
		RadioButton b2 = (RadioButton)view.findViewById(R.id.radioButton2);
		if(activity.isSound()){
			b1.setChecked(true);
		}else{
			b2.setChecked(true);
		}
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				String pointNum = pointNumEditText.getText().toString();
				if (pointNum == null || "".equals(pointNum)) {
					Toast.makeText(activity, "请输入5~30之间的整数。", Toast.LENGTH_LONG)
							.show();
					return;
				}
				boolean isSound = false;
				int num = Integer.parseInt(pointNum);
				activity.setPointNum(num);
				activity.handler.sendEmptyMessage(UntangleActivity.GAME);
				int radioId = radioGroup.getCheckedRadioButtonId();
				isSound = (radioId == R.id.radioButton1) ? true : false;
				activity.setSound(true, isSound);
			}
		});
		builder.show();
	}

	/**
	 * 显示胜利以后的对话框
	 * 
	 * @param activity
	 */
	public static void showWinDialog(final UntangleActivity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setIcon(R.drawable.xixi);
		builder.setTitle("(*^__^*) 嘻嘻……");
		builder.setMessage("恭喜你，胜利了！");
		builder.setPositiveButton("下一关", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				activity.setPointNum(activity.getPointNum() + 1);
				activity.handler.sendEmptyMessage(UntangleActivity.GAME);
			}
		});
		builder.setNeutralButton("重玩", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				activity.handler.sendEmptyMessage(UntangleActivity.GAME);
			}
		});
		builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				activity.handler.sendEmptyMessage(UntangleActivity.EXIT);
			}
		});
		builder.show();
	}
}
