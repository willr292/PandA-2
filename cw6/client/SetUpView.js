"use strict";

/**
 * A view that allows users to enter their team id, or to choose which game to
 * spectate.
 *
 * @param viewId the id of the view in the html document.
 * @param joinFormId the id of the form used for joining games in the html document.
 * @param spectateFormId the id of the form used for joining games in the html document.
 * @param listId the id of the ListView in the html document.
 */
var SetUpView = function (viewId, joinFormId, spectateFormId, matchmakerFormId, aiFormId, idFormId, mainPaneId, waitingPaneId, idPaneId, listId) {
  this.view = document.getElementById(viewId);
  this.mainPane = document.getElementById(mainPaneId);
  this.waitingPane = document.getElementById(waitingPaneId);
  this.idPane = document.getElementById(idPaneId);
  this.joinForm = document.getElementById(joinFormId);
  this.spectateForm = document.getElementById(spectateFormId);
  this.matchmakerForm = document.getElementById(matchmakerFormId);
  this.idForm = document.getElementById(idFormId);
  this.aiForm = document.getElementById(aiFormId);
  this.list = new ListView(listId);
  this.teamId = "";

  var self = this;

  var setCheckboxValue = function (name, value) {
    self.aiForm.elements[name + "_Checkbox"].checked = value;
  };
  for (var i = 0; i < AIPlayers.length; i++) {
    setCheckboxValue(AIPlayers[i], true);
  }
  this.mmHost = this.matchmakerForm.elements["hostField"];
  this.mmPort = this.matchmakerForm.elements["portField"];
  this.aiHost = this.aiForm.elements["hostField"];
  this.aiPort = this.aiForm.elements["portField"];

  this.mmHost.value = "localhost";
  this.mmPort.value = "8123";
  this.aiHost.value = "localhost";
  this.aiPort.value = "8121";

  var getValue = function (event) {
    event.preventDefault();
    var teamName = event.target['0'].value;
    event.target['0'].value = "";
    return teamName;
  };
  var sendJoin = function (event) {
    event.preventDefault();
    self.setAIPlayers();

    var join = function () {
      var value = getValue(event);
      self.teamId = value;
      if (value.length && gameMessenger.isConnected()) {
        self.showPane(self.waitingPane);
        self.sendRegisterMessage(value);
      }
    };

    if (AIPlayers.length > 0) self.tryAIConnection(join);
    else join();
  };
  var sendSpectate = function (event) {
    event.preventDefault();
    if (self.list.gameId) requestConnection();
  };
  var tryMatchmaker = function (event, callback) {
    event.preventDefault();
    self.tryMatchmakerConnection(callback);
  };
  var disAI = function (event) {
    event.preventDefault();
    self.disconnectAI();
  };
  var disMatchmaker = function (event) {
    event.preventDefault();
    self.disconnectMatchmaker();
  };
  var beginGame = function (event) {
    event.preventDefault();
    self.showWaitingString("Waiting for other player(s) to begin game.");
    self.showPane(self.waitingPane);
    gameMessenger.sendJoin();
  };
  if (this.joinForm.addEventListener) this.joinForm.addEventListener("submit", sendJoin, false);
  else if (this.joinForm.attachEvent) this.joinForm.attachEvent('onsubmit', sendJoin);
  if (this.spectateForm.addEventListener) this.spectateForm.addEventListener("submit", sendSpectate, false);
  else if (this.spectateForm.attachEvent) this.spectateForm.attachEvent('onsubmit', sendSpectate);
  if (this.matchmakerForm.addEventListener) this.matchmakerForm.addEventListener("submit", function (event) {tryMatchmaker(event, sendEnquire);}, false);
  else if (this.matchmakerForm.attachEvent) this.matchmakerForm.attachEvent('onsubmit', function (event) {tryMatchmaker(event, sendEnquire);});
  if (this.matchmakerForm.addEventListener) this.mmPort.addEventListener("change", disMatchmaker, false);
  else if (this.matchmakerForm.attachEvent) this.mmPort.attachEvent('onchange', disMatchmaker);
  if (this.aiForm.addEventListener) this.aiPort.addEventListener("change", disAI, false);
  else if (this.aiForm.attachEvent) this.aiPort.attachEvent('onchange', disAI);
  if (this.idForm.addEventListener) this.idForm.addEventListener("submit", beginGame, false);
  else if (this.idForm.attachEvent) this.idForm.attachEvent('onsubmit', beginGame);

  document.getElementById("joinButton").disabled = true;
  document.getElementById("joinButton").style.background = "#777777";
  document.getElementById("spectateButton").disabled = true;
  document.getElementById("spectateButton").style.background = "#777777";
};

/**
 * Updates the list of games available to spectate.
 *
 * @param games the new list of games available to spectate.
 */
SetUpView.prototype.updateList = function (games) {
  this.list.setGames(games);
};

/**
 * Sends a REGISTER message to the matchmaker or a REGISTER_MATCH message if the
 * correct data is supplied.
 *
 * @param teamName the team name with which to register with.
 */
