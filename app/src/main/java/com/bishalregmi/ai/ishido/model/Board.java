package com.bishalregmi.ai.ishido.model;

/************************************************************
 * Name:  Bishal Regmi                                      *
 * Project:  Project 3 - Ishido for Two                     *
 * Class:  CMPS331 - Artificial Intelligence                *
 * Date:  03/22/2016                                        *
 ************************************************************/

import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to hold the gameBoard
 * Created by Bishal on 1/23/2016.
 * When markTile is called check the game1 conditions.
 * Check if it can be placed at that position.
 * Use integer values as scores. If, the tile is a lonely blank tile return o
 * if it is surrounded by a friend of same color or symbol return 1 else return -1.
 * Check the surrounding rows first abiding by the rules. If its top/bottom row, only check the row bottom/top of it respectively. else check both.
 * Check the surrounding columns with the rules. If its left/right row, only check the column left/right of it respectively. else check both.
 * If only checking one row/column return the score you get else add the score and return.
 * Add the scores from row and column only if they are not negative number. That will be the score. Add it to the existing score.
 * To check for possible moves, get the available symbols list and loop through the entire gameboard to see for empty tiles. If found, loop through all the color combinations to check if any can be placed in the tile.
 * If found, return true, else Loop until the game1 is finished and return false;
 */
public class Board {
    private Tile[][] gameBoard = new Tile[8][12];
    private Player human = new Player();
    private Player computer = new Player();
    private int turn = -1;
    private Deck deck = new Deck();
    private StateReader stateReader = new StateReader();
    private List<Move> presetMoves = new ArrayList<Move>(); //List to hold the default moves from the stateReader file
    private int presetHumanScore = 0;
    private int presetComputerScore = 0;
    private int presetTurn = -1;
    private long timer = 0;
    private Move bestMove;

    /*
    * Description: Method to set up the game1 from provided serialized text file
    * Parameters: InputStream for the text file
    * Returns: Nothing
     */
    public void setBoardFromState(InputStream is) {
        stateReader.setInputStream(is);
        updateBoard();
        updateStock();
        presetHumanScore = stateReader.getHumanScore();
        presetComputerScore = stateReader.getComputerScore();
        human.up(presetHumanScore);
        computer.up(presetComputerScore);
        presetTurn = stateReader.getTurn();
        turn= presetTurn;
    }

    /*
    * Description: Method to set up a new game with a fresh board and randomly generated stock
    * Parameters: turn as Integer to determine the next player, 0 is human and 1 is computer
    * Returns: Nothing
     */
    public void buildNewGame(int turn){
        deck.buildNewStock();
        this.turn = turn;

    }

    /*
    * Description: Method to determine the next player
    * Parameters: Nothing
    * Returns: Integer, 0 representing human and 1 representing computer
     */
    public int getTurn(){
        return turn;
    }

    /*
    * Description: Method to get the time taken to run the last algorithm
    * Parameters: Nothing
    * Returns: the time ni seconds as a double value
     */
    public double getAlgorithmTime(){
        return (double) timer/1000;
    }

    /*
    * Description: Method to get the move from the minmax algorithm which might or might not include alpha/beta pruning
    * Parameters: boolean pruning to determine if alpha/beta pruning should be used,
    *             int cutoff to specify the depth till which the search is to done
    *             boolean computer to determine whether computer or human should be the first maximizer
    * Returns: move from the algorithm as a Move object
     */
    private Move getMovesFromMinMax(boolean pruning, int cutoff, boolean computer){

        //default values for alpha and beta, opposites of what they need to be
        //this corresponds to the case when pruning is not to be used
        int alpha = Integer.MAX_VALUE;
        int beta = Integer.MIN_VALUE;
        if(pruning){
            //reversing the values when prunign is to be used
            alpha = Integer.MIN_VALUE;
            beta = Integer.MAX_VALUE;
        }

        //timer for the algorithm
        long startTime = System.currentTimeMillis();
        minmax(cutoff, true, getHumanScore(), getComputerScore(), computer, alpha, beta);
        long endTime = System.currentTimeMillis();
        timer= endTime-startTime;
        if(bestMove!= null) {
            if(computer) {
                markTile(bestMove);
                deck.incrementStockIndex();
            }
            Log.v("stockIndex",""+deck.stockIndex);
            Log.v("remainingTiles",""+deck.getRemainingTileCount());
            return bestMove;
        }
        else {
            return null;
        }
    }

    /*
    * Description: Method to get the hint for next move from the minmax algorithm which might or might not include alpha/beta pruning
    * Parameters: boolean pruning to determine if alpha/beta pruning should be used,
    *             int cutoff to specify the depth till which the search is to done
    * Returns: hint from the algorithm as a Move object
     */
    public Move getHint(boolean pruning, int cutoff){
        return  getMovesFromMinMax(pruning,cutoff,false);
    }

