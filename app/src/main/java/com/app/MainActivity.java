package com.app;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private MySurfaceView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surfaceView = findViewById(R.id.idSurfaceView);
        surfaceView.startDrawing();
    }

    public void clickRed(View view) {
        surfaceView.changeColor(Color.RED);
    }

    public void clickYellow(View view) {
        surfaceView.changeColor(Color.YELLOW);
    }

    public void clickBlue(View view) {
        surfaceView.changeColor(Color.BLUE);
    }

    public void clickGreen(View view) {
        surfaceView.changeColor(Color.GREEN);
    }

    public void clickClear(View view) {
        surfaceView.clearCanvas();
    }
}