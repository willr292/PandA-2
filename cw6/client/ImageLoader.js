"use strict";

/**
 * A class to load images.
 *
 * @param callback the function to call when all images have finished loading.
 */
var ImageLoader = function (callback) {
  this.callback = callback;
  this.imagesLeft = 0;
  this.images = {};
};

/**
 * Adds the image to the images object and calls the callback function if all images have loaded.
 *
 * @param tag the key to use when storing the image in the images object.
 * @param url the url of the image.
 */
ImageLoader.prototype.load = function (tag, url) {
  var img = document.createElement('img');
  img.src = url;
  this.images[tag] = img;
  this.imagesLeft++;
  var self = this;
  var sendImages = function () {
    if (--self.imagesLeft <= 0) {
      self.callback(self.images);
    }
  };
  if (img.addEventListener) img.addEventListener('load', sendImages);
  else if (img.attachEvent) image.attachEvent('onload', sendImages);
};
