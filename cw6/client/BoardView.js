"use strict";

/**
 * A view that displays the board and handles user input.
 *
 * @param canvas the canvas to draw the counters to.
 * @param boardCanvas the canvas to draw the baord image to.
 * @param dropDown the drop down to display available moves with.
 * @param colour the background colour of the view.
 * @param boardImage the image to draw to the boardCanvas.
 * @param locations the positions of the nodes on the board.
 * @param playerColours the colours of the players in the game.
 */
var BoardView = function (canvas, boardCanvas, dropDown, colour, boardImage, locations, playerColours) {
  this.aiPlaying = false;
  this.cAnim = {
    "Blue": null,
    "Green": null,
    "Red": null,
    "Yellow": null,
    "White": null,
    "Black": null
  };
  this.canvas = canvas;
  this.boardCanvas = boardCanvas;
  this.dropDown = dropDown;
  this.board = new Board(boardImage);
  this.locations = locations;
  this.colour = colour
  this.currentPlayer = null;
  this.selectedNode = null;
  this.playerPositions = {
    "Blue": {node: 0, x: 0, y: 0},
    "Green": {node: 0, x: 0, y: 0},
    "Red": {node: 0, x: 0, y: 0},
    "Yellow": {node: 0, x: 0, y: 0},
    "White": {node: 0, x: 0, y: 0},
    "Black": {node: 0, x: 0, y: 0}
  };
  this.playerColours = playerColours;
  this.validMoves = [];

  var self = this;
  if (canvas.addEventListener) {
    canvas.addEventListener("mousedown", function (event) {self.mouseDown(event, canvas)}, false);
    canvas.addEventListener("mousemove", function (event) {self.mouseMoved(event, canvas)}, false);
  } else if (canvas.attachEvent) {
    canvas.attachEvent("onmousedown", function (event) {self.mouseDown(event, canvas)});
    canvas.attachEvent("ommousemove", function (event) {self.mouseMoved(event, canvas)});
  }
  return this;
};

/**
 * Starts the animation that draws a growing circle around the current player.
 */
BoardView.prototype.startPing = function () {
  this.animator = new Animator(10, 60, "ease-in-out");
  var self = this;
  var startTime = Date.now();
  var step = function () {
    if (self.animator) {
      var percentCompleted = (Date.now() - startTime) / 1000;
      self.animator.percentCompleted = percentCompleted;
      self.revalidate();
      if (percentCompleted >= 1) self.startPing();
      else self.id = window.requestAnimationFrame(step);
    }
  };
  this.id = window.requestAnimationFrame(step);
};

/**
 * Cancels the animation that draws a growing circle around the current player.
 */
BoardView.prototype.cancelPing = function () {
  if (this.id) {
    window.cancelAnimationFrame(this.id);
    this.id = undefined;
    this.animator = undefined;
  }
};

/**
 * Animates the specified player's counter on the board.
 *
 * @param colour the player whose counter is to be animated.
 */
BoardView.prototype.animateCounter = function (colour) {
  var playerObj = this.playerPositions[colour];
  var pos = this.locations[playerObj.node - 1];
  playerObj.xAnimator = new Animator(playerObj.x, pos.x, "ease-in-out");
  playerObj.yAnimator = new Animator(playerObj.y, pos.y, "ease-in-out");
  var self = this;
  var startTime = Date.now();
  var step = function () {
    var percentCompleted = (Date.now() - startTime) / 1000;
    if (percentCompleted > 1) percentCompleted = 1;
    playerObj.xAnimator.percentCompleted = percentCompleted;
    playerObj.yAnimator.percentCompleted = percentCompleted;
    playerObj.x = playerObj.xAnimator.getValue();
    playerObj.y = playerObj.yAnimator.getValue();
    self.revalidate();
    if (percentCompleted < 1) self.cAnim[colour] = window.requestAnimationFrame(step);
  };
  this.cAnim[colour] = window.requestAnimationFrame(step);
};

BoardView.prototype.cancelAnimation = function (colour) {
  window.cancelAnimationFrame(this.cAnim[colour]);
};

/**
 * Draws the overlay view to the overlay canvas.
 */