    /*
  * Description: method to get the computers next move the minmax algorithm which might or might not include alpha/beta pruning
    * Parameters: boolean pruning to determine if alpha/beta pruning should be used,
    *             int cutoff to specify the depth till which the search is to done
    * Returns: hint from the algorithm as a Move object
     */
    public Move getComputerMove(boolean pruning, int cutoff){
        return  getMovesFromMinMax(pruning,cutoff,true);
    }

    /*
    * Description: method to flip the turn.
      * Parameters: nothing
      * Returns: nothing.
       */
    public void flipTurn(){
        if(turn == 0){
            turn = 1;
        }
        else if(turn == 1){
            turn = 0;
        }
    }


    /*
      * Description: method to get all the moves that are imported from the stateReader and remain default.
      * Parameters: Nothing
      * Returns: List of Moves
       */
    public List<Move> getPresetMoves() {
        return presetMoves;
    }

    /*
      * Description: method to get the tile placed in the specified row and column of the game game1
      * Parameters: row and column of the tile
      * Returns: tile at the location as a Tile object
       */
    public Tile getTile(int row, int col) {
        return gameBoard[row][col];
    }

    /*
    * Description: Private method to populate the game1 with the values from the file, update the deck and remaining tile counts accordingly
    * Parameters: Nothing
    * Returns: Nothing
    */
    private void updateBoard() {
        Integer[] tempBoard = stateReader.getLayout();
        int row = 0;
        int col = 0;
        char[] symbols = deck.getSymbols();
        int[] colors = deck.getColors();
        //traversing through the list of tiles from the file
        for (int i = 0; i < tempBoard.length; i++) {
            int combo = tempBoard[i];
            int color = (combo / 10) - 1;
            int symbol = (combo % 10) - 1;
            //extracting the combination and setting on the gameboard
            //also updating the deck
            if (color == -1 || symbol == -1) {
                gameBoard[row][col] = null;
            } else {
                Tile tile = new Tile(colors[color], symbols[symbol]);
                gameBoard[row][col] = tile;
                deck.markCombo(tile);
                presetMoves.add(new Move(row, col, tile));
            }
            col++;
            //if last column, go to next row
            if (col == 12) {
                row++;
                col = 0;
            }
        }
    }

    /*
    * Description: Private method to update the deck stock with the values from the file.
    * Parameters: Nothing
    * Returns: Nothing
    */
    public void updateStock() {
        Integer[] stock = stateReader.getStock();
        Tile[] tiles = new Tile[stock.length];
        char[] symbols = deck.getSymbols();
        int[] colors = deck.getColors();
        //updating the stock from the file
        for (int i = 0; i < stock.length; i++) {
            int combo = stock[i];

            int color = (combo / 10) - 1;
            int symbol = (combo % 10) - 1;
            Tile tile = new Tile(colors[color], symbols[symbol]);
            tiles[i] = tile;
        }
        deck.setStock(tiles);

    }


