package com.example.assessment2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.assessment2.databinding.ActivityUserChoiceBinding;
import com.example.assessment2.ui.login.LoginActivity;

import java.util.Objects;

public class User_Choice extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityUserChoiceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityUserChoiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Button volunteer = findViewById(R.id.volunteer);
        Button create_site = findViewById(R.id.create_site);

        Button my_site = findViewById(R.id.my_sites);

        Button logout = findViewById(R.id.logout);
        Button siteVolun = findViewById(R.id.volunteer_site);

        TextView welcomeText = findViewById(R.id.welcomeText);
        Intent intent_welcome = getIntent(); //Get the input from user in "Goal Setting" activity


        String message = (String) Objects.requireNonNull(intent_welcome.getExtras()).get("message");
        //   eventsList = (ArrayList<Event>) intent.getExtras().get("event");
        welcomeText.setText(message);

        volunteer.setOnClickListener(v -> {
            Intent intent = new Intent(this, Main_For_User.class);

            startActivity(intent);
        });

        create_site.setOnClickListener(v -> {
            Intent intent = new Intent(this, siteCreate.class);

            startActivity(intent);
        });

        my_site.setOnClickListener(v -> {
            Intent intent = new Intent(this, My_Site.class);
            startActivity(intent);

        });

        siteVolun.setOnClickListener(v -> {
            Intent intent = new Intent(this, myVolunteerList.class);
            startActivity(intent);

        });

        logout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Log out success",
                    Toast.LENGTH_SHORT).show();

        });
    }


}