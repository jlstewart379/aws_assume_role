package com.jstewart.testamazonlogin;

import com.amazon.identity.auth.device.authorization.api.AuthorizationListener;

import android.os.Bundle;

public interface AuthorizationManager {
	
	public void authorize(String options, Bundle args, AuthorizationListener listener); 

}
