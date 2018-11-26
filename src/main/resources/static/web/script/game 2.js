
var url = window.location.href;
var id = paramObj(url);
var shipLength = 0;
var $messagesToPlayers;
var shipsArray = [];
var shipClass = "";
var cleanColor = [];
var salvo = [];
var shipClassElement;
var arr = [];
var locationsArr = [];
var waiting;
messageDisplayTime = 1000;
var connection;

$(function () {
    document.getElementById("submitShots").addEventListener("click", linkToSalvos);
    document.getElementById("submitShips").addEventListener("click", linkToShips);
    document.getElementById("out").addEventListener("click", logOut);
    document.getElementById("redirectToGames").addEventListener("click", linkToGames);
    document.getElementById("submitShots").addEventListener("click", linkToSalvos);

    id = parseQueryObject();
    if (id.hasOwnProperty("gp")) {
        var id = id.gp;
        connection = "/api/game_view/" + id;
        $.getJSON(connection, fetchGameplayData);
    }
});

function fetchGameplayData () {
    $.getJSON("/api/game_view/" + id.gp)
        .done(function (data) {
            console.log(data);
            displayMessage("");
            $messagesToPlayers = $('#panelInfo');
            viewPlayers(data);
            createTable(1);
            createTable(2);
            $("#salvosMap").hide();
            $("#history").hide();
            $("#shootingControls").hide();
            $("#boardClear").click(clearTheBoard);
            var myGrid = "#yourShipsGrid";
            var opponentGrid = "#salvosMap";
            addBackgoundToTables(myGrid);
            addBackgoundToTables(opponentGrid);
            shipsGrid(data, "#yourShipsGrid");
            postingSalvos(data, "#salvosMap");
            postingSalvos(data, "#yourShipsGrid");
            printHistoryTable(data);

            if (data.user_ships.length !== 0) {
                console.log(data.user_ships);
                $("#salvosMap").show();
                $("#placingShipsControls").hide();
                $(".ship").hide();
                $("#shootingControls").show();
                $("#history").show();

            }
            $("td[data-length]").click(choseShip);
            $("td[data-location1]").mouseover(hoverHighlight);
            $("td[data-location1]").mouseleave(hoverCleaned);
            $("td[data-location1]").click(placeShipsOnTheTable);
            messages(data);
            gamePlayersTurns(data);

            if (waiting == true) {
                $("td[data-location2]").mouseover(shootShip);
                $("td[data-location2]").mouseleave(removeBomb);
                $("td[data-location2]").click(postedShoots);
            }
        })
        .catch(function (error) {
            console.log("Error: " + error);
        });
}

const logOut = function() {
    $.post("/api/logout").done(function () {
        location.replace("/web/games.html");
    });
}

