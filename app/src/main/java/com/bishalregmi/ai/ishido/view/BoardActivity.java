package com.bishalregmi.ai.ishido.view;

/************************************************************
 * Name:  Bishal Regmi                                      *
 * Project:  Project 3 - Ishido for Two                     *
 * Class:  CMPS331 - Artificial Intelligence                *
 * Date:  03/22/2016                                        *
 ************************************************************/

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.bishalregmi.ai.ishido.R;
import com.bishalregmi.ai.ishido.model.Board;
import com.bishalregmi.ai.ishido.model.Deck;
import com.bishalregmi.ai.ishido.model.Move;
import com.bishalregmi.ai.ishido.model.Tile;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/************************************************************
 * * Created by Bishal on 1/22/2016.
 * This is the activity class for the game1.
 * Implement gameboard as an 8X12 grid each holding a button.
 * Create button using java code.
 * push it to a list of button so that you can reset it later.
 * Check what mode has been selected and show the items as required by the corresponding mode or else hide them.
 * Get the tile combination and check if it is valid by checking the gameboard array.
 * If yes,mark the game1 and also mark the array in the game1 class.
 * If no, pop an error
 * update the score
 * check the remaining tile count
 * check for possible moves
 * if any of the above two is not satisfied display the error giving options either to reset or exit
 * else generate a new tile using deck class and mark the patch with the combination
 * or repopulate the deck spinner and color spinner with the available tiles
 * For the reset button, when it is clicked, reset all the tiles on the grid, reset the gameboard array, reset the deck, reset the score.
 ************************************************************/


public class BoardActivity extends Activity {

