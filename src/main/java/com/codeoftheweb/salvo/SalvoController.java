package com.codeoftheweb.salvo;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.*;
import static java.util.stream.Collectors.toList;


@RestController
@RequestMapping("/api")
public class SalvoController {


    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GamePlayerRepository gamePlayerRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private ShipRepository shipRepository;
    @Autowired
    private SalvoRepository salvoRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private Cache cache;


    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<String> createPlayer(String firstName,
                                               String lastName,
                                               String userName,
                                               String password) {

        if (userName.isEmpty()) {
            return new ResponseEntity<>("No name given", HttpStatus.FORBIDDEN);
        }
        final Player existingPlayer = playerRepository.findByUserName(userName);
        if (existingPlayer != null) {
            return new ResponseEntity<>("Name is already taken", HttpStatus.CONFLICT);
        }
        playerRepository.save(new Player(firstName, lastName, userName, password));
        return new ResponseEntity<>("Player created", HttpStatus.CREATED);
    }


    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Object> createGame(Authentication authentication) {
        final Map<String, Object> response = new HashMap<>();
        if (isGuest(authentication)) {
            response.put("error", "please log-in");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } else {
            final Player player = currentAuthenticatedUser(authentication);
            final Game newGame = new Game();
            gameRepository.save(newGame);
            final GamePlayer firstGamePlayer = new GamePlayer(player, newGame);
            gamePlayerRepository.save(firstGamePlayer);
            response.put("gpid", firstGamePlayer.getId());
            cache.apiGamesResponseChanged = true;
            return new ResponseEntity<>(response, HttpStatus.CREATED);
            }
    }


   @RequestMapping(path = "/games", method = RequestMethod.GET)
    public Map<String, Object> getGames(Authentication authentication){
        Map<String, Object> DTO = new LinkedHashMap<>();
        final String user = currentAuthenticatedUserName(authentication);
       if (user != null) {
           if (!cache.apiPlayer.containsKey(user)) {
               Player player = playerRepository.findByUserName(user);
               cache.apiPlayer.put(user, makePlayerDTO(player));
           }
           DTO.put("currentUser", cache.apiPlayer.get(user));
       } else  {
           DTO.put("currentUser", "unidentified user");
       }
        DTO.put("game", gameRepository
                .findAll()
                .stream()
                .map(game -> makeGameDTO(game))
                .collect(toList()));
        DTO.put("leaderBoard", playerRepository
                .findAll()
                .stream()
                .map(player -> makeScoreDto(player))
                .collect(toList()));
 
        return DTO;
    }


    @RequestMapping(path = "games/{id}/players", method = RequestMethod.POST)
    public ResponseEntity<Object> joinGame(@PathVariable long id,
                                           Authentication authentication) {
        final Player currentUser = currentAuthenticatedUser(authentication);
        final Map<String, Object> response = new HashMap<>();
        if (isGuest(authentication)) {
            response.put("error", "please log in");
            return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
        }
        final Game gameJoined = gameRepository.findOne(id);
        if (gameJoined == null) {
            response.put("error", "No such game");
            return new ResponseEntity<Object>(response, HttpStatus.FORBIDDEN);
        }
        if (gameJoined.getGamePlayers().size() == 2) {
            response.put("error", "Game is full");
            return new ResponseEntity<Object>(response, HttpStatus.FORBIDDEN);
        }
        final GamePlayer newGamePlayer = new GamePlayer(currentUser, gameJoined);
        gamePlayerRepository.save(newGamePlayer);
        final Long gpid = newGamePlayer.getId();
        response.put("gpid", gpid);
        cache.apiGamesResponseChanged = true;
        return new ResponseEntity<Object>(response, HttpStatus.CREATED);
    }


