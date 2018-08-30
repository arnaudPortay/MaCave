package com.dev.portay.macave;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dev.portay.macave.db.entity.CellarItem;
import com.dev.portay.macave.db.entity.Wine;

import java.util.Date;

public class AddWineActivity extends AppCompatActivity
{

    public static final String NAME_REPLY = "com.dev.portay.macave.NAME_REPLY";
    public static final String YEAR_REPLY = "com.dev.portay.macave.YEAR_REPLY";

    public static final String WINE_REPLY = "com.dev.portay.macave.WINE_REPLY";
    public static final String CELLAR_ITEM_REPLY = "com.dev.portay.macave.CELLAR_ITEM_REPLY";

    private EditText mEditWineNameView;
    private EditText mEditWineYearView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wine);
        mEditWineNameView = findViewById(R.id.editName);
        mEditWineYearView = findViewById(R.id.editYear);

        final Button lButton = findViewById(R.id.buttonSave);
        lButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent lReplyIntent = new Intent();
                if (TextUtils.isEmpty(mEditWineNameView.getText()) || TextUtils.isEmpty(mEditWineYearView.getText()))
                {
                    setResult(RESULT_CANCELED, lReplyIntent);
                }
                else
                {
                    String lName = mEditWineNameView.getText().toString();
                    String lYear = mEditWineYearView.getText().toString();

                    lReplyIntent.putExtra(WINE_REPLY, new Wine(lName,"bordeaux","red","test"));
                    lReplyIntent.putExtra(CELLAR_ITEM_REPLY, new CellarItem(-1, Integer.parseInt(lYear), 12));

                    setResult(RESULT_OK, lReplyIntent);
                }
                finish();
            }
        });
    }
}
