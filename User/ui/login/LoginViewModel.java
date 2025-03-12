package com.example.assessment2.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.assessment2.DBase;
import com.example.assessment2.Person;
import com.example.assessment2.data.LoginRepository;
import com.example.assessment2.data.Result;
import com.example.assessment2.data.model.LoggedInUser;
import com.example.assessment2.R;

import java.util.List;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private final LoginRepository loginRepository;

    private final DBase refDatabase = new DBase();

    private final List<Person> temp = refDatabase.getPersonList();

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = loginRepository.login(username, password);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    //  username validation check
    public boolean isUserNameValid(String username) {
        boolean result;

        if (username == null) {
            return false;
        } else {
            int i = 0;
            do{
                result = username.equals(temp.get(i).getName());
                if(result) {break;}
                i++;
            } while (i < temp.size());


        }
        return result;
    }

    // A placeholder password validation check
    public boolean isPasswordValid(String password) {
        boolean result;

        if (password == null) {
            return false;
        } else {
            int i = 0;
            do{
                result = password.equals(temp.get(i).getPassword());
                if(result) {break;}
                i++;
            } while (i < temp.size());
        }
        return result;
    }
}