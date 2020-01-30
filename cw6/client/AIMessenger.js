"use strict";

/**
 * Constructs a new messenger to communicate with the AI.
 *
 * @param url the url of the AI server.
 */
var AIMessenger = function () {};

AIMessenger.prototype = Object.create(Messenger.prototype);
AIMessenger.prototype.constructor = GameMessenger;

/**
 * Decodes a message and acts accordingly.
 *
 * @param message the message to react to.
 */
AIMessenger.prototype.handleMessage = function (message) {
  var decodedMessage = JSON.parse(message);
  if (debug) console.log("MESSAGE IN FROM AI:" + decodedMessage.type);
  switch (decodedMessage.type) {
    case "JOIN":
      this.interpretJoin(decodedMessage);
      break;
    case "MOVE":
      this.interpretMove(decodedMessage);
      break;
    default:
      break;
  }

};

/**
 * Passes the JOIN message through to the GameMessenger and hence to the
 * judge.
 *
 * @param messageJoin the JOIN message.
 */
AIMessenger.prototype.interpretJoin = function (messageJoin) {
  gameMessenger.sendMessage(messageJoin);
};

/**
 * Passes the MOVE message through to the GameMessenger and hence to the
 * judge.
 *
 * @param messageMove the MOVE message.
 */
AIMessenger.prototype.interpretMove = function (messageMove) {
  gameMessenger.sendMessage(messageMove);
};