function paramObj (url) {
    let obj = {};
    let reg = /(?:[?&]([^?&#=]+)(?:=([^&#]*))?)(?:#.*)?/g;
    url.replace(reg, function(match, param, val) {
        obj[decodeURIComponent(param)] = val === undefined ? "" : decodeURIComponent(val);
    });
    return obj;
}
paramObj(url);

const viewPlayers = (data) => {
    var gamePlayers = data.game.gamePlayer;
    var infoPlayer = document.getElementById("player");
    var opponent = document.getElementById("opponent");
    for (var i = 0; i < gamePlayers.length; i++) {
        if (id.gp == gamePlayers[0].id ) {
            infoPlayer.innerHTML = gamePlayers[0].player.userName + " (you)";
        }

        else if(opponent === ""){
            opponent =  alert("Your opponent hasn't joined the game yet");
        }
        else if(id.gp == gamePlayers[i].id) {
            opponent.innerHTML = gamePlayers[i].player.userName + " (enemy)";
        }
    }
}

const gamePlayersTurns = (data) => {

    $("#submitShots").hide();
        //// message here to let user kow he is waiting for the enemy to place ships
        if (data.enemyShipsPlaced == false && data.first === id.gp && data.user_ships.length != 0) {
           setTimeout(function () {
                $.getJSON(connection, fetchGameplayData);
            }, 5000);
        }
        if (data.enemyShipsPlaced === true) {
            $("#submitShots").show();
        }
        // turn numbers for both players
        var myTurnNumber = 1;
        var enemyTurnNumber = 1;
        for (var gpid in data.salvoes) {
            console.log(data.salvoes);
            for (var key in data.salvoes[gpid]) {
                console.log(data.salvoes[gpid]);
                if (id.gp === gpid) {
                    myTurnNumber = +key + 1;
                } else {
                    enemyTurnNumber = +key + 1;
                }
            }
        }

        // number of ships each player has left
        var myShipsFree;
        var yourShipsFree;
        for (var i = 0; i < data.history.length; ++i) {
            if (id.gp == data.history[i].gpid) {
                for (var j = 0; j < data.history[i].action.length; ++j) {
                    if (data.history[i].action[j].left <= yourShipsFree || yourShipsFree == undefined) {
                        //  ships that still have to sink of the enemy
                        yourShipsFree = data.history[i].action[j].left;
                        console.log(data.history[i].action[j].left);
                    }
                }
            } else {
                for (var l = 0; l < data.history[i].action.length; ++l) {
                    if (data.history[i].action[l].left <= myShipsFree || myShipsFree == undefined) {
                        myShipsFree = data.history[i].action[l].left;
                    }
                }
            }
        }

        if (myTurnNumber == enemyTurnNumber && data.first == id.gp) {
            $("#submitShots").css("background-color", "yellow");
            $("#submitShots").html("End Turn");
            $("#submitShots").removeAttr("disabled");
            waiting = false;
            $("#message").html("It is your turn! Select 5 squares and click 'End Turn'. ");
            console.log("hello");
        }
        else if (myTurnNumber < enemyTurnNumber) {
            $("#submitShots").css("background-color", "red");
            $("#submitShots").html("End Turn");
            $("#submitShots").removeAttr("disabled");
            waiting = false;
            $("#message").html("It is your turn! Select 5 squares and click 'End Turn'. ");
            console.log("hello");
        } else {
            $("#submitShots").css("background-color", "yellow");
            $("#submitShots").html("Enemy Turn");
            $('#submitShots').attr("disabled", "disabled")
            $("#message").html("Wait for the enemy to finish his turn.");
            waiting = true;
            displayTemporaryMessage("Wait for the enemy to finish his turn",  messageDisplayTime);
            if (data.user_ships.length != 0) {
            /*  setTimeout(function () {
                    $.getJSON(connection, fetchGameplayData);
                }, 5000);*/
            }
        }

        // game finished!
   //
   // if ( losser in hunkedShips)
 //   {
        if (yourShipsFree === 0 && yourShipsFree != undefined) {
            $("#message").html("You are the winner!");
            console.log("You won")
            $('#submitShots').hide();
        } else if (myShipsFree == 0) {
            $("#message").html("You lost! :( ");
            $('#submitShots').hide();
        }
   // }
        var first;
        if (id.gp == data.first) {
            first = true;
        } else {
            first = false;
        }

        console.log("My turn number: " + myTurnNumber);
        console.log("Enemy turn number: " + enemyTurnNumber);
        console.log("My ships left: " + myShipsFree);
        console.log("Enemy ships left: " + yourShipsFree);
        console.log("Enemy ships placed: " + data.enemyShipsPlaced);
        console.log("First? " + first);
        console.log("Are you Waiting? " + waiting);
}

function printHistoryTable(data) {
    var historyOutPut = "";
    var enemyOutput = "";
    $.each(data.history, function (index) {
        // each user
        var object = data.history[index];
        if (id.gp == object.gpid) {
            console.log(id.gp);
            for (var n = 0; n < object.action.length; n++) {
                historyOutPut += Mustache.render($("#myTemplate").html(), object.action[n]);
            }
            // this is the enemy user
        } else if (id.gp != object.gpid) {
            for (var i = 0; i < object.action.length; i++) {
                enemyOutput += Mustache.render($("#myTemplate").html(), object.action[i]);
            }
        }
    });
    $("#output").html(historyOutPut);
    $("#output2").html(enemyOutput);
}

var addBackgoundToTables = function(map) {
    var $locationCenter = $(map).find("tr");
    var $topTds = $($locationCenter[1]).find("td");
    $($topTds[1]).addClass("topLeft");
    for (var i = 2; i < $topTds.length -1; ++i) {
        $($topTds[i]).addClass("topCenter");
    }
    $($topTds[$topTds.length-1]).addClass("topRight");
    for (var c = 2; c < $locationCenter.length -1; ++c) {
        var $tds = $($locationCenter[c]).find("td");
        $($tds[1]).addClass("middleLeft");
        for (var e = 2; e < $tds.length; ++e ) {
            $($tds[e]).addClass("middleCenter")
        }
        $($tds[$tds.length - 1]).addClass("middleRight");
    }
    var $lastRowTds = $($locationCenter[$locationCenter.length-1]).find("td");
    $($lastRowTds[1]).addClass("bottomLeft");
    for (var w = 2; w < $lastRowTds.length + 1; ++w) {
        $($lastRowTds[w]).addClass("bottomCenter");
    }
    $($lastRowTds[$lastRowTds.length-1]).addClass("bottomRight");
}

const linkToGames = function () {
    location.assign("/web/games.html");
}

var displayMessage = function (message) {
    // Clear any temporary message timeout if it exists
    if (displayTemporaryMessage.timeoutID != undefined && displayTemporaryMessage.timeoutID != 0) {
        clearTimeout(displayTemporaryMessage.timeoutID);
        displayTemporaryMessage.timeoutID = 0;
    }
    $('#panelInfo').html(message);
}

var displayTemporaryMessage = function (message, waitDuration) {
    // If no temporary message is being displayed, we store the current message
    if (displayTemporaryMessage.timeoutID == undefined || displayTemporaryMessage.timeoutID == 0) {
        displayTemporaryMessage.previousMessage = $('#panelInfo').html();
    } else {
        // If there was a temporary message, we'll stop its timeout
        clearTimeout(displayTemporaryMessage.timeoutID);
        displayTemporaryMessage.timeoutID = 0;
    }
    $('#panelInfo').html(message);
    displayTemporaryMessage.timeoutID = setTimeout(function () {
        displayPreviousMessage();
        displayTemporaryMessage.timeoutID = 0;
    }, waitDuration);
}

var displayPreviousMessage  = function() {
    $('#panelInfo').html(displayTemporaryMessage.previousMessage);
}

var messages = function(data) {
    if (data.user_ships.length == 0) {
        $("#message").html("Place the ships on the map.");
        return true;
    }
    if (data.enemyShipsPlaced == false) {
        $("#message").html("Waiting for the enemy to place ships.");
    }
}

var createTable = function(competitor) {
    var size = 10;
    var alphabet = ' ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('');
    var output = "";
    output += "<tr>";
    output += "<th></th>"
    for (var i = 0; i < size + 1 ; i++) {
        output += "<th>" + alphabet[i] + "</th>";
    }
    output += "<th></th>"
    output += "</tr>";
    output += "<tr>";
    output += "<td></td>"
    for (var t = 0; t < size + 1 ; t++) {
        output += "<td></td>";
    }
    output += "<td></td>"
    output += "</tr>";
    var players = 1;
    for (var j = 0; j < size; ++j) {
        output += "<tr>" + "<td data-location" + competitor + "= side>" + players + "</td>";
        output +=	"<td></td>";
        for (var c = 0; c < alphabet.length && c < size; c++) {
            output += '<td class ="gridCell" data-location' + competitor + '="' + alphabet[c + 1] + players + '">' + " " + "</td>";
        }
        output += "<td></td>"
        output += "</tr>";
        players++;
    }
    output += "<tr>"
    for (var m = 0; m < size + 3; ++m) {
        output += "<td></td>"
    }
    output += "</tr>"

    if (competitor === 1) {
        $("#yourShipsGrid").html(output);
    } else {
        $("#salvosMap").html(output);
    }
}

const  placeShipsOnTheTable = function () {
    console.log(shipClass)
    if (shipClass === "") {
        return false;
    }
    var alphabet = ' ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('');
    var start = $(this).attr("data-location1");
    var firstRow = start.split("");
    var startNumber = start.slice(1);
    if (!$("#vertical").is(":checked")) {
        for (var i = 0; i < alphabet.length; ++i) {
            if (alphabet[i] == firstRow[0]) {
                var nextLetter = alphabet.slice(i);
                locationsArr = [];
                var cellsToBeColored = [];
                for (var j = 0; j < shipLength; ++j) {
                    var letter = nextLetter[j].slice(0, 1);
                    var location = (letter + startNumber);
                    if (isOverlap(location)) {
                        displayTemporaryMessage("You may not place ships on top of one another", messageDisplayTime);
                        return false;
                    }
                    else if (isOverEdge(location)) {
                        displayTemporaryMessage("You may not place ships over the edge of the map", messageDisplayTime);
                        return false;
                    } else {
                        cellsToBeColored.push(location);
                        locationsArr.push(location);
                    }
                }
                for (var p = 0; p < cellsToBeColored.length; p++) {
                    paintedShipOnTheGrid(cellsToBeColored[p], shipClass, p, "hor");
                }
                shipClassElement.addClass("shipNoPlaced");
                addtoShipsObj();
                shipLength = 0;
                shipClass = "";
            }
        }

    } else if ($("#vertical").is(":checked")) {
        var currentNumber = startNumber;
        locationsArr = [];
        var toColor = [];
        for (var t = 0; t < shipLength; ++t) {
            var loc = firstRow[0] + currentNumber;
            if (isOverlap(loc)) {
                displayTemporaryMessage("You may not place ships on top of one another", messageDisplayTime);
                return false;
            }

            else if (isOverEdgeVertical(loc)) {
                displayTemporaryMessage("You may not place ships over the edge of the map", messageDisplayTime);
                return false;
            } else {
                toColor.push(loc);
                locationsArr.push(loc);
            }
            currentNumber++;
        }

        for (var n = 0; n < toColor.length; n++) {
            console.log(n)
            paintedShipOnTheGrid(toColor[n],shipClass,n, "ver");
        }
        shipClassElement.addClass("shipNoPlaced");
        addtoShipsObj();
        shipLength = 0;
        shipClass = "";
    }
}

var clearTheBoard = function() {
    shipLength = 0;
    shipClass = "";
    locationsArr = [];
    shipsArray = [];
    arr = [];
    $("td[data-location1]").removeClass("shipPlaced");
    $(".ship").removeClass("shipNoPlaced");
    window.location.reload(true);

}

var hoverCleaned = function() {
    for (var i = 0; i < arr.length; i++) {
        arr[i].removeClass("highlight");
        arr[i].removeClass("overlap");
    }
    arr = [];
}

var isOverlap = function(toCheck) {
    for (var i = 0; i < shipsArray.length; i++) {
        for (var j = 0; j < shipsArray[i].location.length; j++) {
            if (shipsArray[i].location[j] == toCheck) {
                return true;
            }
        }
    }
    return false;
}

var sameShip = function(tocheck) {
    for (var i = 0; i < shipsArray.length; i++) {
        for (var j = 0; j < shipsArray[i].shipClass.length; j++) {
            if (shipsArray[i].shipClass[j] == tocheck) {
                return true
            }
        }
    }
    return false;
}

const shipsGrid = function (data, tableSelector) {
    var $position = $(tableSelector).find(".gridCell");
    for (var i = 0; i < data.user_ships.length; ++i) {
        for (var k = 0; k < data.user_ships[i].location.length; ++k) {
            for (var j = 0; j < $position.length; ++j) {
                var $field = $($position[j]);
                if (data.user_ships[i].location[k] === $field.attr("data-location1")) {
                    $($field).addClass("shipPlaced");
                    $($field).addClass(data.user_ships[i].shipClass);
              }
            }
        }
    }
}

const hoverHighlight = function() {
    if (!$(this).hasClass("gridCell")) {
        return false;
    }
    for (var i = 0; i < arr.length; i++) {
        arr[i].removeClass("highlight");
        arr[i].removeClass("overlap");
    }
    arr = [];
    var alphabet = ' ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('');
    var current = $(this).attr("data-location1");
    var currentLetter = current.split("");
    var currentNumber = current.slice(1);
    if (!$("#vertical").is(":checked")) {
        for (var i = 0; i < alphabet.length; ++i) {
            if (alphabet[i] == currentLetter[0]) {
                var nextLetter = alphabet.slice(i);
                var test = [];
                for (var j = 0; j < shipLength; ++j) {
                    var letter = nextLetter[j].slice(0, 1);
                    var location = letter + currentNumber;
                    $("td[data-location1='" + location + "']").addClass("highlight");
                    arr.push($("td[data-location1='" + location + "']"));
                    test.push(location);
                    if (isOverlap(location)) {
                        arr.push($("td[data-location1='" + location + "']").addClass("overlap"));
                    }
                }
                for (var u = 0; u < test.length; u++) {
                    if (isOverEdge(test[u])) {
                        for (let l = 0; l < test.length; l++) {
                            $("td[data-location1='" + test[l] + "']").addClass("overlap");
                        }
                    }
                }
            }
        }
    } else if ($("#vertical").is(":checked")) {
        // here goes vertical logic
        var currentN = currentNumber;
        var overTheShip = [];
        for (var m = 0; m < shipLength; m++) {
            var loc = currentLetter[0] + currentN;
            $("td[data-location1='" + loc + "']").addClass("highlight");
            arr.push($("td[data-location1='" + loc + "']"));
            overTheShip.push(loc);
            currentN++
            if (isOverlap(loc)) {
                arr.push($("td[data-location1='" + loc + "']").addClass("overlap"));
            }

            if(sameShip(loc)) {
                arr.push($("td[data-location1='" + location + "']").addClass("shipNoPlaced"));
            }
        }
        for (var v = 0; v < overTheShip.length; v++) {
            if (isOverEdgeVertical(overTheShip[v])) {
                for (var g = 0; g < overTheShip.length; g++) {
                    $("td[data-location1='" + overTheShip[g] + "']").addClass("overlap");
                }
                displayTemporaryMessage("This place is a wrong place", messageDisplayTime)

            }
        }
    }
}

var isOverEdge = function(toCheck) {
    var alphabet = "KLMNOPQRSTUVWXYZ".split("");
    var letter = toCheck.split("");
    for (var i = 0; i < alphabet.length; ++i) {
        if (alphabet[i] == letter[0]) {
            return true;
        }
    }
    return false;
}

var isOverEdgeVertical = function(toCheck) {
    var numbers = ["-2", "-1", "11", "12", "13", "14", "15", "16"];
    var number = toCheck.slice(1);
    for (var i = 0; i < numbers.length; ++i) {
        if (numbers[i] == number) {
            return true;
        }
    }
    return false;
}

var choseShip = function() {
    shipLength = $(this).attr("data-length");
    console.log(shipLength);
    shipClass = $(this).parent().attr("id");
    console.log(shipClass);
    shipClassElement = $(this).parent();
    console.log(shipClassElement);
}

var addtoShipsObj = function (data) {
    var ship = {};
    var shipLocation = $(this).attr("data-location1");
    ship.location = locationsArr;
    console.log(shipsArray);
    ship.shipClass = shipClass;
    shipClassElement = $(this).parent();
    console.log(ship.shipClass);
    var newShip = {
        "location": ship.location,
        "shipClass": ship.shipClass
    }
    for (var i = 0; i < shipsArray.length; i++){
        if (shipsArray[i].shipClass === newShip.shipClass) {
            displayTemporaryMessage("the ship is already taken", (messageDisplayTime * 1.5));
            shipsArray.splice(i, 1);
            $("td[data-location1]."+ newShip.shipClass).removeClass(shipClass);
            console.log($("td[data-location1]."+ newShip.shipClass));
            return false;
        }
    }

    if (shipsArray.length > 5) {
        console.log("You can't place more than 5 ships!");
        alert("You can't place more than 5 ships!");
        shipsArray.splice(4);
        $("td[data-location1]").removeClass("shipPlaced");
        alert("Please press clear the board to continue");
    }
    console.log("You have " + [(shipsArray.length)+1] + " ships at this moment.");
    shipsArray.push(newShip);
}

var paintedShipOnTheGrid = function(location, shipClass,index, direction) {
    console.log(index)
    $("td[data-location1='" + location + "']").addClass("shipPlaced");
    $("td[data-location1='" + location + "']").addClass(shipClass);
    $("td[data-location1='" + location + "']").addClass(shipClass + "-" + index+"-" + direction);
}

var linkToShips = function () {
    for (var i = 0; i < shipsArray.length; i++){
        displayTemporaryMessage("What you are sending  is : " + shipsArray[i].shipClass, messageDisplayTime);
    displayTemporaryMessage("You are sending this number of ships: " + shipsArray.length, messageDisplayTime);
    console.log("What you are sending  is : " + shipsArray[i].shipClass);
    console.log("You are sending this number of ships: " + shipsArray.length);
}
    if (shipsArray.length === 5) {
        $.post({
            url: "/api/games/players/" + id.gp + "/ships",
            data: JSON.stringify(shipsArray),
            dataType: "json",
            contentType: "application/json"
        }).done(function () {
            location.reload();
            console.log("Success!");
        }).fail(function () {
            console.log("Fail!");
        });
    }  else if (shipsArray.length >= 5){
        alert("You can't send more than 5 ships");
        return false

    } else {
        alert("You must place all the ships on the board");
    }
}

var linkToSalvos = function (data) {
    console.log(salvo);

    if (salvo.length !== 5) {
        displayTemporaryMessage("First you need to select 5 salvos", messageDisplayTime);
        return false;
    }
    $.post({
        url: "/api/games/players/" + id.gp + "/salvos",
        data: JSON.stringify(salvo),
        dataType: "json",
        contentType: "application/json"
    }).done(function () {
        location.reload();
        displayMessage("You have sent your salvo!")
        console.log("You have sent your salvo!");
        salvo = [];
    }).fail(function () {
        console.log("You failed sending salvo!");
    });
}

var postedShoots = function(data) {
    var currentLocation = $(this).attr("data-location2");
    if (!$(this).hasClass("gridCell")) {
        return false;
    }
    if ($(this).hasClass("bombed")) {
        $("td[data-location2='" + currentLocation + "']").removeClass("bombed");
        for (var i = 0; i < salvo.length; ++i) {
            if (salvo[i] == currentLocation) {
                displayTemporaryMessage("You can't shoot at the same place", messageDisplayTime/2)
                salvo.splice(i, 1);
                return false
            }
        }
    }
    if (salvo.length >= 5) {
        return false;
    }
    for (var key in data.salvoes) {
        if (data.id === key) {
            console.log(data.id);
            var keys = data.salvoes[key];
            for (var key2 in keys) {
                var values = keys[key2];
                for (var y = 0; y < values.length; y++) {
                    if (values[y] === currentLocation) {
                        console.log("You may not shoot previously shot locations");
                        return false
                    }
                }
            }
        }
    }
    if (isPreviouslyShot(currentLocation)) {
        console.log("Again, it has been shot");
        return false;
    }

    $("td[data-location2='" + currentLocation + "']").removeClass("toBomb");
    $("td[data-location2='" + currentLocation + "']").addClass("bombed");
    salvo.push(currentLocation);
}

var isPreviouslyShot = function (data, current) {
    for (var key in data.salvoes) {
        if (id.gp == key) {
            for (var turn in data.salvoes[key]) {
                var t = data.salvoes[key];
                for (var i = 0; i < t[turn].length; ++i) {
                    if (current == t[turn][i]) {
                        return true;
                    }
                }
            }
        }
    }
    return false;
}

var postingSalvos = function(data, tableSelector) {
    for (var key in data.salvoes) {
        //my shots on the enemy map
        if (key == id.gp) {
            var $myMap = $("#salvosMap").find(".gridCell");
            var mySalvos = data.salvoes[key];
            for (var turnKey in mySalvos) {
                // Arrays with shots
                var mySalvoTurn = mySalvos[turnKey];
                for (var i = 0; i < mySalvoTurn.length; ++i) {
                    for (var j = 0; j < $myMap.length; ++j) {
                        var $field = $($myMap[j]);
                        if (mySalvoTurn[i] == $field.attr("data-location2")) {
                            var toCheck = $field.attr("data-location2");
                            if (isHit(data, toCheck)) {
                                $field.addClass("bombedDuck");
                            } else {
                                // to add a feature to turn turns in hits onn and off
                                $field.html("<p class='notHit'>" + turnKey + "</p>");
                            }
                        }
                    }
                }
            }
        }
        //enemy shots on my map
        if (key != id.gp) {
            var $enemyMap = $("#yourShipsMap").find(".fields");
            var enemySalvos = data.salvoes[key];
            for (var enemyTurnKey in enemySalvos) {
                // Arrays with shots
                var enemySalvoTurn = enemySalvos[enemyTurnKey];
                for (var i = 0; i < enemySalvoTurn.length; ++i) {
                    for (var j = 0; j < $enemyMap.length; ++j) {
                        var $enemyField = $($enemyMap[j]);
                        if (enemySalvoTurn[i] == $enemyField.attr("data-location1")) {
                            var toCheck2 = $enemyField.attr("data-location1");
                            if (isHit(data, toCheck2)) {
                                $($enemyField).addClass("bombedDuck");
                            } else {
                                // to add a feature to turn turns in hits onn and off
                                $enemyField.html("<p class='notHit'>" + enemyTurnKey + "</p>");
                            }
                        }
                    }
                }
            }
        }
    }
}

var shootShip = function() {
    if (salvo.length >= 5) {
        return false;
    }
    if (!$(this).hasClass("gridCell")) {
        return false;
    }
    for (var i = 0; i < arr.length; i++) {
        arr[i].removeClass("toBomb");
        arr[i].removeClass("overlap");
    }
    arr = [];
    var current = $(this).attr("data-location2");
    if (isPreviouslyShot(current)) {
        console.log("Has been shot before");
        return false;
    }

    $("td[data-location2 ='" + current + "']").addClass("toBomb");
    if ($("td[data-location2 = '" + current + "']" ).hasClass("bombed")) {
        $("td[data-location2 = '" + current + "']" ).removeClass("toBomb");
        $("td[data-location2 = '" + current + "']" ).addClass("bombed");
    }

    arr.push($("td[data-location2='" + current + "']"));
}

var removeBomb = function () {
    $(this).removeClass("toBomb");
}


function isHit(data, toCheck){
    for (var i = 0; i<data.history.length; ++i) {
        if (data.history[i].gpid == id.gp) {
            for (var w = 0; w< data.history[i].action.length; ++w) {
                for (var turn in data.history[i].action[w]) {
                    var hits = data.history[i].action[w].hit;
                    for (var k = 0; k<hits.length; ++k) {
                        if (toCheck == hits[k]) {
                            return true;
                        }
                    }
                }
            }
        } else {
            for (var q = 0; q< data.history[i].action.length; ++q) {
                for (var turn in data.history[i].action[q]) {
                    var hits2 = data.history[i].action[q].hit;
                    for (var r = 0; r<hits2.length; ++r) {
                        if (toCheck == hits2[r]) {
                            return true;
                        }
                    }
                }
            }
        }
    }
    return false;
}


function parseQueryObject() {
    // using substring to get a string from position 1
    var queryString = location.search.substring(1); /*"?gp=1&mp=23&sdfs=3rr"*/ ;
    var obj = {};
    // You can pass a regex into Javascript's split operator.
    var arr = queryString.split(/=|&/);
    if (queryString !== "") {
        arr.forEach(function (item, index) {
            if (index % 2 === 0) {
                obj[item] = arr[index + 1];
            }
        });
    }
    return obj;
}
