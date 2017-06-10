package fr.unicaen.info.l3.tron.model;

import fr.unicaen.info.l3.tron.controller.Main;
import fr.unicaen.info.l3.tron.model.ai.AIUtils;
import fr.unicaen.info.l3.tron.model.player.AbstractPlayer;
import fr.unicaen.info.l3.tron.model.player.PlayerBot;
import fr.unicaen.info.l3.tron.utils.Coordinates2D;
import fr.unicaen.info.l3.tron.utils.Movement;
import fr.unicaen.info.l3.tron.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Current game
 */
public final class Game implements Cloneable {

    /**
     * Board game
     */
    protected Grid grid;

    /**
     * List of all players
     */
    protected List<AbstractPlayer> playerList;

    /**
     * For each player, contains all the squares of his territory
     */
    Map<AbstractPlayer, List<Square>> playersTerritories;

    /**
     * Number of players per coalition: key = coalition, value = number of members
     */
    Map<Integer, Integer> numberOfMemberByCoalition;

    /**
     * Lets find out if the game is finished or not
     */
    boolean endOfGame;

    /**
     * Reference to current player
     */
    private AbstractPlayer currentPlayer;

    /**
     * Reference to the last player to play
     */
    private AbstractPlayer lastPlayer;

    /**
     * Number of turn (one turn is a set of strokes)
     */
    private int turn;

    /**
     * Number of strokes
     */
    private int stroke;

    /**
     * Default constructor
     */
    public Game() {
        if (Main.GRID_SIZE == 0) {
            Random rand = new Random();
            int min = Utils.minimumGridSize(Main.TOTAL_PLAYERS);
            int max = min * 2;

            Main.GRID_SIZE = rand.nextInt(max - min + 1) + min;

            min = 0;
            max = Main.TOTAL_PLAYERS - 1;
            Main.FIRST_PLAYER_POSITION = rand.nextInt(max - min + 1) + min;
        }
        if (Main.RANDOM) {
            Random rand = new Random();

            Main.PLAYER_LOCATION = new Coordinates2D[Main.TOTAL_PLAYERS];
            int index = 0;

            int min = 0;
            int max = Main.GRID_SIZE - 1;
            Coordinates2D coordinates2D;
            do {
                do {
                    coordinates2D = new Coordinates2D(rand.nextInt(max - min + 1) + min,
                        rand.nextInt(max - min + 1) + min);
                } while (Utils.inList(coordinates2D, Main.PLAYER_LOCATION));
                Main.PLAYER_LOCATION[index] = coordinates2D;
                index++;
            } while (index < Main.TOTAL_PLAYERS);
        }

        this.grid = new Grid(Main.GRID_SIZE);
        endOfGame = false;

        initialize();

    }

    /**
     * Accessor of grid
     *
     * @return Returns the grid
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Accessor of playerList
     *
     * @return Return the player list
     */
    public List<AbstractPlayer> getPlayerList() {
        return playerList;
    }

    /**
     * Accessor of currentPlayer
     *
     * @return Return the current player
     */
    public AbstractPlayer getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Accessor of the list of player territories
     *
     * @return Returns the list of player territories
     */
    public Map<AbstractPlayer, List<Square>> getPlayersTerritories() {
        return playersTerritories;
    }

    /**
     * Observable accessor of currentPlayer
     *
     * @return Returns the current player into an observable property
     */
    public AbstractPlayer currentPlayerProperty() {
        return currentPlayer;
    }

    /**
     * Boolean accessor of endOfGame
     *
     * @return Returns TRUE if the game is finished, FALSE otherwise
     */
    public boolean isEndOfGame() {
        return endOfGame;
    }

    /**
     * Observable accessor of endOfGame
     *
     * @return Returns the state of the game into an observable property
     */
    public boolean endOfGameProperty() {
        return endOfGame;
    }

