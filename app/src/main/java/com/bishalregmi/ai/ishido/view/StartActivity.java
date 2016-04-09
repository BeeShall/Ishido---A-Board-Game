package com.bishalregmi.ai.ishido.view;

/************************************************************
 * Name:  Bishal Regmi                                      *
 * Project:  Project 3 - Ishido for Two                     *
 * Class:  CMPS331 - Artificial Intelligence                *
 * Date:  03/22/2016                                        *
 ************************************************************/

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.bishalregmi.ai.ishido.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/************************************************************
 * This is the activity class for the splash screen
 * It start a new Intent for BoardActivity
 * It kills itself after that
 *  *
 * Project 2
 * This activity will have a spinner to choose the state
 * When the user chooses the state and hits play the state will be loaded from the Board Class
 * The app will return back to this activity, if restarted.
 ************************************************************/
public class StartActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button loadButton = (Button) findViewById(R.id.loadButton);
        Button newButton = (Button) findViewById(R.id.newButton);

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder tossAlert = new AlertDialog.Builder(StartActivity.this);
                tossAlert.setTitle("Coin toss");
                tossAlert.setMessage("Heads or Tails?");
                tossAlert.setPositiveButton("Heads", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tossTheCoin(0);
                    }
                });
                tossAlert.setNegativeButton("Tails", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tossTheCoin(1);
                    }
                });
                tossAlert.show();
            }
        });

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Dialog box to select the file to load the game from
                AlertDialog.Builder alert = new AlertDialog.Builder(StartActivity.this);
                alert.setTitle("Select a game");

                final File[] files = Environment.getExternalStorageDirectory().listFiles();

                //list to hold the name of files
                List<String> fileNameList = new ArrayList<String>();
                for (File file : files) {
                    String nameOfFile = file.getName();
                    if(nameOfFile.endsWith(".txt")) {
                        fileNameList.add(nameOfFile);
                    }
                }

                String[] items = new String[fileNameList.size()];
                items = fileNameList.toArray(items);


                alert.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView lw = ((AlertDialog)dialog).getListView();
                        String fileName = (String)lw.getAdapter().getItem(which);
                        //Value 1 stands for loaded game
                        startBoardActivity(1, -1, fileName);
                    }
                });
                alert.show();
            }
        });
    }

    /*
   * Description: function to start the Board Activity
   * Parameters: int value1 to determine the type of game, 0 means new game and 1 means loaded game
   *             int value2 as the turn determined from coin toss
   *             String fileName as the filename to be loaded in case of laoded game
   * Returns:  none
    */
    private void startBoardActivity(int value1, int value2, String fileName){
        try {
            Class boardClass = Class.forName("com.bishalregmi.ai.ishido.view.BoardActivity");
            Intent myIntent = new Intent(StartActivity.this, boardClass);
            //arg1 is load or new
            //arg2 is tossValue or fileID respectively
            myIntent.putExtra("arg1",value1);
            if(value1 == 0) {
                myIntent.putExtra("arg2", value2);
            }
            else if(value1 == 1){
                myIntent.putExtra("arg2", fileName);
            }
            startActivity(myIntent);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    /*
   * Description: function to toss the coin, then display the message based on the coin toss results and then laund the boardActivity with the respective arguments
   * Parameters: int choice for the coin toss, 0 as Heads and 1 as Tails
   * Returns:  none
    */
    private void tossTheCoin(int choice){
        Random random = new Random();
        final int toss = Math.abs(random.nextInt()%2);
        //0 means computer and 1 means human
        AlertDialog.Builder alert = new AlertDialog.Builder(StartActivity.this);
        alert.setTitle("Coin toss");
        String message;
        final int turn;
        if(toss == 0){
            message = "Heads!\n";
        }
        else{
            message = "Tails!\n";
        }
        if(toss == choice){
            message+="Congratulations! You play first!";
            turn = 0;
        }
        else{
            message+="Sorry! Computer plays first";
            turn =1;
        }
        alert.setMessage(message +" first");
        alert.setPositiveButton("Play", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startBoardActivity(0,turn, null);
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


}
