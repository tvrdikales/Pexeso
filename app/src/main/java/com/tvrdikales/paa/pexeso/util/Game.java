package com.tvrdikales.paa.pexeso.util;

import com.tvrdikales.paa.pexeso.util.util.UnnableToStartException;

import java.io.Serializable;

public class Game implements Serializable {
    private String player1Name;     // jméno hráče č.1
    private String player2Name;     // jméno hráče č.2
    private Boolean player2AI;      // je hráč č.2 počítač ?
    private Integer pairsCount;     // počet hracích karet(dvojic)

    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }

    public void setPlayer2AI(Boolean player2AI) {
        this.player2AI = player2AI;
    }

    public void setPairsCount(Integer pairsCount) {
        this.pairsCount = pairsCount;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public Boolean getPlayer2AI() {
        return player2AI;
    }

    public Integer getPairsCount() {
        return pairsCount;
    }

    public void startGame() throws UnnableToStartException{
        if(getPlayer1Name()==null) throw new UnnableToStartException("Zadejte jméno hráče č.1");
        if(getPlayer2Name()==null & !getPlayer2AI()) throw new UnnableToStartException("Zadejte jméno hráče č.2 nebo zvolte hru proti AI");
        if(getPairsCount()==null) throw new UnnableToStartException("Nastavte počet párů");
    }

}
