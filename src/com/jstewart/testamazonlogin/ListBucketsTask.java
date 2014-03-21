package com.jstewart.testamazonlogin;

import java.util.List;

import android.os.AsyncTask;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class ListBucketsTask extends AsyncTask<AmazonS3Client, Void, String> {

	private LoginFragment fragment;

	public ListBucketsTask(LoginFragment fragment) {
		this.fragment = fragment;
	}

	@Override
	protected String doInBackground(AmazonS3Client... clients) {
		ObjectListing listing = clients[0].listObjects(""); //add bucket name
		List<S3ObjectSummary> summaries = listing.getObjectSummaries();

		String names = "";

		for (S3ObjectSummary summary : summaries) {
			names = names + summary.getKey()
					+ "\n"; 
		}
		return names;
	}

	@Override
	protected void onPostExecute(String itemNames) {
		fragment.listObjectsOnScreen(itemNames);
	}
}
