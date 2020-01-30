"use strict";

QUnit.test("Judge: Ready message received correctly.", function (assert) {
  cleanUpForTest();
  var readyMessage = {
    "type": "READY",
    "game_id": 0,
    "n_detectives": 1,
    "rounds": [
      true,
      true
    ],
    "colours": [
      "Black",
      "Blue"
    ],
    "locations": {
      "Black": 42,
      "Blue": 27
    },
    "tickets": {
      "Black": {
        "Taxi": 10,
        "Bus": 10,
        "Underground": 10,
        "Secret": 5,
        "Double": 2
      },
      "Blue": {
        "Taxi": 10,
        "Bus": 8,
        "Underground": 4,
        "Secret": 0,
        "Double": 0
      }
    },
    "current_round": 0
  };
  gameMessenger.handleMessage(JSON.stringify(readyMessage));
  assert.strictEqual(guiConnector.testContainer.setSetUpViewVisible[0] == false,
            true,
            "The hide method in SetUpView should have been called.");
  assert.ok(guiConnector.testContainer.setTicketView.length == 2,
            "The setTicketView method in GUIConnector should have been called.");
  assert.deepEqual(guiConnector.testContainer.setTicketView[0],
                  [ true, true ],
                  "The list of rounds should not be changed when passed into setTicketView.");
  assert.equal(guiConnector.testContainer.setTicketView[1],
              0,
              "The current round should not be changed when passed into setTicketView.");

  assert.ok(guiConnector.testContainer.setPlayerLocations.length == 1,
            "The setPlayerLocations method in GUIConnector should have been called.");
  assert.deepEqual(guiConnector.testContainer.setPlayerLocations[0],
                  {
                    "Black": 42,
                    "Blue": 27
                  },
                  "The locations of the players should not be changed when passed into setPlayerLocations.");

  assert.ok(guiConnector.testContainer.setPlayerTickets.length == 1,
            "The setPlayerTickets method in GUIConnector should have been called.");
  assert.deepEqual(guiConnector.testContainer.setPlayerTickets[0],
                  {
                    "Black": {
                      "Taxi": 10,
                      "Bus": 10,
                      "Underground": 10,
                      "Secret": 5,
                      "Double": 2
                    },
                    "Blue": {
                      "Taxi": 10,
                      "Bus": 8,
                      "Underground": 4,
                      "Secret": 0,
                      "Double": 0
                    }
                  },
                  "The tickets of the players should not be changed when passed into setPlayerTickets.");
});

QUnit.test("Judge: Notify turn message received correctly.", function (assert) {
  cleanUpForTest();
  var notifyTurnMessage = {
    "type": "NOTIFY_TURN",
    "game_id": 0,
    "location": 42,
    "timestamp": 123456789,
    "token": 42,
    "valid_moves": [
      {
        "move": {
          "colour": "Blue"
        },
        "move_type": "MovePass"
      }
    ]
  };
  gameMessenger.handleMessage(JSON.stringify(notifyTurnMessage));
  assert.ok(guiConnector.testContainer.startTurn.length > 0,
            "The startTurn method in GUIConnector should have been called.");
  assert.deepEqual(guiConnector.testContainer.startTurn[0],
                    notifyTurnMessage,
                    "The decoded notify turn message should have been passed"
                    + " directly to the startTurn method in GUIConnector.");
});

QUnit.test("Judge: Notify message received correctly - 1.", function (assert) {
  cleanUpForTest();
  var notifyMessage = {
    "type": "NOTIFY",
    "game_id": 0,
    "move": {
      "colour": "Blue",
      "target": 14,
      "ticket": "Taxi"
    },
    "move_type": "MoveTicket"
  };
  gameMessenger.handleMessage(JSON.stringify(notifyMessage));
  assert.equal(guiConnector.testContainer.animatePlayer.length,
            2,
            "The animatePlayer method in GUIConnector should have been called"
            + " as a MoveTicket was in the notify message.");
  assert.equal(guiConnector.testContainer.animatePlayer[0],
              "Blue",
              "The colour of the player should stay the same when passed to the"
              + " animatePlayer method.");
  assert.equal(guiConnector.testContainer.animatePlayer[1],
              14,
              "The target of the player should stay the same when passed to the"
              + " animatePlayer method.");

  assert.ok(guiConnector.testContainer.removeTicket.length == 2,
            "The removeTicket method in GUIConnector should have been called"
            + " as a MoveTicket was in the notify message.");
  assert.equal(guiConnector.testContainer.removeTicket[0],
              "Blue",
              "The colour of the player should stay the same when passed to the"
              + " removeTicket method.");
  assert.equal(guiConnector.testContainer.removeTicket[1],
              "Taxi",
              "The ticket of the player should stay the same when passed to the"
              + " removeTicket method.");

  assert.ok(guiConnector.testContainer.updateTicketView.length == 0,
            "The updateTicketView method in GUIConnector should not have been"
            + " called as the colour of the player was not Black");
});

