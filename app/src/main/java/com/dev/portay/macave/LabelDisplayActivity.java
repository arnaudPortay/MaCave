package com.dev.portay.macave;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

public class LabelDisplayActivity extends AppCompatActivity
{
    public static final String  ARG_LABEL = "label";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_display);

        String lLabel = getIntent().getStringExtra(ARG_LABEL);

        if (lLabel.compareTo("") != 0)
        {
            File lFile = new File(lLabel);
            if (lFile.exists() && findViewById(R.id.LabelView) != null)
            {
                Picasso.get().load(lFile).placeholder(android.R.drawable.ic_menu_gallery).into((ImageView)findViewById(R.id.LabelView));

                findViewById(R.id.LabelView).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        setResult(RESULT_OK, new Intent());
                        finish();
                    }
                });
            }
        }
    }
}
