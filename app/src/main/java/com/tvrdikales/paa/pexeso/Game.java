package com.tvrdikales.paa.pexeso;

import java.io.Serializable;
import java.util.ArrayList;

public class Game implements Serializable {
    private ArrayList<Player> players = new ArrayList<>();
    private Integer groupsCount;     // počet hracích karet(dvojic)

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public Player getNextPlayer(Player player) {
        int index = players.indexOf(player);
        return players.get(++index % players.size());
    }

    public void setGroupsCount(Integer groupsCount) {
        this.groupsCount = groupsCount;
    }

    public Integer getGroupsCount() {
        return groupsCount;
    }

    public void startGame() throws UnnableToStartException {
        if (players.size() == 0) throw new UnnableToStartException("Zadejte jména hráčů");
        if (getGroupsCount() == null) throw new UnnableToStartException("Nastavte počet párů");
    }

    public int getGroupSize() {
        return 2;
    }

    public void setGroupSize(int groupSize){
        // možnost rožšířit výběr obtížnosti o hledání trojic, čtveřic
    }
}
