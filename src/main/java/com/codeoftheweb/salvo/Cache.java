package com.codeoftheweb.salvo;


import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Cache {

    //games.html
    Map<String,Object> apiLeaderBoardDto;
    Map<String,Map<String, Object>> apiPlayer = new LinkedHashMap<>();
    List<Object> apiGamesDto;
    boolean apiGamesResponseChanged = true;


    //gameview
    Map<Long, Object> apiGameView;
    Map<Long, Boolean> apiGameViewChanged;
}
