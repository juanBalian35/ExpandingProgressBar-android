package expandingprogressbar.megapho.com.expandingprogressbar_example;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import expandingprogressbar.megapho.com.expandingprogressbar.ExpandingProgressBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView loadingTV = findViewById(R.id.tv_loading);

        final TextView helloWorldTV = findViewById(R.id.tv);
        helloWorldTV.setVisibility(View.INVISIBLE);

        final ExpandingProgressBar e = findViewById(R.id.epb);
        e.start();

        e.setOnExpandListener(new ExpandingProgressBar.onExpandListener() {
            @Override
            public void preExpand() {
                Animation fadeOut = new AlphaAnimation(1, 0);
                fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
                fadeOut.setDuration(250);

                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        loadingTV.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });

                loadingTV.startAnimation(fadeOut);
            }

            @Override
            public void postExpand() {
                helloWorldTV.setVisibility(View.VISIBLE);
            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                e.expand();
            }
        }, 2750);
    }
}