    /*
   * Description: Pmethod to generate move using the minmax algorithm that migh or mognt not use alpha beta pruning
   * Parameters: int cutoff as depth to which the search is to be conducted
   *             boolean maximizer to specify if the node is supposed to be a maximizer or minimizer
   *             int humanScore as the score for human at the instant that will be used to calculate heuristic
   *             int computerrScore as the score for computer at the instant that will be used to calculate heuristic
   *             boolean computer to determine if the node is supposed be computer's move or human's
   *             int alpha as alpha value in case alpha beta pruning it to be done, set maximum value if not
   *             int beta as beta value in case alpha beta pruning is ti be dine, set minimum value if not
   * Returns: Nothing
   */
    private int minmax(int cutOff, boolean maximizer, int humanScore, int computerScore, boolean computer,int alpha, int beta){
        boolean pruning = false;
        //checking if pruning is to be used or not
        if(alpha != Integer.MAX_VALUE && beta != Integer.MIN_VALUE) pruning = true;
        //if cutoff is reached or the node is a leaf node(there is no node after this)
        if(cutOff== 0  || !isNextMoveAvailable()){
            int heuristic;
            if(turn == 1){
                //if its computer's turn
                heuristic = computerScore-humanScore;
            }
            else {
                //if it's human's turn
                heuristic = humanScore-computerScore;
            }
            return heuristic;
        }
        if(maximizer){
            //default heuristic is negative infinity for maximizer
            int bestHeuristic = Integer.MIN_VALUE;
            //temporary bestMove for the current depth
            Move tempBestMove = null;
            //get a next available move on a row major order
            Move child = getNextMove(new Move(-1,-1,deck.getNextTile()));
            //looping till there are no child left
            while(child != null){
                //adding the score for the heuristic based on whose turn it is
                //changing the player since the next node is supposed be played by a different player.
                if(computer){
                    computerScore+= child.score;
                    computer = false;
                }
                else{
                    humanScore+= child.score;
                    computer = true;
                }
                markTile(child);
                //running the algorithm as child as the root with a cuttof decremented by 1.
                int tempHeuristic = minmax(cutOff - 1, false, humanScore, computerScore, computer, alpha,beta);
                clearTile(child);
                //since its a maximizer, select the heuristic only if its maximum
                if(tempHeuristic>bestHeuristic){
                    //set it as the temporary best move and the heuristic
                    tempBestMove = child;
                    bestHeuristic= tempHeuristic;
                }
                if(pruning) {
                    //if pruning is to be done, since its a maximizer, only set alpha as the new heuristic in case its greater.
                    if (bestHeuristic > alpha) alpha = bestHeuristic;
                    //However, if the alpha value is greater than or equal to beta, neither the node nor its children is useful because no better node than here.
                    if (alpha>=beta) break;
                }
                //Since the player was changed to be a different palyer before the child was considerd, now after the child has been considered the player needs to rolled back to what it is.
                computer = !computer;
                //score were also updaed based on the turn which need to be reverted.
                if(computer){
                    computerScore-= child.score;
                }
                else{
                    humanScore-= child.score;
                }
                //getting the next child
                child = getNextMove(child);
            }
            //moving a step back on stock after a depth of tiles in considered.
            deck.decrementStockIndex();
            //assigning the best move while coming back from recursion.
            bestMove =tempBestMove;
            //returning the best heruristic from the depth.
            return bestHeuristic;

        }else{
            //default heuristic is positive infinity for minimzier
            int bestHeuristic = Integer.MAX_VALUE;
            //temporary bestMove for the current depth
            Move tempBestMove = null;
            //get a next available move on a row major order
            Move child = getNextMove(new Move(-1,-1,deck.getNextTile()));
            //looping till there are no child left
            while(child != null){
                //adding the score for the heuristic based on whose turn it is
                //changing the player since the next node is supposed be played by a different player.
                if(computer){
                    computerScore+= child.score;
                    computer = false;
                }
                else{
                    humanScore+= child.score;
                    computer = true;
                }
                markTile(child);
                //running the algorithm as child as the root with a cuttoff decremented by 1.
                int tempHeuristic = minmax(cutOff - 1, true, humanScore, computerScore, computer, alpha, beta);
                clearTile(child);
                //since its a minimizer, select the heuristic only if its minimum
                    if (tempHeuristic < bestHeuristic) {
                        //set it as the temporary best move and the heuristic
                        tempBestMove = child;
                        bestHeuristic = tempHeuristic;
                    }
                    if (pruning) {
                        //if pruning is to be done, since its a minimizer, only set beta as the new heuristic in case its lower.
                        if (bestHeuristic < beta) beta = bestHeuristic;
                        //However, if the beta value is less than or equal to alpha, neither the nor its children is useful because no better node than beta is coming from here.
                        if (beta <= alpha) break;
                    }
                //Since the player was changed to be a different palyer before the child was considerd, now after the child has been considered the player needs to rolled back to what it is.
                computer = !computer;
                //score were also updaed based on the turn which need to be reverted.
                if(computer){
                    computerScore-= child.score;
                }
                else{
                    humanScore-= child.score;
                }
                //getting the next child
                child = getNextMove(child);
            }
            //moving a step back on stock after a depth of tiles in considered.
            deck.decrementStockIndex();
            //assigning the best move while coming back from recursion.
            bestMove = tempBestMove;
            //returning the best heruristic from the depth.
            return bestHeuristic;
        }
    }

    /*
    * Description: method to determine if there is any move available for the next tile on top of the stact
    * Parameters: None
    * Returns: boolean value, true if move is available, else false.
    */
    public boolean isNextMoveAvailable(){
        if(deck.getRemainingTileCount()==0) return false;
        Move move = getNextMove(new Move(-1,-1,deck.getNextTile()));
        deck.decrementStockIndex();
        if(move == null) return false;
        else return true;
    }

    /*
 * Description: Private function to get the next possible move after the last move
 * Parameters: lastMove as moveObject
 * Returns: nextMove as a Move object
  */
    private Move getNextMove(Move move) {;
        int row = move.row;
        int col = move.col;
        Move tempMove = null;
        //row-major searching of next possible move from the given move
        while ((row < 7 && row >= -1) || (col < 11 && col >= -1)) {
            if (row == -1) {
                row++;
            }
            if (col < 11 && col >= -1) {
                col++;
            } else if (row != 7) {
                row++;
                col = 0;
            } else {
                continue;
            }
            tempMove = checkMove(new Move(row, col, move.tile), false);
            if (tempMove != null) {
                return tempMove;
            }
        }
        return null;
    }

