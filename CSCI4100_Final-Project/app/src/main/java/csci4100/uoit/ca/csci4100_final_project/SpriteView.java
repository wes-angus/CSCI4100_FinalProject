package csci4100.uoit.ca.csci4100_final_project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class SpriteView extends View {

    private Handler handler;
    final int REFRESH_DELAY = 300;

    private Runnable animationThread = new Runnable() {
        @Override
        public void run() {
            // redraw
            invalidate();
        }
    };

    protected class Sprite {
        private Bitmap spriteSheet;
        int SPRITE_WIDTH  = 32;
        int SPRITE_HEIGHT = 32;

        private int row, col;
        private int mrow, mcol;



        void setSpriteSheet(Bitmap spriteSheet) {
            this.spriteSheet = spriteSheet;
        }

        void nextFrame() {
            // advance to the next column
            col++;

            // wrap to next row, if necessary
            if (col >= mcol) {
                row++;
                col = 0;
            }

            // wrap up to top row, if necessary
            if (row >= mrow) {
                row = 0;
            }
        }

        void initializeAnimation(int resourceID, int sprite_columns, int sprite_rows, int sprite_width, int sprite_height) {
            row = 0;
            col = 0;

            // load the image into a bitmap
            Bitmap spriteSheet = BitmapFactory.decodeResource(getResources(), resourceID);
            SPRITE_WIDTH = sprite_width;
            SPRITE_HEIGHT = sprite_height;

            mrow = sprite_rows;
            mcol = sprite_columns;

            // unscale the bitmap (Android scales bitmaps, based on screen density
            spriteSheet = Bitmap.createScaledBitmap(spriteSheet, SPRITE_WIDTH * sprite_columns, SPRITE_HEIGHT * sprite_rows, false);

            setSpriteSheet(spriteSheet);
        }

        void draw(Canvas canvas) {
            // create a brush
            Paint blackFill = new Paint();
            blackFill.setARGB(255, 0, 0, 0);
            blackFill.setStyle(Paint.Style.FILL);

            // determine the source and destination boundaries
            int left = col * SPRITE_WIDTH;
            int top = row * SPRITE_HEIGHT;
            Rect source = new Rect(left, top, left + SPRITE_WIDTH, top + SPRITE_HEIGHT);
            RectF dest = new RectF(0, 0, SPRITE_WIDTH*2, SPRITE_HEIGHT*2);

            // draw the current frame
            canvas.drawBitmap(spriteSheet, source, dest, null);
        }
    }

    Sprite sprite;

    public SpriteView(Context context) {
        super(context);
        init();
    }

    public SpriteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        sprite = new Sprite();
        sprite.initializeAnimation(R.drawable.android_sheet_s,10,3,32,23);
        handler = new Handler();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        sprite.draw(canvas);
        handler.postDelayed(animationThread, REFRESH_DELAY);
        sprite.nextFrame();
    }
}
