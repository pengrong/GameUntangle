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
	 * ��ʾ��Ϸ���öԻ���
	 * 
	 * @param activity
	 */
	public static void showPreferencesDialog(final UntangleActivity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setIcon(R.drawable.preferences);
		builder.setTitle("��Ϸ����");
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
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				String pointNum = pointNumEditText.getText().toString();
				if (pointNum == null || "".equals(pointNum)) {
					Toast.makeText(activity, "������5~30֮���������", Toast.LENGTH_LONG)
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
	 * ��ʾʤ���Ժ�ĶԻ���
	 * 
	 * @param activity
	 */
	public static void showWinDialog(final UntangleActivity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setIcon(R.drawable.xixi);
		builder.setTitle("(*^__^*) ��������");
		builder.setMessage("��ϲ�㣬ʤ���ˣ�");
		builder.setPositiveButton("��һ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				activity.setPointNum(activity.getPointNum() + 1);
				activity.handler.sendEmptyMessage(UntangleActivity.GAME);
			}
		});
		builder.setNeutralButton("����", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				activity.handler.sendEmptyMessage(UntangleActivity.GAME);
			}
		});
		builder.setNegativeButton("�˳�", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				activity.handler.sendEmptyMessage(UntangleActivity.EXIT);
			}
		});
		builder.show();
	}
}
