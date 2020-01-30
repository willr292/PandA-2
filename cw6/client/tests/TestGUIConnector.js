"use strict";

var GUIConnector = function () {
  this.cleanUpForTest();
};

GUIConnector.prototype.setTicketView = function (rounds, currentRound) {
  this.testContainer.setTicketView = [rounds, currentRound];
};

GUIConnector.prototype.setPlayerLocations = function (locations) {
  this.testContainer.setPlayerLocations = [locations];
};

GUIConnector.prototype.setPlayerTickets = function (tickets) {
  this.testContainer.setPlayerTickets = [tickets];
};

GUIConnector.prototype.animatePlayer = function (player, location) {
  this.testContainer.animatePlayer = [player, location];
};

GUIConnector.prototype.removeTicket = function (player, ticket) {
  this.testContainer.removeTicket = [player, ticket];
};

GUIConnector.prototype.updateTicketView = function (ticket, target) {
  this.testContainer.updateTicketView = [ticket, target];
};

GUIConnector.prototype.startTurn = function (notifyTurnMessage, aiMove) {
  this.testContainer.startTurn = [notifyTurnMessage, aiMove];
};

GUIConnector.prototype.endTurn = function (move) {
  this.testContainer.endTurn = [move];
};

GUIConnector.prototype.setGameOver = function (gameOverMessage) {
  this.testContainer.setGameOver = [gameOverMessage];
};

GUIConnector.prototype.updateGamesList = function (games) {
  this.testContainer.updateGamesList = [games];
};

GUIConnector.prototype.getSelectedGame = function () {
  this.testContainer.getSelectedGame = [true];
  return 42;
};

GUIConnector.prototype.showGameIdPane = function (gameId) {
  this.testContainer.showGameIdPane = [gameId];
};

GUIConnector.prototype.showStringInSetUpView = function (string) {
  this.testContainer.showStringInSetUpView = [string]
};

GUIConnector.prototype.setSetUpViewVisible = function (show) {
  this.testContainer.setSetUpViewVisible = [show];
};

GUIConnector.prototype.cleanUpForTest = function () {
  this.testContainer = {
    "setTicketView": [],
    "setPlayerLocations": [],
    "setPlayerTickets": [],
    "animatePlayer": [],
    "removeTicket": [],
    "updateTicketView": [],
    "startTurn": [],
    "endTurn": [],
    "setGameOver": [],
    "updateGamesList": [],
    "getSelectedGame": [],
    "showGameIdPane": [],
    "showStringInSetUpView": [],
    "setSetUpViewVisible": []
  };
  this.testContainer.setSetUpViewVisible = [];
};
