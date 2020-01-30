"use strict";

/**
 * A view to display a drop down list of moves.
 *
 * @param canvas the canvas to draw to.
 * @param ticketImgs an object containing all ticket images.
 */
var DropDown = function(canvas, ticketImgs) {
  this.canvas = canvas;
  this.ticketImgs = ticketImgs;
  this.canvas.width = 100;
  this.canvas.height = 100;
  this.width = canvas.width;
  this.height = canvas.height;
  this.canvas.style.left = "0px";
  this.canvas.style.top = "0px";
  this.canvas.style.zIndex = "-1";
  this.selectedItem = -1;
  var self = this;
  if (canvas.addEventListener) {
    canvas.addEventListener("mousedown", function (event) {self.mouseDown(event, canvas)}, false);
    canvas.addEventListener("mousemove", function (event) {self.mouseMoved(event, canvas)}, false);
    canvas.addEventListener("mouseout", function (event) {self.mouseOut(event, canvas)}, false);
  } else if (canvas.attachEvent) {
    canvas.attachEvent("onmousedown", function (event) {self.mouseDown(event, canvas)});
    canvas.attachEvent("onmousemove", function (event) {self.mouseMoved(event, canvas)});
    canvas.attachEvent("onmouseout", function (event) {self.mouseOut(event, canvas)});
  }
};

/**
 * Called when the user clicks on the view.
 *
 * @param event the event sent from the browser containing the click location.
 * @param canvas the canvas for this view.
 */
DropDown.prototype.mouseDown = function (event, canvas) {
  var mouseLoc = this.positionRelativeTo({x: event.clientX, y: event.clientY}, canvas);
  if (this.moves) {
    var move = this.moves[Math.floor(mouseLoc.y / 30)];
    this.setVisible(false);
    guiConnector.endTurn(move);
  }
};

/**
 * Called when the user moves their mouse in the view. The selected item is updated.
 *
 * @param event the event sent from the browser containing the mouse location.
 * @param canvas the canvas for this view.
 */
DropDown.prototype.mouseMoved = function (event, canvas) {
  var mouseLoc = this.positionRelativeTo({x: event.clientX, y: event.clientY}, canvas);
  this.selectedItem = Math.floor(mouseLoc.y / 30);
  this.repaint();
};

/**
 * Called when the user moves their mouse out of the view. The selected item is updated.
 *
 * @param event the event sent from the browser.
 * @param canvas the canvas for this view.
 */
DropDown.prototype.mouseOut = function (event, canvas) {
  this.selectedItem = -1;
  this.repaint();
};

/**
 * Returns the position relative to an element.
 *
 * @param pos the position to be related to the element.
 * @param element the element to find the position relative to.
 * @return the position relative to an element.
 */
DropDown.prototype.positionRelativeTo = function(pos, element) {
  var x = pos.x;
  var y = pos.y;
  x -= element.offsetLeft;
  y -= element.offsetTop;
  return {x: x, y: y};
};

/**
 * Sets the list of moves to display in the drop down.
 *
 * @param moves the list of moves to display.
 */
DropDown.prototype.setMoves = function(moves) {
  this.moves = moves;
  this.canvas.height = (moves.length * 30) + 4;
};

/**
 * Sets the position of this view in the window.
 *
 * @param pos the position for this view.
 * @param canvas the board canvas.
 */
DropDown.prototype.setPosition = function(pos, canvas) {
  var x = pos.x + canvas.offsetLeft;
  var xDiff = (pos.x + this.canvas.width) - canvas.width;
  if (xDiff > 0) x -= xDiff;
  var y = pos.y + canvas.offsetTop;
  var yDiff = (pos.y + this.canvas.height) - canvas.height;
  if (yDiff > 0) y -= yDiff;
  this.canvas.style.left = x + "px";
  this.canvas.style.top = y + "px";
};

/**
 * Sets the visibilty of this view above the board.
 *
 * @param visible the boolean that decides the visibilty of this view.
 */
DropDown.prototype.setVisible = function(visible) {
   if (visible) this.canvas.style.zIndex = "2";
   else this.canvas.style.zIndex = "-1";
};

/**
 * Re-draws the whole view.
 */
DropDown.prototype.repaint = function() {
  this.width = this.canvas.width;
  this.height = this.canvas.height;
  this.paintCanvas();
};

/**
 * Draws the view to the specified canvas.
 */
DropDown.prototype.paintCanvas = function() {
  var context = this.canvas.getContext("2d");
  context.clearRect(0, 0, this.canvas.width, this.canvas.height);

  context.globalCompositeOperation = 'source-over';
  context.fillStyle = "#000000";
  context.fillRoundRect(0, 0, this.width, this.height - 4, 5);
  context.globalCompositeOperation = 'source-in';
  context.fillStyle = "#4692FF";
  context.fillRect(0, this.selectedItem * 30, this.width, 30);
  context.globalCompositeOperation = 'destination-over';

  context.fillStyle = "rgba(255, 255, 255, 0.96)";
  context.fillRoundRect(0, 0, this.width, this.height - 4, 5);
  context.fillStyle = "rgba(0, 0, 0, 0.2)";
  context.fillRoundRect(0, 0, this.width, this.height, 5);

  context.globalCompositeOperation = 'source-over';
  context.textAlign = "left";
  context.font = "14pt sans-serif";
  context.fillStyle = "black";
  //Draw moves
  for (var i = 0, y = 0; i < this.moves.length; i++, y += 30) {
    var moveObj = this.moves[i];
    if (moveObj['move_type'] == "MoveTicket") {
      context.drawImage(this.ticketImgs[moveObj.move.ticket + "_Colour"], 40, y + 8);
    } else if (moveObj['move_type'] == "MoveDouble") {
      context.drawImage(this.ticketImgs["Double_Colour"], 10, y + 8);
      context.fillText(":", 32, y + 20);
      context.drawImage(this.ticketImgs[moveObj.move.move1.ticket + "_Colour"], 40, y + 8);
      context.drawImage(this.ticketImgs[moveObj.move.move2.ticket + "_Colour"], 70, y + 8);
    }
  }
};
