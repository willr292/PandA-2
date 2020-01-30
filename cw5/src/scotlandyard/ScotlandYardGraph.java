package scotlandyard;

import graph.*;

import java.util.*;

public class ScotlandYardGraph extends UndirectedGraph<Integer, Transport> {

    public List<Move> generateMoves(Colour player, Integer location, Map<Ticket,Integer> tickets,List<Integer> playerLocs) {

        List<Move> validMoves = new ArrayList<Move>();
        List<Edge<Integer,Transport>> nearEdges = getEdgesFrom(getNode(location));
        Move move;

        for(Edge<Integer, Transport> edge : nearEdges){
            //Individual moves for Mr X and all detectives.
            if(!playerLocs.contains(edge.getTarget().getIndex())) {
                move = MoveTicket.instance(player, Ticket.fromTransport(edge.getData()), edge.getTarget().getIndex());
                validMoves.add(move);
            }

            if(player.equals(Colour.Black)) {
                //Mr X individual secret moves.
                if(!playerLocs.contains(edge.getTarget().getIndex())) {
                    move = MoveTicket.instance(player, Ticket.Secret, edge.getTarget().getIndex());
                    validMoves.add(move);
                }
                //Mr X Double Moves with variations using secret tickets.
                for(Edge<Integer, Transport> edge2 : getEdgesFrom(edge.getTarget())) {

                    if(!playerLocs.contains(edge.getTarget().getIndex()) && !playerLocs.contains(edge2.getTarget().getIndex())) {
                        move = MoveDouble.instance(player, Ticket.fromTransport(edge.getData()), edge.getTarget().getIndex(), Ticket.fromTransport(edge2.getData()), edge2.getTarget().getIndex());
                        validMoves.add(move);
                        move = MoveDouble.instance(player, Ticket.Secret, edge.getTarget().getIndex(), Ticket.fromTransport(edge2.getData()), edge2.getTarget().getIndex());
                        validMoves.add(move);
                        move = MoveDouble.instance(player, Ticket.fromTransport(edge.getData()), edge.getTarget().getIndex(), Ticket.Secret, edge2.getTarget().getIndex());
                        validMoves.add(move);
                        move = MoveDouble.instance(player, Ticket.Secret, edge.getTarget().getIndex(), Ticket.Secret, edge2.getTarget().getIndex());
                        validMoves.add(move);
                    }
                }
            }
        }

        //Remove the move from validMoves if the player does not have enough tickets.
        List<Move> invalidMoves = new ArrayList<Move>();
        for(Move m : validMoves) {

            if(m instanceof MoveTicket) {
                for(Map.Entry<Ticket, Integer> x : findTicket((MoveTicket) m).entrySet()) {
                    if(x.getValue() > tickets.get(x.getKey())){
                        invalidMoves.add(m);

                    }
                }
            } else if(m instanceof MoveDouble) {
                for(Map.Entry<Ticket, Integer> x : findTicket((MoveDouble) m).entrySet()) {
                    if(x.getValue() > tickets.get(x.getKey()) || !player.equals(Colour.Black)){
                        invalidMoves.add(m);
                    }
                }
            }
        }
        validMoves.removeAll(invalidMoves);

        //Add a move pass if the detectives have no valid moves.
        if(validMoves.size() == 0 && !player.equals(Colour.Black)) {

            Move movePass = MovePass.instance(player);
            validMoves.add(movePass);
        }

        return validMoves;
    }

    //Functions for finding what tickets are required for playing a certain move.
    private Map<Ticket, Integer> findTicket(MoveTicket m){
        Map<Ticket, Integer> reqTickets = new HashMap<Ticket, Integer>();
        reqTickets.put(m.ticket, 1);
        return reqTickets;
    }

    private Map<Ticket, Integer> findTicket(MoveDouble m) {
        Map<Ticket, Integer> reqTickets = new HashMap<Ticket, Integer>();
        for(Ticket t : Ticket.values()) {
            reqTickets.put(t, 0);
        }
        reqTickets.put(m.move1.ticket, reqTickets.get(m.move2.ticket) + 1);
        reqTickets.put(m.move2.ticket, reqTickets.get(m.move2.ticket) + 1);
        reqTickets.put(Ticket.Double , 1);
        return reqTickets;
    }



}
