package com.xc.xcloadingimageview;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        XCLoadingImageView imageView2 = (XCLoadingImageView) findViewById(R.id.img2);
        imageView2.setMaskOrientation(XCLoadingImageView.MaskOrientation.LeftToRight);
//        imageView2.setProgress(50);
    }
}
