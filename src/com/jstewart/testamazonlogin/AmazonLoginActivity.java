package com.jstewart.testamazonlogin;

import com.amazon.identity.auth.device.authorization.api.AmazonAuthorizationManager;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class AmazonLoginActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		AmazonAuthorizationManager mAuthManager = new AmazonAuthorizationManager(this,
				Bundle.EMPTY);
		
		setContentView(R.layout.activity_amazon_login);

		FragmentManager fm = getFragmentManager();
		Fragment loginFragment = fm
				.findFragmentById(R.id.login_fragment_container);

		if (loginFragment == null) {
			loginFragment = new LoginFragment();
		}

		fm.beginTransaction().add(R.id.login_fragment_container, loginFragment)
				.commit();

	}

}
