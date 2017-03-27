package rsklabs.com.rocketfighter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.ArrayList;


import rsklabs.com.activity.GameActivity;
import rsklabs.com.activity.MainActivity;
import rsklabs.com.helper.RocketFighterHelper;
import rsklabs.com.uiComponents.Boom;
import rsklabs.com.uiComponents.Enemy;
import rsklabs.com.uiComponents.Player;
import rsklabs.com.uiComponents.Star;

/**
 * Created by Senthilkumar on 10/23/2016.
 */
public class GameView extends SurfaceView implements Runnable {

    volatile boolean playing;
    private Thread gameThread = null;
    private Player player;
    private Handler handler;
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    private Enemy[] enemies;

    private int enemyCount = 3;

    private ArrayList<Star> stars = new
            ArrayList<Star>();

    //defining a boom object to display blast
    private Boom boom;
    private int enemyDispCount = 0;

    private int score =0;
    private int lives = 0;
    private int destroyed = 0;

    public GameView(Context context, int screenX, int screenY) {
        super(context);
        player = new Player(context, screenX, screenY);
        handler = new Handler(Looper.getMainLooper());

        surfaceHolder = getHolder();
        paint = new Paint();

        int starNums = 100;
        for (int i = 0; i < starNums; i++) {
            Star s = new Star(screenX, screenY);
            stars.add(s);
        }

        enemies = new Enemy[enemyCount];
        for (int i = 0; i < enemyCount; i++) {
            enemies[i] = new Enemy(context, screenX, screenY);
            enemyDispCount = enemyDispCount + enemies[i].getCount();

        }

        //initializing boom object
        boom = new Boom(context);
        int boomCount = boom.getBoomCount();

    }

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }
    }

    private void update() {
        player.update();

        //setting boom outside the screen
        boom.setX(-650);
        boom.setY(-650);

        for (Star s : stars) {
            s.update(player.getSpeed());
        }

        for (int i = 0; i < enemyCount; i++) {
            enemies[i].update(player.getSpeed());

            //if collision occurrs with player
            if (Rect.intersects(player.getDetectCollision(), enemies[i].getDetectCollision())) {

                //displaying boom at that location
                boom.setX(enemies[i].getX());
                boom.setY(enemies[i].getY());
                score= score+10;
                destroyed= destroyed+1;
                enemies[i].setX(-600);

                if(destroyed == 100) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(getContext(), "You Rock!!! Destroy more.", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            } else if (player.getDetectCollision().exactCenterX()>=enemies[i].getDetectCollision().exactCenterX()) {
                lives=lives+1;
                enemies[i].setX(-600);
                if(lives >=10) {
                    stop();
                    boolean createSuccessful = new RocketFighterHelper(getContext()).create(score);
                    handler.post(new Runnable() {
                        public void run() {
                            new AlertDialog.Builder(getContext()).setTitle("GAME OVER").setMessage("Score:" +score).setPositiveButton("Replay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                    Intent mi = new Intent(getContext(), GameActivity.class);
                                    getContext().startActivity(mi);
                                }

                            }).setNegativeButton("Main Menu", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    ((GameActivity)getContext()).finish();
                                    Intent mi = new Intent(getContext(), MainActivity.class);
                                    mi.putExtra("showAds", "true");
                                    mi.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    getContext().startActivity(mi);
                                }
                            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    dialog.dismiss();
                                    Intent mi = new Intent(getContext(), MainActivity.class);
                                    getContext().startActivity(mi);
                                }
                            }).create().show();
                        }
                    });
                }

            }
        }
    }

    private void draw() {
        score = score+2;
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);

            paint.setColor(Color.WHITE);

            for (Star s : stars) {
                paint.setStrokeWidth(s.getStarWidth());
                canvas.drawPoint(s.getX(), s.getY(), paint);
            }

            canvas.drawBitmap(
                    player.getBitmap(),
                    player.getX(),
                    player.getY(),
                    paint);

            for (int i = 0; i < enemyCount; i++) {
                canvas.drawBitmap(
                        enemies[i].getBitmap(),
                        enemies[i].getX(),
                        enemies[i].getY(),
                        paint
                );
            }

            //drawing boom image
            canvas.drawBitmap(
                    boom.getBitmap(),
                    boom.getX(),
                    boom.getY(),
                    paint
            );

            //Drawing the score
            paint.setColor(Color.argb(255,  249, 129, 0));
            paint.setTextSize(30);
            canvas.drawText("Score: " + score + "   Missed: " + lives + "/ 10   Destroyed: "+destroyed, 10,50, paint);

            surfaceHolder.unlockCanvasAndPost(canvas);

        }
    }

    private void control() {
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void stop(){
        playing = false;

    }
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                player.stopBoosting();
                break;
            case MotionEvent.ACTION_DOWN:
                player.setBoosting();
                break;
        }
        return true;
    }

}