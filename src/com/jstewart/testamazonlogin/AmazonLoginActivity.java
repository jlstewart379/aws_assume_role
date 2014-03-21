package com.jstewart.testamazonlogin;

import java.util.List;

import com.amazonaws.services.s3.model.Bucket;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class AmazonLoginActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

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

	public void listBuckets(List<Bucket> buckets) {
		// TODO Auto-generated method stub

	}

}
