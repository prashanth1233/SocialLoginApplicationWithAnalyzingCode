package com.example.prasanth.socialloginapplication.sociallogins;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.prasanth.socialloginapplication.MainActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import static android.widget.Toast.LENGTH_SHORT;


public class GoogleSignIn implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleSignInOptions googleSignInOptions;
    private GoogleApiClient googleApiClient;
    private Context context;

    public GoogleApiClient initGooglePlus(Context context) {
        this.context = context;
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        /*If you need to request additional scopes to access Google APIs, specify them with requestScopes*/
        /*Ex: .requestId()*/

        createGoogleApiClient();
        return googleApiClient;
    }

    private void createGoogleApiClient() {
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso

        googleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage((MainActivity) context, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(context, "Google SignIn failed", LENGTH_SHORT).show();
    }
}
