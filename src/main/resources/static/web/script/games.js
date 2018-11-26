
$(function () {
    $("#signin").click(logIn);
    $("#submitlogout").click(logOut);
    $("#register").hide();
    $("#newUser").click(displayRegisterForm);
    $("#back").click(displayBackToLogIn);
    $("#register").click(registerUser);
    $("#newGame").click(newGame);
    $("#login").css("display", "none");
    $.getJSON("/api/games", loadedData).fail(function () {
        $("#login").show();
    });
});



function loadedData(data) {
    $("#submitlogout").hide();
    $("#newGame").hide();
    console.log(data);
    leaderBoardTable(data);
    printList(data);
    greet(data);
    $(".joinGame").click(joinGame);
    $(".backGame").click(backToGame);
    $(".joinGame").hide();
    $("#gameList").attr('class', 'col-sm-5');
    $("#leaderBoard").attr('class', 'col-sm-4');
        if (data.currentUser != "unidentified user") {
            console.log(data.currentUser);
            $("#submitlogout").show();
            $("#newGame").show();
            $(".joinGame").show();
            $("#gameList").attr('class', 'col-sm-6');
            $("#leaderBoard").attr('class', 'col-sm-6');
        } else {
            console.log("The user is not identified")
        }
}

    function newGame() {
        $.post("/api/games")
            .done(function (data) {
            location.assign("/web/game.html?gp=" + data.gpid);
            console.log(data.gpid);
        });
    }

    function joinGame(data) {
        $.post("/api/games/" + $(this).attr("data-game-id") + "/players")
            .done(function (data) {
                location.assign("/web/game.html?gp=" + data.gpid);
                console.log(data.gpid);
            }).fail(function (data) {
            var obj = JSON.parse(data.responseText);
            alert(obj.error);
        });
    }

    function backToGame() {
        window.location = '/web/game.html?gp=' + $(this).attr("data-gamePlayer-id");
    }

    function displayRegisterForm() {
        const inputName = $("<div id='name' class='form-group'><label for='name'>First name:</label><input type='name' class='form-control'></div>");
        const inputLastName = $("<div id='lastname' class='form-group'><label for='lastname'>Last name:</label><input type='lastname' class='form-control'></div>");
        $("#login").prepend(inputName);
        $("#login").prepend(inputLastName);
        $("#register").show();
        $("#back").show();
        $("#newUser").hide();
        $("#checkbox").hide();
        $("#signin").hide();
    }


    function displayBackToLogIn() {
        $("#name").remove();
        $("#lastname").remove();
        $("#register").hide();
        $("#back").hide();
        $("#newUser").show();
        $("#checkbox").show();
        $("#signin").show();
    }

    function registerUser() {
        var name = $("#name").val();
        var lastName = $("#lastname").val();
        var email = $("#email").val();
        var password = $("#pwd").val();

        $.post("/api/players", {
            firstName: name,
            lastname: lastName,
            userName: email,
            password: password
        }).done(function () {
            $.post("/api/login", {
                userName: email,
                password: password
            }).done(function () {
                $.getJSON("/api/games", loadedData);
            }).fail(function () {
            });
        }).fail(function (r) {
            console.log(r);
            alert("User already exists");
        });
    }


const displayFormsInputs = function(button, display) {
    document.getElementById("login").style.display = "none";
    $("#logout").css("display", "none");
    $(button).css("display", display);
}



function greet(data) {
    if (data.currentUser.userName == null) {
        alert("Are you sure that you want to leave?")
        displayFormsInputs("#login", "inline");
    } else if (data.currentUser.userName !== null) {
        $("#greetings").html("Hello " + data.currentUser.userName);
        displayFormsInputs("#logout", "inline");
    }
}



 const logIn = function () {
        let obj = {
            userName: $("#email").val(),
            password: $("#pwd").val()
        };
        $.post("/api/login", obj)
            .done(function () {
            $.getJSON("/api/games", loadedData);
            $("#submitlogout").show()/*fail(function () {
                })*/})
            .fail(function (r) {
                console.log(r);
                alert("invalid user");
            })
        ;
}


 const logOut = function () {
    fetch("/api/logout", {
        credentials: 'include',
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/x-www-form-urlencoded'
        },
    }).then(r => {
        if (r.status === 200) {
            $.post("/api/logout").done(function () {
                $.getJSON("/api/games", loadedData);
            }).catch(e => console.log(e))
        }
    })
}


const printList = function (data)  {
    let output = "";
    for (let i = 0; i < data.game.length; i++) {
        let datum = new Date(data.game[i].created);
        // showing only the games that have not ended
        if (data.game[i].isFinished == false) {
            output += "<tr>";
            output += "<td>";
            output += "<a" + linkingData(data.currentUser, data.game[i]) + ">";
            output += datum.toDateString();
            output += "</a>";
            output += "</td>";
            output += "<td>";
            for (let j = 0; j < data.game[i].gamePlayer.length; ++j) {
                output += data.game[i].gamePlayer[j].player.userName + "<br>";
            }
            output += "</td>";
            if (data.game[i].gamePlayer.length === 1) {
                if (data.game[i].gamePlayer[0].player.id !== data.currentUser.id) {
                    output += "<td><button type='button' class='btn btn-default joinGame' data-game-id=" + data.game[i].id + ">" + "Join" + "</button></td>";
                } else {
                    output += "<td><button type='button' class='btn btn-default backGame' data-gamePlayer-id=" + data.game[i].gamePlayer[0].id + ">" + "Return" + "</button></td>";
                }
            } else {
                for (let n = 0; n < data.game[i].gamePlayer.length; ++n) {
                    if (data.game[i].gamePlayer[n].player.id === data.currentUser.id) {
                        output += "<td><button type='button' class='btn btn-default backGame' data-gamePlayer-id=" + data.game[i].gamePlayer[n].id + ">" + "Return" + "</button></td>";
                    }
                }
            }
            output += "</tr>";
        }
    }
    $("#games2").html(output);
}


    const linkingData = function (currentUser, game) {
    let link = "";
    if (currentUser !== "guest user") {
        for (let j = 0; j < game.gamePlayer.length; j++) {
            if (game.gamePlayer[j].player.id == currentUser.id) {
                link = " href='/web/game.html?gp=" + game.gamePlayer[j].id + "' ";
            }
        }
    }
    return link;
     }


    const leaderBoardTable = function (data) {
        let orderedArray = data.leaderBoard;
        orderedArray.sort(function (a, b) {
            return b.total - a.total;
        });
        let tbody = document.getElementById("bodyLeaderBoard");
        tbody.innerHTML = "";
        for (let i = 0; i < data.leaderBoard.length; i++) {
            let tr = document.createElement("tr");
            let tdName = document.createElement("td");
            tdName.innerHTML = data.leaderBoard[i].name;
            tr.appendChild(tdName);
            let arrayWon = data.leaderBoard[i].won;
            let arrayLost = data.leaderBoard[i].lost;
            let arrayDraw = data.leaderBoard[i].draws;
            let totalScore = data.leaderBoard[i].total;
            let tdTotalScore = document.createElement("td");
            tdTotalScore.innerHTML += orderedArray[i].total;
            tr.appendChild(tdTotalScore);
            let tdWon = document.createElement("td");
            tdWon.innerHTML = arrayWon;
            let tdLost = document.createElement("td");
            tdLost.innerHTML = arrayLost;
            let tdDraw = document.createElement("td");
            tdDraw.innerHTML = arrayDraw;
            tr.appendChild(tdWon);
            tr.appendChild(tdLost);
            tr.appendChild(tdDraw);
            tbody.appendChild(tr);
        }
    }


