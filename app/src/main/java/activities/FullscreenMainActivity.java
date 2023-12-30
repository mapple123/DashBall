package activities;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import classBallKorb.Ball;
import classBallKorb.Korb;
import de.janstudio.dashball.R;


public class FullscreenMainActivity extends AppCompatActivity {

    private TextView tvScore, tvZeit, tvHighscore;
    private int punkte = 0, highscore = 0;
    private ImageView imgBall, imgKorb;
    private Handler handler = new Handler(), handlerTime = new Handler();
    private Timer timer = new Timer(), timerTime  = new Timer();
    private float downX;
    private float downY;
    private int screenHeight;
    private int screenWidth;
    private final String GRUEN = "Grün";
    private final String GELB = "Gelb";
    private final String BLAU = "Blau";
    private final String ROT = "Rot";
    private Dialog myPopup;
    private  int seconds = 0;

    private String rfarbe;
    private Ball ball = null;

    private float korbRot = 0;
    private ImageButton imageButtonPause;
    private boolean gover= false, isPaus = false;
    private SharedPreferences sharedpreferences;
    private final String HIGHSCOREPREF = "Highscore";
    private final String HSTRING = "Hscore";

    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen_main);
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.frameLayout);

        tvScore = (TextView) findViewById(R.id.textViewScore);
        tvZeit = (TextView) findViewById(R.id.textViewTime);
        tvHighscore = (TextView) findViewById(R.id.textViewHScore);
        tvScore.setText("Punkte:"+ punkte);
        imgBall = (ImageView) findViewById(R.id.imageViewBall);
        imgKorb = (ImageView) findViewById(R.id.imageViewQuadrat);
        imageButtonPause = (ImageButton) findViewById(R.id.imageButtonPause);
        sharedpreferences = getSharedPreferences(HIGHSCOREPREF, Context.MODE_PRIVATE);
        imageButtonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!gover){
                    onPauseFunction();
                    showPauseMenu();
                    isPaus = true;
                }
            }
        });
        imgKorb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (korbRot >= 270){
                    korbRot = 0;
                }else{
                    korbRot += 90;
                }


                float startDegree =  korbRot;
                float endDegree =  startDegree + 90;
                RotateAnimation anim = new RotateAnimation(startDegree, endDegree, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);

                anim.setInterpolator(new LinearInterpolator());
                anim.setRepeatCount(0);
                anim.setFillAfter(true);
                anim.setDuration(300);
                imgKorb.startAnimation(anim);
                imgKorb.setRotation(startDegree-endDegree);
            }
        });

        rfarbe = randomFarbe();
        ball = createBall(setImageView(rfarbe), rfarbe);
        WindowManager wm = getWindowManager();
        Display d = wm.getDefaultDisplay();
        Point size = new Point();
        d.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        imgBall.setX(0.0f);
        imgBall.setY(screenHeight + 80f);

        onResumeFunction();
        sharedpreferences = getPreferences(MODE_PRIVATE);
        highscore = sharedpreferences.getInt(HSTRING, 0);
        tvHighscore.setText("Highscore:"+ highscore);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }

    private void hide() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
    private void changePos(){
            downY += 10;
        if(imgBall.getY() > screenHeight){
            downX = (screenWidth - imgBall.getDrawable().getIntrinsicWidth()) / 2;
            downY = 0f;
        }
        imgBall.setX(downX);
        imgBall.setY(downY);
        Rect rc1 = new Rect();
        imgKorb.getHitRect(rc1);
        Rect rc2 = new Rect();
        imgBall.getHitRect(rc2);
        rc2.bottom =+ rc2.bottom -100;

        if (Rect.intersects(rc1, rc2) ) {
            downY = 0;
            Korb korb = new Korb();

            if (korbRot == 0) {
                korb.setFrabe(ROT);
            } else if (korbRot == 90) {
                korb.setFrabe(GELB);
            } else if (korbRot == 180) {
                korb.setFrabe(BLAU);
            } else if (korbRot == 270) {
                korb.setFrabe(GRUEN);
            } else {
                korbRot = 0;
            }
            if (korb != null && ball.getFarbe().equals(korb.getFrabe())) {
                punkte++;
                tvScore.setText("Punkte:" + punkte);
                if (punkte > highscore) {
                    highscore++;
                    tvHighscore.setText("Highscore:" + highscore);
                }

                String randomFarbe = randomFarbe();
                rfarbe = randomFarbe;
                ball = createBall(setImageView(rfarbe), randomFarbe);
            } else {
                timerTime.cancel();
                timer.cancel();
                gover = true;
                isPaus = false;
                saveHighScore();

                final Dialog myPopup = new Dialog(this);
                myPopup.setContentView(R.layout.mypopup);
                myPopup.setCancelable(false);
                myPopup.show();

                ImageButton imageButton = myPopup.findViewById(R.id.imageButton);
                ImageButton button = myPopup.findViewById(R.id.button);

                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gover = false;
                        rfarbe = randomFarbe();
                        ball = createBall(setImageView(rfarbe), rfarbe);
                        onResumeFunction();
                        myPopup.cancel();
                        punkte = 0;
                        tvScore.setText("Punkte:" + punkte);
                        seconds = 0;
                        korbRot = 0;
                        float startDegree = korbRot;
                        float endDegree = startDegree;
                        RotateAnimation anim = new RotateAnimation(startDegree, endDegree,
                                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

                        anim.setInterpolator(new LinearInterpolator());
                        anim.setRepeatCount(0);
                        anim.setFillAfter(true);
                        anim.setDuration(500);
                        imgKorb.startAnimation(anim);
                        imgKorb.setRotation(korbRot);

                    }

                });
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myPopup.dismiss();
                        FullscreenMainActivity.this.finish();
                    }
                });
            }
            imgBall.setY(downY);
        }
    }

    private Ball createBall(Drawable img, String farbe){
        Ball ball = new Ball(img, farbe);
        return ball;
    }
    private String randomFarbe(){
        String [] farben = {"Grün", "Gelb", "Blau", "Rot"};
        Random random = new Random();
        int zz = random.nextInt(farben.length);
        return farben[zz];
    }

    private Drawable setImageView(String farbe){
        Drawable[] imageViews = {getResources().getDrawable(R.drawable.red_ball), getResources().getDrawable(R.drawable.blue_ball), getResources().getDrawable(R.drawable.green_ball), getResources().getDrawable(R.drawable.yellow_ball)};
        switch(farbe){
            case ROT: imgBall.setImageDrawable(imageViews[0]);
                return imageViews[0];
            case BLAU: imgBall.setImageDrawable(imageViews[1]);
                return imageViews[1];
            case GRUEN: imgBall.setImageDrawable(imageViews[2]);
                return imageViews[2];
            case GELB: imgBall.setImageDrawable(imageViews[3]);
                return imageViews[3];
            default: return null;
        }
    }

    private void onPauseFunction(){
        timer.cancel();
        timerTime.cancel();
    }
    private void onResumeFunction(){
        timer = new Timer();
        timerTime = new Timer();
        timer.schedule(new TimerTask(){

            @Override
            public void run() {
                handler.post( new Runnable() {
                    @Override
                    public void run() {
                        changePos();
                    }
                });
            }
        }, 0, 20);
        timerTime.schedule(new TimerTask() {

            @Override
            public void run(){
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        int hours = seconds / 3600;
                        int minutes = (seconds % 3600) / 60;
                        int secs = seconds % 60;

                        String time
                                = String
                                .format(Locale.getDefault(),
                                        "%d:%02d:%02d", hours,
                                        minutes, secs);
                        tvZeit.setText(time);
                        seconds++;
                    }
                });
            }
        }, 0, 1000);
    }

    protected void onPause() {
        super.onPause();
        if (!gover && !isPaus){
            onPauseFunction();
            showPauseMenu();
            isPaus = true;
        }
    }

    private void showPauseMenu(){
        myPopup = new Dialog(this);
        myPopup.setContentView(R.layout.mypopuppause);
        myPopup.setCancelable(false);
        myPopup.show();

        ImageButton imageButtonPause = myPopup.findViewById(R.id.imageButtonPPause);
        ImageButton imageButtonQuitt = myPopup.findViewById(R.id.imageButtonBeenden);
        imageButtonQuitt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myPopup.dismiss();
                FullscreenMainActivity.this.finish();
            }
        });

       imageButtonPause.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               myPopup.dismiss();
               onResumeFunction();
           }
       });
    }

    private void saveHighScore(){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(HSTRING, highscore);
        editor.apply();
    }

}