package com.example.prasanth.socialloginapplication.sociallogins;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.prasanth.socialloginapplication.MainActivity;
import com.example.prasanth.socialloginapplication.R;
import com.example.prasanth.socialloginapplication.sociallogins.interfaces.TwitterPageNavigateInterface;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;


public class TwitterSignIn {
    private TwitterAuthClient twitterAuthClient;
    private final Context context;
    private final TwitterPageNavigateInterface twitterPageNavigateInterface;

    public TwitterSignIn(Context context, TwitterPageNavigateInterface twitterPageNavigateInterface) {
        this.context = context;
        this.twitterPageNavigateInterface = twitterPageNavigateInterface;
         /*Authorization configuration details*/
        TwitterAuthConfig authConfig = new
                TwitterAuthConfig(context.getString(R.string.twitter_consumer_key), context.getString(R.string.twitter_secret_key));

        /*Fabric SDK separates functionality into modules called Kits.
        You must indicate which kits you wish to use via Fabric.with().*/

        Fabric.with(context, new com.twitter.sdk.android.Twitter(authConfig));


    }

    public void initTwitter() {

        twitterAuthClient = new TwitterAuthClient();
        //make the call to login
        twitterAuthClient.authorize((MainActivity) context, new Callback<TwitterSession>() {
            String profileImage;

            @Override
            public void success(Result<TwitterSession> result) {
                final String userName = result.data.getUserName();
                final TwitterSession session = result.data;
                Call<User> userResult = Twitter.getApiClient(session).getAccountService().verifyCredentials(true, false);
                userResult.enqueue(new Callback<User>() {

                    @Override
                    public void success(Result<User> result) {
                        User user = result.data;
                        profileImage = user.profileImageUrl;

                        if (TextUtils.isEmpty(user.email)) {
                            twitterAuthClient.requestEmail(session, new Callback<String>() {
                                @Override
                                public void success(Result<String> result) {
                                    String email = result.data;
                                    twitterPageNavigateInterface.navigateToTwitterProfilePage(userName, email, profileImage, 0L);
                                }

                                @Override
                                public void failure(TwitterException exception) {
                                    twitterPageNavigateInterface.navigateToTwitterProfilePage(userName, null, profileImage, 0L);
                                }
                            });
                        } else {
                            twitterPageNavigateInterface.navigateToTwitterProfilePage(userName, user.email, user.profileImageUrl, 0L);
                        }
                    }

                    @Override
                    public void failure(TwitterException exception) {

                    }
                });
            }

            @Override
            public void failure(com.twitter.sdk.android.core.TwitterException exception) {
                Toast.makeText(context, "Login failed", Toast.LENGTH_LONG).show();
            }
        });
    }
}
