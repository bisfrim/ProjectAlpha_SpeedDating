package com.bizzy.projectalpha.speeddating.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.bizzy.projectalpha.speeddating.R;
import com.bizzy.projectalpha.speeddating.models.UserUploadedPhotos;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class PhotoListAdapter extends ParseQueryAdapter<UserUploadedPhotos> {

	public PhotoListAdapter(Context context) {
		super(context, new ParseQueryAdapter.QueryFactory<UserUploadedPhotos>() {
			public ParseQuery<UserUploadedPhotos> create() {
				// Here we can configure a ParseQuery to display
				// only top-rated meals.
				ParseQuery query = new ParseQuery("UserUploadedPhotos");
				return query;
			}
		});
	}

	@Override
	public View getItemView(UserUploadedPhotos photo, View v, ViewGroup parent) {

		if (v == null) {
			v = View.inflate(getContext(), R.layout.photo_list_adapter, null);
		}

		super.getItemView(photo, v, parent);

		ParseImageView photoImage = (ParseImageView) v.findViewById(R.id.icon);
		ParseFile photoFile = photo.getParseFile("photo");
		if (photoFile != null) {
			photoImage.setParseFile(photoFile);
			photoImage.loadInBackground(new GetDataCallback() {
				@Override
				public void done(byte[] data, ParseException e) {
					// nothing to do
				}
			});
		}
		return v;
	}

}
