package com.jstewart.testamazonlogin;

import java.util.Iterator;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amazon.identity.auth.device.AuthError;
import com.amazon.identity.auth.device.authorization.api.AmazonAuthorizationManager;
import com.amazon.identity.auth.device.authorization.api.AuthorizationListener;
import com.amazon.identity.auth.device.authorization.api.AuthzConstants;
import com.amazon.identity.auth.device.shared.APIListener;
import com.amazonaws.auth.WebIdentityFederationSessionCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;

public class LoginFragment extends Fragment {

	private ImageButton loginButton;
	private Button listBucketsButton;
	private TextView bucketsList;
	private AmazonAuthorizationManager mAuthManager;
	private WebIdentityFederationSessionCredentialsProvider wif;
	private String[] AUTH_SCOPES = new String[] { "profile" };
	public AmazonS3Client s3Client;

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
		bucketsList = (TextView) view
				.findViewById(R.id.listed_buckets_textView);

		loginButton = (ImageButton) view
				.findViewById(R.id.login_with_amazon_button);
		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				mAuthManager.authorize(AUTH_SCOPES, Bundle.EMPTY,
						new AuthListener());

			}
		});

		listBucketsButton = (Button) view
				.findViewById(R.id.list_buckets_button);
		listBucketsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				listAccountBuckets();
			}
		});

		return view;
	}

	private void listAccountBuckets() {
		new ListBucketsTask(this).execute(s3Client);
	}

	/**
	 * Callback for ListBucketsTask.
	 * 
	 * @param buckets
	 *            - the list of buckets returned from task
	 */
	public void listObjectsOnScreen(String names) {
		bucketsList.setText(names);
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

			Log.d("Profile Listener - onSuccess", "We have bundle data. Id: "

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

			// log the success response
			for (Iterator<String> itr = response.keySet().iterator(); itr
					.hasNext();) {

				String key = itr.next();
				Log.d("AuthToken Listener - onSuccess",
						"Successfully received auth token response with key: "
								+ key);
				Log.d("AuthToken Listener - onSuccess",
						"The value of that key is: " + response.getString(key));
			}

			token = response
					.getString("com.amazon.identity.auth.device.authorization.token");

			wif = new WebIdentityFederationSessionCredentialsProvider(token,
					"www.amazon.com",
					""); //add arn for role
			s3Client = new AmazonS3Client(wif);
		}
	}
}
