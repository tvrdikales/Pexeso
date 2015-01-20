package com.tvrdikales.paa.pexeso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by Ales on 28.12.2014.
 */
public class Player implements Serializable {
    private boolean AI;
    private String name;
    private int color;
    private int points;

    //Pamět AI hráče. Každý hráč má svou pamět pro případ rozšíření možností hry o možnost kdy hráč nevidí tahy ostatních hráčů
    //AI - pamět pro otočené karty dle skupin
    private ArrayList<Card>[] visitedCardsByGroups;
    //AI - šance nalezení jednotlivých karet
    private TreeSet<CardChance> chanceToFind;

    public Player(int color, String name, boolean AI) {
        this.color = color;
        this.name = name;
        this.AI = AI;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public boolean isAI() {
        return AI;
    }

    public void addPoint() {
        points++;
    }

    public int getPoints() {
        return points;
    }

    @Override
    public String toString() {
        return name + ": " + String.format("%2d", points);
    }

    // AI

    public void inicializeAI(ArrayList<Card> allCards, int groupsCount) {
        visitedCardsByGroups = new ArrayList[groupsCount];
        for (int i = 0; i < groupsCount; i++) {
            visitedCardsByGroups[i] = new ArrayList<>();
        }

        chanceToFind = new TreeSet<>();
        for (Card c : allCards) {
            chanceToFind.add(new CardChance(c, 0));
        }
    }

    public void cardShowed(Card card) {
        if (!visitedCardsByGroups[card.getGroup() - 1].contains(card)) {
            visitedCardsByGroups[card.getGroup() - 1].add(card);
        }

        for (CardChance chance : chanceToFind) {
            //chance.sub((float) 0.1);
            if (chance.getCard().equals(card)) {
                chance.setChance((chance.getChance() * 2) + 1);
            }
        }
        resortCardsByChance();
    }

    private void resortCardsByChance() {
        // seřazení dle hodnoty
        TreeSet<CardChance> newChanceToFind = new TreeSet<>();
        newChanceToFind.addAll(chanceToFind);
        chanceToFind = newChanceToFind;
    }

    public void groupFounded(int group) {
        for (CardChance c : chanceToFind) {
            if (c.getCard().getGroup() == group) {
                chanceToFind.remove(c);
            }
        }

        visitedCardsByGroups[group - 1].clear();
    }

    public Card getUnknownCard(ArrayList<Card> showed) {
        for(CardChance chance: chanceToFind){
            if(!showed.contains(chance.getCard())){return chance.getCard();}
        }
        return null;
    }

    public float chanceForGroup(int group, int groupSize){
        if ((visitedCardsByGroups[group-1] != null) && (groupSize == visitedCardsByGroups[group-1].size())) {
            float chance = 1;
            for (CardChance c : chanceToFind) {
                if (visitedCardsByGroups[group-1].contains(c.getCard())) {
                    chance = chance * c.getChance();
                }
            }
            return chance;
        }else{
            return 0;
        }
    }

    public ArrayList<Card> getCardsFromGroup(int group) {
        return visitedCardsByGroups[group - 1];
    }

    public boolean isDiscovered(int groupId, int groupSize) {
        return visitedCardsByGroups[groupId - 1].size() == groupSize;
    }

    private class CardChance implements Comparable<CardChance> {
        private Card card;
        private float chance;

        private CardChance(Card card, float chance) {
            this.card = card;
            this.chance = chance;
        }

        public Card getCard() {
            return card;
        }

        public float getChance() {
            return chance;
        }

        public void sub(float value) {
            this.chance = this.chance - value;
            if (this.chance < 0) this.chance = 0;
        }

        public void setChance(float chance) {
            this.chance = chance;
        }

        @Override
        public int compareTo(CardChance another) {
            if (chance < another.getChance()) {
                return -1;
            } else if (chance > another.getChance()) {
                return 1;
            } else {
                return 1;
            }
        }
    }
}
