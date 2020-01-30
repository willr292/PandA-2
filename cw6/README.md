# CWK6:

In this coursework, you will make an Artificial Intelligence (AI) to play the game and also finish off a nicer client than the one we provided in the last coursework. That sounds cool, right? Well, let's turn it up a notch. We have provided a Matchmaker and Judge along with the messaging side of the AI so you can play Scotland Yard over the network! We have also provided the graphical side of the client, but we need you to wire it up.


***

**TODO:** Create a repository containing the following code:

* [cw6.zip](cw6.zip)

***


As well as the aforementioned client, which is found in the `client` directory, the project structure has changed a bit since the last coursework. Much of the `src` directory from the last coursework is now packaged in an included `.jar` file so you won't have access to many of those `.java` files. Also hidden  in the `.jar` are a number of unfamiliar components for the project, lets look at what's new:

![System diagram](Diagram.png)

So, what do all of those blocks mean? Let's start with the Matchmaker. The Matchmaker is a Java server that clients connect to when they want to be placed into a game. Who they should play against is decided by the Matchmaker based on their performance so far. To this end, the Matchmaker also records the scores for each team in the game. We are using the Elo rating system that, if you're interested, can be found [here](https://en.wikipedia.org/wiki/Elo_rating_system). The Judge is also a Java server that uses an extended model which allows it to judge multiple games at the same time. But, how does the extended model differ from the normal model, I hear you say! Well, because the Judge must judge multiple games, we have added a few methods to save the current game state and load in a new one.

Okay, so that explains the bottom half of the diagram, what about the top? The AI block is actually another Java server, the difference here is that you will host this on your own computer (or a lab machine) come competition day, and only your client will connect to it. The idea is that when it's time for an AI player to make a move, your client will request a move from your connected AI and forward it on to the Judge.

You might be wondering how all of these servers are communicating. They use a number of message objects that are converted to strings and sent over the network. You will not need to deal with the networking but *will* be handling the messages after they arrive at the client. As such we have included the list of all the messages used to communicate between the components of the system (in the appendix below). This is meant to be reference for when you are wiring up the JavaScript client, so you do not need to study it now.

# Wiring up the client

As previously stated, the first part of this coursework will involve implementing some message handling in the client so that we can use it to play a game. The client is written in JavaScript and runs in the browser as a web application. As such, all you need to do to start the client is to open up `index.html` located in the `client` folder in your browser. Note: your browser must support WebSockets and the HTML 5 canvas element, if it currently does not, please update it.


***

**TODO:** Start the client by opening up `index.html` located in `client`.

***


Since the client is (obviously) a client, it needs to connect to a server in order to play games. You will notice that the 'Matchmaker connection' settings are filled in but the 'connect' button is red, indicating that we are not connected to a Matchmaker. This makes sense because we have not yet started the Matchmaker, let's do this now and while we are at it we will connect a Judge as well.


***

**TODO:** Start the Matchmaker and Judge by running `./ant/bin/ant matchmaker` and `./ant/bin/ant judge` in separate terminal windows (you must start the Matchmaker first, and you only need to start the Matchmaker and Judge once - you can reconnect to your hearts content).

***


If you click the connect button now your GUI should connect to the Matchmaker and let you know by changing the connect button's color to green. Congratulations! you are now all connected up, we cannot play games yet as we still need to implement the messaging inside the client.

Inside the `client` directory you will find a number of `.js` files as well as some resources and tests. Many of these files are used to draw the views in the GUI and you will not need to change them. The classes you should be aware of are:

* `Messenger`: Sends and receives messages between the client and the various servers it connects to. Also handles changing connections when necessary.
* `GameMessenger`: Inherits from `Messenger` and deals with sending and receiving messages from the Judge and Matchmaker.
* `AIMessenger`: Also Inherits from `Messenger`, deals with sending and receiving messages from the AI (which you will be implementing later).
* `GUIConnector`: Provides methods for updating the views.

The client is initialised by `Main.js` and sends messages out to the Matchmaker and Judge as a result of user input in the views. Received messages are either dealt with in `GameMessenger`, updating the views appropriately, or are routed to the connected AI via the `AIMessenger`.

Let's begin by looking at how `GameMessenger` handles incoming messages.
```
GameMessenger.prototype.handleMessage = function (message) {
  var decodedMessage = JSON.parse(message);
  if (debug) console.log("MESSAGE IN:" + decodedMessage.type);
  switch (decodedMessage.type) {
    case "REGISTERED":
      this.interpretRegistered(decodedMessage);
      break;
    ...
  }
};
```
This is a portion of the `handleMessage` method in `GameMessenger`, which is called when the client receives a message from the Matchmaker or Judge. You can see that the message is passed in as a parameter. It is then decoded, this is because when we send these messages around, we do so as JSON strings. You can find more information about JSON [here](http://json.org) but for now just know that `JSON.decode` returns to us an object which contains all the fields of the message.

After printing the message (if the debug variable is true) we simply have a switch statement which directs the message to one of `GameMessenger`'s `interpret[X]` methods where `[X]` is a message name. This is the `interpretRegistered` method which is called when a message of type `REGISTERED` is received:
```
GameMessenger.prototype.interpretRegistered = function (messageRegistered) {
  gameId = messageRegistered['game_id'];
  guiConnector.showGameIdPane(gameId);
  this.storedMessage = messageRegistered;
};
```
You can see that we access one of the message's fields and call a method in `GUIConnector` to update the views appropriately. This method also alters the `storedMessage` field of `GameMessenger`.

It will be your job to make sure that the remainder of the `GameMessenger` methods are implemented in such a way that the GUI functions correctly. This may seem daunting but you have the appendix below which contains documentation of all of the messages and also documentation of the `GUIConnector` API. The `client/tests` folder contains a testing suite for you to validate your code.

This testing suite will be a little different to what you have seen before as we are now testing JavaScript code and so cannot use JUnit. Instead we use a slightly confusingly named framework called [QUnit](http://qunitjs.com/). To run these tests simply open up the `test.html` file located in `client/tests` in the browser. Most of the tests will be failing when you begin, take a look at what their messages tell you. Use these messages to understand where the problems are in the code.


***

**TODO:** Implement the methods in `GameMessenger` so that you pass the provided tests and the GUI functions correctly.

***


## Implementing an AI

In this part, your task is to write an AI that can play as any player in Scotland Yard. An AI is simply a `Player`, which must return a move when it has been given a list of valid moves. We have included an example AI in `src/player/RandomPlayer.java`. This simply returns a random move from the valid moves list (artificial unintelligence); hopefully your AI will be better than this one!

Each Player is created using a player service by using a `PlayerFactory`. We have provided this interface for you, and you can read about how it works in the Javadocs. We have also included an example implementation of this in `src/player/RandomPlayerFactory.java`, this creates a new `RandomPlayer` when asked for a player. When you have created your own AI `Player` and associated `PlayerFactory`, you need to edit `src/AIService.java` to make use of your `PlayerFactory`.

You can then start your AI using the following command:
```
./ant/bin/ant ai
```
This will make the AI server listen on port number 8121, if you would like to use a different port run the command:
```
./ant/bin/ant ai-with -Dargs="ai_port"
```
where `ai_port` will be replaced with the port you would like. Please note that port numbers 8122, 8123 and 8124 are used by the Matchmaker and Judge by default.

Right, so it just sits there doing nothing, does it? Well, yes, yes it does.

That's because we need to start up some of its friends before it can go and play! Now, go and start the Matchmaker and Judge as you did when testing the client. If you want to use some different ports for the Matchmaker, use this command:
```
./ant/bin/ant matchmaker-with -Dargs="judge_port mm_client_port"
```
and if you want to use some different ports for the Judge, use this command:
```
./ant/bin/ant judge-with -Dargs="judge_client_port matchmaker_ip judge_port"
```
Awesome, so now you have those running, it's time to start playing some games (for testing of course ...). Navigate to `client/index.html` in your web browser and connect to both your AI and the Matchmaker. Do the same again in a new tab / window, but don't connect to the AI. In the tab that is connected to the AI select some players to be AI players in the list and start playing!


***

**TODO:** Start up the provided AI and make sure everything works properly.

***


### Part 1

Okay, it's time to get your feet wet with a little bit of artificial intelligence. In this part, you will implement a board scoring heuristic. To do this, create a `score` method in your `Player` class (or another sensible place) that takes into account attributes such as distance to detectives, freedom of movement based on available target locations, etc. You should then use this method to select the best move to return in the `notify` method of your `Player`.


***

**TODO:** Implement your own `Player` and `PlayerFactory` in new class files (make up appropriate names!) and modify the `AIService.java` class to use them. Next, implement a `score` method. Finally, use the `score` method in the `notify` method of your `Player`.

***


### Part 2

Awesome, your AI is now making (hopefully) sensible moves! I think we can do better though, let's try and look further than one move ahead. To do this, implement the MiniMax algorithm to build a game tree. Your algorithm needs to be able to adjust the depth to which it searches as you will only have a maximum of 15 seconds per move in the competition.


***

**TODO:** Implement the MiniMax algorithm.

***


### Part 3

Now, you should have a pretty sweet AI! In this part, you should try to make it even better. This could include implementing alpha-beta pruning, Quiescence search or other approaches you can think of - be creative.


***

**TODO:** Make your AI even better!

***


## Appendix

### GUIConnector API

| Method                 | Parameters                    | Description |
|------------------------|-------------------------------|-------------|
| **setTicketView**      | `rounds`, `currentRound`      | This method takes in the list of round booleans along with  the current round and populates the view that shows Mr X's previous moves. |
| **setPlayerLocations** | `locations`                   | This method takes in an object containing the map of players to their locations and puts the players onto the board. |
| **setPlayerTickets**   | `tickets`                     | This method takes in an object containing the map of players to their tickets and populates the ticket views for each player. |
| **animatePlayer**      | `player`, `location`          | This method takes in a players colour and a location and animates the player to that location on the board. |
| **removeTicket**       | `player`, `ticket`            | This method takes in a players colour and a tickets and decrements the number of that type of ticket they have by one. |
| **updateTicketView**   | `ticket`, `target`            | This method takes in a ticket and a target and updates the view that shows Mr X's previous moves accordingly (this should only be called with Mr X's tickets and targets). |
| **startTurn**          | `notifyTurnMessage`, `aiMove` | This method takes in a NotifyTurn message and a boolean describing whether it is an ai move or not and updates the views for the start of a turn. |
| **endTurn**            | `move`                        | This method takes in the chosen move and updates the views accordingly for the end of a move. It also sends the move to the Judge. |
| **setGameOver**        | `gameOverMessage`             | This method takes in a GameOver message and updates the views accordingly. |
| **updateGamesList**    | `games`                       | This method takes in a list of available games to spectate and updates the list in SetUpView accordingly. |

###Messages
These are the messages used by various parts of the project to talk to each other. You read the names of the attributes as "`Java` (JavaScript)", i.e. the part in brackets is used when retrieving the corresponding value from a message object in the JavaScript code.

### Initialisation messages:
| Message                                         | Description | Attributes |
|-------------------------------------------------|-------------|------------|
|**MessageTcp (TCP)**                             | This is sent from a TcpMessenger to a TcpServer to say that this connection should not use the Websocket protocol (don't worry about this). This is just used to establish a connection.     |                                             |
|**MessageConnection (CONNECTION)**               | This is sent from the Judge to the Matchmaker to register it. This message is also used to give a client a particular Judges connection information (host, port) so it can spectate a game.| `String host` (host), `Integer port` (port) |
|**MessageRequestConnection (REQUEST_CONNECTION)**| This is sent from a client to the Matchmaker to ask for the connection information (host, port) of a Judge so the client can spectate a game. | `Integer gameId` (game_id) |

### Game set-up messages:
| Message                                 | Description                                                                                                                                 | Attributes                      |
|-----------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------|
|**MessageRegister (REGISTER)**           | This is sent from the client to the Matchmaker. This is used to ask to be matched in a random game.                                         | `String studentId` (student_id) |
|**MessageRegisterMatch (REGISTER_MATCH)**| This is sent from the client to the Matchmaker. This is used to ask to be matched against specific people.                                  | `String sudentId` (student_id), `List<String> opponents` (opponents) |
|**MessageSpectate (SPECTATE)**           | This is sent from the client to the Judge. This is used to spectate a game.                                                                 | `Integer gameId` (game_id)      |
|**MessageNewInstance (NEW_INSTANCE)**    | This is sent from the Matchmaker to the Judge. This tells the Judge to make a new game with the specified number of detectives and game id. | `Integer gameId` (game_id), `Integer nDetectives` (n_detectives)          |
|**MessageInstance (INSTANCE)**           | This is sent from the Judge to the Matchmaker. This tells the Matchmaker that the specified game has been created and clients can now join. | `Integer gameId` (game_id)      |
|**MessagePendingGame (PENDING_GAME)**    | This is sent from the matchmaker to the client. This tells the client who they will play against and to wait until further messages.        | `Integer gameId` (game_id), `List<String> opponents` (opponents) |

### Game play messages:
| Message                           | Description                                                                                                                                                                       | Attributes                                                                                                      |
|-----------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------|
|**MessageRegistered (REGISTERED)** | This is sent from the Matchmaker to the Client. This gives the client the connection information (host, port) of the Judge and the colours of the players this client is playing. | `Integer gameId` (game_id), `List<Colour> colours` (colours), `String host` (host), `Integer port` (port)                |
|**MessageJoin (JOIN)**             | This is sent from the Client to the Judge. This joins a single player to a specified game.                                                                                        | `Integer gameId` (game_id), `Colour colour` (colour)                                                                     |
|**MessageReady (READY)**           | This is sent from the Judge to the Client. This contains all of the information to set up the Client and any associated AI's.                                                     | `Integer gameId` (game_id), `Integer numDetectives` (n_detectives), `List<Boolean> rounds` (rounds), `List<Colour> colours` (colours), `Map<Colour, Integer> locations` (locations), `Map<Colour, Map<Ticket, Integer>> tickets` (tickets), `Integer currentRound` (current_round)                                                                          |
|**MessageNotifyTurn (NOTIFY_TURN)**| This is sent from the Judge to the Client. This alerts the Client to make a move.                                                                                                 | `Integer gameId` (game_id), `List<Move> validMoves` (valid_moves), `long timestamp` (timestamp), `Integer token` (token) |
|**MessageMove (MOVE)**             | This is sent from the Client to the Judge. This contains the chosen move for a player and the secret token for that move.                                                         | `Integer gameId` (game_id), `Move move` (move), `Integer token` (token)                                                  |
|**MessageNotify (NOTIFY)**         | This is sent from the Judge to all Clients. This is used to update the Clients when another client makes a move.                                                                  | `Integer gameId` (game_id), `Move move` (move)                                                                           |
|**MessageGameOver (GAME_OVER)**    | This is sent from the Judge to the Clients to tell them when the game is over.                                                                                                    | `Integer gameId` (game_id), `Set<Colour> winners` (winners)                                                              |

### Bulk data transfer messages:
| Message                                 | Description                                                                                                       | Attributes                                |
|-----------------------------------------|-------------------------------------------------------------------------------------------------------------------|-------------------------------------------|
|**MessageEnquire (ENQUIRE)**             | This is sent from the Client to the Matchmaker. This requests the list of all current games available to spectate.|                                           |
|**MessageGames (GAMES)**                 | This is sent from the Matchmaker to the Client. This contains the list of games available to spectate.            | `Map<Integer, List<String>> games` (games)|
|**MessageRequestScores (REQUEST_SCORES)**| This is sent from the Matchmaker GUI to the Matchmaker. This requests the map of all teams and their scores.      |                                           |
|**MessageScores (SCORES)**               | This is sent from the Matchmaker to the Matchmaker GUI. This contains the map of all teams and their scores.      | `Map<String, Double>  scores` (scores)    |

### Cleanup messages:
| Message                | Description                                                                                        | Attributes |
|------------------------|----------------------------------------------------------------------------------------------------|------------|
|**MessageStop (STOP)**  | This is used internally in the AIServer, Judge and Matchmaker. It tells the server to stop running.|            |
|**MessageClose (CLOSE)**| This closes the current connection.                                                                |            |
