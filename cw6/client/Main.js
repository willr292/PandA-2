"use strict";

var AIPlayers = ["Black"];

var gameMessenger;
var aiMessenger;

var guiConnector;

var setUpView;
var notify;
var dropDown;
var board;
var players = {};
var ticketView;
var timer;

var gameId;

//Loads the views when the page loads.
if (window.addEventListener) window.addEventListener('load', load, false);
else if (window.attachEvent) window.attachEvent('onload', load);

/**
 * Downloads all images and calls initialise when done.
 */
function load () {
  setUpView = new SetUpView("setUpView", "joinForm", "spectateForm", "matchmakerForm", "aiForm", "idForm", "setUpMain", "setUpWaiting", "setUpId", "list");
  guiConnector = new GUIConnector();
  gameMessenger = new GameMessenger();
  aiMessenger = new AIMessenger();
  var imageLoader = new ImageLoader(initialise);
  imageLoader.load("Taxi", "resources/ticket_taxi.png");
  imageLoader.load("Bus", "resources/ticket_bus.png");
  imageLoader.load("Underground", "resources/ticket_underground.png");
  imageLoader.load("Taxi_Dark", "resources/ticket_taxi_dark.png");
  imageLoader.load("Bus_Dark", "resources/ticket_bus_dark.png");
  imageLoader.load("Underground_Dark", "resources/ticket_underground_dark.png");
  imageLoader.load("Secret", "resources/ticket_secret.png");
  imageLoader.load("Double", "resources/ticket_double.png");
  imageLoader.load("Taxi_Colour", "resources/ticket_taxi_colour.png");
  imageLoader.load("Bus_Colour", "resources/ticket_bus_colour.png");
  imageLoader.load("Underground_Colour", "resources/ticket_underground_colour.png");
  imageLoader.load("Secret_Colour", "resources/ticket_secret_colour.png");
  imageLoader.load("Double_Colour", "resources/ticket_double_colour.png");
  imageLoader.load("Board", "resources/board.png");
}

/**
 * Draws all of the views in an initial state.
 *
 * @param images an object containing all images.
 */
function initialise (images) {
  var ticketData = {
    rounds: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24],
    roundBools: [false, false, true, false, false, false, false, true, false, false, false, false, true, false, false, false, false, true, false, false, false, false, false, true],
    locations: [null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null],
    tickets: [null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null]
  };
  var playerColours = {
    "Blue": "#205B7F",
    "Green": "#63AA7F",
    "Red": "#B83E47",
    "Yellow": "#FFCD4A",
    "White": "#EDEDED",
    "Black": "#333333"
  };
  notify = new NotifyView("notify", "notify_h1", 5);
  dropDown = new DropDown(document.getElementById("dropDown"), images);
  board = new BoardView(document.getElementById("overlay"), document.getElementById("background"), dropDown, "#666666", images["Board"], JSON.parse(positionData), playerColours).repaint();
  players["Red"] = new PlayerView(document.getElementById("redPlayer"), playerColours["Red"], null, images).repaint();
  players["Yellow"] = new PlayerView(document.getElementById("yellowPlayer"), playerColours["Yellow"], null, images).repaint();
  players["Green"] = new PlayerView(document.getElementById("greenPlayer"), playerColours["Green"], null, images).repaint();
  players["Blue"] = new PlayerView(document.getElementById("bluePlayer"), playerColours["Blue"], null, images).repaint();
  players["White"] = new PlayerView(document.getElementById("whitePlayer"), playerColours["White"], null, images).repaint();
  players["Black"] = new PlayerView(document.getElementById("blackPlayer"), playerColours["Black"], null, images).repaint();
  ticketView = new TicketView(document.getElementById("tickets"), playerColours["Black"], ticketData, images).repaint();
  timer = new TimerView(document.getElementById("timer"), "#158BBA", "#D8060F", 15);
  resizeHandler([board, ticketView]);
}

/**
 * Re-draws the specified views when the page is resized.
 *
 * @param flexViews the view to be re-drawn when the page is resized.
 */
function resizeHandler (flexViews) {
  window.addEventListener('resize', function(event) {
    for (var i = 0; i < flexViews.length; i++) {
      flexViews[i].repaint();
    }
  });
}

/**
 * Sends an ENQUIRE message to the current server.
 */
function sendEnquire () {
  var enquireMessage = {};
  enquireMessage["type"] = "ENQUIRE";
  gameMessenger.sendMessage(enquireMessage);
}

/**
 * Requests the judge host and port needed to spectate a game.
 */
function requestConnection () {
  console.log("Here");
  var gameId = setUpView.list.getGameId();
  var requestMessage = {};
  requestMessage['type'] = 'REQUEST_CONNECTION';
  requestMessage['game_id'] = gameId;
  gameMessenger.sendMessage(requestMessage);
}
