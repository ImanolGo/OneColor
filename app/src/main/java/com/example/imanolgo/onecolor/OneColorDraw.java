package com.example.imanolgo.onecolor;

/**
 * Created by imanolgo on 30/04/15.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

public class OneColorDraw extends View {
    Paint paint = new Paint();

    private int oneColor = Color.WHITE;

    public OneColorDraw(Context context) {
        super(context);
    }

    @Override
    public void onDraw(Canvas canvas) {
        int size = 115;
        int margin = 5;
        int position = 30;
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);
        canvas.drawRect(position, position, size+margin, size+margin, paint);
        paint.setStrokeWidth(0);
        paint.setColor(oneColor);
        canvas.drawRect(position+margin, position+margin, size, size, paint );
    }

    public void setColor(int color){
        //Log.i("OneColorDraw", "SetColor-> " + color);
        //Log.i("OneColorDraw", "SetColor-> " + Integer.toHexString(color));
        //Log.i("OneColorDraw", "SetColor-> R: " + Color.red(color) + ", G: " + Color.green(color) + ", B: " + Color.blue(color));
        oneColor=color;

    }

    public int getColor(){return oneColor;}

}
