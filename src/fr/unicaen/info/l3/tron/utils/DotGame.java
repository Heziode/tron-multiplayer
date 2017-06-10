package fr.unicaen.info.l3.tron.utils;

import fr.unicaen.info.l3.tron.model.GameTree;
import fr.unicaen.info.l3.tron.model.Square;
import fr.unicaen.info.l3.tron.model.player.AbstractPlayer;

/**
 * Allows to export a game tree in DOT language.
 *
 * @see <a href="http://www.graphviz.org/">Graphviz</a>
 */
public final class DotGame {

    /**
     * Set of colors for displaying each coalition
     */
    private static final String[][] COALITIONS_COLORS = {
        {"#2980b9", "#3498db", "#67CBFF"},
        {"#d35400", "#e67e22", "#FFB155"},
        {"#27ae60", "#2ecc71", "#61FFA4"},
        {"#c0392b", "#e74c3c", "#FF7F6F"},
        {"#16a085", "#1abc9c", "#4DEFCF"},
        {"#f39c12", "#f1c40f", "#FFF742"},
        {"#8e44ad", "#9b59b6", "#CE8CE9"},
    };

    /**
     * Define where to store calculations done by panoid
     */
    public static String PATH_TO_SAVE_FILES = "paranoid_traceback";

    /**
     * Reference to a game state (starting point for creating the tree)
     */
    private static GameTree gameTree;

    /**
     * Will contain a list of all links that will be added to the end of the dot file
     */
    private static StringBuilder dotNodeEdges;

    /**
     * Private constructor to prevent instantiation
     */
    private DotGame() {
    }

    /**
     * Allows to convert a decision tree (which is the paranoid calculation) into Dot language for display.
     *
     * @return Returns a decision tree in Dot language
     */
    private static String toDotLanguage() {
        StringBuilder result = new StringBuilder("digraph G{rankdir = BT;");
        dotNodeEdges = new StringBuilder();
        result.append(getDotNodes(gameTree)).append(dotNodeEdges.toString())
            .append("}");
        return result.toString();
    }

    /**
     * Builds a tree with different states of play
     *
     * @param gameTree Reference to a node of a game tree
     * @return Returns a tree in Dot format
     */
    private static String getDotNodes(GameTree gameTree) {
        StringBuilder result = new StringBuilder();
        result.append(getDotGame(gameTree));

        for (GameTree dg : gameTree.getChilds()) {
            result.append(getDotNodes(dg));

            dotNodeEdges.append("node").append(dg.getCurrentId()).append("->").append("node")
                .append(gameTree.getCurrentId()).append(" ");
            if (dg.isMax()) {
                dotNodeEdges.append("[color=red,penwidth=3.0] ");
            }
        }

        return result.toString();
    }

    /**
     * Retrieves the representation of a game state
     *
     * @param gameTree Reference to a node of a game tree
     * @return Returns a game board in Dot language
     */
    private static String getDotGame(GameTree gameTree) {
        StringBuilder result = new StringBuilder();

        result.append("node").append(gameTree.getCurrentId())
            .append("[shape = none label = <<table border=\"0\" cellspacing=\"0\">");
        int size = gameTree.getCurrentGame().getGrid().getSize();
        result.append("<tr><td colspan=\"").append(size).append("\"><font color=\"goldenrod\">node")
            .append(gameTree.getCurrentId())
            .append("</font></td></tr>");
        for (int line = size - 1; line >= 0; line--) {
            result.append("<tr>");
            for (int column = 0; column < size; column++) {
                result.append("<td border=\"1\"");
                Square square = gameTree.getCurrentGame().getGrid().getGrid()[column][line];
                String content = null;
                switch (square.getState()) {
                    case BLOCKED:
                        content = "B";
                        result.append(" bgcolor=\"").append("#34495e").append("\"").append(" >");
                        break;
                    case NOTVISITED:
                        AbstractPlayer player = gameTree.getCurrentGame().getPlayerTerritoryBySquare(square);
                        content = player != null ? "T" + player.getPlayerID() : "";

                        result.append(" bgcolor=\"")
                            .append(player != null ? getColor(player.getCoalition(), 2) : "#34495e").append("\"")
                            .append(" >");

                        break;
                    case OCCUPIED:
                        content = "J" + square.getPlayer().getPlayerID();
                        if (square.getPlayer() != null) {
                            result.append(" bgcolor=\"").append(getColor(square.getPlayer().getCoalition(), 1))
                                .append("\"");
                        }
                        result.append(" >");
                        break;
                    case VISITED:
                        content = "V" + square.getPlayer().getPlayerID();
                        if (square.getPlayer() != null) {
                            result.append(" bgcolor=\"").append(getColor(square.getPlayer().getCoalition(), 0))
                                .append("\"");
                        }
                        result.append(" >");
                        break;
                }

                result.append(content).append("</td>");
            }
            result.append("</tr>");
        }

        result.append("<tr><td colspan=\"").append(size).append("\">R : ").append(gameTree.getNodeValue(gameTree
            .getCurrentGame().getCurrentPlayer().getCoalition())).append("</td></tr>");
        if (gameTree.getChilds().size() > 0) {
            result.append("<tr><td colspan=\"").append(size).append("\">").append(gameTree.getState().toString())
                .append("</td></tr>");
        }

        result.append("</table>> color=\"#2c3e50\"]");

        return result.toString();
    }

    /**
     * Retrieves the coalition color. The table contains for each row a coalition and for each column, first the
     * background color, then the color of the player and finally the color of the territory
     *
     * @param coalition Coalition of which one wishes to have the color
     * @param index     Indicates which value retrieved
     * @return Returns a string corresponding to the color in hexadecimal format, example <code>#FFEEDD<code/>
     */
    private static String getColor(int coalition, int index) {
        return COALITIONS_COLORS[coalition % COALITIONS_COLORS.length][index];
    }

    /**
     * Save the Dot formatted tree to a file
     *
     * @param gameTree Reference to a node of a game tree
     * @param fileName File name
     */
    public static void saveInFile(GameTree gameTree, String fileName) {
        DotGame.gameTree = gameTree;
        SaveData.saveInFile(PATH_TO_SAVE_FILES + "/" + fileName, toDotLanguage());
    }
}
