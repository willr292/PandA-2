"use strict";

var AIPlayers = ["Black"];

var gameId;

var board = {};
var guiConnector = new GUIConnector();
var gameMessenger = new GameMessenger();
var aiMessenger = new AIMessenger();

function sendEnquire () {
  var enquireMessage = {};
  enquireMessage["type"] = "ENQUIRE";
  gameMessenger.sendMessage(enquireMessage);
}

function requestConnection () {
  var gameId = guiConnector.testContainer.getSelectedGame();
  var requestMessage = {};
  requestMessage['type'] = 'REQUEST_CONNECTION';
  requestMessage['game_id'] = gameId;
  gameMessenger.sendMessage(requestMessage);
}

function cleanUpForTest () {
  gameMessenger.cleanUpForTest();
  aiMessenger.cleanUpForTest();
  guiConnector.cleanUpForTest();
}
