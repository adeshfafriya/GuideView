package com.cricmads.guideviewlibrary;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.widget.NestedScrollView;

import java.util.ArrayList;

public class GuideModel {
    private View view;
    private String title, content, buttonText;
    private static Boolean scrollEnabled = true;

    public GuideModel(View view, String title, String content, String buttonText) {
        this.view = view;
        this.title = title;
        this.content = content;
        this.buttonText = buttonText;
    }

    private View getView() {
        return view;
    }

    private String getTitle() {
        return title;
    }

    private String getContent() {
        return content;
    }

    private String getButtonText() {
        return buttonText;
    }

    public static void initiateGuideView(final ArrayList<GuideModel> guideModels, final int position){

        scrollEnabled = false;

        boolean isClickable;
        isClickable = position == guideModels.size()-1;
        GuideModel guideModel = guideModels.get(position);
        GuideView.Builder builder = new GuideView.Builder()
                .setTitle(guideModel.getTitle())
                .setButtonText(guideModel.getButtonText())
                .setContentText(guideModel.getContent())
                .setTargetView(guideModel.getView())
                .setIsClickable(isClickable)
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                        if (position < guideModels.size()-1)
                            initiateGuideView(guideModels, position+1);
                        scrollEnabled = true;
                    }
                });
        GuideView guideView = builder.build();
        guideView.show();

    }
    public static void initiateGuideView(final ArrayList<GuideModel> guideModels) {
        initiateGuideView(guideModels, 0);
    }
    public static void initiateScrollGuideView(NestedScrollView scrollFrame, final ArrayList<GuideModel> guideModels, final int position){

        scrollFrame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return !scrollEnabled;
            }
        });
        initiateGuideView(guideModels, position);
    }
    public static void initiateScrollGuideView(NestedScrollView scrollFrame, final ArrayList<GuideModel> guideModels){
        initiateScrollGuideView(scrollFrame, guideModels, 0);
    }
}
