package rsklabs.com.activity;

/**
 * Created by Senthilkumar on 10/23/2016.
 */
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.widget.Toast;
import rsklabs.com.rocketfighter.GameView;

public class GameActivity extends AppCompatActivity {

    private GameView gameView;
    private boolean gamePaused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Getting display object
        Display display = getWindowManager().getDefaultDisplay();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //Getting the screen resolution into point object
        Point size = new Point();
        display.getSize(size);

        //Initializing game view object
        //this time we are also passing the screen size to the GameView constructor
        gameView = new GameView(this, size.x, size.y);
        //adding it to contentview
        setContentView(gameView);
        Toast.makeText(this, "Dash and Blast the Asteroids.", Toast.LENGTH_SHORT).show();

    }

    //pausing the game when activity is paused
    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    //running the game when activity is resumed
    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            gamePaused = true;
            gameView.pause();

            // dialog box
            new AlertDialog.Builder(this).setTitle("GAME PAUSED").setMessage("The game is paused.").setPositiveButton("Resume", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    gameView.resume();
                }

            }).setNegativeButton("Main Menu", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                    Intent mi = new Intent(getApplicationContext(), MainActivity.class);
                    mi.putExtra("showAds", "true");
                    startActivity(mi);



                }
            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                    gameView.resume();
                }
            }).create().show();

            return false;

        }
        return super.onKeyDown(keyCode, event);
    }


}
