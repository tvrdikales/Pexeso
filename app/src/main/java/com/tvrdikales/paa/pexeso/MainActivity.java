package com.tvrdikales.paa.pexeso;

import com.tvrdikales.paa.pexeso.util.Game;
import com.tvrdikales.paa.pexeso.util.SystemUiHider;
import com.tvrdikales.paa.pexeso.util.util.UnnableToStartException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainActivity extends Activity {
    public static final int PAIRS_COUNT_SETTING_REQUEST = 100;
    public static final int PLAYERS_SETTINGS_REQUEST = 200;
    public static final int GAME_RESULT = 300;

    public Game actualGame = new Game();

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final View contentView = findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                // mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            // controlsView.animate()
                            // .translationY(visible ? 0 : mControlsHeight)
                            // .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            //  controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public void startGame(View view){
        try{
            actualGame.startGame();
            Intent intent = new Intent(this,TableActivity.class);
            intent.putExtra(TableActivity.GAME_SETTINGS, actualGame);
            startActivityForResult(intent, GAME_RESULT);
        }catch(UnnableToStartException ex){
            Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public void difficultyChange(View view) {
        Intent intent = new Intent(this, DifficultyActivity.class);
        if (actualGame.getPairsCount() != null)
            intent.putExtra(DifficultyActivity.DEFAULT_SEEKBAR_VALUE, actualGame.getPairsCount());
        startActivityForResult(intent, PAIRS_COUNT_SETTING_REQUEST);
    }

    public void playersChange(View view) {
        Intent intent = new Intent(this, PlayersActivity.class);
        if (actualGame.getPlayer1Name() != null)
            intent.putExtra(PlayersActivity.PLAYER_1_NAME, actualGame.getPlayer1Name());
        if (actualGame.getPlayer2Name() != null)
            intent.putExtra(PlayersActivity.PLAYER_2_NAME, actualGame.getPlayer2Name());
        if (actualGame.getPlayer2AI() != null)
            intent.putExtra(PlayersActivity.AI_PLAYER, actualGame.getPlayer2AI());
        startActivityForResult(intent, PLAYERS_SETTINGS_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PLAYERS_SETTINGS_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    actualGame.setPlayer1Name(data.getStringExtra(PlayersActivity.PLAYER_1_NAME));
                    actualGame.setPlayer2Name(data.getStringExtra(PlayersActivity.PLAYER_2_NAME));
                    actualGame.setPlayer2AI(data.getBooleanExtra(PlayersActivity.AI_PLAYER, false));
                    ((TextView)findViewById(R.id.playersTextView)).setText(actualGame.getPlayer2AI().booleanValue()?"1+AI":"2");
                }
                return;

            case PAIRS_COUNT_SETTING_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    int pairsCount = data.getIntExtra(DifficultyActivity.PAIRS_COUNT_RESULT, 0);
                    actualGame.setPairsCount(pairsCount);
                    if (pairsCount != 0) {
                        TextView pairsView =(TextView)findViewById(R.id.pairsTextView);
                        pairsView.setText(pairsCount + "");
                    }
                }
                return;
        }
    }
}
