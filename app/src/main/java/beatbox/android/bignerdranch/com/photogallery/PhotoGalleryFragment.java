package beatbox.android.bignerdranch.com.photogallery;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class PhotoGalleryFragment extends Fragment {

	private static final String TAG = PhotoGalleryFragment.class.getSimpleName();

	private RecyclerView mRecyclerGallery;
	private List<GalleryItem>  mItems = new ArrayList<>();

	public static PhotoGalleryFragment newInstance() {
		PhotoGalleryFragment fragment = new PhotoGalleryFragment();
		return fragment;
	}

	public PhotoGalleryFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);//for async tast to stay alive
		new FetcherItemsTask().execute();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.photo_gallery_fragment, container, false);
		mRecyclerGallery = (RecyclerView) view.findViewById(R.id.recylcer_view);
		mRecyclerGallery.setLayoutManager(new GridLayoutManager(getActivity(), 3));
		setupAdapter();
		return view;
	}

	private void setupAdapter() {
		if(isAdded()){
			mRecyclerGallery.setAdapter(new PhotoAdapter(mItems));
		}
	}

	private class PhotoHolder extends RecyclerView.ViewHolder{
		private TextView mTitle;

		public PhotoHolder(View itemView) {
			super(itemView);
			mTitle = (TextView) itemView;
		}

		public void bindGalleryItem(GalleryItem item){
			mTitle.setText(item.toString());
		}
	}

	private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>{

		List<GalleryItem> mItems;

		public PhotoAdapter(List<GalleryItem> items){
			mItems = items;
		}

		@Override
		public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			TextView textView = new TextView(getActivity());
			return new PhotoHolder(textView);
		}

		@Override
		public void onBindViewHolder(PhotoHolder holder, int position) {
			holder.bindGalleryItem(mItems.get(position));
		}

		@Override
		public int getItemCount() {
			return mItems.size();
		}
	}

	private class FetcherItemsTask extends AsyncTask<Void, Void, List<GalleryItem>>{

		@Override
		protected List<GalleryItem> doInBackground(Void... params) {
			return new FlickerFetcher().fetchItems();
		}

		@Override
		protected void onPostExecute(List<GalleryItem> items) {
			super.onPostExecute(items);
			mItems = items;
			setupAdapter();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
	}




}
