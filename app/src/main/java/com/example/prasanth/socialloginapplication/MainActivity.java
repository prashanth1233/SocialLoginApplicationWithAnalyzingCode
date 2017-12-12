package com.example.prasanth.socialloginapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.prasanth.socialloginapplication.sociallogins.FacebookLogin;
import com.example.prasanth.socialloginapplication.sociallogins.SocialLoginProfilePage;
import com.example.prasanth.socialloginapplication.sociallogins.GoogleSignIn;
import com.example.prasanth.socialloginapplication.sociallogins.TwitterSignIn;
import com.example.prasanth.socialloginapplication.sociallogins.interfaces.FbPageNavigateInterface;
import com.example.prasanth.socialloginapplication.sociallogins.interfaces.TwitterPageNavigateInterface;
import com.facebook.FacebookSdk;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, FbPageNavigateInterface, TwitterPageNavigateInterface {

    private GoogleApiClient googleApiClient;
    private final int GOOGLE_SIGN_IN_REQUEST_CODE = 11011;
    private GoogleSignInAccount googleSignInAccount;
    private boolean googleSignInStatus = false;
    private boolean twitter_button_click = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        /*
          Uncomment this code for a single time. Then please check your log to find keyhash.
          You have to give this keyhash in facebook developers site to perform login with facebook.
         */
        /*try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.prasanth.socialloginapplication",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("KeyHash:", "Error : "+e.getMessage());


        } catch (NoSuchAlgorithmException e) {
            Log.e("KeyHash:", "Error : "+e.getMessage());

        }*/
    }


    private void initViews() {
        Button google_sign_in_button = findViewById(R.id.google_sign_in_button);
        Button facebook_sign_in_button = findViewById(R.id.facebook_sign_in_button);
        Button twitter_sign_in_button = findViewById(R.id.twitter_sign_in_button);

        google_sign_in_button.setOnClickListener(this);
        facebook_sign_in_button.setOnClickListener(this);
        twitter_sign_in_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.google_sign_in_button:
                GoogleSignIn googleSignIn = new GoogleSignIn();

                /*checking for first time signIn*/
                if (!googleSignInStatus) {
                    if (googleApiClient == null) {
                        googleApiClient = googleSignIn.initGooglePlus(this);
                        signUpWithGoogle();
                    } else
                        signUpWithGoogle();
                } else
                    photoUrlNullCheck();
                break;
            case R.id.facebook_sign_in_button:
                FacebookLogin facebookLogin = new FacebookLogin(this, this);
                facebookLogin.initFaceBook();
                facebookLogin.signUpWithFacebook();
                break;
            case R.id.twitter_sign_in_button:
                twitter_button_click = true;
                TwitterSignIn twitterSignIn = new TwitterSignIn(this, this);
                twitterSignIn.initTwitter();
                break;
            default:
                break;
        }
    }


    private void signUpWithGoogle() {
        /*Starting the intent prompts the user to select a Google account to sign in with*/
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent()
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            GoogleSignInResult signInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(signInResult);
        }
        /*twitter_button_click is used to check whether twitter signIn button is clicked or not*/
        if (twitter_button_click)
            if (new TwitterAuthClient().getRequestCode() == requestCode) {
                new TwitterAuthClient().onActivityResult(requestCode, resultCode, data);
            }
        if (FacebookSdk.isFacebookRequestCode(requestCode)) {
            FacebookLogin.fbCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(GoogleSignInResult signInResult) {
        if (signInResult.isSuccess()) {
            // Signed in successfully
            googleSignInStatus = true;
            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
            googleSignInAccount = signInResult.getSignInAccount();
            photoUrlNullCheck();
        }
    }

    @Override
    public void navigateToFbProfilePage(String name, String email, String imageUrl, long userId) {
        navigateToProfilePage(name, email, imageUrl, userId);
    }

    @Override
    public void navigateToTwitterProfilePage(String name, String email, String imageUrl, long userId) {
        navigateToProfilePage(name, email, imageUrl, userId);
    }

    private void navigateToProfilePage(String name, String email, String imageUrl, long userId) {
        Intent startProfileActivityIntent = new Intent(MainActivity.this, SocialLoginProfilePage.class);
        startProfileActivityIntent.putExtra("NAME", name);
        startProfileActivityIntent.putExtra("E_MAIL", email);
        startProfileActivityIntent.putExtra("IMAGE_URL", imageUrl);
        startProfileActivityIntent.putExtra("User_Id", userId);
        startActivity(startProfileActivityIntent);
    }

    private void photoUrlNullCheck() {
        String displayName = googleSignInAccount.getDisplayName();
        String email = googleSignInAccount.getEmail();
        try {
            String imageUrl = googleSignInAccount.getPhotoUrl() == null ? null : googleSignInAccount.getPhotoUrl().toString();
            if (imageUrl != null) {
                navigateToFbProfilePage(displayName, email,
                        imageUrl, 0L);
            } else {
                String photoUrl = "";
                navigateToFbProfilePage(displayName, email,
                        photoUrl, 0L);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}