package com.example.prasanth.socialloginapplication.sociallogins;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.example.prasanth.socialloginapplication.sociallogins.interfaces.FbPageNavigateInterface;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class FacebookLogin {

    private final Context context;
    private JSONObject jsonObjectResponse;
    /*The CallbackManager manages the callbacks into the FacebookSdk from an Activity's or Fragment's onActivityResult() method.*/
    public static CallbackManager fbCallbackManager;
    private String fbUserId;
    private final FbPageNavigateInterface fbPageNavigateInterface;
    private Bundle responseUserData;

    public FacebookLogin(Context context, FbPageNavigateInterface fbPageNavigateInterface) {
        this.context = context;
        this.fbPageNavigateInterface = fbPageNavigateInterface;
    }


    public void initFaceBook() {
        fbCallbackManager = CallbackManager.Factory.create();
        /*registerCallback() Registers a login callback to the given callback manager*/
        LoginManager.getInstance().registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                fbUserId = loginResult.getAccessToken().getUserId();
                /*newMeRequest() Creates a new Request configured to retrieve a user's own profile.*/
                final GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("LoginActivity.", response.toString());
                        Log.e("position of email", "hello" + response.toString().indexOf("graphObject:"));
                        Log.v("response", String.valueOf(response));
                        String a = response.toString().substring(response.toString().lastIndexOf("graphObject:") + 12, response.toString().indexOf("}") + 1);
                        try {
                            jsonObjectResponse = new JSONObject(a);
                            try {
                                /*storing fb user data in bundle to send to other activity*/
                                responseUserData = new Bundle();
                                responseUserData.putString("name", jsonObjectResponse.getString("name"));
                                responseUserData.putString("email", jsonObjectResponse.getString("email"));
                                responseUserData.putString("gender", jsonObjectResponse.getString("gender"));
                                responseUserData.putString("pictureLink", jsonObjectResponse.getString("link"));
                                responseUserData.putString("fbUserId", fbUserId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            /*using interfaces to call intent*/
                            fbPageNavigateInterface.navigateToFbProfilePage(jsonObjectResponse.getString("name"), jsonObjectResponse.getString("email"), jsonObjectResponse.getString("link"), Long.parseLong(loginResult.getAccessToken().getUserId()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                /*requesting for the required user fields*/
                parameters.putString("fields", "id,name,email,gender,birthday,link");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.d("On error", "Uh oh... Looks like something went wrong.");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("On error", "Uh oh... Looks like something went wrong." + error.toString());
            }
        });
    }

    public void signUpWithFacebook() {
        /*Logs the user in with the requested read permissions.*/
        LoginManager.getInstance().logInWithReadPermissions((Activity) context, Arrays.asList("user_photos", "email", "user_birthday", "public_profile", "user_posts"));
    }
}
