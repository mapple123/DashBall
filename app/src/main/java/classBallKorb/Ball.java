package classBallKorb;

import android.graphics.drawable.Drawable;

public class Ball {
    private Drawable imgBall;
    private String farbe;

    public Ball(Drawable imgBall, String farbe){
        this.imgBall = imgBall;
        this.farbe = farbe;
    }

    public Drawable getImgBall() {
        return imgBall;
    }

    public void setImgBall(Drawable imgBall) {
        this.imgBall = imgBall;
    }

    public String getFarbe() {
        return farbe;
    }

    public void setFarbe(String farbe) {
        this.farbe = farbe;
    }
}