    /**
     * Accessor of turn
     *
     * @return Returns the current turn
     */
    public int getTurn() { return turn; }

    /**
     * Accessor of lastPlayer
     *
     * @return Returns the last player to play
     */
    public AbstractPlayer getLastPlayer() {
        return lastPlayer;
    }

    /**
     * Updates player territories
     */
    public void updatePlayersTerritories() {
        playersTerritories = AIUtils.territory(this);
    }

    /**
     * Initializes a game
     */
    private void initialize() {
        this.playerList = new ArrayList<AbstractPlayer>();
        endOfGame = false;
        numberOfMemberByCoalition = new HashMap<Integer, Integer>();

        for (int i = 0; i < Main.COALITIONS_PLAYER.length; i++) {
            numberOfMemberByCoalition.put(i, 0);
        }

        placePlayer();
    }

    /**
     * Create a new game
     */
    public void newGame() {
        grid.initialize();
        initialize();
    }

    /**
     * Puts players on the grid
     */
    private void placePlayer() {
        if ((Main.PLAYER_LOCATION == null) || (Main.PLAYER_LOCATION.length != Main.TOTAL_PLAYERS)) {
            placePlayerWithDefaultLocation();
        }
        else {
            placePlayerWithCustomLocation();
        }

        currentPlayer = playerList.get(Main.FIRST_PLAYER_POSITION);
        updatePlayersTerritories();
    }

    /**
     * Puts players on the grid in order to the distance between each player is maximum.
     */
    private void placePlayerWithDefaultLocation() {
        int optimalDistance = Utils.getOptimalDistance(grid.getSize(), Main.TOTAL_PLAYERS);
        int index = Main.FIRST_POSITION_SHIFT;
        ArrayList<Square> borderSquare = new ArrayList<Square>(Utils.getBorderSquare(this));
        Main.PLAYER_LOCATION = new Coordinates2D[Main.TOTAL_PLAYERS];

        int column = 0;
        int line = 0;
        for (int i = 0; i < Main.TOTAL_PLAYERS; i++) {
            if (index >= borderSquare.size()) {
                index -= borderSquare.size();
            }
            column = borderSquare.get(index).getColumn();
            line = borderSquare.get(index).getLine();
            PlayerBot player = new PlayerBot(column, line, Main.PLAYER_ORDERING[i], playerList.size());
            Main.PLAYER_LOCATION[i] = new Coordinates2D(column, line);

            playerList.add(player);
            grid.changeState(column, line, player);
            numberOfMemberByCoalition
                .put(player.getCoalition(), numberOfMemberByCoalition.get(player.getCoalition()) + 1);
            index += optimalDistance;
        }
    }

    /**
     * Place players with personalized coordinates
     */
    private void placePlayerWithCustomLocation() {
        int column;
        int line;
        for (int i = 0; i < Main.TOTAL_PLAYERS; i++) {

            column = Main.PLAYER_LOCATION[i].getColumn();
            line = Main.PLAYER_LOCATION[i].getLine();
            PlayerBot player = new PlayerBot(column, line, Main.PLAYER_ORDERING[i], playerList.size());

            playerList.add(player);
            grid.changeState(column, line, player);
            numberOfMemberByCoalition
                .put(player.getCoalition(), numberOfMemberByCoalition.get(player.getCoalition()) + 1);
        }
    }

    /**
     * Move a player if the destination is valid
     *
     * @param playerIndex Index of the player to move
     * @param movement    Movement to execute
     */
    public void move(int playerIndex, Movement movement) {
        AbstractPlayer player = playerList.get(playerIndex);
        if (player.canMoveTo(movement, grid)) {
            player.move(movement, grid);
        }
        else {
            player.setAlive(false);
        }
    }

