package com.ttf.googleauthbutton;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.ttf.googleauthbutton.databinding.LayoutGoogleAuthButtonBinding;

public class GoogleAuthButton extends ConstraintLayout {
  private static final String TAG = "GoogleAuthButton";

  public GoogleAuthButton(@NonNull Context context) {
    super(context);

    init();
  }

  public GoogleAuthButton(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);

    init();
  }

  public GoogleAuthButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    init();
  }

  public GoogleAuthButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);

    init();
  }

  LayoutGoogleAuthButtonBinding binding;

  private void init() {
    LayoutInflater inflater = (LayoutInflater) getContext()
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    binding = LayoutGoogleAuthButtonBinding.inflate(inflater, this, true);

    Stroke[] strokes = new Stroke[] {
            new Stroke(0, getResources().getColor(R.color.blue_400)),
            new Stroke(0, getResources().getColor(R.color.orange_500)),
            new Stroke(0, getResources().getColor(R.color.green_500)),
            new Stroke(0, getResources().getColor(R.color.red_500)),
    };

    binding.stroke.setStrokes(strokes);

    binding.animationView.pauseAnimation();
  }

  public void startAuthProcess() {
    binding.stroke.accelerateStroke(15, 2000, true);
    binding.animationView.playAnimation();

    Animation animation = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
    animation.setFillAfter(true);

    binding.text.startAnimation(animation);
    binding.arrow.startAnimation(animation);
  }

  public void finishAuthProcess() {
    binding.stroke.deaccelerateStroke(3000);
    binding.animationView.addAnimatorListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {

      }

      @Override
      public void onAnimationEnd(Animator animation) {

      }

      @Override
      public void onAnimationCancel(Animator animation) {

      }

      @Override
      public void onAnimationRepeat(Animator animation) {
        float density = getResources().getDisplayMetrics().density;
        int padding = (int) (20 * density);

        binding.animationView.setPadding(padding, padding, padding, padding);
        binding.animationView.setAnimation("check.json");
        binding.animationView.setRepeatCount(0);
        binding.animationView.playAnimation();
        binding.animationView.removeAllAnimatorListeners();
      }
    });
  }

}