    //declared gameboard and deck;
    final Board gameBoard = new Board();
    final Deck deck = gameBoard.getGameDeck();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);


        //Receiving the field Id of text file from StartActivity.
        final Integer gameType = (Integer) getIntent().getSerializableExtra("arg1");
        //If its a new game
        if(gameType == 0){
            Integer turn = (Integer) getIntent().getSerializableExtra("arg2");
            Log.v("Turn received",""+turn);
            gameBoard.buildNewGame(turn);
        }
        //If its a loaded game
        else if (gameType == 1){
            String fileName = (String) getIntent().getSerializableExtra("arg2");

            Log.v("filename",""+fileName);
            //Opening the file as InputStream
            try {
                if(isExternalStorageReadable()) {
                    //fetching the filename to load and loading the game from the file by sending the file as InputStream to gameBoard
                    String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+fileName;
                    InputStream is = new FileInputStream(filePath);
                    gameBoard.setBoardFromState(is);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            //setting up the gameBoard from the state.
        }

        final Button tilePatch = (Button) findViewById(R.id.tilePatch);
        //setting the preview Tile
        markButton(deck.getNextTile(), tilePatch);
        deck.decrementStockIndex();


        //The entire grid for the game
        final GridLayout gridLayout = (GridLayout) findViewById(R.id.gridView);


        final TextView humanScore = (TextView) findViewById(R.id.humanScoreLabel);
        final TextView computerScore = (TextView) findViewById(R.id.computerScoreLabel);
        final TextView nextPlayer = (TextView)findViewById(R.id.nextPlayerLabel);
        changeNextPlayer(gameBoard.getTurn(), nextPlayer);

        //setting the initial score
        humanScore.setText("" + gameBoard.getHumanScore());
        computerScore.setText(""+gameBoard.getComputerScore());

        final NumberPicker cutOffPicker = (NumberPicker)findViewById(R.id.numberPicker);
        //disabling keyboard, setting max/min value for numberpicker
        cutOffPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        cutOffPicker.setMaxValue(deck.getRemainingTileCount()); //maximum number of tiles is 72
        cutOffPicker.setMinValue(1);

        //array to hold last hint button
        final Button[] hint = new Button[1];

        //array to hold the tempStock Index required for peek button
        final int[] index = {0};

        //List to keep track of all the buttons so that they can be resetted
        final List<Button> gridButtons = new ArrayList<Button>();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 13; col++) {


                final Button button1 = new Button(this);
                //setting the button id such that it is easy to extract the row and column to mark the gameboard key being first two digits as row number and last 2 digits being column number;

                button1.setId(row * 100 + col);
                button1.setTextColor(Color.parseColor("#FF38ACEC"));
                button1.setBackgroundResource(android.R.drawable.btn_default);
                button1.setWidth(10);
                button1.setHeight(10);
                //labelling the rows and columns
                if (row == 0 || col == 0) {
                    button1.setEnabled(false);
                    button1.setTextColor(Color.parseColor("#FFFFFFFF"));
                    if (row == 0 && col != 0) {
                        button1.setText("" + col);
                    }
                    if (col == 0 && row != 0) {
                        button1.setText("" + row);
                    }
                } else {
                    Tile tempTile = gameBoard.getTile(row - 1, col - 1);
                    if (tempTile != null) {
                        markButton(tempTile, button1);
                    }


                    gridButtons.add(button1);

                    //update the button from the game1

                    button1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(gameBoard.getTurn()==0) {
                                int buttonId = v.getId();
                                //boolean randomMode = false;

                                //Using the tilePatch to get the attributes for the button
                                Tile tile = deck.getNextTile();
                                deck.decrementStockIndex();


                                //Extracting the row and column using the button id and the algorithm used
                                //attempting to mark the tile in the gameboard array
                                if (gameBoard.checkMove(new Move((buttonId / 100) - 1, (buttonId % 100) - 1, tile), true) != null) {
                                    deck.incrementStockIndex();
                                    //marking the tile in the grid

                                    //clearing the hint
                                    if(hint[0]!=null) {
                                        hint[0].clearAnimation();
                                        hint[0].setBackgroundResource(android.R.drawable.btn_default);
                                        hint[0].setText("");
                                        hint[0]=null;
                                    }
                                    markButton(tile, button1);
                                    index[0]=0;
                                    humanScore.setText("" + gameBoard.getHumanScore());

                                    gameBoard.flipTurn();
                                    changeNextPlayer(gameBoard.getTurn(), nextPlayer);

                                    markButton(deck.getNextTile(), tilePatch);
                                    deck.decrementStockIndex();
                                    cutOffPicker.setMaxValue(deck.getRemainingTileCount());

                                    //checking if the game is full
                                    if(!gameBoard.isNextMoveAvailable()){
                                        openFinishDialog("moves");
                                    }

                                    if (deck.getRemainingTileCount() == 0) {
                                        openFinishDialog("tiles");
                                    }


                                } else {
                                    Toast.makeText(BoardActivity.this, "Invalid Move", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(BoardActivity.this, "Sorry! It's computer's turn!", Toast.LENGTH_SHORT).show();
                            }

                        }

                    });
                }

                //Adding the buttons to the grid
                GridLayout.Spec gridRow = GridLayout.spec(row, 1);
                GridLayout.Spec gridCol = GridLayout.spec(col, 1);

                GridLayout.LayoutParams gridLayoutParam = new GridLayout.LayoutParams(gridRow, gridCol);
                gridLayout.addView(button1, gridLayoutParam);

            }
        }

        Button peekButton = (Button)findViewById(R.id.peekButton);
        peekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gameBoard.getTurn() ==0){
                    Tile tile = deck.getTileAt(++index[0]);
                    markButton(tile,tilePatch);
                }else{
                    Toast.makeText(BoardActivity.this, "Sorry! It's computer's turn!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        final CheckBox pruningCheckBox = (CheckBox)findViewById(R.id.pruningCheck);

        final TextView timer = (TextView) findViewById(R.id.timeLabel);

        final Button goButton = (Button) findViewById(R.id.goButton);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameBoard.getTurn() == 1) {
                    boolean pruning = false;
                    if (pruningCheckBox.isChecked()) {
                        pruning = true;
                    }
                    Move move = gameBoard.getComputerMove(pruning, cutOffPicker.getValue());
                        Button button = gridButtons.get(move.row * 12 + move.col);
                        markButton(move.tile, button);
                        gameBoard.flipTurn();

                        Animation animation = new AlphaAnimation(1, 0);
                        animation.setDuration(500);
                        button.setAnimation(animation);

                        timer.setText("" + gameBoard.getAlgorithmTime() + " s");
                        computerScore.setText("" + gameBoard.getComputerScore());
                        changeNextPlayer(gameBoard.getTurn(), nextPlayer);

                        markButton(deck.getNextTile(), tilePatch);
                        deck.decrementStockIndex();
                        cutOffPicker.setMaxValue(deck.getRemainingTileCount());

                    if(!gameBoard.isNextMoveAvailable()){
                        openFinishDialog("moves");
                    }

                } else {
                    Toast.makeText(BoardActivity.this, "Sorry! It's your turn!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Button hintButton = (Button) findViewById(R.id.hintButton);
        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameBoard.getTurn() == 0) {
                    //clearing the last hint
                    if(hint[0]!=null) {
                        hint[0].clearAnimation();
                        hint[0].setBackgroundResource(android.R.drawable.btn_default);
                        hint[0].setText("");
                    }
                    boolean pruning = false;
                    if (pruningCheckBox.isChecked()) {
                        pruning = true;
                    }
                    Move move = gameBoard.getHint(pruning, cutOffPicker.getValue());
                        Button button = gridButtons.get(move.row * 12 + move.col);
                        markButton(move.tile, button);
                        button.setEnabled(true);
                        hint[0]=button;

                        Animation animation = new AlphaAnimation(1, 0);
                        animation.setDuration(500);
                        animation.setRepeatCount(Animation.INFINITE);
                        animation.setRepeatMode(Animation.REVERSE);
                        button.setAnimation(animation);

                        Toast.makeText(BoardActivity.this,"This move will yield a score of: "+move.score,Toast.LENGTH_SHORT).show();
                        timer.setText("" + gameBoard.getAlgorithmTime() + " s");
                } else {
                    Toast errorToast = Toast.makeText(BoardActivity.this, "Sorry! It's computer's turn!", Toast.LENGTH_SHORT);
                    errorToast.show();
                }
            }
        });
        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                saveGame();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });


        //EventListener for resetting the grid and the gameboard
        Button resetButton = (Button) findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTiles(gridButtons, tilePatch);
            }

        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }



    /*
    * Description: Private function to show the dialog box when the moves or tiles are finished. Quits the app or resets the game depending upon user's choices.
    * Parameters: string for the type of error to print
    * Returns: nothing
     */

    private void openFinishDialog(String errorString) {
        AlertDialog.Builder alert = new AlertDialog.Builder(BoardActivity.this);
        alert.setTitle("Game Over!");
        String winner;
        if(gameBoard.getHumanScore()>gameBoard.getComputerScore()){
            winner =" Congratulations! You won!\n";
        }
        else{
            winner = " Sorry! You lost!\n";
        }
        alert.setMessage("No more " + errorString + winner +"! Your score is: " + gameBoard.getHumanScore() + " \n Computer's score is: "+gameBoard.getComputerScore());

        alert.setPositiveButton("Restart", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Class startClass = Class.forName("com.bishalregmi.ai.ishido.view.StartActivity");
                    Intent myIntent = new Intent(BoardActivity.this, startClass);
                    startActivity(myIntent);

                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
                android.os.Process.killProcess(android.os.Process.myPid());

            }
        });

        alert.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });
        alert.show();
    }

    /*
       * Description: method to switch the turns in the label.
       * Parameters: int turn as the next turn, TextView pplayerLabel as the label for the turn to be specified on
       * Returns: nothing.
        */
    private void changeNextPlayer(int turn, TextView playerLabel){
        if(turn == 0){
            playerLabel.setText("Human");
        }
        else if(turn == 1){
            playerLabel.setText("Computer");
        }
    }


    /*
    * Description: Private function reset the game1, gridButtons, the scoreLabel and tilepatch
    * Parameters: List of gridbuttons and the tilepatch
    * Returns: nothing
     */
    private void resetTiles(List<Button> gridButtons, Button tilePatch) {
        //clearing all the buttons
        resetButtons(gridButtons);
        gameBoard.reset();
        markButton(deck.getNextTile(), tilePatch);
        deck.decrementStockIndex();
        //setting the buttons with preset moves

        TextView humanScore = (TextView) findViewById(R.id.humanScoreLabel);
        int gameScore = gameBoard.getHumanScore();
        humanScore.setText("" + gameScore);

        TextView computerScore = (TextView) findViewById(R.id.computerScoreLabel);
        gameScore = gameBoard.getComputerScore();
        computerScore.setText("" + gameScore);

        TextView turn = (TextView) findViewById(R.id.nextPlayerLabel);
        changeNextPlayer(gameBoard.getTurn(),turn);

    }


    /*
   * Description: Private function reset the boardButtons to the initial state
   * Parameters: List of gridbuttons
   * Returns: nothing
    */
    public void resetButtons(List<Button> gridButtons) {
        for (Button button : gridButtons) {
            button.setBackgroundResource(android.R.drawable.btn_default);
            button.setText("");
            button.clearAnimation();
            button.setEnabled(true);
        }

        //setting the preset moves from the game StateReader
        List<Move> presetMoves = gameBoard.getPresetMoves();
        for (Move move : presetMoves) {
            int location = ((move.row * 12) + move.col);
            Button button = gridButtons.get(location);
            markButton(move.tile, button);
        }
    }

    /*
     * Description: Private General function to mark a specific tile buttons with the given attributes.
     * Parameters: Tile and Button.
     * Returns: nothing
      */
    private void markButton(Tile tile, Button button) {

        button.setBackgroundColor(tile.color);

        String symbol = Character.toString(tile.symbol);
        button.setText(symbol);
        button.setEnabled(false);
    }

    /*
       * Description: method to save the game in a serialized text file.
       * Parameters: nothing
       * Returns: nothing.
        */
    private void saveGame(){
        String layoutString = "Layout:\n";

        //Writing the layout/board
        for(int i=0; i<8;i++){
            for(int j=0; j<12;j++){

                Tile tile = gameBoard.getTile(i,j);
                if(tile == null){
                    layoutString+="00";
                }
                else {
                    int color = deck.getIndexForColor(tile.color) + 1;
                    layoutString += color;
                    int symbol = deck.getIndexForSymbol(tile.symbol) + 1;
                    layoutString += symbol;
                }
                    layoutString += " ";

            }
            layoutString+="\n";
        };

        //Writing the stock
        String stockString =" \n"+"Stock:\n";
        List<Tile> stock = deck.getRemainingStock();
        for(Tile tile: stock){
            int color = deck.getIndexForColor(tile.color)+1;
            stockString+=color;
            int symbol = deck.getIndexForSymbol(tile.symbol)+1;
            stockString+=symbol;
            stockString+=" ";
        }
        stockString+=" \n";

        //Writing the scores
        String scoreString = " \n"+"Human Score:\n"+gameBoard.getHumanScore()+"\n"+" \n"+"ComputerScore:\n"+gameBoard.getComputerScore()+"\n";
        scoreString+=" \n";

        //Writing the next player
        scoreString+="Next Player:\n";
        int turn = gameBoard.getTurn();
        if(turn == 0){
            scoreString+="Human\n";
        }
        else if(turn == 1){
            scoreString+="Computer\n";
        }

        //combining all the strings
        String writeString = layoutString+stockString+scoreString;
        try{
            if(isExternalStorageWritable()) {
                File file = getFile();
                OutputStream os = new FileOutputStream(file);
                os.write(writeString.getBytes());
                os.close();
                Toast toast = Toast.makeText(BoardActivity.this, "Game saved successfully as " + file.getName(), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
       * Description: method to check if the external storage is writable
       * Parameters: nothing
       * Returns: boolean value, true if is writable, else false
        */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /*
      * Description: method to check if the external storage is readable
      * Parameters: nothing
      * Returns: boolean value, true if is readable, else false
       */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) ;
    }

    /*
      * Description: method to get a unique fileName for the game, with format Game[uniqueNumber].txt
      * Parameters: nothing
      * Returns: the file to be used as a file object
       */
    private File getFile(){
        int i=1;
      while(true){
          String fileName = "Game"+i+".txt";
          String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+fileName;
          File file = new File(filePath);
          if(!file.exists()){
              return file;
          }
          i++;
      }
    }
}
