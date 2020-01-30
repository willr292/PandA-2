"use strict";

//Denotes whether to log incoming and outgoing messages.
var debug = true;

/**
 * A class to communicate with the matchmaker and the judge.
 *
 * @param url the url of the matchmaker.
 */
var Messenger = function (url) {
  this.url = url;
};

/**
 * Changes which server this messenger connects to.
 *
 * @param url the url of the server to connect to.
 * @param callback the callback function to be called once a connection is established.
 */
Messenger.prototype.changeConnection = function (url, callback, errorCallback) {
  if (this.socket) this.socket.close();
  this.connected = false;
  this.socket = new WebSocket(url);
  var self = this;
  this.socket.onerror = function (event) {
    self.connected = false;
    if (errorCallback) errorCallback(event);
  };
  this.socket.onopen = function (event) {
    self.connected = true;
    if (callback) callback();
  };
  this.socket.onmessage = function (event) {
    self.handleMessage(event.data);
  };
  this.socket.onclose = function (event) {
    self.connected = false;
  };
};

/**
 * Closes which socket this messenger uses.
 *
 * @param callback the callback function to be called once the socket has been closed.
 */
Messenger.prototype.close = function (callback) {
  if (this.socket) this.socket.close();
    if (callback) callback();
};

/**
 * Returns true if this websocket is connected to a server, false otherwise.
 *
 * @return true if this websocket is connected to a server, false otherwise.
 */
Messenger.prototype.isConnected = function () {
  return this.socket && this.connected;
};

/**
 * Writes the specified message to the currently connected websocket.
 *
 * @param message the message to write to the websocket.
 */
Messenger.prototype.sendMessage = function (message) {
  if (debug) console.log("MESSAGE OUT:" + message.type);
  if (this.socket && this.connected) this.socket.send(JSON.stringify(message));
  else console.log("Not sent");
};
