package com.ttf.googleauthbutton;


import android.graphics.Paint;
import android.graphics.Path;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

public class Stroke {
  private static final String TAG = "Stroke";

  private Path path = new Path();
  private int length;
  private Paint paint;

  private @ColorInt int color;

  public Stroke(int length, Paint paint) {
    this.length = length;
    this.paint = paint;
  }

  public Stroke(int length, int color) {
    this.length = length;
    this.color = color;
  }

  public Path getPath() {
    return path;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public void initPaint(@NonNull Paint basePaint) {
    paint = new Paint(basePaint);
    paint.setColor(color);
  }

  public Paint getPaint() {
    return paint;
  }
}
