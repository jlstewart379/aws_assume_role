package com.jstewart.testamazonlogin;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.amazon.identity.auth.device.AuthError;
import com.amazon.identity.auth.device.authorization.api.AmazonAuthorizationManager;
import com.amazon.identity.auth.device.authorization.api.AuthorizationListener;
import com.amazon.identity.auth.device.authorization.api.AuthzConstants;
import com.amazon.identity.auth.device.shared.APIListener;

public class LoginFragment extends Fragment {

	private ImageButton loginButton;
	private AmazonAuthorizationManager mAuthManager;
	private String[] AUTH_SCOPES = new String[] { "profile" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);



	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_login, null);
		
		loginButton = (ImageButton) view.findViewById(
				R.id.login_with_amazon_button);
//		loginButton.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//
//				mAuthManager.authorize(AUTH_SCOPES, Bundle.EMPTY,
//						new AuthListener());
//
//			}
//		});

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

		}
	}

}