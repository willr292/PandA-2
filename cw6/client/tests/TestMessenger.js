"use strict";

var debug = true;

var Messenger = function (url) {
  this.url = url;
  this.cleanUpForTest();
};

Messenger.prototype.changeConnection = function (url, callback, errorCallback) {
  this.connected = true;
  this.url = url;
  this.testContainer.changeConnection = [url];
  if (callback) callback();
};

Messenger.prototype.close = function (callback) {
  this.testContainer.close = [true];
  if (callback) callback();
};

Messenger.prototype.setConnected = function (connected) {
  this.connected = connected;
}

Messenger.prototype.isConnected = function () {
  this.testContainer.isConnected = [true];
  return this.connected;
};

Messenger.prototype.sendMessage = function (message) {
  this.testContainer.sentMessages.push(message);
};

Messenger.prototype.cleanUpForTest = function () {
  this.testContainer = {
    "changeConnection": [],
    "close": [],
    "isConnected": []
  };
  this.testContainer.sentMessages = [];
  this.connected = true;
};
