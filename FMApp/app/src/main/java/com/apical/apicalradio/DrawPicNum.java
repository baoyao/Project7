package com.apical.apicalradio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;

import com.example.fmapp.R;

public class DrawPicNum {

	private Context context;
	private Bitmap mBack;
	Bitmap alterBitemp;

	public DrawPicNum(Context context) {
		this.context = context;
	mBack = BitmapFactory.decodeResource(context.getResources(),
	R.drawable.number_bg);
	alterBitemp = Bitmap.createBitmap(mBack.getWidth(), mBack.getHeight(),
			mBack.getConfig());//getWidth: 395 getHeight: 123
}

	public Bitmap getStringPic(String str) {

		int width = 0;
		if (str.contains(".")) {
			if (str.length() > 4) {
				width = 0;
			} else {
				width = 27;
			}
		} else {
			if (str.length() > 3) {
				width = 0;
			} else {
				width = 27;
			}
		}

		alterBitemp.eraseColor(0); // 清空位图
		Canvas canvas = new Canvas(alterBitemp); // 创建画布
		canvas.drawBitmap(mBack, new Matrix(), null); // 在画布上绘图
		for (int i = 0; i < str.length(); i++) {
			Bitmap bitmap = getNumBitmap(str.charAt(i));
			canvas.drawBitmap(bitmap, width, 2, null); // 在画布指定位置绘图
			width += bitmap.getWidth();
		}
		return alterBitemp;
	}

	public Bitmap getIntPic(int num) {
		int width = 0;
		String strNum = Integer.toString(num);
		if (strNum.length() > 3) {
			width = 0;
		} else {
			width = 27;
		}
		alterBitemp.eraseColor(0); // 清空位图
		Canvas canvas = new Canvas(alterBitemp); // 创建画布
		canvas.drawBitmap(mBack, new Matrix(), null); // 在画布上绘图
		for (int i = 0; i < strNum.length(); i++) {
			Bitmap bitmap = getNumBitmap(strNum.charAt(i));
			canvas.drawBitmap(bitmap, width, 2, null); // 在画布指定位置绘图
			width += bitmap.getWidth();
		}
		return alterBitemp;
	}

	/**
	 * 获取使用图片显示的时间
	 * 
	 * @return
	 */
	public Bitmap getFloatPic(float num) {
		int width = 0;
		String strNum = String.format("%.1f", num); // 浮点型只要一个小数点
		if (strNum.length() > 4) {
			width = 0;
		} else {
			width = 27;
		}
		alterBitemp.eraseColor(0); // 清空位图
		Canvas canvas = new Canvas(alterBitemp); // 创建画布
		canvas.drawBitmap(mBack, new Matrix(), null); // 在画布上绘图
		for (int i = 0; i < strNum.length(); i++) {
			Bitmap bitmap = getNumBitmap(strNum.charAt(i));
			canvas.drawBitmap(bitmap, width, 2, null); // 在画布指定位置绘图
			width += bitmap.getWidth();
			System.out.println("char = " + strNum.charAt(i) + " width = "
					+ width);
		}
		return alterBitemp;
	}

	/**
	 * 获取相应的数字图片
	 * 
	 * @param number
	 * @return
	 */
	private Bitmap getNumBitmap(char ch) {
		Bitmap bitmap = null;
		switch (ch) {
		case '0':
			bitmap = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.zero);
			break;
		case '1':
			bitmap = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.one);
			break;
		case '2':
			bitmap = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.twe);
			break;
		case '3':
			bitmap = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.three);
			break;
		case '4':
			bitmap = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.four);
			break;
		case '5':
			bitmap = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.five);
			break;

		case '6':
			bitmap = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.six);
			break;
		case '7':
			bitmap = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.seven);
			break;
		case '8':
			bitmap = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.eight);
			break;
		case '9':
			bitmap = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.nine);
			break;
		case '.':
			bitmap = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.dot);
			break;
		default:
			bitmap = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.zero);
			break;
		}
		return bitmap;
	}
}
