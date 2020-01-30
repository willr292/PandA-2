"use strict";

/**
 * A view to display messages to the user.
 *
 * @param viewId the id of the view in the html document.
 * @param h1Id the id of the h1 element in the html document.
 * @param timeLimit the time to show each message for.
 */
var NotifyView = function (viewId, h1Id, timeLimit) {
  this.view = document.getElementById(viewId);
  this.view.style['z-index'] = "-1";
  this.view.style['opacity'] = "0";
  this.h1 = document.getElementById(h1Id);
  this.timeLimit = timeLimit * 1000;
  this.queue = [];
  var self = this;
  if (this.view.addEventListener) this.view.addEventListener("mouseenter", function () {self.clear();}, false);
  else this.view.attachEvent("onmouseenter", function () {self.clear();});
};

/**
 * Adds a message to the queue of messages to be displayed.
 *
 * @param message the message to display.
 */
NotifyView.prototype.setText = function (message) {
  this.queue.push(message);
  this.pollQueue();
};

/**
 * Polls the queue of messages taking them off and displaying them in turn.
 */
NotifyView.prototype.pollQueue = function () {
  if (this.queue.length > 0 && this.view.style['z-index'] == -1) {
    this.view.style['z-index'] = "2";
    this.view.style['opacity'] = "1";
    this.h1.innerHTML = this.queue.shift();
    var self = this;
    var remove = function () {
      self.cancel();
      self.pollQueue();
    };
    this.id = window.setTimeout(remove, this.timeLimit);
  }
};

/**
 * Hides the view and stops polling the queue.
 */
NotifyView.prototype.cancel = function () {
  if (this.id) window.clearTimeout(this.id);
  this.view.style['z-index'] = "-1";
  this.view.style['opacity'] = "0";
};

/**
 * Hides the view and clears the queue of messages.
 */
NotifyView.prototype.clear = function () {
  this.cancel();
  this.queue = [];
};