    /*
     * Description: Public function to check if a certain move is legal.
     * Parameters: Move to check as a move object, boolean value to indicate if that move is to be marked in the game1 or not.
     * Returns: move as a move Object with the respective score if its legal, null otherwise.
      */
    public Move checkMove(Move move, boolean mark) {
        //if there is already a tile in that position, return null
        if (gameBoard[move.row][move.col] != null) {
            return null;
        }
        move.score = checkBoardConditions(move);

        //if the score is 0 or greater its valid.
        if (move.score > -1) {
            if (mark) {
                markTile(move);
            }
            return move;
        }
        return null;
    }


    /*
    * Description: Private function to mark the gameboard array with specific move and also update deck and score.
    * Parameters: Move as a moveObject
    * Returns: Nothing
     */

    private void markTile(Move move) {
        gameBoard[move.row][move.col] = move.tile;
        deck.markCombo(move.tile);
        if (move.score > 0) {
            addScore(move.score);
        }
    }

    /*
    * Description: Private function to clear the gameboard array with specific move and also update deck and score.
    * Parameters: Move as a moveObject
    * Returns: Nothing
   */
    private void clearTile(Move move) {
        gameBoard[move.row][move.col] = null;
        deck.deleteCombo(move.tile);
        subScore(move.score);

    }

    /*
  * Description: Private function to check if a move can be placed on a specific position based on its surrounding moves.
  * Parameters: Move as a moveObject
  * Returns: score yielded from the move as integer, -1 if the move is illegal.
   */
    private int checkBoardConditions(Move move) {
        int row = move.row;
        int col = move.col;
        Tile tile = move.tile;
        int leftScore = checkTile(row, col-1,tile);
        if(leftScore == -1){
            return -1;
        }
        int rightScore = checkTile(row, col+1,tile);
        if(rightScore == -1){
            return -1;
        }
        int upScore = checkTile(row-1,col,tile);
        if(upScore == -1){
            return -1;
        }
        int downScore = checkTile(row+1,col,tile);
        if(downScore == -1){
            return -1;
        }
        return leftScore+rightScore+upScore+downScore;
    }

    /*
    * Description: Private function to check if  a given tile combination matches with the attributes of the tile at the given row and column index with the game rules..
    * Parameters: Move as a moveObject
    * Returns: Score from the move as integer, -1 if invalid move.
     */

    private int checkTile(int row, int col, Tile tile) {
        if(row<0 || row>7 || col<0 || col>11){
            return 0;
        }
        if (gameBoard[row][col] == null) {
            return 0;
        }
        if (gameBoard[row][col].color == tile.color || gameBoard[row][col].symbol == tile.symbol) {
            return 1;
        }
        return -1;
    }

    /*
    * Description: Public function to get the Deck Object.
    * Parameters: Nothing.
    * Returns: Deck object.
     */

    public Deck getGameDeck() {
        return deck;
    }

    /*
    * Description: Private function to add the move score.
    * Parameters: move score as integer.
    * Returns: nothing.
     */
    private void addScore(int moveScore) {
        if(turn == 0) {
            human.up(moveScore);
        }else if (turn == 1){
            computer.up(moveScore);
        }
    }

    /*
  * Description: Private function to add the move score.
  * Parameters: move score as integer.
  * Returns: nothing.
   */
    private void subScore(int moveScore) {
        if(turn == 0) {
            human.down(moveScore);
        }else if (turn == 1){
            computer.down(moveScore);
        }
    }

     /*
    * Description: Public function to reset the game including the deck, score and the gameboard and all the associated lists
    * Parameters: move score as integer.
    * Returns: nothing.
     */

    public void reset() {
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[i].length; j++) {
                gameBoard[i][j] = null;
            }
        };
        human.reset();
        computer.reset();
        deck.resetStock();
        for (Move move : presetMoves) {
            markTile(move);
        }

        human.up(presetHumanScore);
        computer.up(presetComputerScore);
        turn =presetTurn;
    }

     /*
    * Description: Public function to get the human score.
    * Parameters: nothing.
    * Returns:  human score as an integer.
     */
    public int getHumanScore() {
        return human.getScore();
    }

    /*
   * Description: Public function to get the computer score.
   * Parameters: nothing.
   * Returns:  computer score as an integer.
    */
    public int getComputerScore(){return computer.getScore();}

}

