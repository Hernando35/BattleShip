package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
public class Salvo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;
    private Integer turnNumber;

    @ElementCollection
    @Column(name = "salvoLocation")
    private List<String> salvoLocation = new ArrayList<>();

    public Salvo () {}

    public Salvo(Integer turnNumber, List<String> salvoLocation, GamePlayer gamePlayer) {
        gamePlayer.addSalvos(this);
        this.turnNumber = turnNumber;
        this.salvoLocation = salvoLocation;
        this.gamePlayer = gamePlayer;
    }

    public long getId() {
        return id;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public Integer getTurnNumber() {
        return turnNumber;
    }
    public void setTurnNumber(Integer turnNumber) {
        this.turnNumber = turnNumber;
    }

    public List<String> getSalvoLocation() {
        return salvoLocation;
    }

    public void setSalvoLocation(List<String> locations) {
        this.salvoLocation = salvoLocation;
    }


}

