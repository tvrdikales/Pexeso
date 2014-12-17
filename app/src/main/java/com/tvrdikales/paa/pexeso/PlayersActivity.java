package com.tvrdikales.paa.pexeso;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.tvrdikales.paa.pexeso.util.util.SystemUiHider;

import java.util.ArrayList;


public class PlayersActivity extends Activity {
    public static final String PLAYER_1_NAME = "player1Name";
    public static final String PLAYER_2_NAME = "player2Name";
    public static final String AI_PLAYER = "aiPlayer";

    CheckBox aiCheckBox;
    EditText player1NameEdit;
    EditText player2NameEdit;

    // nastavení chování aktivity - generovaný kód
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final boolean TOGGLE_ON_CLICK = true;
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    private SystemUiHider mSystemUiHider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);

        final View contentView = findViewById(R.id.fullscreen_content);

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
                            //        .translationY(visible ? 0 : mControlsHeight)
                            //        .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            //controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        aiCheckBox = (CheckBox) findViewById(R.id.player2TypeCheckBox);
        player1NameEdit = (EditText) findViewById(R.id.player1Name);
        player2NameEdit = (EditText) findViewById(R.id.player2Name);

        player1NameEdit.setText(getIntent().getStringExtra(PLAYER_1_NAME) == null ? getResources().getString(R.string.default_player_1_name) : getIntent().getStringExtra(PLAYER_1_NAME));
        player2NameEdit.setText(getIntent().getStringExtra(PLAYER_2_NAME) == null ? getResources().getString(R.string.default_player_2_name) : getIntent().getStringExtra(PLAYER_2_NAME));
        aiCheckBox.setChecked(getIntent().getBooleanExtra(AI_PLAYER, false));
        player2TypeCheckBoxChange(aiCheckBox);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_players, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    public void player2TypeCheckBoxChange(View view) {
        if (aiCheckBox.isChecked()) {
            player2NameEdit.setVisibility(View.INVISIBLE);
            aiCheckBox.setTextColor(getResources().getColor(R.color.white_text_color));
        } else {
            player2NameEdit.setVisibility(View.VISIBLE);
            aiCheckBox.setTextColor(getResources().getColor(R.color.disabled_text));
        }
    }

    public void playersConfirmed(View view) {
        if (player1NameEdit.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), R.string.player_1_name_invalid, Toast.LENGTH_SHORT).show();
            return;
        }
        if (player2NameEdit.getText().toString().equals("") & !aiCheckBox.isChecked()) {
            Toast.makeText(getApplicationContext(), R.string.player_2_name_invalid, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = getIntent();
        intent.putExtra(PLAYER_1_NAME, player1NameEdit.getText().toString());
        intent.putExtra(PLAYER_2_NAME, player2NameEdit.getText().toString());
        intent.putExtra(AI_PLAYER, aiCheckBox.isChecked());

        this.setResult(Activity.RESULT_OK, intent);
        this.finish();
    }
}
