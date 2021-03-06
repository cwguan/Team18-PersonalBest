package com.android.personalbest.UIdisplay;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.android.personalbest.MainActivity;
import com.android.personalbest.R;
import com.android.personalbest.SharedPrefData;
import com.android.personalbest.firestore.FirestoreAdaptor;
import com.android.personalbest.firestore.FirestoreFactory;
import com.android.personalbest.firestore.IFirestore;
import com.android.personalbest.signin.GoogleSignAndOut;
import com.android.personalbest.signin.ISignIn;
import com.android.personalbest.signin.SignInFactory;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;

/**
 * A login screen that offers login via email/password.
 */
public class LoginUI extends AppCompatActivity {
    ISignIn gSignInAndOut;

    private static int RC_SIGN_IN_KEY = 100;
    String TAG = LoginUI.class.getName();
    private static int RC_SIGN_IN = RC_SIGN_IN_KEY;

    IFirestore firestore;
    String email = "login@login.com";
    public static final String FIRESTORE_SERVICE_KEY = "FIRESTORE_SERVICE_KEY";
    public static final String FIRESTORE_ADAPTOR_KEY = "FIRESTORE_ADAPTOR";


    /**
     * Begins at the start of the Login Activity to see if user has an account already.
     */
    @Override
    protected void onStart()
    {
        super.onStart();
        gSignInAndOut = SignInFactory.create(MainActivity.signin_indicator,this, TAG);

        FirebaseApp.initializeApp(this);

        // Determine what implementation of IFirestore to use
        String firestoreKey = getIntent().getStringExtra(FIRESTORE_SERVICE_KEY);
        if (firestoreKey == null) {
            FirestoreFactory.put(FIRESTORE_ADAPTOR_KEY, new FirestoreFactory.BluePrint() {
                @Override
                public IFirestore create(Activity activity, String userEmail) {
                    return new FirestoreAdaptor(activity, userEmail);
                }
            });
            // Default Firestore implementation using our adaptor
            firestore = new FirestoreAdaptor(this, email);
        } else {
            firestore = FirestoreFactory.create(firestoreKey, this, email);
        }


        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null and user can sign in.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(gSignInAndOut.updateUI(account))
        {   SharedPrefData.setAccountId(this, GoogleSignIn.getLastSignedInAccount(this).getId());
            launchHomeScreenActivity();
        }
    }

    /**
     * Checks the result of calling signIn() method.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN)
        {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if ( gSignInAndOut.handleSignInResult(task) )
            {
                // If the user already has an account, goes to MainActivity; otherwise, goes to GetToKnowYou
                firestore.loginCheckIfUserExists(GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getEmail(), this);
            }
        }
    }


    /**
     * Creates buttons for create account and google sign in.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        gSignInAndOut = new GoogleSignAndOut(this, TAG);

        //Create account button
        Button createAccountButton = findViewById(R.id.create_account_button);
        createAccountButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //Switches to the get personal info activity
                launchGetToKnowYouActivity();
            }
        });

        // Create and set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                gSignInAndOut.signIn();
            }
        });

    }

    public void launchGetToKnowYouActivity() {
        Intent intent = new Intent(this, GetToKnowYouUI.class);
        startActivity(intent);

    }

    public void launchHomeScreenActivity()
    {
        Intent intent = new Intent (this, MainActivity.class);
        startActivity(intent);
    }
}