    /**
     * Move the current user if the destination is valid
     *
     * @param movement Movement to execute
     * @return Returns true if the movement could be performed, false otherwise
     */
    public boolean move(Movement movement) {
        if (!endOfGame) {
            AbstractPlayer player = currentPlayer;
            if (player.canMoveTo(movement, grid)) {
                player.move(movement, grid);
                return true;
            }
            else {
                if (grid.getAvailableNeighbour(grid.getSquareAt(player.getCoordinates())).size() == 0) {
                    player.setAlive(false);

                    int nbDead = 0;

                    for (int i = 0; i < playerList.size(); i++) {
                        if (!playerList.get(i).isAlive()) {
                            nbDead++;
                        }
                    }

                    if (nbDead == playerList.size() - 1) {
                        nextPlayer();
                        checkVictory();
                    }
                }
                return false;
            }
        }
        return true;
    }

    /**
     * Goes to next player
     */
    public void nextPlayer() {

        int index = currentPlayer.getPlayerID();
        int newIndex = -1;

        for (int i = index + 1; i < playerList.size(); i++) {
            if ((playerList.get(i).isAlive())) {
                newIndex = i;
                break;
            }
        }
        if (newIndex < 0) {
            turn++;
            for (int i = 0; i < index; i++) {
                if ((playerList.get(i).isAlive())) {
                    newIndex = i;
                    break;
                }
            }
        }

        if (newIndex >= 0) {
            lastPlayer = currentPlayer;
            currentPlayer = playerList.get(newIndex);
        }
        stroke++;
        updatePlayersTerritories();
        checkVictory();
    }

    /**
     * Checks the conditions of victories.<br>
     * The condition of victory is: there is only the remaining members of one coalition alive.
     *
     * @return Returns TRUE if the win conditions are verified, FALSE otherwise
     */
    public boolean checkVictory() {

        Map<Integer, Integer> numberOfMemberDeadByCoalition = new HashMap<Integer, Integer>();

        for (int i = 0; i < Main.COALITIONS_PLAYER.length; i++) {
            numberOfMemberDeadByCoalition.put(i, 0);
        }

        for (int i = 0; i < playerList.size(); i++) {
            if (!playerList.get(i).isAlive()) {
                numberOfMemberDeadByCoalition.put(playerList.get(i).getCoalition(),
                    numberOfMemberDeadByCoalition.get(playerList.get(i).getCoalition()) + 1);
            }
        }

        int fullCoalitionDead = numberOfMemberByCoalition.size();

        for (int i = 0; i < numberOfMemberDeadByCoalition.size(); i++) {
            if (numberOfMemberDeadByCoalition.get(i).intValue() == numberOfMemberByCoalition.get(i).intValue()) {
                fullCoalitionDead--;
            }
        }

        if (fullCoalitionDead != 1) {
            return false;
        }
        endOfGame = true;
        return true;
    }

    /**
     * Running the game in console mode
     */
    public void running() {
        while (!endOfGame) {

            Utils.println("Player " + currentPlayer.getPlayerID() + " move [" +
                currentPlayer.getLine() + ";" + currentPlayer.getColumn() + "]");

            currentPlayer.play(this);

            Utils.println(" to [" + currentPlayer.getLine() + ";" + currentPlayer.getColumn() +
                "] (" + currentPlayer.getLastMovement() + ")");

            nextPlayer();
        }

        Utils.println("Coalition " + currentPlayer.getCoalition() + " won.");

    }

    /**
     * Returns all possible new states of play following the legal movements of the current player
     *
     * @return List of game states
     */
    public List<Game> getNextStep() {
        List<Game> l = new LinkedList<Game>();

        if (!this.isEndOfGame()) {
            Game g = this.clone();

            for (Movement m : Movement.values()) {
                if (g.currentPlayer.canMoveTo(m, g.getGrid())) {
                    g.move(m);
                    g.nextPlayer();

                    l.add(g);
                    g = this.clone();
                }
            }
        }

        return l;
    }

