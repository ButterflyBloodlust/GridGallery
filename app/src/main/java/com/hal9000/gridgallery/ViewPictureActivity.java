package com.hal9000.gridgallery;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class ViewPictureActivity extends AppCompatActivity {

    private static final String KEY_PICTURES = "pics";
    private static final String KEY_REQUESTED_POS = "requestedPos";
    private ArrayList<Integer> imageIDs;
    private int currentPosition = -1;
    private ImageView galleryPicture;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_picture);

        initToolbar();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                getSupportActionBar().hide();
            }
        }

        galleryPicture = (ImageView)findViewById(R.id.galleryImage);

        imageIDs = (ArrayList<Integer>) getIntent().getSerializableExtra(KEY_PICTURES);
        currentPosition = getIntent().getIntExtra(KEY_REQUESTED_POS, 0);

        changePicture(currentPosition);

        final ImageButton previousItemButton = findViewById(R.id.previous_item_button);
        previousItemButton.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View v1)
            {
                if (imageIDs != null && imageIDs.size() > 0 && currentPosition > 0) {
                    changePicture(--currentPosition);
                }
            }
        });

        final ImageButton nextItemButton = findViewById(R.id.next_item_button);
        nextItemButton.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View v1)
            {
                if (imageIDs != null && imageIDs.size() > 0 && imageIDs.size() > currentPosition + 1) {
                    changePicture(++currentPosition);
                }
                else{
                    Toast.makeText(getApplicationContext(),"No more pictures", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initToolbar() {
        Toolbar displayActToolbar = findViewById(R.id.simple_toolbar);
        setSupportActionBar(displayActToolbar);
    }

    private void changePicture(int arrayPosition){
        //galleryPicture.setLayoutParams(new LinearLayout.LayoutParams(500,500));
        //galleryPicture.setScaleType(ImageView.ScaleType.CENTER_CROP);
        galleryPicture.setImageResource(imageIDs.get(arrayPosition));
    }

    // starter - to control passing and reading Extra in the same class
    public static void start(Context context, ArrayList<Integer> imageIDs, int requestedPos) {
        Intent starter = new Intent(context, ViewPictureActivity.class);
        starter.putExtra(KEY_PICTURES, imageIDs);
        starter.putExtra(KEY_REQUESTED_POS, requestedPos);
        context.startActivity(starter);
        //Log.d("ViewPictureActivity", "after start");
    }
}
