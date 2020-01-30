"use strict";

/**
 * A view to display the tickets for a player.
 *
 * @param canvas the canvas to draw to.
 * @param colour the colour of the player to whom this view belongs.
 * @param tickets the tickets for this view's player.
 * @param ticketImgs an object containing all images of tickets.
 */
var PlayerView = function (canvas, colour, tickets, ticketImgs) {
  this.canvas = canvas;
  this.colour = colour;
  this.tickets = tickets;
  this.ticketImgs = ticketImgs;
  this.initialX = 8;
  this.initialY = 59;
  this.ticketWidth = 20;
  this.ticketHeight = 14;
  return this;
};

/**
 * Returns the number of tickets this player has.
 *
 * @param ticket the ticket to get the number for.
 * @return the number of tickets this player has.
 */
PlayerView.prototype.getTicketNumber = function (ticket) {
  return this.tickets[ticket];
};

/**
 * Updates the number of tickes for this player.
 *
 * @param ticket the ticket whose number to update.
 * @param number the new number for the ticket.
 */
PlayerView.prototype.updateTicketNumber = function (ticket, number) {
  this.tickets[ticket] = number;
};

/**
 * Draws the view to the specified canvas.
 */
PlayerView.prototype.paintCanvas = function () {
  var context = this.canvas.getContext("2d");
  //Draw background
  context.fillStyle = this.colour;
  context.fillRect(0, 0, this.canvas.width, this.canvas.height);
  if (this.tickets) {
    //Draw ticket images
    var tickets = Object.keys(this.tickets);
    for (var i = 0, x = this.initialX; i < tickets.length; i++, x += 30) {
      context.drawImage(this.ticketImgs[tickets[i]], x, this.initialY);
      //Draw ticket bars
      for (var j = 0, y = this.initialY - 6; j < this.tickets[tickets[i]]; j++, y -= 4) {
        context.fillStyle = "white";
        var darkString = "_Dark";
        if (tickets[i].indexOf(darkString, tickets[i].length - darkString.length) !== -1) context.fillStyle = "black";
        context.fillRect(x, y, 20, 2);
      }
    }
  } else {
    //Draw an x
    context.moveTo(10, 10);
    context.lineTo(this.canvas.width - 10, this.canvas.height - 10);
    context.moveTo(this.canvas.width - 10, 10);
    context.lineTo(10, this.canvas.height - 10);
    context.strokeStyle = "#666666";
    context.stroke();
  }
};

/**
 * Re-draws the whole view.
 */
PlayerView.prototype.repaint = function () {
  this.canvas.width = this.canvas.clientWidth;
  this.canvas.height = this.canvas.clientHeight;
  this.paintCanvas();
  return this;
};
