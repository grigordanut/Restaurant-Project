package com.example.danut.restaurant;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddRestNew extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rest_new);

        Button buttonAddRestNew = findViewById(R.id.btnAddrestNew);
        buttonAddRestNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context contextNew = AddRestNew.this;

                AlertDialog.Builder alert = new AlertDialog.Builder(contextNew);
                alert.setTitle("Record New Restaurant ");
                alert.setMessage("Please Restaurant Name:");
                // Set an EditText view to get user input
                final EditText trackName = new EditText(contextNew);
                alert.setView(trackName);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String textString = trackName.getText().toString(); // Converts the value of getText to a string.
                        if (textString.trim().length() == 0) {

                            Context context = getApplicationContext();
                            CharSequence error = "Please enter a restaurant name" + textString;
                            int duration = Toast.LENGTH_LONG;
                            trackName.setError("Please enter restaurant name");

                            Toast toast = Toast.makeText(context, error, duration);
                            toast.show();
                        }
                    }
                });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
            }
        });
    }
}