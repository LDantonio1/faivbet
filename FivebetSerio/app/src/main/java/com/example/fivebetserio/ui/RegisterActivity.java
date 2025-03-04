package com.example.fivebetserio.ui;

import static com.example.fivebetserio.util.Constants.USER_COLLISION_ERROR;
import static com.example.fivebetserio.util.Constants.WEAK_PASSWORD_ERROR;

import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import android.view.View;
import com.example.fivebetserio.model.Result;
import com.example.fivebetserio.model.User;
import com.example.fivebetserio.ui.home.viewModel.user.UserViewModel;
import com.example.fivebetserio.ui.home.viewModel.user.UserViewModelFactory;
import com.example.fivebetserio.util.ServiceLocator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.example.fivebetserio.repository.user.IUserRepository;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fivebetserio.R;
import org.apache.commons.validator.routines.EmailValidator;
import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private UserViewModel userViewModel;
    private TextInputEditText editTextName, editTextSurname, editTextPassword, editTextEmail;
    private EditText editTextDate;
    ProgressBar progressBar;
    int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        IUserRepository userRepository = ServiceLocator.getInstance().getUserRepository(getApplication());

        userViewModel = new ViewModelProvider(this, new UserViewModelFactory(userRepository)).get(UserViewModel.class);
        userViewModel.setAuthenticationError(false);

        editTextName = findViewById(R.id.register_name);
        editTextSurname = findViewById(R.id.register_surname);
        editTextPassword = findViewById(R.id.register_password);
        editTextEmail = findViewById(R.id.register_email);
        editTextDate = findViewById(R.id.register_date);
        progressBar = findViewById(R.id.progressBar);

        ImageButton backButton = findViewById(R.id.back_button_register);
        Button registerButton = findViewById(R.id.register_button);

        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            //animazione personalizzata
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        // Disabilita la tastiera (già fatto nell'XML)
        editTextDate.setFocusable(false);

        //gestisce la selezione della data di nascita
        editTextDate.setOnClickListener(v -> {
            // Ottieni la data corrente per impostarla come valore iniziale
            final Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);

            // Apri il DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    RegisterActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        year = selectedYear;
                        month = selectedMonth + 1; // I mesi partono da 0, quindi aggiungiamo 1
                        day = selectedDay;
                        // Mostra la data selezionata nell'EditText
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        editTextDate.setText(selectedDate);
                    },
                    year, month, day
            );

            datePickerDialog.show();
        });


        //controlla se la password e la mail sono valide e controlla che l'utente sia maggiorenne
        registerButton.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            String email, password;
            email = String.valueOf(editTextEmail.getText());
            password = String.valueOf(editTextPassword.getText());

            // Controllo email
            if (!isEmailOk(email)) {
                progressBar.setVisibility(View.GONE);
                editTextEmail.setError("Email is not correct");
                return;
            }

            // Controllo password
            if (!isPasswordOk(password)) {
                progressBar.setVisibility(View.GONE);
                editTextPassword.setError("Password needs to have at least 7 characters");
                return;
            }

            // Controllo età
            if (!isAdult(year, month, day)) {
                progressBar.setVisibility(View.GONE);
                editTextDate.setError("You must be an adult");
                return;
            }

            if (!userViewModel.isAuthenticationError()) { // Controlla se non c'è stato un errore di autenticazione
                userViewModel.getUserMutableLiveData(email, password, false).observe(this, result -> { // Attende il risultato della registrazione
                    progressBar.setVisibility(View.GONE); // Nasconde la barra di caricamento
                    if (result.isSuccess()) { // Se la registrazione ha successo
                        User user = ((Result.UserSuccess) result).getData();
                        userViewModel.setAuthenticationError(false);
                        // Passa alla schermata principale
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else { // Se la registrazione fallisce
                        userViewModel.setAuthenticationError(true);
                        Snackbar.make(findViewById(android.R.id.content),
                                getErrorMessage(((Result.Error) result).getMessage()),
                                Snackbar.LENGTH_SHORT).show(); // Mostra un messaggio di errore
                    }
                });
            } else {
                userViewModel.getUser(email, password, false); // Tenta di registrare l'utente di nuovo
            }
        });
    }

    private boolean isEmailOk(String email){
        return EmailValidator.getInstance().isValid(email);  //libreria esterna per controllo per la mail
    }

    private boolean isPasswordOk(String password){
        return password.length() > 7; // controllo per password
    }

    // controllo per utente maggiorenne
    public boolean isAdult(int year, int month, int day) {
        // Ottieni la data attuale
        Calendar today = Calendar.getInstance();
        int currentYear = today.get(Calendar.YEAR);
        int currentMonth = today.get(Calendar.MONTH) + 1;
        int currentDay = today.get(Calendar.DAY_OF_MONTH);
        int age = currentYear - year;

        if (currentMonth < month || (currentMonth == month && currentDay < day)) {
            age--;
        }
        return age >= 18;
    }

    private String getErrorMessage(String message) {
        switch (message) {
            case WEAK_PASSWORD_ERROR:
                return getString(R.string.error_password_login);
            case USER_COLLISION_ERROR:
                return getString(R.string.error_collision_user);
            default:
                return getString(R.string.error_unexpected);
        }
    }

}