"use strict";

/**
 * A view to display Mr X's previous moves. It also shows when he will be visible during the game.
 *
 * @param canvas the canvas to draw to.
 * @param colour the background colour of the view.
 * @param ticketData the object containing the data required to draw the view.
 * @param ticketImgs an object containing all images of tickets.
 */
var TicketView = function (canvas, colour, ticketData, ticketImgs) {
  this.canvas = canvas;
  this.colour = colour;
  this.ticketData = ticketData;
  this.ticketImgs = ticketImgs;
  return this;
};

/**
 * Returns the ticket data that matches the size of the view. If the view is less
 * than 800px wide, it will only display the tickets from when Mr X was last visible
 * to when he will be next visible.
 *
 * @return the ticket data that matches the size of the view.
 */
TicketView.prototype.getTicketData = function () {
  if (this.canvas.width >= 800) return this.ticketData;
  var ticketData = {
    rounds: [],
    roundBools: [],
    locations: [],
    tickets: []
  };
  var lastVisible = 0;
  var nextVisible = 0;
  for (var i = 0; i < this.ticketData.rounds.length; i++) {
    if (this.ticketData.roundBools[i]) {
      if (this.ticketData.tickets[i]) {
        lastVisible = i;
      } else {
        nextVisible = i;
        break;
      }
    }
  }
  ticketData.rounds = this.ticketData.rounds.slice(lastVisible, nextVisible + 1);
  ticketData.roundBools = this.ticketData.roundBools.slice(lastVisible, nextVisible + 1);
  ticketData.locations = this.ticketData.locations.slice(lastVisible, nextVisible + 1);
  ticketData.tickets = this.ticketData.tickets.slice(lastVisible, nextVisible + 1);
  return ticketData;
};

/**
 * Draws the view to the specified canvas.
 */
TicketView.prototype.paintCanvas = function () {
  var context = this.canvas.getContext("2d");
  var ticketData = this.getTicketData();
  //Draw background
  context.fillStyle = this.colour;
  context.fillRect(0, 0, this.canvas.width, this.canvas.height);
  //Draw round numbers, tickets and locations
  var rounds = ticketData.rounds;
  var roundBools = ticketData.roundBools;
  var locations = ticketData.locations;
  var tickets = ticketData.tickets;
  context.textAlign = "center";
  context.fillStyle = "white";
  context.strokeStyle = "white";
  context.lineWidth = 2;
  var padding = (this.canvas.height - 42) / 2;
  for (var i = rounds.length - 1, x = this.canvas.width - 34; i >= 0; i--, x -= 30) {
    //Draw round number
    context.font = "8pt sans-serif";
    context.fillText(rounds[i], x + 15, padding + 8);
    //Draw ticket
    if (tickets[i]) {
      context.drawImage(this.ticketImgs[tickets[i]], x + 5, padding + 14);
    } else {
      var height = 14;
      if (roundBools[i]) height = 28;
      context.strokeRoundRect(x + 5, padding + 14, 20, height, 2);
    }
    //Draw locations
    if (locations[i] && roundBools[i]) {
      context.font = "10pt sans-serif";
      context.fillText(locations[i], x + 15, padding + 42);
    }
  }
  x += 24;
  //Draw labels
  context.textAlign = "right";
  context.font = "10pt sans-serif";
  context.fillText("Round:", x, padding + 8);
  context.fillText("Ticket:", x, padding + 25);
  context.fillText("Location:", x, padding + 42);
};

/**
 * Re-draws the whole view.
 */
TicketView.prototype.repaint = function () {
  this.canvas.width = this.canvas.clientWidth;
  this.canvas.height = this.canvas.clientHeight;
  this.paintCanvas();
  return this;
};