QUnit.test("Judge: Notify message received correctly - 2.", function (assert) {
  cleanUpForTest();
  var notifyMessage1 = {
    "type": "NOTIFY",
    "game_id": 0,
    "move": {
      "colour": "Black",
      "move1": {
        "colour": "Black",
        "target": 14,
        "ticket": "Taxi"
      },
      "move2": {
        "colour": "Black",
        "target": 20,
        "ticket": "Bus"
      }
    },
    "move_type": "MoveDouble"
  };
  var notifyMessage2 = {
    "type": "NOTIFY",
    "game_id": 0,
    "move": {
      "colour": "Black",
      "target": 14,
      "ticket": "Taxi"
    },
    "move_type": "MoveTicket"
  };
  var notifyMessage3 = {
    "type": "NOTIFY",
    "game_id": 0,
    "move": {
      "colour": "Black",
      "target": 20,
      "ticket": "Bus"
    },
    "move_type": "MoveTicket"
  };
  gameMessenger.handleMessage(JSON.stringify(notifyMessage1));
  assert.ok(guiConnector.testContainer.removeTicket.length == 2,
            "The removeTicket method in GUIConnector should have been called"
            + " as a MoveDouble was in the notify message.");
  assert.equal(guiConnector.testContainer.removeTicket[0],
              "Black",
              "The colour of the player should stay the same when passed to the"
              + " removeTicket method.");
  assert.equal(guiConnector.testContainer.removeTicket[1],
              "Double",
              "The ticket of the player should be 'Double' when passed to the"
              + " removeTicket method.");
  assert.ok(guiConnector.testContainer.animatePlayer.length == 0,
            "The animatePlayer method in GUIConnector should not have been called"
            +" as a MoveDouble was in the notify message.");

  gameMessenger.handleMessage(JSON.stringify(notifyMessage2));
  assert.ok(guiConnector.testContainer.animatePlayer.length == 2,
            "The animatePlayer method in GUIConnector should have been called"
            + " as a MoveTicket was in the notify message.");
  assert.equal(guiConnector.testContainer.animatePlayer[0],
              "Black",
              "The colour of the player should stay the same when passed to the"
              + " animatePlayer method.");
  assert.equal(guiConnector.testContainer.animatePlayer[1],
              14,
              "The target of the player should stay the same when passed to the"
              + " animatePlayer method.");

  assert.ok(guiConnector.testContainer.removeTicket.length == 2,
            "The removeTicket method in GUIConnector should have been called"
            + " as a MoveTicket was in the notify message.");
  assert.equal(guiConnector.testContainer.removeTicket[0],
              "Black",
              "The colour of the player should stay the same when passed to the"
              + " removeTicket method.");
  assert.equal(guiConnector.testContainer.removeTicket[1],
              "Taxi",
              "The ticket of the player should stay the same when passed to the"
              + " removeTicket method.");

  assert.ok(guiConnector.testContainer.updateTicketView.length == 2,
            "The updateTicketView method in GUIConnector should have been"
            + " called as the colour of the player was Black");
  assert.equal(guiConnector.testContainer.updateTicketView[0],
              "Taxi",
              "The ticket of the player should stay the same when passed to the"
              + " updateTicketView method.");
  assert.equal(guiConnector.testContainer.updateTicketView[1],
              "14",
              "The target of the player should stay the same when passed to the"
              + " updateTicketView method.");

  gameMessenger.handleMessage(JSON.stringify(notifyMessage3));
  assert.ok(guiConnector.testContainer.animatePlayer.length == 2,
            "The animatePlayer method in GUIConnector should have been called"
            + " as a MoveTicket was in the notify message.");
  assert.equal(guiConnector.testContainer.animatePlayer[0],
              "Black",
              "The colour of the player should stay the same when passed to the"
              + " animatePlayer method.");
  assert.equal(guiConnector.testContainer.animatePlayer[1],
              20,
              "The target of the player should stay the same when passed to the"
              + " animatePlayer method.");

  assert.ok(guiConnector.testContainer.removeTicket.length == 2,
            "The removeTicket method in GUIConnector should have been called"
            + " as a MoveTicket was in the notify message.");
  assert.equal(guiConnector.testContainer.removeTicket[0],
              "Black",
              "The colour of the player should stay the same when passed to the"
              + " removeTicket method.");
  assert.equal(guiConnector.testContainer.removeTicket[1],
              "Bus",
              "The ticket of the player should stay the same when passed to the"
              + " removeTicket method.");

  assert.ok(guiConnector.testContainer.updateTicketView.length == 2,
            "The updateTicketView method in GUIConnector should have been"
            + " called as the colour of the player was Black");
  assert.equal(guiConnector.testContainer.updateTicketView[0],
              "Bus",
              "The ticket of the player should stay the same when passed to the"
              + " updateTicketView method.");
  assert.equal(guiConnector.testContainer.updateTicketView[1],
              20,
              "The target of the player should stay the same when passed to the"
              + " updateTicketView method.");
});

QUnit.test("Judge: Notify message received correctly - 3.", function (assert) {
  cleanUpForTest();
  var notifyMessage = {
    "type": "NOTIFY",
    "game_id": 0,
    "move": {
      "colour": "Blue"
    },
    "move_type": "MovePass"
  };
  gameMessenger.handleMessage(JSON.stringify(notifyMessage));
  assert.ok(guiConnector.testContainer.animatePlayer.length == 0,
            "The animatePlayer method in GUIConnector should not have been called"
            + " as a MovePass was in the notify message.");

  assert.ok(guiConnector.testContainer.removeTicket.length == 0,
            "The removeTicket method in GUIConnector should not have been called"
            + " as a MovePass was in the notify message.");

  assert.ok(guiConnector.testContainer.updateTicketView.length == 0,
            "The updateTicketView method in GUIConnector should not have been"
            + " called as the colour of the player was not Black");
});

QUnit.test("Judge: Game over message received correctly.", function (assert) {
  cleanUpForTest();
  var gameOverMessage = {
    "type": "GAME_OVER",
    "winners": ["Black"],
    "game_id": 0
  };
  gameMessenger.handleMessage(JSON.stringify(gameOverMessage));
  assert.equal(gameId,
                null,
                "The global gameId should be set to null when the game is over.");
  assert.deepEqual(guiConnector.testContainer.setGameOver[0],
                gameOverMessage,
                "The decoded game over message should have been passed directly"
                + " to the setGameOver method in GUIConnector.");
});
