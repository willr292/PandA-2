"use strict";

QUnit.test("AI: Ready message is recieved correctly.", function (assert) {
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

  aiMessenger.setConnected(false);
  gameMessenger.handleMessage(JSON.stringify(readyMessage));
  var messages = aiMessenger.testContainer.sentMessages;
  assert.equal(aiMessenger.testContainer.sentMessages.length,
                0,
                "The AIMessenger should not try to send a message if the ai is not connected.");

  cleanUpForTest();
  AIPlayers = [];
  gameMessenger.handleMessage(JSON.stringify(readyMessage));
  messages = aiMessenger.testContainer.sentMessages;
  assert.equal(messages.length,
                0,
                "The AIMessenger should not try to send a message if no ai players are in a game.");

  cleanUpForTest();
  AIPlayers = ["Blue", "Green"];
  gameMessenger.handleMessage(JSON.stringify(readyMessage));
  messages = aiMessenger.testContainer.sentMessages;
  assert.deepEqual(messages[0],
                    readyMessage,
                    "The AIMessenger should forward the ready message to the ai as there are ai players.");
});

QUnit.test("AI: Notify turn message is recieved correctly.", function (assert) {
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
  aiMessenger.setConnected(false);
  gameMessenger.handleMessage(JSON.stringify(notifyTurnMessage));
  var messages = aiMessenger.testContainer.sentMessages;
  assert.equal(aiMessenger.testContainer.sentMessages.length,
                0,
                "The AIMessenger should not try to send a message if the ai is not connected.");

  cleanUpForTest();
  AIPlayers = [];
  gameMessenger.handleMessage(JSON.stringify(notifyTurnMessage));
  messages = aiMessenger.testContainer.sentMessages;
  assert.equal(messages.length,
                0,
                "The AIMessenger should not try to send a message if no ai players are in a game.");

  cleanUpForTest();
  AIPlayers = ["Blue", "Green"];
  gameMessenger.handleMessage(JSON.stringify(notifyTurnMessage));
  messages = aiMessenger.testContainer.sentMessages;
  assert.ok(guiConnector.testContainer.startTurn.length > 1,
            "The startTurn method in GUIConnector should have been called with a second argument.");
  assert.deepEqual(messages[0],
                    notifyTurnMessage,
                    "The AIMessenger should forward the notify turn message to the ai as the Blue player is an ai player.");

  cleanUpForTest();
  AIPlayers = ["Black", "Green"];
  gameMessenger.handleMessage(JSON.stringify(notifyTurnMessage));
  messages = aiMessenger.testContainer.sentMessages;
  assert.deepEqual(messages.length,
                    0,
                    "The AIMessenger should not send the notify turn message to the ai as the Blue player is not an ai player.");
});

QUnit.test("AI: Notify message is recieved correctly - 1.", function (assert) {
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

  aiMessenger.setConnected(false);
  gameMessenger.handleMessage(JSON.stringify(notifyMessage));
  var messages = aiMessenger.testContainer.sentMessages;
  assert.equal(messages.length, 0, "The AIMessenger should not try to send a message if the ai is not connected.");

  cleanUpForTest();
  AIPlayers = [];
  gameMessenger.handleMessage(JSON.stringify(notifyMessage));
  messages = aiMessenger.testContainer.sentMessages;
  assert.equal(messages.length, 0, "The AIMessenger should not try to send a message if no ai players are in a game.");

  cleanUpForTest();
  AIPlayers = ["Blue", "Green"];
  gameMessenger.handleMessage(JSON.stringify(notifyMessage));
  messages = aiMessenger.testContainer.sentMessages;
  assert.deepEqual(messages[0], notifyMessage, "The AIMessenger should forward the notify message to the ai.");
});

QUnit.test("AI: Notify message is recieved correctly - 2.", function (assert) {
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
  cleanUpForTest();
  AIPlayers = ["Blue", "Green"];
  gameMessenger.handleMessage(JSON.stringify(notifyMessage1));
  gameMessenger.handleMessage(JSON.stringify(notifyMessage2));
  gameMessenger.handleMessage(JSON.stringify(notifyMessage3));
  var messages = aiMessenger.testContainer.sentMessages;
  assert.deepEqual(messages[0], notifyMessage1, "The AIMessenger should forward the notify double move message to the ai.");
  assert.deepEqual(messages[1], notifyMessage2, "The AIMessenger should forward the notify move 1 message to the ai.");
  assert.deepEqual(messages[2], notifyMessage3, "The AIMessenger should forward the notify move 2 message to the ai.");
});

QUnit.test("AI: Game over message is recieved correctly.", function (assert) {
  cleanUpForTest();
  var gameOverMessage = {
    "type": "GAME_OVER",
    "winners": ["Black"],
    "game_id": 0
  };

  aiMessenger.setConnected(false);
  gameMessenger.handleMessage(JSON.stringify(gameOverMessage));
  var messages = aiMessenger.testContainer.sentMessages;
  assert.equal(aiMessenger.testContainer.sentMessages.length,
                0,
                "The AIMessenger should not try to send a message if the ai is not connected.");

  cleanUpForTest();
  AIPlayers = [];
  gameMessenger.handleMessage(JSON.stringify(gameOverMessage));
  messages = aiMessenger.testContainer.sentMessages;
  assert.equal(messages.length,
                0,
                "The AIMessenger should not try to send a message if no ai players are in a game.");

  cleanUpForTest();
  AIPlayers = ["Blue", "Green"];
  gameMessenger.handleMessage(JSON.stringify(gameOverMessage));
  messages = aiMessenger.testContainer.sentMessages;
  assert.deepEqual(messages[0],
                    gameOverMessage,
                    "The AIMessenger should forward the game over message to the ai.");
});
