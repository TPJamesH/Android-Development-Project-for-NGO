package com.example.assessment2.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.assessment2.Person;
import com.example.assessment2.R;
import com.example.assessment2.Register;
import com.example.assessment2.User_Choice;
import com.example.assessment2.databinding.ActivityLoginBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private String user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        /////////////////////////////////////////////////////////////
        /*
        //get the spinner from the xml.
        Spinner dropdown = findViewById(R.id.spinner);
//create a list of items for the spinner.
        String[] items = new String[]{"User", "Super User"};
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
//set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);
         */
        ///////////////////////////////////////////////////////////
        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
       // final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;
        Button loginButton = findViewById(R.id.login);
        TextView registerButton = findViewById(R.id.signup_switch);
/*
        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

 */
/*
        loginViewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
            }
            if (loginResult.getSuccess() != null) {
                updateUiWithUser(loginResult.getSuccess());
            }
            setResult(Activity.RESULT_OK);

            //Complete and destroy login activity once successful
            finish();
        });

 */

        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.GONE);

            if(loginViewModel.isUserNameValid(usernameEditText.getText().toString()) &&
                    loginViewModel.isPasswordValid(passwordEditText.getText().toString())) {
                Toast.makeText(getApplicationContext(),
                        "Redirecting...",Toast.LENGTH_SHORT).show();
                try {
                    updateUiWithUser(usernameEditText.getText().toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else{
                Toast.makeText(getApplicationContext(), "Incorrect Information, please try again",Toast.LENGTH_SHORT).show();

            }
        });

        registerButton.setOnClickListener(v->{
            Intent intent = new Intent(this, Register.class);
            startActivity(intent);
        });
    }

    private void updateUiWithUser(String username) throws IOException {
        // TODO : initiate successful logged in experience
      user = username;
        Intent intent = new Intent(this, User_Choice.class);
        intent.putExtra("message", "Welcome " + username);
        createFile();
        startActivity(intent);
    }


    private void createFile() throws IOException {
        String filename = "username.txt";

        File file = new File(this.getFilesDir(), filename);

        FileOutputStream fos = new FileOutputStream(file);
        OutputStreamWriter osw = new OutputStreamWriter(fos);

        try{
            osw.write(user);
        } catch (IOException e) {
            Toast.makeText(this, "File doesn't exist, please reload the app", Toast.LENGTH_SHORT).show();
        }

        osw.flush();
        osw.close();
        fos.close();
    }

}