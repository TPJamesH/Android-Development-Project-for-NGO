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

        Button see_all_sites = findViewById(R.id.see_all_sites);


        Button logout = findViewById(R.id.logout);


        TextView welcomeText = findViewById(R.id.welcomeText);
        Intent intent_welcome = getIntent(); //Get the input from user in "Goal Setting" activity


        String message = (String) Objects.requireNonNull(intent_welcome.getExtras()).get("message");
        //   eventsList = (ArrayList<Event>) intent.getExtras().get("event");
        welcomeText.setText(message);

        see_all_sites.setOnClickListener(v -> {
            Intent intent = new Intent(this, Main_For_User.class);

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