"use strict";

/**
 * A view to display a list.
 *
 * @param containerId the id of the ul in the html document.
 */
var ListView = function (containerId) {
  this.container = document.getElementById(containerId);
};

/**
 * Updates the view with a new list.
 *
 * @param games the new list with which to update the views.
 */
ListView.prototype.setGames = function (games) {
  this.games = games;
  this.container.innerHTML = "";
  var keys = Object.keys(games);
  for (var i = 0; i < keys.length; i++) {
    var item = document.createElement('li');
    item.innerHTML = this.format(games[keys[i]]);
    item.id = keys[i];
    if (i == 0) {
      item.classList.add("selected");
      this.gameId = keys[i];
    }
    this.container.appendChild(item);
  }
  var self = this;
  if (this.container.addEventListener) this.container.addEventListener("click", function (event) {self.onClick(event);}, "false");
  else if (this.container.attachEvent) this.container.attachEvent("onclick", function (event) {self.onClick(event);});
}

/**
 * Called when the user clicks on an item in the list. It updates which item has been selected.
 *
 * @param event the event from the browser containing which item has been clicked on.
 */
ListView.prototype.onClick = function (event) {
  var items = this.container.children;
  for (var i = 0; i < items.length; i++) {
    items[i].classList.remove("selected");
  }
  if (event.target.id == "list") {
    this.gameId = null;
  } else {
    this.gameId = event.target.id;
    event.target.classList.add("selected");
  }
};

/**
 * Returns the id of the element currently selected.
 *
 * @return the id of the element currently selected.
 */
ListView.prototype.getGameId = function () {
  if (this.gameId) return this.gameId;
  else return null;
};

/**
 * Returns a formatted string for an item in the list.
 *
 * @param teamNames the item to be formatted.
 * @return a formatted string for an item in the list.
 */
ListView.prototype.format = function (teamNames) {
  if (!teamNames.length) return "";
  var string = teamNames[0];
  for (var i = 1; i < teamNames.length; i++) {
    string += " vs " + teamNames[i];
  }
  return string;
};
