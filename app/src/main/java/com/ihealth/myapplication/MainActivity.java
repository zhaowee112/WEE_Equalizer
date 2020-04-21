package com.ihealth.myapplication;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.ihealth.myapplication.view.EqualizerView;

public class MainActivity extends AppCompatActivity {

    private EqualizerView qqMusicEqualizerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        qqMusicEqualizerView  = findViewById(R.id.qq);
        int[] array=new int[]{0,0,6,-6};
        qqMusicEqualizerView.setDecibelArray(array);
        qqMusicEqualizerView.setUpdateDecibelListener(new EqualizerView.updateDecibelListener() {
            @Override
            public void updateDecibel(int[] decibels) {
                Log.d("zw","-------" + decibels[3]);
            }

            @Override
            public void getDecibelWhenMoveUp(int[] decibels) {
                Log.d("zw","抬起时 = " + decibels[3]);
            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
