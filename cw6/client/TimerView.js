"use strict";

/**
 * A view to display a progress bar that takes the specified time to complete.
 *
 * @param canvas the canvas to draw to.
 * @param colour the colour of the progress bar.
 * @param duration the time for the progress bar to complete.
 */
var TimerView = function (canvas, startColour, endColour, duration) {
  this.canvas = canvas;
  this.startColour = startColour;
  this.endColour = endColour;
  this.currentColour = this.startColour;
  this.duration = duration * 1000;
};

/**
 * Animates the progress bar back to the start.
 */
TimerView.prototype.reset = function () {
  if (this.animating) this.cancel();
  var animator = new Animator(1, 0, "ease-in-out");
  var colourAnimators = this.getColourAnimators(this.endColour, this.startColour, "ease-in-out");
  this.animate(animator, colourAnimators, 1000);
};

/**
 * Animates the progress bar to completion in the specified time.
 */
TimerView.prototype.start = function () {
  if (this.animating) this.cancel();
  var animator = new Animator(0, 1, "linear");
  var colourAnimators = this.getColourAnimators(this.startColour, this.endColour, "linear");
  this.animate(animator, colourAnimators, this.duration);
};

/**
 * Returns an object containing rbg animators from the start colour to
 * the end colour with the specified easing.
 *
 * @param start the start colour (in hex).
 * @param end the end colour (in hex).
 * @param easing the easing for the animators.
 * @return an object containing rgb animators from the start colour to
 * the end colour with the specified easing.
 */
TimerView.prototype.getColourAnimators = function (start, end, easing) {
  var startColour = start.slice(1).split("");
  var endColour = end.slice(1).split("");
  var colourAnimators = {
    rAnimator: new Animator(parseInt(startColour[0].toString(16) + startColour[1].toString(16), 16),
                      parseInt(endColour[0].toString(16) + endColour[1].toString(16), 16), easing),
    gAnimator: new Animator(parseInt(startColour[2].toString(16) + startColour[3].toString(16), 16),
                      parseInt(endColour[2].toString(16) + endColour[3].toString(16), 16), easing),
    bAnimator: new Animator(parseInt(startColour[4].toString(16) + startColour[5].toString(16), 16),
                      parseInt(endColour[4].toString(16) + endColour[5].toString(16), 16), easing)
  };
  return colourAnimators;
};

/**
 * Returns the colour made from an object containing rgb animators.
 *
 * @param colourAnimators the object containing the rgb animators.
 * @return the colour made from an object containing rgb animators.
 */
TimerView.prototype.getColour = function (colourAnimators) {
  var currentColour = "#" + ("0" + parseInt(colourAnimators.rAnimator.getValue(), 10).toString(16)).slice(-2)
                          + ("0" + parseInt(colourAnimators.gAnimator.getValue(), 10).toString(16)).slice(-2)
                          + ("0" + parseInt(colourAnimators.bAnimator.getValue(), 10).toString(16)).slice(-2);
  return currentColour;
};

/**
 * Animates the progress bar using the specified animator.
 *
 * @param animator the animator for the animation.
 * @param colourAnimators the object containing animators to animate the colour change.
 * @param duration the duration of the animation.
 */
TimerView.prototype.animate = function (animator, colourAnimators, duration) {
  if (!this.animating) {
    this.animator = animator;
    this.colourAnimators = colourAnimators;
    var self = this;
    var startTime = Date.now();
    var endTime = startTime + duration;
    var step = function () {
      var duration = endTime - startTime;
      var percentCompleted = (Date.now() - startTime) / duration;
      self.animator.percentCompleted = percentCompleted;
      self.colourAnimators.rAnimator.percentCompleted = percentCompleted;
      self.colourAnimators.gAnimator.percentCompleted = percentCompleted;
      self.colourAnimators.bAnimator.percentCompleted = percentCompleted;
      self.repaint();
      if (percentCompleted >= 1) self.cancel();
      else self.id = window.requestAnimationFrame(step);
    };
    this.animating = true;
    this.id = window.requestAnimationFrame(step);
  }
};

/**
 * Cancels any current animations.
 */
TimerView.prototype.cancel = function () {
  if (this.id) {
    window.cancelAnimationFrame(this.id);
    this.id = undefined;
    this.animating = false;
    this.animator = undefined;
  }
};

/**
 * Draws the view to the specified canvas.
 */
TimerView.prototype.paintCanvas = function () {
  var context = this.canvas.getContext("2d");
  //Draw line
  var x = this.animator.getValue() * this.canvas.width;
  var currentColour = this.getColour(this.colourAnimators);
  context.fillStyle = currentColour;
  context.fillRect(0, 0, x, this.canvas.height);
};

/**
 * Re-draws the whole view.
 */
TimerView.prototype.repaint = function () {
  this.canvas.width = this.canvas.clientWidth;
  this.canvas.height = this.canvas.clientHeight;
  if (this.animator) this.paintCanvas();
};