    @RequestMapping("/game_view/{id}")
    public ResponseEntity<Object> gameView(@PathVariable long id,
                                           Authentication authentication) {
        final Map<String, Object> map = new LinkedHashMap<>();

        final List<Object> gamePlayers = new ArrayList<>();
        final List<Object> location = new ArrayList<>();
        final List<Object> history = new ArrayList<>();

        final GamePlayer userGamePlayer = gamePlayerRepository.findOne(id);
        if (isGuest(authentication)) {
            map.put("error", "please log in");
            return new ResponseEntity<Object>(map, HttpStatus.UNAUTHORIZED);
        }
        if (userGamePlayer == null) {
            map.put("error", "no such game player");
            return new ResponseEntity<Object>(map, HttpStatus.UNAUTHORIZED);
        }
        final Player player = currentAuthenticatedUser(authentication);
        boolean gamer = false;
        for (GamePlayer gamePlayer : player.getGamePlayers()) {
            if (id == gamePlayer.getId()) {
                gamer = true;
            }
        }
        if (player != null && gamer) {
            map.put("game", makeGameDTO(userGamePlayer.getGame()));
            map.put("salvoes", userGamePlayer.getSalvos()
                    .stream()
                    .map( gp -> makeSalvoDTO( gp ))
                    .collect(toList()));

            map.put("createdDate", userGamePlayer.getGame().getDate());
            map.put("history", userGamePlayer.getGame().getGamePlayers()
                    .stream()
                    .map(gp -> makeHistoryDTO(gp))
                    .collect(toList()));

            final Map<Long, Object> playerSalvos = new LinkedHashMap<>();

            for (GamePlayer gp : userGamePlayer.getGame().getGamePlayers())
                if (gp.isFirstGamePlayer())  {
                  map.put("first", userGamePlayer.getGame().getGamePlayers()
                    .stream()
                    .map( rp -> makeGamePlayerDTO(gp))
                          .collect(toList()));
                };
            for (GamePlayer gp : userGamePlayer.getGame().getGamePlayers()) {
                gamePlayers.add(makeGamePlayerDTO(gp));
                history.add(makeHistoryDTO(gp));
            }
            for (Ship sp : userGamePlayer.getShips()) {
                location.add(makeShipDTO(sp));
            }
            map.put("enemyShipsPlaced", PlacedShips(userGamePlayer));
            map.put("user_ships", location);
            map.put("gamePlayers", gamePlayers);

            return new ResponseEntity<Object>(map, HttpStatus.OK);
        } else {
            map.put("error", "Houston we have a problem");
            return new ResponseEntity<Object>(map, HttpStatus.UNAUTHORIZED);
        }
    }


