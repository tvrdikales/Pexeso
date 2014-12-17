package com.tvrdikales.paa.pexeso;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.tvrdikales.paa.pexeso.util.Game;


public class TableActivity extends Activity {

    public static final String GAME_SETTINGS = "gameSettings";
    private Game gameSettings;
    private int[][] table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        gameSettings = (Game) getIntent().getSerializableExtra(GAME_SETTINGS);
        int cardsCount = gameSettings.getPairsCount() * 2;
        int tableHeight = (int) (Math.round(Math.sqrt(cardsCount))) + 1;
        int tableWidth = (int) (Math.round(cardsCount / tableHeight)) + 1;

        table = new int[tableHeight][tableWidth];

        TableLayout layout = (TableLayout) (findViewById(R.id.tableLayout));
        TableLayout.LayoutParams params = new TableLayout.LayoutParams();

        for (int i = 0; i < tableHeight; i++) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);

            for (int j = 0; j < tableWidth; j++) {
                table[i][j] = Math.round(i*tableWidth+j / 2);
                ImageButton btn = new ImageButton(this);
                btn.setImageResource(R.drawable.ic_launcher);
                row.addView(btn);
            }

            layout.addView(row,i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_table, menu);
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
}
