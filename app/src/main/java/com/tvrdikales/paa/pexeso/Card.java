package com.tvrdikales.paa.pexeso;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageButton;

/**
 * Created by Ales on 19.12.2014.
 */
public class Card extends ImageButton implements Comparable<Card>{
    private final int PADDING = 5;
    private int group;

    public Card(Context context, int group) {
        super(context);
        this.setPadding(PADDING,PADDING,PADDING,PADDING);
        this.group = group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getGroup() {
        return group;
    }

    public void showCard() {
        this.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), getResources().getIdentifier("pic_" + group + "", "drawable", getContext().getPackageName())), this.getWidth()-2*PADDING, this.getHeight()-2*PADDING, true));
    }

    public void hideCard() {
        this.setImageResource(R.drawable.ic_launcher);
    }

    public void removeCard() {
        this.setVisibility(INVISIBLE);
    }

    public boolean inGroupWith(Card card) {
        return ((Card) card).getGroup() == this.group;
    }

    public void swapCard(Card card) {
        int group;
        group = this.group;
        this.group = card.getGroup();
        card.setGroup(group);
    }

    @Override
    public int compareTo(Card another) {
        if (this.group <another.getGroup()) {
            return -1;
        } else{
            return 1;
        }
    }
}
