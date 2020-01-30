"use strict";

QUnit.test("Matchmaker: Connection message is recieved correctly", function (assert) {
  cleanUpForTest();
  var connectionMessage = {
    type:"CONNECTION",
    host:"host",
    port:1234
  };
  gameMessenger.handleMessage(JSON.stringify(connectionMessage));

  var expectedURL = "ws://host:1234"
  var url = gameMessenger.testContainer.changeConnection[0];
  assert.equal(url, expectedURL, "The changeConnection method should have been"
    + " called with the new connection information before sending the spectate"
    + " message.");

  var expectedMessage = {};
  expectedMessage["type"] = "SPECTATE";
  expectedMessage["game_id"] = 42;
  var messages = gameMessenger.testContainer.sentMessages;
  assert.deepEqual(messages[0], expectedMessage, "The spectate message should"
    +" have been sent after changing the connection.");
});

QUnit.test("Matchmaker: Pending game message is recieved correctly", function (assert) {
  cleanUpForTest();
  var pengingGameMessage = {
    type:"PENDING_GAME",
    game_id:1,
    opponents:[
      "player1",
      "player2"
    ]
  };
  gameMessenger.handleMessage(JSON.stringify(pengingGameMessage));

  var showString = guiConnector.testContainer.showStringInSetUpView;
  assert.ok(showString.length == 1,
            "The showStringInSetUpView method in GUIConnector should have been called");
  assert.equal(showString[0], "Waiting for: player1,player2", "The string passed"
    +" into showWaitingString should be correct.");
});

QUnit.test("Matchmaker: Registered message is recieved correctly", function (assert) {
  cleanUpForTest();
  var registeredMessage = {
    type:"REGISTERED",
    game_id:1,
    colours:[
      "Blue",
      "Green"
    ],
    host:"host",
    port:1234
  };
  gameMessenger.handleMessage(JSON.stringify(registeredMessage));

  assert.equal(gameId, 1, "The global variable gameId should have been updated.");

  var gameIdPane = guiConnector.testContainer.showGameIdPane;
  assert.ok(gameIdPane.length == 1,
            "The showGameIdPane method in GUIConnector should have been called");
  assert.equal(gameIdPane[0], 1, "The gameId passed into setUpView.showGameIdPane should be correct.");

  assert.deepEqual(gameMessenger.storedMessage, registeredMessage, "The registered"
    +" message should have been stored in gameMessenger's variable storedMessage.");
});
