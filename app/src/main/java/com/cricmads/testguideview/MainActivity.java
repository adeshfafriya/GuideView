package com.cricmads.testguideview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cricmads.guideviewlibrary.GuideModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ArrayList<GuideModel> guideModelArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView v6, v7, v8, v9, v10;
        v6 = findViewById(R.id.textView6);
        v7 = findViewById(R.id.textView7);
        v8 = findViewById(R.id.textView8);
        v9 = findViewById(R.id.textView9);
        v10 = findViewById(R.id.textView10);
        NestedScrollView scrollView = findViewById(R.id.scrollView);


        guideModelArrayList.add(new GuideModel(v9,
                "This is view 9",
                "This is an example of content \n for the selected view",
                null));
        guideModelArrayList.add(new GuideModel(v7,
                "This is view 7",
                "This is an example of content \n for the selected view",
                null));
        guideModelArrayList.add(new GuideModel(v6,
                "This is view 6",
                "This is an example of content \n for the selected view",
                "Ok"));

        guideModelArrayList.add(new GuideModel(v10,
                "This is view 10",
                "This is an example of content \n for the selected view",
                "Last"));
        guideModelArrayList.add(new GuideModel(v8,
                "This is view 8",
                "This is an example of content \n for the selected view",
                "Ok"));

        GuideModel.initiateScrollGuideView(scrollView, guideModelArrayList);

        v8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Clicked 8?", Toast.LENGTH_SHORT).show();
            }
        });
        v9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Clicked 9?", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
