package com.jstewart.testamazonlogin;

import java.util.Iterator;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.amazon.identity.auth.device.AuthError;
import com.amazon.identity.auth.device.authorization.api.AmazonAuthorizationManager;
import com.amazon.identity.auth.device.authorization.api.AuthorizationListener;
import com.amazon.identity.auth.device.authorization.api.AuthzConstants;
import com.amazon.identity.auth.device.shared.APIListener;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.AWSSessionCredentials;
import com.amazonaws.auth.WebIdentityFederationSessionCredentialsProvider;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceAsyncClient;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleWithWebIdentityRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleWithWebIdentityResult;
import com.amazonaws.services.securitytoken.model.Credentials;

public class LoginFragment extends Fragment {

	private ImageButton loginButton;
	private AmazonAuthorizationManager mAuthManager;
	private String[] AUTH_SCOPES = new String[] { "profile" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAuthManager = new AmazonAuthorizationManager(getActivity(),
				Bundle.EMPTY);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_login, null);

		loginButton = (ImageButton) view
				.findViewById(R.id.login_with_amazon_button);
		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				mAuthManager.authorize(AUTH_SCOPES, Bundle.EMPTY,
						new AuthListener());

			}
		});

		return view;
	}

	private class AuthListener implements AuthorizationListener {

		@Override
		public void onError(AuthError error) {
			Log.e("Auth Listener - onError", "Error getting profile data: "
					+ error.getMessage());
			Toast.makeText(getActivity(), "Error getting profile data",
					Toast.LENGTH_SHORT).show();

		}

		@Override
		public void onSuccess(Bundle response) {
			mAuthManager.getProfile(new ProfileListener());

		}

		@Override
		public void onCancel(Bundle response) {
			Toast.makeText(getActivity(), "Authorization canceled",
					Toast.LENGTH_SHORT).show();
		}

	}

	private class ProfileListener implements APIListener {

		@Override
		public void onError(AuthError response) {
			Log.e("Profile Listener - onError", "Error getting profile data: "
					+ response.getMessage());
			Toast.makeText(getActivity(), "Error getting profile data",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onSuccess(Bundle response) {
			Bundle profileData = response
					.getBundle(AuthzConstants.BUNDLE_KEY.PROFILE.val);
			String id = profileData
					.getString(AuthzConstants.PROFILE_KEY.USER_ID.val);
			String name = profileData
					.getString(AuthzConstants.PROFILE_KEY.NAME.val);
			String email = profileData
					.getString(AuthzConstants.PROFILE_KEY.EMAIL.val);

			Log.e("Profile Listener - onSuccess", "We have bundle data. Id: "

			+ id + " Name: " + name + " Email: " + email);

			mAuthManager.getToken(AUTH_SCOPES, new AuthTokenListener());

		}
	}

	private class AuthTokenListener implements APIListener {

		@Override
		public void onError(AuthError response) {
			Log.e("AuthToken Listener - onError",
					"Error getting profile data: " + response.getMessage());

		}

		@Override
		public void onSuccess(Bundle response) {

			String token = "";

			for (Iterator<String> itr = response.keySet().iterator(); itr
					.hasNext();) {

				String key = itr.next();
				Log.i("AuthToken Listener - onSuccess",
						"Successfully received auth token response with key: "
								+ key);
				Log.i("AuthToken Listener - onSuccess",
						"The value of that key is: " + response.getString(key));
			}

			token = response
					.getString("com.amazon.identity.auth.device.authorization.token");

			AssumeRoleWithWebIdentityRequest request = new AssumeRoleWithWebIdentityRequest();
			request.setProviderId("www.amazon.com");
			request.setRoleArn("arn:aws:iam::541242782423:role/TestWebIdentityRole");
			request.setRoleSessionName("AmazonTestLoginSession");
			request.setWebIdentityToken(token);
			request.setDurationSeconds(3600);

			WebIdentityFederationSessionCredentialsProvider wif = new WebIdentityFederationSessionCredentialsProvider(
					token, "www.amazon.com",
					"arn:aws:iam::541242782423:role/TestWebIdentityRole");

			String subjectFromWIF = wif.getSubjectFromWIF();
			
			Log.d("AuthTokenListener - onSuccess",
					"Got something from the wif: " + subjectFromWIF);

			// AWSSecurityTokenServiceAsyncClient client = new
			// AWSSecurityTokenServiceAsyncClient();

			// AWSSecurityTokenServiceClient client = new
			// AWSSecurityTokenServiceClient();
			// AssumeRoleWithWebIdentityResult result = client
			// .assumeRoleWithWebIdentity(request);

			 AWSCredentials credentials = wif.getCredentials();
			 

			
			 Log.d("Auth token listener",
			 "Received temporary security credentials. The Access Key is: "
			 + credentials.getAWSAccessKeyId()
			 + " the secret key is: "
			 + credentials.getAWSSecretKey()
			 + " the session token is: "
			 );
			
		}
	}
}