BoardView.prototype.paintCanvas = function () {
  var context = this.canvas.getContext("2d");
  //Draw counters
  var colours = Object.keys(this.playerPositions);
  for (var i = 0; i < colours.length; i++) {
    var colour = colours[i];
    context.fillStyle = this.playerColours[colour];
    var playerObj = this.playerPositions[colour];
    if (playerObj.x != 0 || playerObj.y != 0) {
      var point = this.board.scaledPosition(playerObj);
      context.shadowColor = '#000';
      context.shadowBlur = 15;
      context.shadowOffsetX = 0;
      context.shadowOffsetY = 0;
      if (this.currentPlayer == colour) {
        context.strokeStyle = this.playerColours[colour];
        if (this.animator) context.strokeCircle(point.x, point.y, this.board.scale * this.animator.getValue(), 4);
      }
      context.circle(point.x, point.y, this.board.scale * 30);
      context.fill();
    }
  }
  context.shadowBlur = 0;
  //Draw selection circle
  if (this.currentPlayer && this.selectedNode && this.selectedNode.node > 0) {
    var point = this.board.scaledPosition(this.selectedNode);
    //Draw outer circle
    if (this.isValidMove(this.selectedNode)) context.strokeStyle = "#4BC94D";
    else context.strokeStyle = "#E53C38";
    context.strokeCircle(point.x, point.y, (this.board.scale * 40) + 4, 4);
  }
};

/**
 * Draws the board image to the board canvas.
 */
BoardView.prototype.paintBoardCanvas = function () {
  var context = this.boardCanvas.getContext("2d");
  //Draw background
  context.fillStyle = this.colour;
  context.fillRect(0, 0, this.boardCanvas.width, this.boardCanvas.height);
  //Draw image
  var size = this.board.makeSize(this.boardCanvas);
  var pos = this.board.makePosition(size, this.boardCanvas);
  context.drawImage(this.board.boardImage, pos.x, pos.y, size.width, size.height);
};

/**
 * Re-draws the whole view.
 */
BoardView.prototype.repaint = function () {
  this.boardCanvas.width = this.boardCanvas.clientWidth;
  this.boardCanvas.height = this.boardCanvas.clientHeight;
  this.paintBoardCanvas();
  this.revalidate();
  return this;
};

/**
 * Re-draws the overlay of the view.
 */
BoardView.prototype.revalidate = function () {
  this.canvas.width = this.canvas.clientWidth;
  this.canvas.height = this.canvas.clientHeight;
  this.paintCanvas();
  return this;
};

/**
 * Called when the user clicks on the view. It shows the drop down or makes a move if possible.
 *
 * @param event the event from the browser containing information about the click.
 * @param canvas the canvas for this view.
 */
BoardView.prototype.mouseDown = function (event, canvas) {
  this.dropDown.setVisible(false);
  if (this.aiPlaying) return;
  if (!this.validMoves) return;
  var mouseLoc = this.positionRelativeTo({x: event.clientX, y: event.clientY}, canvas);
  var trueLoc = this.board.unscaledPosition(mouseLoc);
  this.selectedNode = this.findPosition(trueLoc);
  var moves = this.validMoves[this.selectedNode.node];
  if (moves) {
    if (moves.length == 1) return guiConnector.endTurn(moves[0]);
    if (this.selectedNode.x == 0 && this.selectedNode.y == 0) this.dropDown.setVisible(false);
    else this.dropDown.setVisible(true);
    var scaledLoc = this.board.scaledPosition(this.selectedNode);
    this.dropDown.setPosition(scaledLoc, this.canvas);
    this.dropDown.setMoves(moves);
    this.dropDown.repaint();
  }
};

/**
 * Called when the user moves their mouse in the view. It updates the circle denoting whether
 * the user can move to the location.
 *
 * @param event the event from the browser containing the mouse location.
 * @param canvas the canvas for this view.
 */
BoardView.prototype.mouseMoved = function (event, canvas) {
  if (!this.aiPlaying) {
    var mouseLoc = this.positionRelativeTo({x: event.clientX, y: event.clientY}, canvas);
    var trueLoc = this.board.unscaledPosition(mouseLoc);
    this.selectedNode = this.findPosition(trueLoc);
  }
  else {
    this.selectedNode = null;
  }
  this.repaint();
}

/**
 * Returns the position relative to an element.
 *
 * @param pos the position to be related to the element.
 * @param element the element to find the position relative to.
 * @return the position relative to an element.
 */
BoardView.prototype.positionRelativeTo = function(pos, element) {
  var x = pos.x;
  var y = pos.y;
  x -= element.offsetLeft;
  y -= element.offsetTop;
  return {x:x, y:y};
}

/**
 * Returns the node in the map closest to the mouse position within a boundary.
 *
 * @param searchPos the position to find the node closest to.
 * @return the node in the map closest to the mouse position within a boundary.
 */
BoardView.prototype.findPosition = function (searchPos) {
  var minDist = Number.POSITIVE_INFINITY;
  var minPos = {x: 0, y: 0};
  for (var i = 0; i < this.locations.length; i++) {
    var pos = this.locations[i];
    var dist = Math.sqrt(Math.pow(pos.x - searchPos.x, 2) + Math.pow(pos.y - searchPos.y, 2));
    if (dist < minDist && dist < 80) {
      minDist = dist;
      minPos = pos;
    }
  }
  return minPos;
};

