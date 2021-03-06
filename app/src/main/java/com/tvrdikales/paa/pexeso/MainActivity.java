package com.tvrdikales.paa.pexeso;

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

import com.tvrdikales.paa.pexeso.util.SystemUiHider;

import java.util.ArrayList;

public class MainActivity extends Activity {
    public static final int PAIRS_COUNT_SETTING_REQUEST = 100;
    public static final int PLAYERS_SETTINGS_REQUEST = 200;
    public static final int GAME_RESULT = 300;

    public Game actualGame = new Game();

    //generovaný kód
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final boolean TOGGLE_ON_CLICK = true;
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
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

    public void startGame(View view) {
        try {
            actualGame.startGame();
            Intent intent = new Intent(this, TableActivity.class);
            intent.putExtra(TableActivity.GAME_SETTINGS, actualGame);
            startActivityForResult(intent, GAME_RESULT);
        } catch (UnnableToStartException ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void difficultyChange(View view) {
        Intent intent = new Intent(this, DifficultyActivity.class);
        if (actualGame.getGroupsCount() != null) intent.putExtra(DifficultyActivity.DEFAULT_SEEKBAR_VALUE, actualGame.getGroupsCount());
        startActivityForResult(intent, PAIRS_COUNT_SETTING_REQUEST);
    }

    public void playersChange(View view) {
        Intent intent = new Intent(this, PlayersActivity.class);
        intent.putExtra(PlayersActivity.PLAYERS, actualGame.getPlayers());
        startActivityForResult(intent, PLAYERS_SETTINGS_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PLAYERS_SETTINGS_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    actualGame.setPlayers((ArrayList<Player>) data.getSerializableExtra(PlayersActivity.PLAYERS));
                    int human=0,ai=0;
                    for(Player p :actualGame.getPlayers()){
                        if(p.isAI()){ai++;}else{human++;}
                    }
                    ((TextView) findViewById(R.id.playersTextView)).setText(String.format("%d + %d AI",human,ai));
                }
                return;

            case PAIRS_COUNT_SETTING_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    int pairsCount = data.getIntExtra(DifficultyActivity.PAIRS_COUNT_RESULT, 0);
                    actualGame.setGroupsCount(pairsCount);
                    if (pairsCount != 0) {
                        TextView pairsView = (TextView) findViewById(R.id.pairsTextView);
                        pairsView.setText(pairsCount + "");
                    }
                }
                return;
        }
    }
}
