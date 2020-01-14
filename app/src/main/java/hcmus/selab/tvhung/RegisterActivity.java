package hcmus.selab.tvhung;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText input_name, input_email, input_password, input_confirm_password;
    private TextInputLayout layout_name, layout_email, layout_password, layout_confirm_password;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        input_name = findViewById(R.id.input_name);
        input_email = findViewById(R.id.input_email);
        input_password = findViewById(R.id.input_password);
        input_confirm_password = findViewById(R.id.input_confirm_password);

        layout_name = findViewById(R.id.layout_name);
        layout_email = findViewById(R.id.layout_email);
        layout_password = findViewById(R.id.layout_password);
        layout_confirm_password = findViewById(R.id.layout_confirm_password);


        // Btn listener add
        findViewById(R.id.link_login).setOnClickListener(this);
        findViewById(R.id.btn_signup).setOnClickListener(this);


        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btn_signup)
        {
            createAccount(input_email.getText().toString(), input_password.getText().toString());
        }
        if(id == R.id.link_login)
        {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        }

    }

    private boolean validateForm() {
        if (TextUtils.isEmpty(input_name.getText().toString())) {
            layout_name.setError("Required.");
            return false;
        }
        else if (TextUtils.isEmpty(input_email.getText().toString())) {
            layout_email.setError("Required.");
            return false;
        } else if (TextUtils.isEmpty(input_password.getText().toString())) {
            layout_password.setError("Required.");
            return false;
        } else if (TextUtils.isEmpty(input_confirm_password.getText().toString())) {
            layout_confirm_password.setError("Required.");
            return false;
        } else if (!input_password.getText().toString().equals(input_confirm_password.getText().toString())){
            layout_confirm_password.setError("Password does not match.");
            return false;
        }
        else {
            layout_email.setError(null);
            layout_password.setError(null);
            return true;
        }
    }

    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }
        if (password.length() < 8)
        {
            Toast.makeText(RegisterActivity.this, "Password needs to be at lease 8 characters", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(RegisterActivity.this, "Successfully registered", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(getBaseContext(), MainActivity.class));
                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "Fail to sign up", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
