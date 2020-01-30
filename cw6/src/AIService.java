import ai.*;
import net.*;
import player.*;

/**
 * The AIService uses your AI to play a game of scotlandyard. Your AI can be
 * asked to play as any player (Detective or Mr X), so make sure it works for
 * both!
 *
 * To start the AI, run the following:
 * <pre>
 *   ant ai
 * </pre>
 * or,
 * <pre>
 *   ant ai-with -Dargs="clientPort"
 * </pre>
 * where clientPort is the port with which to bind the server that the
 * JavaScript GUIs connect to.
 */

public class AIService {

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);

        System.out.println("Welcome! AI service started on localhost:" + port);

        //TODO: Replace this player factory with your own AI.
        //PlayerFactory factory = new RandomPlayerFactory();
        PlayerFactory factory = new MrXAIFactory("graph.txt");
        //PlayerFactory factory = new DetectiveAIFactory("graph.txt");
        MessengerServer<Integer> server = new Server(port);
        AIServer client = new AIServer(server, "graph.txt", factory);
        client.run();
    }

}
