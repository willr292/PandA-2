"use strict";

var GUIConnector = function () {};

GUIConnector.prototype.setTicketView = function (rounds, currentRound) {
  currentRound++;
  var newTicketData = {
    rounds: [],
    roundBools: rounds.slice(currentRound),
    locations: [],
    tickets: []
  };
  for (var i = 0; i < rounds.length - currentRound; i++) {
    newTicketData.rounds[i] = i + currentRound;
    newTicketData.locations[i] = null;
    newTicketData.tickets[i] = null;
  }
  ticketView.ticketData = newTicketData;
  ticketView.repaint();
};

GUIConnector.prototype.setPlayerLocations = function (locations) {
  var keys = Object.keys(locations);
  for (var i = 0; i < keys.length; i++) {
    board.setPosition(keys[i], locations[keys[i]]);
  }
};

GUIConnector.prototype.setPlayerTickets = function (tickets) {
  var colours = Object.keys(tickets);
  for (var i = 0; i < colours.length; i++) {
    if (colours[i] == "White") {
      players[colours[i]].tickets = {
        "Taxi_Dark": tickets[colours[i]]["Taxi"],
        "Bus_Dark": tickets[colours[i]]["Bus"],
        "Underground_Dark": tickets[colours[i]]["Underground"]
      };
    } else if (colours[i] == "Black") {
      players[colours[i]].tickets = {
        "Secret": tickets[colours[i]]["Secret"],
        "Double": tickets[colours[i]]["Double"]
      };
    } else {
      players[colours[i]].tickets = {
        "Taxi": tickets[colours[i]]["Taxi"],
        "Bus": tickets[colours[i]]["Bus"],
        "Underground": tickets[colours[i]]["Underground"]
      };
    }
    players[colours[i]].repaint();
  }
};

GUIConnector.prototype.animatePlayer = function (player, location) {
  board.animateToPosition(player, location);
  board.cancelPing();
  notify.clear();
};

GUIConnector.prototype.removeTicket = function (player, ticket) {
  if (players[player].tickets[ticket]) {
    players[player].tickets[ticket]--;
  } else if (players[player].tickets[ticket + "_Dark"]) {
    players[player].tickets[ticket + "_Dark"]--;
  }
  players[player].repaint();
};

GUIConnector.prototype.updateTicketView = function (ticket, target) {
  var ticketData = ticketView.ticketData;
  for (var i = 0; i < ticketData.rounds.length; i++) {
    if (!ticketData.tickets[i]) {
      ticketData.tickets[i] = ticket;
      if (ticketData.roundBools[i]) ticketData.locations[i] = target;
      break;
    }
  }
  ticketView.repaint();
};

GUIConnector.prototype.startTurn = function (notifyTurnMessage, aiMove) {
  board.aiPlaying = aiMove;

  var addTokenToMoves = function (validMoves, token) {
    for (var i = 0; i < validMoves.length; i++) {
      validMoves[i]['token'] = token;
    }
  };
  if (!board.aiPlaying) {
    var validMoves = notifyTurnMessage['valid_moves'];
    var token = notifyTurnMessage['token'];
    addTokenToMoves(validMoves, token);
    if (validMoves.length == 1 && validMoves[0]['move_type'] == "MovePass")
      return this.endTurn(validMoves[0]);
    board.setValidMoves(notifyTurnMessage['valid_moves']);
  }
  board.currentPlayer = notifyTurnMessage['valid_moves'][0].move.colour;
  notify.clear();
  if (board.currentPlayer == "Black") notify.setText("It's Mr X's turn.");
  else notify.setText("It's the " + board.currentPlayer + " detective's turn.");
  board.cancelAnimation(board.currentPlayer);
  board.setPosition(board.currentPlayer, notifyTurnMessage['location']);
  board.startPing();
  timer.start();
};

GUIConnector.prototype.endTurn = function (move) {
  move['type'] = "MOVE";
  move['game_id'] = gameId;
  gameMessenger.sendMessage(move);
  board.setValidMoves(null);
  board.currentPlayer = null;
  timer.reset();
};

GUIConnector.prototype.setGameOver = function (gameOverMessage) {
  var notifyWinners = function (winners) {
    for (var i = 0; i < winners.length; i++) {
      if (winners[i] == "Black") {
        notify.setText("Mr X wins.");
        return;
      }
    }
    notify.setText("Detectives win.");
  };
  timer.cancel();
  board.setValidMoves(null);
  board.currentPlayer = null;
  notify.clear();
  notifyWinners(gameOverMessage.winners);
  board.cancelPing();
  window.setTimeout(function () {setUpView.show();}, 5000);
};

GUIConnector.prototype.updateGamesList = function (games) {
  setUpView.updateList(games);
};

GUIConnector.prototype.getSelectedGame = function () {
  return setUpView.list.getGameId();
};

GUIConnector.prototype.showGameIdPane = function (gameId) {
  setUpView.showGameIdPane(gameId);
};

GUIConnector.prototype.showStringInSetUpView = function (string) {
  setUpView.showWaitingString(string);
};

GUIConnector.prototype.setSetUpViewVisible = function (show) {
  if (show) setUpView.show();
  else setUpView.hide();
};
