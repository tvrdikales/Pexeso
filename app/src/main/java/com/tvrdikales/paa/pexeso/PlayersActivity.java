package com.tvrdikales.paa.pexeso;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
    public static final String PLAYERS = "players";

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

        ArrayList<Player> players = (ArrayList<Player>) getIntent().getSerializableExtra(PLAYERS);

        if (players.size() == 0) {
            player1NameEdit.setText(getResources().getString(R.string.default_player_1_name));
            player2NameEdit.setText(getResources().getString(R.string.default_player_2_name));
            aiCheckBox.setChecked(false);
        } else {
            player1NameEdit.setText(players.get(0).getName());
            player2NameEdit.setText(players.get(1).getName());
            aiCheckBox.setChecked(players.get(1).isAI());
        }
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
        ArrayList<Player> result = new ArrayList<>();
        result.add(new Player(Color.GREEN, player1NameEdit.getText().toString(), false));
        result.add(new Player(Color.YELLOW, player2NameEdit.getText().toString(), aiCheckBox.isChecked()));
        intent.putExtra(PLAYERS, result);

        this.setResult(Activity.RESULT_OK, intent);
        this.finish();
    }
}
