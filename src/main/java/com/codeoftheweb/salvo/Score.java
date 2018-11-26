package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;
@Entity
public class Score {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn (name="game_id")
    private Game game;

    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn (name="player_id")
    private Player player;
    private Double score;
    private Date finisDate;

    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn (name = "gamePlayer_id")
    private GamePlayer gamePlayer;


    public Score(){  }
    public Score(Game game, Player player, Double score){
        this.game = game;
        this.player = player;
        this.score = score;
        this.finisDate = new Date();
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() { return player; }

    public void setPlayer(Player player) { this.player = player; }

    public Double getScore() {
        return score;
    }
    public Date getFinisDate() {
        return finisDate;
    }


}









