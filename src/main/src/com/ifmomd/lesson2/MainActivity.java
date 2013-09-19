package com.ifmomd.lesson2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.ifmomd.main.R;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int[] qualityState = {0};

        final ImageView targetImage = (ImageView)findViewById(R.id.imageContainer);
        final TextView text = (TextView)findViewById(R.id.textView1);

        final Bitmap bitmap = BitmapFactory.decodeResource(
                getApplicationContext().getResources(),
                R.drawable.source);

        final int[][] picture = BitmapToMatrix(bitmap);

        long time = System.currentTimeMillis();
        showPicture(targetImage, picture, qualityState[0]);
        text.setText((System.currentTimeMillis() - time) + " ms.");

        targetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long time = System.currentTimeMillis();
                qualityState[0] = 3 - qualityState[0];
                showPicture(targetImage, picture, qualityState[0]);
                text.setText((System.currentTimeMillis() - time) + " ms.");
           }
        });

    }

    private void showPicture(ImageView image, int[][] picture, int quality){
        image.setImageBitmap(MatrixToBitmap(applyScreen(rotateBy90Degrees(gripe(picture, 1 / 1.73, 1 / 1.73, quality)), 28)));
    }

    private int[][] rotateBy90Degrees(int[][] source){
        int height = source.length;
        if (height == 0) return new int[0][0];
        int width = source[0].length;

        int [][] dist = new int[width][height];
        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                dist[j][height - 1 - i] = source[i][j];
            }
        }
        return dist;
    }

    private int[] linearMatrix(int[][] source){
        int height = source.length;
        if (height == 0) return new int[0];
        int width = source[0].length;

        int[] dist = new int[height * width];
        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                dist[i * width + j] = source[i][j];
            }
        }
        return dist;
    }

    private int[][] BitmapToMatrix(Bitmap source){
        int width = source.getWidth();
        int height = source.getHeight();
        int[][] dist = new int[height][width];
        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                dist[i][j] = source.getPixel(j, i);
            }
        }
        return dist;
    }

    private Bitmap MatrixToBitmap(int[][] source){
        if (source.length == 0) return Bitmap.createBitmap(new int[0], 0, 0, Bitmap.Config.ARGB_8888);
        return Bitmap.createBitmap(linearMatrix(source), source[0].length, source.length, Bitmap.Config.ARGB_8888);
    }

    private int[][] applyScreen(int[][] source, double effect){
        if (source.length == 0) return new int[0][0];
        int[][] dist = new int[source.length][source[0].length];
        if (effect > 100) effect = 100;
        if (effect < 0) effect = 0;

        for (int i = 0; i < source.length; i++){
            for (int j = 0; j < source[i].length; j++){
                int alpha = Color.alpha(source[i][j]);
                int red = Color.red(source[i][j]);
                int green = Color.green(source[i][j]);
                int blue = Color.blue(source[i][j]);
                red = (int) ((0xFF - red) * effect / 100 + red);
                green = (int) ((0xFF - green) * effect / 100 + green);
                blue = (int) ((0xFF - blue) * effect / 100 + blue);

                dist[i][j] = alpha * 0x1000000 + red * 0x10000 + green * 0x100 + blue;
            }
        }
        return dist;
    }

    private int[][] gripe(int[][] source, double xScale, double yScale, int quality){
        if (source.length == 0 || (int)(source.length / yScale) == 0) return new int[0][0];
        int width = source[0].length;
        int height = source.length;
        int[][] dist = new int[(int) (height * yScale)][(int) (width * xScale)];

        if (quality != 0){
            int[] widthBounds = new int[dist[0].length + 1];
            int[] heightBounds = new int[dist.length + 1];
            for (int i = 0; i <= dist.length; i++){
                heightBounds[i] = (int)(i * quality / yScale);
            }
            for (int j = 0; j <= dist[0].length; j++){
                widthBounds[j] = (int)(j * quality / xScale);
            }

            for (int i = 0; i < dist.length; i++){
                for (int j = 0; j < dist[i].length; j++){
                    int alpha = 0;
                    int red = 0;
                    int green = 0;
                    int blue = 0;
                    int num = 0;
                    for (int ii = heightBounds[i]; ii < heightBounds[i + 1]; ii++){
                        for (int jj = widthBounds[j]; jj < widthBounds[j + 1]; jj++){
                            alpha += Color.alpha(source[ii / quality][jj / quality]);
                            red += Color.red(source[ii / quality][jj / quality]);
                            green += Color.green(source[ii / quality][jj / quality]);
                            blue += Color.blue(source[ii / quality][jj / quality]);
                            num++;
                        }
                    }
                    if (num != 0){
                        dist[i][j] = ((alpha / num << 24) | (red / num << 16) | (green / num << 8) | (blue / num));
                    } else {
                        dist[i][j] = source[((int) (i / yScale))][((int) (j / xScale))];
                    }
                }
            }
        } else {
            for (int i = 0; i < dist.length; i++){
                for (int j = 0; j < dist[i].length; j++){
                    dist[i][j] = source[((int) (i / yScale))][((int) (j / xScale))];
                }
            }
        }
        return dist;
   }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


}
