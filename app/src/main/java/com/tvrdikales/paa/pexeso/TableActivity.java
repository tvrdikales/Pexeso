package com.tvrdikales.paa.pexeso;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.tvrdikales.paa.pexeso.util.util.SystemUiHider;

import java.util.ArrayList;
import java.util.Random;

public class TableActivity extends Activity {

    public static final String GAME_SETTINGS = "gameSettings";
    public static final int MINIMAL_CARD_SIZE = 70;
    public static final int CARD_MARGIN = 4;

    private static enum GameStates {
        ROUND_STARTED, HIDE_CARDS, WAIT
    }

    private Game gameSettings;
    private Player actualPlayer;
    private GameStates state = GameStates.ROUND_STARTED;
    private ArrayList<Card> cards = new ArrayList<>();
    private ArrayList<Card> showedCards = new ArrayList<>();

    // nastavení chování aktivity - generovaný kód
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final boolean TOGGLE_ON_CLICK = true;
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    private SystemUiHider mSystemUiHider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

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

        //vlastní kód třídy

        gameSettings = (Game) getIntent().getSerializableExtra(GAME_SETTINGS);

        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();

        int cardsCount = gameSettings.getGroupsCount() * 2;

        // snaha o rozložení karet tak, aby tvořily obdelník. Nejmenší možný počet řádků je 2
        int cardsVerticalMaxCount = (int) (Math.round(Math.sqrt(cardsCount)));
        while (cardsCount % cardsVerticalMaxCount != 0 || (screenHeight / cardsVerticalMaxCount) < (MINIMAL_CARD_SIZE + 2 * CARD_MARGIN)) {
            if ((cardsCount / (cardsVerticalMaxCount - 1)) > (Math.floor(screenWidth / (MINIMAL_CARD_SIZE + 2 * CARD_MARGIN)))) {
                // karty není možné vykreslit tak, aby byly zarovnány do obdelníka a přitom se vešly na display. Na posledním řádku tedy nebudou všechny karty
                break;
            } else {
                cardsVerticalMaxCount--;
            }
        }

        cards = drawCards((int) Math.floor(cardsCount / cardsVerticalMaxCount), 2, gameSettings.getGroupsCount());
        mixCards(cards, 1);

        for(Player p : gameSettings.getPlayers()){
            if(p.isAI()){p.inicializeAI(cards,gameSettings.getGroupsCount());}
        }
        switchPlayer(gameSettings.getPlayers().get(0));
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