/**
 * Sets the position of a player on the board.
 *
 * @param colour the colour of the player whose position is to be set.
 * @param location the new location of the player.
 */
BoardView.prototype.setPosition = function (colour, location) {
  var playerObj = this.playerPositions[colour];
  playerObj.node = location;
  if (location == 0) {
    playerObj.x = 0;
    playerObj.y = 0;
  } else {
    var pos = this.locations[location - 1];
    playerObj.x = pos.x;
    playerObj.y = pos.y;
  }
  this.revalidate();
};

/**
 * Animates a players counter to a location on the board.
 *
 * @param colour the colour of the player whose position is to be animated.
 * @param location the new location of the player.
 */
BoardView.prototype.animateToPosition = function (colour, location) {
  if (location == 0) return this.setPosition(colour, location);
  this.playerPositions[colour].node = location;
  this.animateCounter(colour);
};

/**
 * Sets the list of valid moves for a player to be used in the drop downs.
 *
 * @param validMoves the list of valid moves for a player.
 */
BoardView.prototype.setValidMoves = function (validMoves) {
  if (validMoves) {
    this.validMoves = {};
    for (var i = 0; i < validMoves.length; i++) {
      var moveObj = validMoves[i];
      var target;
      if (moveObj['move_type'] == "MoveTicket") target = moveObj.move.target;
      else if (moveObj['move_type'] == "MoveDouble") target = moveObj.move.move2.target;
      if (!this.validMoves[target]) this.validMoves[target] = [];
      this.validMoves[target].push(moveObj);
    }
  } else {
    this.validMoves = validMoves;
  }
};

/**
 * Returns true if there exists a valid move to the specified location.
 *
 * @param location the location to test against the list of valid moves.
 * @return true if there exists a valid move to the specified location.
 */
BoardView.prototype.isValidMove = function (location) {
  if (this.validMoves && this.validMoves[location.node]) return true;
  return false;
};

/**
 * A class to maintain the aspect ratio of the board image.
 *
 * @param boardImage the image whose aspect ration is to be maintained.
 */
var Board = function(boardImage) {
  this.boardImage = boardImage;
  this.trueSize = {width : boardImage.width, height : boardImage.height};
  this.scale = 1.0;
  this.offset = {x: 0, y: 0};
}

/**
 * Returns the scaled position on the board. (i.e. the position on the canvas from a positon on the board image).
 *
 * @param pos the position to be scaled.
 * @return the scaled position on the board.
 */
Board.prototype.scaledPosition = function (pos) {
  var x = this.offset.x + ((pos.x + 60) * this.scale);
  var y = this.offset.y + ((pos.y + 60) * this.scale);

  return {x: x, y: y};
};

/**
 * Returns the unscaled position on the board. (i.e. the position on the board image from a position on the canvas).
 *
 * @param pos the position to be unscaled.
 * @return the unscaled position on the board.
 */
Board.prototype.unscaledPosition = function (pos) {
  var x = ((pos.x - this.offset.x) / this.scale) - 60;
  var y = ((pos.y - this.offset.y) / this.scale) - 60;

  return {x: x, y: y};
};

/**
 * Returns the scaled width and height of the board image. This is the biggest it can
 * be inside the canvas whilst retaining aspect ratio.
 *
 * @param canvas the canvas which contains the board image.
 * @return the scaled width and height of the board image.
 */
Board.prototype.makeSize = function (canvas) {
  var imageRatio = this.trueSize.width / this.trueSize.height;
  var canvasRatio = canvas.width / canvas.height;

  var imgWidth;
  var imgHeight;
  if (imageRatio > canvasRatio) {
    imgWidth = canvas.width;
    imgHeight = imgWidth / imageRatio;
  } else {
    imgHeight = canvas.height;
    imgWidth = imgHeight * imageRatio;
  }
  this.scale = imgWidth / this.trueSize.width;

  return {width: imgWidth, height: imgHeight};
};

/**
 * Returns the position of the image in the canvas. This is the position so the
 * board image is centered in the canvas.
 *
 * @param imgSize an object containing the scaled size of the image.
 * @param canvas the canvas which contains the board image.
 * @return the position of the image in the canvas.
 */
Board.prototype.makePosition = function (imgSize, canvas) {
  var x = (canvas.width / 2) - (imgSize.width / 2);
  var y = (canvas.height / 2) - (imgSize.height / 2);

  this.offset = {x: x, y: y};
  return {x: x, y: y};
};
