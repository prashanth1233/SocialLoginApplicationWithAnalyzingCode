package com.example.prasanth.socialloginapplication.sociallogins;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.prasanth.socialloginapplication.R;
import com.squareup.picasso.Picasso;

public class SocialLoginProfilePage extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_profile_page);
        Intent profileIntent = getIntent();
        String name = profileIntent.getStringExtra("NAME");
        String email = profileIntent.getStringExtra("E_MAIL");
        String imageURl = profileIntent.getStringExtra("IMAGE_URL");
        Long userId = profileIntent.getLongExtra("User_Id", 0);

        TextView fbName = findViewById(R.id.name);
        TextView fbEmail = findViewById(R.id.email);
        ImageView fbProfilePic = findViewById(R.id.profileImage);

        fbName.setText(name);

        if (TextUtils.isEmpty(email))
            fbEmail.setText(getResources().getString(R.string.email_warning_msg));
        else
            fbEmail.setText(email);

        if ((userId) != 0L)
            Picasso.with(this)
                    .load("https://graph.facebook.com/" + userId + "/picture?type=large")
                    .into(fbProfilePic);
        else if (!TextUtils.isEmpty(imageURl))
            Picasso.with(this).load(imageURl).into(fbProfilePic);
    }
}