    public ArrayList<Card> drawCards(int cardsInLine, int cardsInGroup, int countOfGroups) {
        // výpočet velikosti karty
        int linesCount = (int)Math.ceil((cardsInGroup * countOfGroups) / cardsInLine);

        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();

        int cardWidth = screenWidth/cardsInLine;
        int cardHeight = screenHeight/linesCount;

        ArrayList<Card> list = new ArrayList<>();

        TableLayout layout = new TableLayout(getApplicationContext());
        TableLayout.LayoutParams params = new TableLayout.LayoutParams();
        params.setMargins(-8, -8, -8, -8);
        layout.setLayoutParams(params);
        layout.requestLayout();

        int val = 0;
        TableRow row = null;
        for (int i = 0; i < cardsInGroup * countOfGroups; i++) {
            // karta začínající nový řádek, nutno nový řádek vytvořit
            if ((i % cardsInLine) == 0) {
                row = new TableRow(this);
                TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
                rowParams.setMargins(-8, -8, -8, -8);
                row.setLayoutParams(rowParams);
                row.requestLayout();
                layout.addView(row);
            }

            if (i % cardsInGroup == 0) {
                val++;
            }

            final Card card = new Card(this, val);
            card.setImageResource(R.drawable.ic_launcher);
            card.setMinimumWidth(cardWidth);
            card.setMinimumHeight(cardHeight);
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    move(card);
                }
            });
            row.addView(card);
            list.add(card);
        }

        RelativeLayout rootView = (RelativeLayout) findViewById(R.id.fullscreen_content);
        rootView.addView(layout);

        return list;
    }

    public void mixCards(ArrayList<Card> cards, int swapCyclesCount) {
        Random r = new Random();
        while (swapCyclesCount > 0) {
            for (Card c : cards) {
                int index = r.nextInt(cards.size());
                c.swapCard(cards.get(index));
            }
            swapCyclesCount--;
        }
    }

    public void move(final Card card) {
        switch (state) {
            case ROUND_STARTED:
                if (showedCards.contains(card)) break;
                showCard(card);
                if (gameSettings.getGroupSize() == showedCards.size()) {
                    roundCompleted();
                }
                break;
            case HIDE_CARDS:
                if (showedCards.contains(card)) {
                    hideCards();
                    switchPlayer(gameSettings.getNextPlayer(actualPlayer));
                    if (actualPlayer.isAI()) {
                        state = GameStates.WAIT;
                        aiPlayerTurn();
                    } else {
                        state = GameStates.ROUND_STARTED;
                    }
                }
                break;

            case WAIT:
                // AI player´s turn
                break;
        }
    }

    public void showCard(Card cardToShow) {
        showedCards.add(cardToShow);
        cardToShow.showCard();
        for (Player player : gameSettings.getPlayers()) {
            if(player.isAI())player.cardShowed(cardToShow);
        }
    }

    public void groupFounded(int discoveredGroupId) {
        for (Player player : gameSettings.getPlayers()) {
            if(player.isAI())player.groupFounded(discoveredGroupId);
        }
    }

    public void switchPlayer(Player newPlayer) {
        actualPlayer = newPlayer;
        findViewById(R.id.fullscreen_content).setBackgroundColor(newPlayer.getColor());
        final Toast toast = Toast.makeText(getApplicationContext(), "Na tahu je " + newPlayer.getName(), Toast.LENGTH_SHORT);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 800);
    }

    public void hideCards(){
        for (Card c : showedCards) {
            c.hideCard();
        }
        showedCards.clear();
    }
    public void aiPlayerTurn() {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < gameSettings.getGroupSize(); i++) {
                    try {
                        boolean showedSameCards = true;
                        for (Card c : showedCards) {
                            if (showedCards.get(0).getGroup() != c.getGroup()) {
                                showedSameCards = false;
                            }
                        }
                        Card aiSelectedCard = null;
                        if (showedSameCards) {
                            // všechny karty ze stejné skupiny
                            if (showedCards.size() == 0) {
                                int biggestChanceToFindGroupId = -1;
                                float biggestChanceToFindChance = 0;
                                for(int j=1;j<=gameSettings.getGroupsCount();j++){
                                    float actualGroupChance = actualPlayer.chanceForGroup(j,gameSettings.getGroupSize());
                                    if(actualGroupChance>biggestChanceToFindChance){
                                        biggestChanceToFindGroupId = j;
                                        biggestChanceToFindChance = actualGroupChance;
                                    }
                                }

                                if (biggestChanceToFindGroupId < 0) {
                                    aiSelectedCard = actualPlayer.getUnknownCard(showedCards);
                                } else {
                                    for (Card c : actualPlayer.getCardsFromGroup(biggestChanceToFindGroupId)) {
                                        if (!showedCards.contains(c)) {
                                            if(Math.random()<biggestChanceToFindChance){
                                                aiSelectedCard = c;
                                            }else{
                                                aiSelectedCard = actualPlayer.getUnknownCard(showedCards);
                                            }
                                            break;
                                        }
                                    }
                                }
                            } else {
                                if (actualPlayer.isDiscovered(showedCards.get(0).getGroup(), gameSettings.getGroupSize())) {
                                    float chance = actualPlayer.chanceForGroup(showedCards.get(0).getGroup(), gameSettings.getGroupSize());
                                    for (Card c : actualPlayer.getCardsFromGroup(showedCards.get(0).getGroup())) {
                                        if (!showedCards.contains(c)) {
                                            if(Math.random()<chance){
                                                aiSelectedCard = c;
                                            }else{
                                                aiSelectedCard = actualPlayer.getUnknownCard(showedCards);
                                            }
                                            break;
                                        }
                                    }
                                } else {
                                    aiSelectedCard = actualPlayer.getUnknownCard(showedCards);
                                }
                            }
                        } else {
                            // byly vybrány karty které se liší. Nelze složit celou skupinu, proto je odkryta nějaká karta o které toho víme nejméně
                            aiSelectedCard = actualPlayer.getUnknownCard(showedCards);
                        }
                        final Card selectedCard = aiSelectedCard;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showCard(selectedCard);
                            }
                        });
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        roundCompleted();
                    }
                }, 2000);
            }
        });
    }

    public void roundCompleted() {
        boolean allCardTheSame = true;
        for (int i = 1; i < showedCards.size(); i++) {
            if (!showedCards.get(0).inGroupWith(showedCards.get(i))) {
                allCardTheSame = false;
                break;
            }
        }

        if (allCardTheSame) {
            actualPlayer.addPoint();
            groupFounded(showedCards.get(0).getGroup());
            for (Card c : showedCards) {
                cards.remove(c);
                c.removeCard();
            }
            showedCards.clear();

            if (cards.isEmpty()) {
                gameOver();
                return;
            }
            if (actualPlayer.isAI()) {
                aiPlayerTurn();
            }
        } else {
            if(actualPlayer.isAI()){
                if(gameSettings.getNextPlayer(actualPlayer).isAI()){
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideCards();
                            switchPlayer(gameSettings.getNextPlayer(actualPlayer));
                            state = GameStates.WAIT;
                            aiPlayerTurn();
                        }
                    }, 2000);
                }else{
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideCards();
                            switchPlayer(gameSettings.getNextPlayer(actualPlayer));
                            state = GameStates.ROUND_STARTED;
                        }
                    }, 3000);
                }
            }else{
                state = GameStates.HIDE_CARDS;
            }
        }
    }

    public void gameOver() {
        if (cards.isEmpty()) {
            // ukázat obrazovku s výsledkama
            Player winner = null;
            for (Player p : gameSettings.getPlayers()) {
                if (winner == null) {
                    winner = p;
                } else {
                    if (p.getPoints() > winner.getPoints()) {
                        winner = p;
                    } else if (p.getPoints() == winner.getPoints()) {
                        winner = null;
                        break;
                    }
                }
            }
            if (winner == null) {
                Toast.makeText(getApplicationContext(), "Remíza více hráčů", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Vítězem se stal " + winner.getName() + " (" + winner.getPoints() + " body)", Toast.LENGTH_LONG).show();
            }
            this.finish();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        for (Player p : gameSettings.getPlayers()) {
            MenuItem item = menu.add(0, p.getColor(), Menu.NONE, p.toString());
        }
        return super.onPrepareOptionsMenu(menu);
    }
}
