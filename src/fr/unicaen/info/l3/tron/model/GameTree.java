package fr.unicaen.info.l3.tron.model;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * A class that stores game states (and information about them)
 */
public final class GameTree {

    /**
     * Number of the current node. Used for transformation into DOT language
     */
    private static int NODE_ID;

    /**
     * Determine if this is a max path or not
     */
    private boolean max;

    /**
     * Current game state
     */
    private Game currentGame;

    /**
     * Reference to child states
     */
    private List<GameTree> childs;

    /**
     * Id of the current node
     */
    private int currentId;

    /**
     * Value of the node defined by paranoid according to the territory of each player (min/max state)
     */
    private int nodeValue;

    /**
     * State of this node
     */
    private State state;

    /**
     * Indicates the last child index that was used to calculate nodeValue. Serves to be up to date.
     */
    private int childPointer;

    /**
     * Logical constructor
     *
     * @param currentGame Current game state
     * @param state       State of this node
     */
    public GameTree(Game currentGame, State state) {
        this.currentGame = currentGame;
        childs = new LinkedList<GameTree>();
        currentId = NODE_ID++;
        nodeValue = -1;
        this.state = state;
    }

    /**
     * Reset the tree node counter
     */
    public static void resetNodeCounter() {
        NODE_ID = 0;
    }

    /**
     * Accessor of currentGame
     *
     * @return Retrun currentGame
     */
    public Game getCurrentGame() {
        return currentGame;
    }

    /**
     * Accessor of childs
     *
     * @return Returns a list of children of the node
     */
    public List<GameTree> getChilds() {
        return childs;
    }

    /**
     * Accessor of currentId
     *
     * @return Returns the current id of the node
     */
    public int getCurrentId() {
        return currentId;
    }

    /**
     * Accessor of max
     *
     * @return Returns TRUE if it is the node max, FALSE otherwise
     */
    public boolean isMax() {
        return max;
    }

    /**
     * Accessor of state
     *
     * @return Returns the current state of the node
     * @see State
     */
    public State getState() {
        return state;
    }

    /**
     * Accessor of nodeValue
     *
     * @param coalition Reference to the coalition of the player who launched the paranoid calculation
     * @return Returns the status of nodeValue
     */
    public int getNodeValue(int coalition) {
        if (nodeValue < 0) {
            updateNodeValue(coalition);
        }
        if (childPointer < childs.size()) {
            updateMinMaxWithChild(childs.get(childPointer++), coalition);
        }
        return nodeValue;
    }

    /**
     * Adds a successor state
     *
     * @param game  Reference of successor
     * @param depth Depth to which to insert the element (recursive call)
     * @return Returns the reference to the added element
     */
    public GameTree addChild(Game game, int depth) {
        if ((depth) > 1) {
            return childs.get(childs.size() - 1).addChild(game, depth - 1);
        }
        else {
            State state = game.getCurrentPlayer().getCoalition() == this.currentGame.getCurrentPlayer().getCoalition
                () ?
                this.state : this.state.next();

            GameTree gameTree = new GameTree(game, state);
            childs.add(gameTree);
            return gameTree;
        }
    }

    /**
     * Changes the max state
     */
    public void addChildMax() {
        removeMax();
        GameTree game = this;
        while (game != null) {
            game.max = true;
            if (game.childs.size() > 0) {

                for (GameTree gameTree : game.childs) {
                    if (gameTree.nodeValue == this.nodeValue) {
                        game = gameTree;
                        break;
                    }
                    else {
                        game = null;
                    }
                }
            }
            else {
                game = null;
            }
        }
    }

    /**
     * Removes the current max state of one of the children (and its children)
     */
    private void removeMax() {
        ListIterator<GameTree> li = childs.listIterator(childs.size());

        while (li.hasPrevious()) {
            GameTree gameTree = li.previous();

            if (gameTree.max) {
                gameTree.max = false;
                gameTree.removeMax();
                break;
            }
        }
    }

    /**
     * Updates the nodeValue value of a node according to the values of his sons
     *
     * @param gameTree Node to update
     * @param coaltion Reference to the allied coalition
     */
    private void updateMinMaxWithChild(GameTree gameTree, int coaltion) {
        switch (this.state) {
            case MIN:
                if (this.nodeValue < 0) {
                    this.nodeValue = Integer.MAX_VALUE;
                }
                this.nodeValue = Math.min(this.nodeValue, gameTree.getNodeValue(coaltion));
                break;

            case MAX:
                this.nodeValue = Math.max(this.nodeValue, gameTree.getNodeValue(coaltion));
                break;
        }
    }

    /**
     * Updates the value of nodeValue based on territory and successor states
     *
     * @param coaltion Reference to the allied coalition
     */
    public void updateNodeValue(int coaltion) {
        if (childs.size() == 0) {
            this.nodeValue = this.getCurrentGame().getTerritoryByCoalition().get(coaltion);
        }
        else {
            for (GameTree gameTree : childs) {
                updateMinMaxWithChild(gameTree, coaltion);
            }
        }
    }

    /**
     * Defines the possible state of a node (MIN or MAX)
     */
    public enum State {
        MIN,
        MAX;

        /**
         * Retrieves the successor state
         *
         * @return Returns the next state
         */
        public State next() {
            return values()[(ordinal() + 1) % values().length];
        }
    }
}
