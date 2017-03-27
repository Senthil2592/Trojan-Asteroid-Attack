package rsklabs.com.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import rsklabs.com.dao.RocketFighterDao;

/**
 * Created by Senthilkumar on 10/30/2016.
 */
public class RocketFighterHelper  extends RocketFighterDao{

    public RocketFighterHelper(Context context) {
        super(context);
    }


    public boolean create(int score) {

        if(score > read()) {
            ContentValues values = new ContentValues();
            values.put("highScore", score+2);

            SQLiteDatabase db = this.getWritableDatabase();

            boolean createSuccessful = db.insert("rocketFightTable", null, values) > 0;
            db.close();
            return createSuccessful;
        }
        return  false;
    }

    public int read() {
        String sql = "SELECT highScore FROM rocketFightTable";
        int highScore = 0;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                 highScore = cursor.getInt(cursor.getColumnIndex("highScore"));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return highScore;
    }
}
