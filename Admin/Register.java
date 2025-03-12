package com.example.assessment2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assessment2.ui.login.LoginActivity;

public class Register extends AppCompatActivity {
    private final DBase refDatabase = new DBase();
    private boolean check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

    }

    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_register);
        TextView input_name = findViewById(R.id.username);
        TextView input_phone = findViewById(R.id.phoneNumber);
        TextView input_pass =findViewById(R.id.password);
        TextView password_confirm = findViewById(R.id.password_confirm);
        TextView lg_switch = findViewById(R.id.login_switch);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button register_button = findViewById(R.id.register);
        ////////////////////////////

        /////////////////////////////////////////////////
        register_button.setOnClickListener( v-> {
            String name = input_name.getText().toString();
            String phone = input_phone.getText().toString();
            String pass = input_pass.getText().toString();
            String pass_con = password_confirm.getText().toString();
            if(TextUtils.isEmpty(name) ||
                    TextUtils.isEmpty(phone) ||
                    TextUtils.isEmpty(pass)  ||
                    TextUtils.isEmpty(pass_con)){
                Toast.makeText(this, "Error: One of the fields is empty", Toast.LENGTH_SHORT).show();
            }
            ///////////////////////////////////
           else if(!(pass.equals(pass_con))) {
                Toast.makeText(this, "Error: Password does not match", Toast.LENGTH_SHORT).show();
            } else{
               refDatabase.writeNewUser(name,pass,phone);

                   Toast.makeText(this, "Success! Redirecting you to login page now", Toast.LENGTH_SHORT).show();
                   Intent intent = new Intent(this, LoginActivity.class);
                   startActivity(intent);


            }
        });
        lg_switch.setOnClickListener(v->{
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

    }
}