SetUpView.prototype.sendRegisterMessage = function (teamName) {
  this.showWaitingString("Waiting for match.");
  var teams = teamName.split(",");
  var registerMessage = {};
  if (teams.length == 1) {
    registerMessage['type'] = "REGISTER";
    registerMessage['student_id'] = teams[0];
  } else {
    registerMessage['type'] = "REGISTER_MATCH";
    registerMessage['student_id'] = teams[0];
    var opponents = [];
    for (var i = 1; i < teams.length; i++) {
      opponents.push(teams[i]);
    }
    registerMessage['opponents'] = opponents;
  }
  gameMessenger.sendMessage(registerMessage);
};

/**
 * Shows this view.
 */
SetUpView.prototype.show = function () {
  var self = this;
  this.view.style.display = "block";
  this.showPane(self.mainPane);
  this.joinForm.elements["teamField"].value = this.teamId;
  this.disconnectAI();
  this.disconnectMatchmaker();
  document.getElementById("joinButton").disabled = true;
  document.getElementById("joinButton").style.background = "#777777";
  document.getElementById("spectateButton").disabled = true;
  document.getElementById("spectateButton").style.background = "#777777";
};

/**
 * Hides this view.
 */
SetUpView.prototype.hide = function () {
  this.view.style.display = "none";
};

SetUpView.prototype.showPane = function(pane) {
  var displayChildren = function (element, display) {
    var children = element.children;
    for(var i = 0; i < children.length; i++) {
      children[i].style.display = display
    }
  }
  this.mainPane.style.display = "none";
  displayChildren(this.mainPane, "none");
  this.waitingPane.style.display = "none";
  displayChildren(this.waitingPane, "none");
  this.idPane.style.display = "none"
  displayChildren(this.idPane, "none");
  pane.style.display = "block";

  displayChildren(pane, "block");
}

/**
 * Shows or hides the waiting pane.
 *
 * @param show whether we want the waiting pane to be shown or hidden.
 */

SetUpView.prototype.showWaitingString = function (string) {
  this.waitingPane.innerHTML = "<h2 class='subheader'>" + string + "</h2>";
};

SetUpView.prototype.showPending = function () {
  var self = this;
  this.showPane(self.waitingPane);
};

SetUpView.prototype.showGameIdPane = function(gameId) {
  var self = this;
  document.getElementById("idLabel").innerHTML = gameId;
  this.showPane(self.idPane);
};

/**
 * Sets values in AIPlayers based on the selected checkboxes
 *
 * @param event the event created when a checkbox state is changed
 */
SetUpView.prototype.setAIPlayers = function () {
  var checkboxes = document.querySelectorAll("input[type=checkbox]");
  AIPlayers = [];
  for (var i = 0; i < checkboxes.length; i++) {
    if (checkboxes[i].checked) AIPlayers.push(checkboxes[i].value);
  }
};

/**
 * Trys a connection to the matchmaker with the data entered into the appropriate fields
 */
SetUpView.prototype.tryMatchmakerConnection = function (passedCallback) {
  var callback = function () {
    document.getElementById("matchmakerButton").style.background = "#63AA7F";
    document.getElementById("joinButton").disabled = false;
    document.getElementById("joinButton").style.background = "#333333";
    document.getElementById("spectateButton").disabled = false;
    document.getElementById("spectateButton").style.background = "#333333";
    if (passedCallback) passedCallback();
  };
  var errorCallback = function (event) {
    document.getElementById("matchmakerButton").style.background = "#333333";
    document.getElementById("joinButton").disabled = true;
    document.getElementById("joinButton").style.background = "#777777";
    document.getElementById("spectateButton").disabled = true;
    document.getElementById("spectateButton").style.background = "#777777";
  };
  var connection = this.getMatchmakerConnection();
  gameMessenger.changeConnection("ws://" + connection.host + ":" + connection.port, callback, errorCallback);
};

/**
 * Trys a connection to the ai with the data entered into the appropriate fields
 */
SetUpView.prototype.tryAIConnection = function (passedCallback) {
  var connection = this.getAiConnection();
  aiMessenger.changeConnection("ws://" + connection.host + ":" + connection.port, passedCallback);
};

SetUpView.prototype.disconnectMatchmaker = function() {
  var callback = function (event) {
    document.getElementById("matchmakerButton").style.background = "#333333";
  };
  gameMessenger.close(callback);
}

SetUpView.prototype.disconnectAI = function() {
  aiMessenger.close();
}

/**
 * Returns the connection information for the matchmaker
 *
 * @return the connection information for the matchmaker
 */
SetUpView.prototype.getMatchmakerConnection = function () {
  var host = this.mmHost.value;
  var port = this.mmPort.value;

  return {host: host, port: port};
};

/**
 * Returns the connection information for the ai
 *
 * @return the connection information for the ai
 */
SetUpView.prototype.getAiConnection = function () {
  var host = this.aiHost.value;
  var port = this.aiPort.value;

  return {host: host, port: port};
};