    /**
     * Returns the territory of each coalition. Key = coalition number, value, number of squares in the coalition.
     *
     * @return Returns the territory of each coalition.
     */
    public Map<Integer, Integer> getTerritoryByCoalition() {
        Map<Integer, Integer> coalitionTerritory = new HashMap<Integer, Integer>();

        Iterator<AbstractPlayer> iterator = getPlayersTerritories().keySet().iterator();
        AbstractPlayer key;
        Integer value;

        while (iterator.hasNext()) {
            key = iterator.next();
            value = getPlayersTerritories().get(key).size();

            if (coalitionTerritory.get(key.getCoalition()) == null) {
                coalitionTerritory.put(key.getCoalition(), value);
            }
            else {
                coalitionTerritory.put(key.getCoalition(), coalitionTerritory.get(key.getCoalition()) + value);
            }
        }

        return coalitionTerritory;
    }

    /**
     * Retrieves the player territories that is associated with a square
     *
     * @param square Square of which one wants to know the "owner"
     * @return Returns the player or null if the square does not belong to any territory
     */
    public AbstractPlayer getPlayerTerritoryBySquare(Square square) {
        for (Map.Entry<AbstractPlayer, List<Square>> entry : playersTerritories.entrySet()) {
            AbstractPlayer key = entry.getKey();
            List<Square> value = entry.getValue();
            for (Square cell : value) {
                if (square.equals(cell)) {
                    return key;
                }
            }
        }
        return null;
    }

    /**
     * Recover usable data from parts in CSV format for export
     *
     * @return Returns a string in one line in CSV format
     */
    public String toCSV() {
        StringBuilder result = new StringBuilder();
        result.append(Main.GRID_SIZE).append(",")
            .append(currentPlayer.getCoalition()).append(",")
            .append(stroke).append(",")
            .append(Main.FIRST_PLAYER_POSITION).append(",")
            .append(Main.TOTAL_PLAYERS).append(",")
            .append(Main.COALITIONS_PLAYER.length).append(",");

        for (int i = 0; i < Main.PLAYER_LOCATION.length; i++) {
            result.append(i != 0 ? " " : "").append(Main.PLAYER_LOCATION[i].toString());
        }
        result.append(",");

        for (int i = 0; i < Main.COALITIONS_PLAYER.length; i++) {
            result.append(i != 0 ? " " : "").append(Main.COALITIONS_PLAYER[i]);
        }
        result.append(",");

        for (int i = 0; i < Main.COALITIONS_PLAYER.length; i++) {
            result.append(i != 0 ? " " : "").append(Main.COALITIONS_DEPTH[i]);
        }
        result.append("\n");

        return result.toString();
    }

    /**
     * Generates a unique file name for the CSV
     *
     * @param path File path
     * @return Returns a string corresponding to the file name (plus the path to that file)
     */
    public String getCSVName(String path) {
        return path + "/" + Main.RESULT_NAME + ".csv";
    }

    @Override
    public Game clone() {
        Game o = null;
        try {
            o = (Game) super.clone();

            Map<AbstractPlayer, AbstractPlayer> correspondence = new HashMap<AbstractPlayer, AbstractPlayer>();

            for (AbstractPlayer p : this.playerList) {
                correspondence.put(p, p.clone());
            }

            o.playerList = new LinkedList<AbstractPlayer>();

            for (AbstractPlayer p : this.playerList) {
                o.playerList.add(correspondence.get(p));
            }

            o.grid = grid.clone(correspondence);

            o.playersTerritories = new HashMap<AbstractPlayer, List<Square>>(this.playersTerritories);

            o.numberOfMemberByCoalition = new HashMap<Integer, Integer>(this.numberOfMemberByCoalition);
            o.endOfGame = this.endOfGame;
            o.currentPlayer = correspondence.get(this.currentPlayer);
            if (o.lastPlayer != null) { o.lastPlayer = correspondence.get(this.lastPlayer); }

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return o;
    }
}
