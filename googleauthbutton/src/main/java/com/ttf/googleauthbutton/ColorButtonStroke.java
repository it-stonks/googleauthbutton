package com.ttf.googleauthbutton;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ColorButtonStroke extends SurfaceView implements SurfaceHolder.Callback {

  private static final String TAG = "GoogleAuthButton";

  public ColorButtonStroke(Context context) {
    super(context);
    parseAttributes(context, null, 0, 0);
    init();
  }

  public ColorButtonStroke(Context context, AttributeSet attrs) {
    super(context, attrs);
    parseAttributes(context, attrs, 0, 0);
    init();
  }

  public ColorButtonStroke(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    parseAttributes(context, attrs, defStyleAttr, 0);
    init();
  }

  public ColorButtonStroke(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    parseAttributes(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private final int left = 10;
  private final int top = 10;
  private int right = 800;
  private int bottom = 200;

  private int strokeStartX = left;
  private int strokeStartY = top;

  private int speed = 5;

  private int accelerateMin = 0;
  private int accelerateMax = 10;

  private final int radius = 20;

  private final RectF rect = new RectF(left, top, right, bottom);

  private final Path secondPath = new Path();

  private Handler handler;

  private @ColorInt
  int colorInBackground;
  private @ColorInt
  int colorOutBackground;

  private Paint strokeBasePaint;
  private Paint paintInBackground;
  private Paint paintOutBackground;

  private Stroke[] strokes;

  /**
   * Флаг отвечающий за одинаковую длину отрезков границы
   */
  private boolean isIdenticalStrokeLength;

  private ValueAnimator accelerateValueAnimator;

  private void parseAttributes(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    TypedArray a = context.obtainStyledAttributes(attrs,
            R.styleable.ColorButtonStroke, defStyleAttr, defStyleRes);

    colorInBackground = a.getColor(R.styleable.ColorButtonStroke_cbs_inBackground, getResources().getColor(R.color.white));
    colorOutBackground = a.getColor(R.styleable.ColorButtonStroke_cbs_outBackground, getResources().getColor(R.color.white));
    speed = a.getInt(R.styleable.ColorButtonStroke_cbs_speed, 0);

    a.recycle();
  }

  public void setStrokes(Stroke[] strokes) {
    setStrokes(strokes, true);
  }

  public void setStrokes(Stroke[] strokes, boolean isIdenticalStrokeLength) {
    this.isIdenticalStrokeLength = isIdenticalStrokeLength;
    this.strokes = strokes;

    initStrokes();
  }

  private void init() {
    initPaints();
    initStrokes();

    SurfaceHolder surfaceHolder = getHolder();
    surfaceHolder.addCallback(this);

    setFocusable(true);

    handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {

        updateStartCords();

        handler.postDelayed(this, 1000 / 60);
      }
    }, 1000 / 60);
  }

  private void initStrokes() {
    if (strokes != null) {
      int perimeter = right * 2 + bottom * 2;

      int side = perimeter / 4;

      for (Stroke stroke : strokes) {
        if (isIdenticalStrokeLength) {
          stroke.setLength(side);
        }

        if (stroke.getPaint() == null) {
          stroke.initPaint(strokeBasePaint);
        }
      }
    }
  }

  private void initPaints() {
    strokeBasePaint = new Paint() {
      {
        setStyle(Style.STROKE);
        setStrokeWidth(20);
        setPathEffect(new CornerPathEffect(radius));
        setAntiAlias(true);
        setDither(true);
      }
    };

    paintInBackground = new Paint() {
      {
        setStyle(Style.FILL);
        setStrokeWidth(6);
        setColor(colorInBackground);
        setAntiAlias(true);
        setDither(true);
      }
    };

    paintOutBackground = new Paint() {
      {
        setStyle(Style.STROKE);
        setStrokeWidth(15);
        setColor(colorOutBackground);
        setPathEffect(new CornerPathEffect(radius * 1.8f));
        setAntiAlias(true);
        setDither(true);
      }
    };
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);

    this.right = right - this.left;
    this.bottom = bottom - this.top;

    rect.left = this.left;
    rect.top = this.top;
    rect.right = this.right;
    rect.bottom = this.bottom;

    initStrokes();
  }

  private void drawSecondStroke(Canvas canvas) {
    secondPath.reset();

    int offset = 14;

    secondPath.moveTo(left - offset, top - offset);
    secondPath.lineTo(right + offset, top - offset);
    secondPath.lineTo(right + offset, bottom + offset);
    secondPath.lineTo(left - offset, bottom + offset);
    secondPath.close();

    canvas.drawPath(secondPath, paintOutBackground);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    canvas.drawColor(colorInBackground);

    drawStrokes(canvas, strokeStartX, strokeStartY, strokes);

    canvas.drawRoundRect(rect, radius, radius, paintInBackground);

    drawSecondStroke(canvas);
  }

  private void updateStartCords() {

    if (strokeStartX == rect.left) {
      // начало слева
      strokeStartY -= speed;
      if (strokeStartY < rect.top) {
        strokeStartY = (int) rect.top;
        strokeStartX += speed;
      } else if (strokeStartY > rect.bottom && speed < 0) {
        strokeStartY = (int) rect.bottom;
        strokeStartX += speed;
      }
    } else if (strokeStartX == rect.right) {
      // начало справа
      strokeStartY += speed;
      if (strokeStartY > rect.bottom) {
        strokeStartY = (int) rect.bottom;
        strokeStartX -= speed;
      } else if (strokeStartY < rect.top && speed < 0) {
        strokeStartY = (int) rect.top;
        strokeStartX -= speed;
      }
    } else if (strokeStartY == rect.top) {
      // начало сверху
      strokeStartX += speed;
      if (strokeStartX > rect.right) {
        strokeStartX = (int) rect.right;
        strokeStartY += speed;
      } else if (strokeStartX < rect.left && speed < 0) {
        strokeStartX = (int) rect.left;
        strokeStartY += speed;
      }
    } else if (strokeStartY == rect.bottom) {
      // начало снизу
      strokeStartX -= speed;
      if (strokeStartX < rect.left) {
        strokeStartX = (int) rect.left;
        strokeStartY -= speed;
      } else if (strokeStartX > rect.right && speed < 0) {
        strokeStartX = (int) rect.right;
        strokeStartY -= speed;
      }
    }
  }

  private void drawStrokes(Canvas canvas, int x, int y, Stroke[] strokes) {
    if (strokes == null) return;

    int startX = x;
    int startY = y;

    for (Stroke stroke : strokes) {
      Path path = stroke.getPath();
      path.reset();
      path.moveTo(startX, startY);

      int length = stroke.getLength();

      for (int i = 0; i < 3; i++) {
        if (startX == rect.left) {
          // начало слева
          int lengthToCorner = (int) (startY - rect.top);

          startX = (int) rect.left;
          if (lengthToCorner >= length) {
            startY = startY - length;

            path.lineTo(startX, startY);
            length = 0;
          } else {
            startY = (int) rect.top;
            path.lineTo(startX, startY);
            length = length - lengthToCorner;
            startX += 1;
          }
        } else if (startX == rect.right) {
          // начало справа

          int lengthToCorner = (int) (rect.bottom - startY);

          startX = (int) rect.right;
          if (lengthToCorner >= length) {
            startY = startY + length;
            path.lineTo(startX, startY);
            length = 0;
          } else {
            startY = (int) rect.bottom;
            path.lineTo(startX, startY);
            length = length - lengthToCorner;
            startX -= 1;
          }
        } else if (startY == rect.top) {
          // начало сверху
          int lengthToCorner = (int) (rect.right - startX);

          startY = (int) rect.top;
          if (lengthToCorner >= length) {
            startX = startX + length;
            path.lineTo(startX, startY);
            length = 0;
          } else {
            startX = (int) rect.right;
            path.lineTo(startX, startY);
            length = length - lengthToCorner;
            startY -= 1;
          }

        } else if (startY == rect.bottom) {
          // начало снизу
          int lengthToCorner = (int) (startX - rect.left);

          startY = (int) rect.bottom;
          if (lengthToCorner >= length) {
            startX = startX - length;
            path.lineTo(startX, startY);
            length = 0;
          } else {
            startX = (int) rect.left;
            path.lineTo(startX, startY);
            length = length - lengthToCorner;
            startY += 1;
          }
        }
      }

      canvas.drawPath(path, stroke.getPaint());
    }


  }

  /**
   * Метод настраивает ускорение открезков обводки
   *
   * @param speed                            - скорость до которой нужно ускориться
   * @param duration                         - продолжительность изменения в мс
   * @param continueWithFloatingAcceleration - продолжать анимацию с плавающим значением ускорения
   */
  public void accelerateStroke(int speed, long duration, boolean continueWithFloatingAcceleration) {
    if (accelerateValueAnimator == null) {
      accelerateMin = 0;
      accelerateMax = speed;

      accelerateValueAnimator = ValueAnimator.ofInt(accelerateMin, accelerateMax);

      accelerateValueAnimator.addUpdateListener(animation -> this.speed = (int) animation.getAnimatedValue());

      accelerateValueAnimator.addListener(new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

          if (continueWithFloatingAcceleration) {
            accelerateMin = speed / 2;
          } else {
            accelerateMin = speed;
          }

          accelerateMax = speed;

          if ((int) accelerateValueAnimator.getAnimatedValue() == accelerateMax) {
            accelerateValueAnimator.setIntValues(accelerateMax, accelerateMin);
          } else {
            accelerateValueAnimator.setIntValues(accelerateMin, accelerateMax);
          }
          accelerateValueAnimator.start();
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
      });
    } else {
      accelerateValueAnimator.setIntValues((Integer) accelerateValueAnimator.getAnimatedValue(), accelerateMax);
    }

    accelerateValueAnimator.setDuration(duration);
    accelerateValueAnimator.start();
  }

  /**
   * Метод настраивает замедление отркзков обводки
   *
   * @param duration - продолжительность изменения
   */
  public void deaccelerateStroke(long duration) {
    if (accelerateValueAnimator != null) {
      accelerateValueAnimator.removeAllListeners();
      accelerateValueAnimator.setIntValues((Integer) accelerateValueAnimator.getAnimatedValue(), 0);
      accelerateValueAnimator.setDuration(duration);
      accelerateValueAnimator.start();
    }
  }

  private SurfaceViewThread thread;

  @Override
  public void surfaceCreated(@NonNull SurfaceHolder holder) {
    thread = new SurfaceViewThread(getHolder(), this);
    thread.setRunning(true);
    thread.start();
  }

  @Override
  public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

  }

  @Override
  public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
    boolean retry = true;
    thread.setRunning(false);
    while (retry) {
      try {
        thread.join();
        retry = false;
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

}