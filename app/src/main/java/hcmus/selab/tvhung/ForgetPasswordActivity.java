package hcmus.selab.tvhung;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText input_email;
    private TextInputLayout layout_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        input_email = findViewById(R.id.input_email);
        layout_email = findViewById(R.id.layout_email);

        findViewById(R.id.btn_resetPassword).setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);

    }

    private boolean validateForm() {
        if (TextUtils.isEmpty(input_email.getText().toString())) {
            layout_email.setError("Required.");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btn_resetPassword) {
            sendPasswordResetEmail();
        }
        if (id == R.id.btn_back){
            finish();
        }
    }

    private void sendPasswordResetEmail() {

        if (!validateForm())
            return;

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        String emailAddress = input_email.getText().toString();

        mAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getBaseContext(), "Password reset email sent", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getBaseContext(), "Failed to send reset email", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}