    @RequestMapping(path = "/games/players/{id}/ships", method = RequestMethod.POST)
    public ResponseEntity<Object> addShips(@PathVariable long id,
                                              Authentication authentication,
                                              @RequestBody List<Ship> ships) {
        final Player currentUser = currentAuthenticatedUser(authentication);
        final Map<String, Object> responseStatus = new HashMap<>();
        if (isGuest(authentication)) {
            responseStatus.put("error", "please log in");
            return new ResponseEntity<Object>(responseStatus, HttpStatus.UNAUTHORIZED);
        }
        final GamePlayer gamePlayer = gamePlayerRepository.findOne(id);
        if (gamePlayer == null) {
            responseStatus.put("error", "No such game player");
            return new ResponseEntity<Object>(responseStatus, HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.getPlayer().getId() != currentUser.getId()) {
            responseStatus.put("error", "the user is attempting to add ships for other players");
            return new ResponseEntity<Object>(responseStatus, HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.getShips().size() != 0) {
            responseStatus.put("error", "the user has already placed the ships");
            return new ResponseEntity<Object>(responseStatus, HttpStatus.FORBIDDEN);
        }
        for (Ship warriorShip : ships) {
            warriorShip.setGamePlayer(gamePlayer);
            shipRepository.save(warriorShip);
        }
        responseStatus.put("success", "the ships have been successfuly placed");
        return new ResponseEntity<Object>(responseStatus, HttpStatus.CREATED);
    }

    @RequestMapping(path = "/games/players/{id}/salvos", method = RequestMethod.POST)
    public ResponseEntity<Object> postSalvos (@PathVariable long gamePlayerId,
                                              Authentication authentication,
                                              @RequestBody List<String> salvo) {
        final Player currentUser = currentAuthenticatedUser(authentication);
        final Map<String, Object> response = new HashMap<>();
        if (isGuest(authentication)) {
            response.put("error", "please log in");
            return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
        }
        final GamePlayer gamePlayer = gamePlayerRepository.findOne(gamePlayerId);
        if (gamePlayer == null) {
            response.put("error", "No such game player");
            return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.getPlayer() != currentUser) {
            response.put("error", "the user is attempting to add salvos for other players");
            return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
        }

        Integer currentTurnNumber = gamePlayer.getSalvos().size() + 1;
        final Salvo mySalvo = new Salvo( currentTurnNumber,  salvo, gamePlayer );
        salvoRepository.save(mySalvo);
        response.put("success", "the salvos have been successfuly placed");
        final Set<Ship> enemyShips = getOtherShips(gamePlayer);
        final Map<Ship, Integer> remainingShipLocations = new HashMap<>();
        final List<Salvo> salvos = gamePlayer.getSalvos().stream()
                .sorted(Comparator.comparingInt(Salvo::getTurnNumber))
                .collect(toList());

        for (Ship ship : enemyShips) {
            remainingShipLocations.put(ship, ship.getLocations().size());
        }

        for (Salvo salvo1 : salvos) {
            getSunk(enemyShips, salvo1, remainingShipLocations);
            if (getLeft(remainingShipLocations) == 0) {
                final Score scoreWon = new Score(gamePlayer.getGame(), gamePlayer.getPlayer(), 1.0);
                final Score scoreLost = new Score(gamePlayer.getGame(), gamePlayer.getEnemyGamePlayer().getPlayer(), 0.0);
                gamePlayer.getGame().setFinished(true);
                scoreRepository.save(scoreWon);
                scoreRepository.save(scoreLost);
                cache.apiGamesResponseChanged = true;
            }
        }
        return new ResponseEntity<Object>(response, HttpStatus.CREATED);
    }



    private Map<Object, Object> makeHistoryDTO (GamePlayer gamePlayer) {
        final Map<Object,Object> DTO = new HashMap<>();
        final List<Salvo> salvos = gamePlayer.getSalvos().stream()
                .sorted(Comparator.comparingInt(Salvo::getTurnNumber))
                .collect(toList());
        final Set<Ship> enemyShips = getOtherShips(gamePlayer);
        final List<Object> turns = new ArrayList<>();
        final Map<Ship,Integer> remainingShipLocations = new HashMap<>();
        for (Ship ship : enemyShips) {
            remainingShipLocations.put(ship, ship.getLocations().size());
        }
        for (Salvo salvo : salvos) {
            final Map<String,Object> turn = new LinkedHashMap<>();
            turn.put("turn", salvo.getTurnNumber());
            turn.put("hit", getHits(enemyShips, salvo));
            turn.put("sunk", getSunk(enemyShips, salvo, remainingShipLocations));
            turn.put("left", getLeft(remainingShipLocations));
            turns.add(turn);
        }
        DTO.put("gpid", gamePlayer.getId());
        DTO.put("action", turns);

        return DTO;
    }

    private Integer getLeft (Map<Ship, Integer> remainingShipLocations) {
        int left = 0;
        for (Integer remainingShipLocation : remainingShipLocations.values()) {
            if (remainingShipLocation != 0) {
                ++left;
            }
        }
        return left;
    }

    private List<String> getSunk(Set<Ship> enemyShips, Salvo sv, Map<Ship, Integer> remainingShipLocations) {
        // this will have the "sunk" ships
        final List<String> sunk = new ArrayList<>();
        //each ship
        for (Ship enemyShip : enemyShips) {
            // it's size number
            Integer shipSize = remainingShipLocations.get(enemyShip);
            // my shots
            for (String shotLocation : sv.getSalvoLocation() ) {
                // ship locations
                for (String shipLocation : enemyShip.getLocations()) {
                    // if the ship has been completely destroyed add it to the SUNK
                    if ( shotLocation.equals(shipLocation) ) {
                        // if not decrement the number
                        shipSize--;
                        remainingShipLocations.put(enemyShip, shipSize);
                        // check if it is destroyed
                        if (shipSize == 0) {
                            // it is sunk, do stuff
                            String ship = enemyShip.getType().toString();
                            sunk.add(ship);
                        }
                    }
                }
            }
        }
        return sunk;
    }

    private List<String> getHits(Set<Ship> enemyShips, Salvo sv) {
        final List<String> hits = new ArrayList<>();
        for (Ship enemyShip : enemyShips) {
            for (String shotLocation : sv.getSalvoLocation() ) {
                for (String shipLocation : enemyShip.getLocations()) {
                    if ( shotLocation.equals(shipLocation )) {
                        hits.add(shotLocation);
                    }
                }
            }
        }
        return hits;
    }




    private boolean PlacedShips(GamePlayer userGamePlayer) {
        for (GamePlayer gp : userGamePlayer.getGame().getGamePlayers()) {
            if (gp.getId() != userGamePlayer.getId()){
                if (gp.getShips().size() != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private Map<String, Object> makeGameDTO(Game game) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put( "id", game.getId() );
        map.put( "created", game.getDate() );
        map.put( "gamePlayer", game.getGamePlayers()
                .stream()
                .map( gamePlayer -> makeGamePlayerDTO( gamePlayer ) )
                .collect( toList() ) );
        map.put("isFinished", game.isFinished());
        return map;
    }

    private Set<Ship> getOtherShips(GamePlayer gamePlayer) {
        final Set<GamePlayer> gamePlayers = gamePlayer.getGame().getGamePlayers();
        for (GamePlayer gamePlayer1 : gamePlayers) {
            if (gamePlayer.getId() != gamePlayer1.getId()) {
                return gamePlayer1.getShips();
            }
        }
        return new HashSet<>();
    }


    private Map<String, Object> makeGamePlayerDTO(GamePlayer gamePlayer) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", gamePlayer.getId() );
        map.put("player", makePlayerDTO(gamePlayer.getPlayer()));
        if (gamePlayer.getScore() != null) {
            map.put("score", gamePlayer.getScore().getScore());
        }
        else{
            map.put("score", null);
        }
        return map;
    }

    private List<Object> makeSalvoesDTO(GamePlayer gamePlayer){
        List<Object> salvoList = new ArrayList<>();
        Set<Salvo> salvos = gamePlayer.getSalvos();
        salvoList = salvos.stream()
                .map(salvo -> makeSalvoDTO(salvo))
                .collect(toList());
        return salvoList;
    }

    private Map<String, Object> makeScoresDto() {
        final Map<String, Object> DTO = new LinkedHashMap<>();
        playerRepository.findAll()
                .stream()
                .forEach(player -> DTO.put(player.getUserName(), makeScoreDto(player)));
        return DTO;
    }

    private Map<String, Object> makeScoreDto(Player player) {
        final Map<String, Object> DTO = new HashMap<>();
        double total = 0;
        double wonList = 0;
        double losesList = 0;
        double drawsList = 0;
        final Set<Score> playerScoreSet = player.getScores();
        for (Score score : playerScoreSet) {
            if (score.getScore() == 1.0) {
                wonList++;
                total += 1;
            }
            if (score.getScore() == 0.5) {
                drawsList++;
                total += 0.5;
            }
            if (score.getScore() == 0) {
                losesList++;
            }
        }
        DTO.put("id", player.getId());
        DTO.put("name", player.getUserName());
        DTO.put("total", total);
        DTO.put("won", wonList);
        DTO.put("lost", losesList);
        DTO.put("draws", drawsList);
        return DTO;
    }


    private Map<String, Object> makePlayerDTO(Player player) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put( "id", player.getId() );
        map.put( "userName", player.getUserName() );
        return map;
    }

    private Map<String, Object> makeShipDTO(Ship ship) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put( "shipType", ship.getType());
        map.put( "location", ship.getLocations());
        map.put("player", ship.getGamePlayer().getPlayer().getId());
        return map;
    }

    private Map<String, Object> makeSalvoDTO (Salvo salvo){
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("turn", salvo.getTurnNumber());
        map.put("Player_id", salvo.getGamePlayer().getId());
        map.put("location", salvo.getSalvoLocation());
        return map;
    }



    private Player currentAuthenticatedUser(Authentication authentication) {
        if (isGuest(authentication)) {
            return null;
        }
        return playerRepository.findByUserName(authentication.getName());
    }

    private String currentAuthenticatedUserName (Authentication authentication) {
        if (isGuest(authentication)) {
            return null;
        }
        return authentication.getName();
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null  ||  authentication instanceof AnonymousAuthenticationToken;
    }
    

    private boolean checkPlayerHasGamePlayerWithId(Player player, long gamePlayerId) {

        Optional<GamePlayer> any = player.getGamePlayers().stream()
                .filter(gamePlayer -> gamePlayerId == gamePlayer.getId())
                .findAny();

        return any.isPresent();
    }
}






