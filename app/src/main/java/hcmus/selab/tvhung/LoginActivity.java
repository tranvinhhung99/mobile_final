package hcmus.selab.tvhung;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// BASED: https://github.com/firebase/quickstart-android/blob/master/auth/app/src/main/java/com/google/firebase/quickstart/auth/java/GoogleSignInActivity.java
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.toString();
    private static final int GOOGLE_SIGN_IN_REQUEST = 9001;


    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private GoogleSignInClient mGoogleSignInClient;
    private TextView           mStatusTextView;
    private TextView           mDetailTextView;

    private EditText input_email, input_password;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextInputLayout layout_email, layout_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        input_email = findViewById(R.id.input_email);
        input_password = findViewById(R.id.input_password);
        layout_email = findViewById(R.id.layout_email);
        layout_password = findViewById(R.id.layout_password);



        // Btn listener add
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_login_google).setOnClickListener(this);
        findViewById(R.id.btn_signup).setOnClickListener(this);

        findViewById(R.id.forgot_password).setOnClickListener(this);

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
//                .requestProfile()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

    }


    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
//                            Toast.makeText(getBaseContext(), "Login with Google Success", Toast.LENGTH_LONG).show();

                            if (!user.isEmailVerified())
                            {
                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "Email sent.");
                                                    Toast.makeText(getBaseContext(), "Verification email sent to " + user.getEmail(), Toast.LENGTH_LONG).show();
                                                }
                                                else
                                                {
                                                    Toast.makeText(getBaseContext(), "Failed to send verification email to " + user.getEmail(), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }
                            startActivity(new Intent(getBaseContext(), MainActivity.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getBaseContext(), "Login with Google Fail", Toast.LENGTH_LONG).show();

                        }

                    }
                });
    }
    // [END auth_with_google]


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GOOGLE_SIGN_IN_REQUEST){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    // [START signin]
    private void signInByGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST);
    }
    // [END signin]

//    private void signOut() {
//        mAuth.signOut();
//    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btn_login)
        {
            signIn(input_email.getText().toString(), input_password.getText().toString());
        }
        if(id == R.id.btn_login_google){
            signInByGoogle();
        }
        if(id == R.id.btn_signup)
        {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        }
        if(id == R.id.forgot_password)
        {
            startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
        }
    }

    public void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(LoginActivity.this, SplashActivity.class));
                    }
                });
    }

    private boolean validateForm() {
        if (TextUtils.isEmpty(input_email.getText().toString())) {
            layout_email.setError("Required.");
            return false;
        } else if (TextUtils.isEmpty(input_password.getText().toString())) {
            layout_password.setError("Required.");
            return false;
        } else {
            layout_email.setError(null);
            layout_password.setError(null);
            return true;
        }
    }

    private void signIn(String email, String password) {
        if (!validateForm()){
            return;
        }

        if (password.length() < 8)
        {
            Toast.makeText(LoginActivity.this, "Password needs to be at lease 8 characters", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                Toast.makeText(LoginActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getBaseContext(), MainActivity.class));
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }


}
