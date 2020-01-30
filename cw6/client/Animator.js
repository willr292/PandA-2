"use strict";

/**
 * A class to aid animations.
 *
 * @param initialValue the initial value to be animated.
 * @param finalValue the value at the end of the animation.
 * @param easing the easing for this animation.
 */
var Animator = function (initialValue, finalValue, easing) {
  this.initialValue = initialValue;
  this.diff = finalValue - initialValue;
  this.easing = easing;
  this.percentCompleted = 0;
};

/**
 * Returns the value at this point in the animation. this.percentCompleted must be updated elsewhere.
 *
 * @return the value at this point in the animation.
 */
Animator.prototype.getValue = function () {
  if (this.easing == "ease-in") return this.diff * Math.pow(this.percentCompleted, 3) + this.initialValue;
  else if (this.easing == "ease-out") return this.diff * (Math.pow(this.percentCompleted - 1, 3) + 1) + this.initialValue;
  else if (this.easing == "ease-in-out" && this.percentCompleted < 0.5) return (this.diff / 2) * Math.pow(this.percentCompleted * 2, 3) + this.initialValue;
  else if (this.easing == "ease-in-out" && this.percentCompleted >= 0.5) return (this.diff / 2) * (Math.pow((this.percentCompleted * 2) - 2, 3) + 2) + this.initialValue;
  else return (this.diff * this.percentCompleted) + this.initialValue;
};
