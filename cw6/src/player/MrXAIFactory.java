package player;

import net.*;
import scotlandyard.*;
import swing.algorithms.*;
import java.io.IOException;
import java.util.*;

public class MrXAIFactory implements PlayerFactory {

    protected Dijkstra dijkstra;
    private ScotlandYardView view;
    public MrXAIFactory(String graphFileName){
        this.dijkstra = new Dijkstra(graphFileName);
    }

    @Override
    public Player getPlayer(Colour colour, ScotlandYardView view, String mapFilename) {
        //TODO: Update this with your AI implementation.
        return new MrXAI(view, mapFilename, dijkstra);
    }

    @Override
    public void ready() {
        //TODO: Any code you need to execute when the game starts, put here.
    }

    @Override
    public List<Spectator> getSpectators(ScotlandYardView view) {
        List<Spectator> spectators = new ArrayList<Spectator>();
        //TODO: Add your AI here if you want it to be a spectator.
        return spectators;
    }

    @Override
    public void finish() {
        //TODO: Any code you need to execute when the game ends, put here.
    }

}
