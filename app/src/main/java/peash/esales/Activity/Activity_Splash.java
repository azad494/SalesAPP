package peash.esales.Activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import Adi.esales.R;
import peash.esales.Helper.Common;

public class Activity_Splash extends Activity {

    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Common common = new Common();

        mp = MediaPlayer.create(this, R.raw.internet_warning);
        if (common.IsInternetConnected() == false) {
            mp.start();
            Toast.makeText(this, "Check your internet connection!!!", Toast.LENGTH_LONG).show();
            this.finish();
        }
        else {
          //  Blink();
            new Thread(new Runnable() {
                public void run() {
                    doWork();
                    Intent intent = new Intent(Activity_Splash.this, LogoutFragment.class);
                    startActivity(intent);
                    finish();
                }
            }).start();
        }
    }
    private void doWork() {
        mp = MediaPlayer.create(this, R.raw.intro);
        mp.start();
        for (int progress=0; progress<100; progress+=20) {
            try
            {
                Thread.sleep(900);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void Blink() {
        ImageView imageView  =  findViewById(R.id.imgLogo);
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000); //You can manage the blinking time with this parameter
        anim.setStartOffset(100);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);

    }

}
