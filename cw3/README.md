# Coursework 3, Draughting a board game

For this coursework you will need this zip file:

* [cw3.zip](cw3.zip)

In this coursework, your task is to finish the implementation of the
draughts game you were working on last week. We have provided a more
up-to-date version of the game and also a [completed jar
package](Draughts.jar)
for you to run and see what the finished project should be like. Your
job is to complete the model functionality so that your game will play
in a similar manner.

***

**TODO:** Refresh your memory of draughts by playing a few rounds of the completed jar file or looking at the [wikipedia page](https://en.wikipedia.org/wiki/English_draughts#Gameplay).

***

## Part 1
Last week we ended by having you write a test that checks if the `initialisePieces` method is working correctly. This test and quite a few more besides are now in the tests folder. Throughout this coursework you should be running these tests and adding any more that help you to complete the tasks. You can run all the tests using the `./ant/bin/ant test` command you used last week or you can run just the tests for a part of the coursework. For this part run `./ant/bin/ant test-part-1`.

The `initialisePieces` method itself was not implemented last week, that will be your first job.

*Remember, you can always look at the provided `javadocs` in the docs folder if you don’t know what a specific method in the provided code does.*

***

**TODO:** Implement the `initialisePieces` method in the model so the `test-part-1` tests pass.

***


## Part 2

Next we will implement the overloaded methods `validMoves(Colour player)` and `validMoves(Colour player, Piece piece, int yOffset, boolean jumpOnly)`. This part will take significantly longer than the other parts so dont worry if you find it tricky.
```java
private Set<Move> validMoves(Colour player) {
    //TODO:
    return new HashSet<Move>();
}
```
This method takes a `Colour` object and returns a set of `Move` objects which should be all the allowed moves that the player of that colour can make. At this point we are only concerned with a single move ahead i.e. if multiple consecutive moves can be made we only want the first to be added.

To generate this set we consider all of the current player’s pieces and what valid moves those pieces can make. The second `validMoves` method helps us to do this by generating a set of valid moves for a single Piece.
```java
private Set<Move> validMoves(Colour player, Piece piece, int
yOffset, boolean jumpOnly) {
    Set<Move> validMoves = new HashSet<Move>();
    if (player.equals(Colour.Red)) yOffset = -yOffset;
    if (!jumpOnly) {
        if (isEmpty(piece.getX() - 1, piece.getY() + yOffset)) {
            validMoves.add(new Move(piece, piece.getX() - 1, piece.getY() + yOffset));
        }
    }
    //TODO: We have given an implementation of how to calculate
    //one of the valid moves (single move to the left), it's
    //your job now to calculate the rest.
    return validMoves;
}
```
This method is partially implemented, however it only generates moves to the left and does not produce any jump moves either. It takes in a number of arguments which are listed below along with what they represent:

* player: The player that is making the move, this is important as we need to know which direction they are playing in.

* yOffset: Players can only move forward (towards the other player) unless they are crowned. We are specifying which direction to consider using yOffset. When yOffset is +1, we are considering all forward moves and when it is -1, we are considering all backward moves.

* jumpOnly: This is true if we are only considering jump moves i.e. moves where a piece is taken by jumping over it. It will be false when we call this function from `validMoves(Colour player)` as we want to consider non-jump moves as well.

You will need to make sure that you understand how each `validMoves` method works in order to complete them. Remember that we are not generating any consecutive moves. Below is a diagram showing an example of the valid moves for a piece. The arrows represent the moves the pseudo code in the image should return.

![Board Diagram](Board-Diagram.png)

***

**TODO:** Complete the two `validMoves` methods so the `test-part-2` tests pass.

***

*Hint: Drawing out which moves a player should be able to make and how these relate to location indexes can be very helpful when doing this part, also it may be useful to implement the second method first.*

Try running the program now, you should be able to make a move if all goes well!

***

**TODO (optional):** Currently we are only considering one move ahead at any point in the game but there are other ways to do it. We could instead have a move contain *every* movement a player makes in their turn. This could be implemented as a set of new objects which either contain multiple `Move` objects, as a tree structure, or as something else entirely. You may need to make significant changes to the model such as updating the `play` method and the `InputPDA`.
***

## Part 3
You’re on the home stretch now! It’s time to implement the `isGameOver` method. Refer to the [wikipedia](https://en.wikipedia.org/wiki/English_draughts#Gameplay) page if you are unsure of game over conditions. Watch out, it’s easy to make a mistake in this method.

***

**TODO:** Implement the `isGameOver` method so the `test-part-3` tests pass.

***

Try running the program now, if all is well with your implementation, the game will end when a game over condition is reached.

You have now implemented a game of draughts, congratulations!
