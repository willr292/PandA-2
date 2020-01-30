"use strict";

/**
 * A file containing miscellaneous methods to aid drawing to a canvas.
 */

/**
 * Draws a rectangle with rounded corners to the canvas.
 *
 * @param x the x coordinate of the top left of the rectangle.
 * @param y the y coordinate of the top left of the rectangle.
 * @param width the width of the rectangle.
 * @param height the height of the rectangle.
 * @param radius the radius of the corners of the rectangle.
 */
CanvasRenderingContext2D.prototype.roundRect = function (x, y, width, height, radius) {
  if (height < 0) height = 0;
  if (width < 0) width = 0;
  if (width < 2 * radius) radius = width / 2;
  if (height < 2 * radius) radius = height / 2;
  this.beginPath();
  this.arc(x + width - radius, y + height - radius, radius, 0, Math.PI/2);
  this.arc(x + radius, y + height - radius, radius, Math.PI/2, Math.PI);
  this.arc(x + radius, y + radius, radius, Math.PI, 3*(Math.PI/2));
  this.arc(x + width - radius, y + radius, radius, 3*(Math.PI/2), Math.PI*2);
  this.closePath();
  return this;
};

/**
 * Stokes a rectangle with rounded corners to the canvas.
 *
 * @param x the x coordinate of the top left of the rectangle.
 * @param y the y coordinate of the top left of the rectangle.
 * @param width the width of the rectangle.
 * @param height the height of the rectangle.
 * @param radius the radius of the corners of the rectangle.
 */
CanvasRenderingContext2D.prototype.strokeRoundRect = function (x, y, width, height, radius) {
  this.roundRect(x, y, width, height, radius).stroke();
};

/**
 * Fills a rectangle with rounded corners on the canvas.
 *
 * @param x the x coordinate of the top left of the rectangle.
 * @param y the y coordinate of the top left of the rectangle.
 * @param width the width of the rectangle.
 * @param height the height of the rectangle.
 * @param radius the radius of the corners of the rectangle.
 */
CanvasRenderingContext2D.prototype.fillRoundRect = function (x, y, width, height, radius) {
  this.roundRect(x, y, width, height, radius).fill();
};

/**
 * Draws a circle to the canvas.
 *
 * @param x the x coordinate of the center of the circle.
 * @param y the y coordinate of the center of the cirle.
 * @param radius the radius of the circle.
 */
CanvasRenderingContext2D.prototype.circle = function (x, y, radius) {
    this.beginPath();
    this.arc(x, y, radius, 0, 2 * Math.PI);
    this.closePath();
    return this;
};

/**
 * Strokes a circle to the canvas.
 *
 * @param x the x coordinate of the center of the circle.
 * @param y the y coordinate of the center of the cirle.
 * @param radius the radius of the circle.
 * @param lineWidth the width of the stroked line.
 */
CanvasRenderingContext2D.prototype.strokeCircle = function (x, y, radius, lineWidth) {
    this.lineWidth = lineWidth;
    this.circle(x, y, radius).stroke();
};

/**
 * Fills a circle on the canvas.
 *
 * @param x the x coordinate of the center of the circle.
 * @param y the y coordinate of the center of the cirle.
 * @param radius the radius of the circle.
 */
CanvasRenderingContext2D.prototype.fillCircle = function (x, y, radius) {
    this.circle(x, y, radius).fill();
};
