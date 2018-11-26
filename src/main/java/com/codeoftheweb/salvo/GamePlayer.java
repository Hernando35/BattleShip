package com.codeoftheweb.salvo;


import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import static javax.persistence.GenerationType.AUTO;


@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private Date date;
    private boolean firstGamePlayer = false;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.LAZY)
    private Set <Ship> ships = new HashSet<>();

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.LAZY)
    private Set<Salvo> salvos = new LinkedHashSet<>();

    public Set<Salvo> getSalvos() {
        return salvos;
    }

    public void setSalvos(Set<Salvo> salvos) {
        this.salvos = salvos;
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public void setShips(Set<Ship> ships) {
        this.ships = ships;
    }


    public Date getDate() {
        return date;
    }

    public GamePlayer () {}

    public GamePlayer (Player player, Game game) {
      if (game.getGamePlayers() == null) {
            firstGamePlayer = true;
        }
        this.player = player;
        this.game = game;
        this.date = new Date();
    }

    public void addShips (Ship ship){
        ship.setGamePlayer(this);
        ships.add(ship);
    }

    public void addSalvos (Salvo salvo){
        salvo.setGamePlayer(this);
        salvos.add(salvo);
    }

    public Player getPlayer() {
        return player;
    }

    public void addPlayer(Player player){
        this.player = player;
        getPlayer().addGamePlayer(getEnemyGamePlayer());
    }


    public void setPlayer(Player player) {
        this.player = player;
    }


    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public long getId() {
        return id;
    }

    public Score getScore(){
        return player.getScore(game);
    }

    public boolean isFirstGamePlayer() {
        return firstGamePlayer;
    }

    public void setFirstGamePlayer(boolean firstGamePlayer) {
        this.firstGamePlayer = firstGamePlayer;
    }

    public GamePlayer getEnemyGamePlayer() {
        final Set<GamePlayer> gamePlayers = this.getGame().getGamePlayers();
        GamePlayer theOtherGamePlayer = null;
        for (GamePlayer gamePlayer1 : gamePlayers) {
            if (this.getId() != gamePlayer1.getId()) {
                theOtherGamePlayer = gamePlayer1;
                break;
            }
        }
        return theOtherGamePlayer;
    }

}

