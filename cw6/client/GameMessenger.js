"use strict";

/**
 * Constructs a new messenger to communicate with the matchmaker and the judge.
 *
 * @param url the url of the matchmaker.
 */
var GameMessenger = function () {};

GameMessenger.prototype = Object.create(Messenger.prototype);
GameMessenger.prototype.constructor = GameMessenger;

/**
 * Decodes a message and acts accordingly.
 *
 * @param message the message to react to.
 */
GameMessenger.prototype.handleMessage = function (message) {
  var decodedMessage = JSON.parse(message);
  if (debug) console.log("MESSAGE IN:" + decodedMessage.type);
  switch (decodedMessage.type) {
    case "REGISTERED":
      this.interpretRegistered(decodedMessage);
      break;
    case "READY":
      this.interpretReady(decodedMessage);
      break;
    case "PENDING_GAME":
      this.interpretPendingGame(decodedMessage);
      break;
    case "NOTIFY_TURN":
      this.interpretNotifyTurn(decodedMessage);
      break;
    case "NOTIFY":
      this.interpretNotify(decodedMessage);
      break;
    case "GAME_OVER":
      this.interpretGameOver(decodedMessage);
      break;
    case "GAMES":
      this.interpretGames(decodedMessage);
      break;
    case "CONNECTION":
      this.interpretConnection(decodedMessage);
    default:
      break;
  }
};

/**
 * Shows the view containing the game id.
 *
 * @param messageRegistered the REGISTERED message.
 */
GameMessenger.prototype.interpretRegistered = function (messageRegistered) {
  gameId = messageRegistered['game_id'];
  guiConnector.showGameIdPane(gameId);
  this.storedMessage = messageRegistered;
};

/**
 * Updates the UI with the information contained in the READY message.
 *
 * @param messageReady the READY message.
 */
GameMessenger.prototype.interpretReady = function (messageReady) {
  //TODO:
  guiConnector.setSetUpViewVisible(false);

  var rounds = messageReady['rounds'];
  var currentRound = messageReady['current_round'];
  guiConnector.setTicketView(rounds,currentRound);

  var locations = messageReady['locations'];
  guiConnector.setPlayerLocations(locations);

  var tickets = messageReady['tickets'];
  guiConnector.setPlayerTickets(tickets);

  if(AIPlayers.length > 0 && aiMessenger.isConnected()) {
    aiMessenger.sendMessage(messageReady);
  }
};

/**
 * Shows the pending game view (Where you wait until your oppenent joins).
 *
 * @param messagePendingGame the PENDING_GAME message.
 */
GameMessenger.prototype.interpretPendingGame = function (messagePendingGame) {
  var missingPlayers = messagePendingGame['opponents'];
  guiConnector.showStringInSetUpView("Waiting for: " + missingPlayers.toString());
};

/**
 * Updates the UI accordingly using the information in the NOTIFY message.
 *
 * @param messageNotify the NOTIFY message.
 */
GameMessenger.prototype.interpretNotify = function (messageNotify) {
  //TODO:\

  var move = messageNotify['move'];
  var player = move['colour'];
  var location = move['target'];
  var ticket = move['ticket'];
  var moveType = messageNotify['move_type'];

  if(moveType == "MoveDouble") {
    guiConnector.removeTicket(player, "Double");
  }
  else if (moveType == "MovePass") {
    //Do nothing as Pass played.
  }
  else {
    guiConnector.removeTicket(player, ticket);
    guiConnector.animatePlayer(player, location);
  }

  if(player == "Black") {
    guiConnector.updateTicketView(ticket, location);
  }

  if(AIPlayers.length > 0 && aiMessenger.isConnected()) {
     aiMessenger.sendMessage(messageNotify);

  }
};

/**
 * Shows the SetUpView again and sets up for a new game.
 * If there are some AI players, it notifies the AI of the game over.
 *
 * @param messageGameOver the GAME_OVER message.
 */
GameMessenger.prototype.interpretGameOver = function (messageGameOver) {
  //TODO:
  guiConnector.setGameOver(messageGameOver);
  if(AIPlayers.length > 0 && aiMessenger.isConnected()) {
        aiMessenger.sendMessage(messageGameOver);
    }
};

/**
 * Updates the list of available games to spectate.
 *
 * @param messageGames the GAMES message.
 */
GameMessenger.prototype.interpretGames = function (messageGames) {
  guiConnector.updateGamesList(messageGames['games']);
};

/**
 * Sends a spectate message to the judge whose connection information is
 * contained within the CONNECTION message.
 *
 * @param messageConnection the CONNECTION message.
 */
GameMessenger.prototype.interpretConnection = function (messageConnection) {
  //TODO:
  var host = messageConnection['host'];
  var port = messageConnection['port'];
  var url = "ws://" + host + ":" + port;
  var message = {};
  message["game_id"] = guiConnector.getSelectedGame();
  message["type"] = "SPECTATE"; //TODO Not sure about this.

  gameMessenger.changeConnection(url);
  gameMessenger.sendMessage(message);
};

/**
 * Sends the notify turn message to the AI server if the current player is an AI player.
 * Updates the views accordingly.
 *
 * @param messageNotifyTurn the NOTIFY_TURN message.
 */
GameMessenger.prototype.interpretNotifyTurn = function (messageNotifyTurn) {
  //TODO:
  var validMoves = messageNotifyTurn['valid_moves'];
  var move = validMoves[0]['move'];
  var player = move['colour'];

  guiConnector.startTurn(messageNotifyTurn);
    if(AIPlayers.length > 0 && aiMessenger.isConnected() && AIPlayers.indexOf(player)>-1) {
        aiMessenger.sendMessage(messageNotifyTurn);

    }
};

/**
 * Sends a JOIN message to the server whose connection details are contained in the REGISTERED message.
 *
 * @param message the REGISTERED message containing connection and game information.
 */
GameMessenger.prototype.sendJoin = function () {
  var self = this;
  var message = this.storedMessage;
  var callback = function () {
    var colours = message.colours;
    var aiColours = [];

    for (var i = 0; i < colours.length; i++) {
      if (AIPlayers.indexOf(colours[i]) != -1) {
        aiColours.push(colours[i]);
      } else {
        var joinMessage = {};
        joinMessage['type'] = "JOIN";
        joinMessage['colour'] =  colours[i];
        joinMessage['game_id'] = message['game_id'];
        self.sendMessage(joinMessage);
      }
    }
    if (aiColours.length > 0 && aiMessenger && aiMessenger.isConnected()) {
      message.colours = aiColours;
      if (aiMessenger) aiMessenger.sendMessage(message);
    }
  };
  var precallback = function () {
    if (self.isConnected()) {
      callback();
    }
  };
  this.changeConnection("ws://" + message.host + ":" + message.port, precallback);
};
