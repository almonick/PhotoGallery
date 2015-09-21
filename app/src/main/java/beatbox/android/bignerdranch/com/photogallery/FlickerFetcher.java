package beatbox.android.bignerdranch.com.photogallery;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bender on 20/09/2015.
 */
public class FlickerFetcher {

	private static final String API_KEY = "15dcd059c0ba2a7b33c0dad088f1fd52";
	private static final String FLICKER_API = "https://api.flickr.com/services/rest/";
	private static final String TAG = FlickerFetcher.class.getSimpleName();
	String url = "https://api.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key=15dcd059c0ba2a7b33c0dad088f1fd52&format=json&nojsoncallback=1";


	public List<GalleryItem> fetchItems() {
		List<GalleryItem> items = new ArrayList<>();
		String url = Uri.parse(FLICKER_API).buildUpon()
				.appendQueryParameter("method", "flickr.photos.getRecent")
				.appendQueryParameter("api_key", API_KEY)
				.appendQueryParameter("format", "json")
				.appendQueryParameter("nojsoncallback", "1")
				.appendQueryParameter("extras", "url_s")
				.build().toString();

		String jsonStr = null;
		try {
			jsonStr = getUrlString(url);
			JSONObject jsonObject = new JSONObject(jsonStr);
			parseItems(items, jsonObject);
		} catch(IOException e) {
			e.printStackTrace();
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return items;
	}

	private void parseItems(List<GalleryItem> items, JSONObject jsonObject) throws JSONException {
		JSONArray photoJsonArray = jsonObject.getJSONObject("photos").getJSONArray("photo");
		for(int i = 0; i < photoJsonArray.length(); i++) {
			JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);
			if(!photoJsonObject.has("url_s")) {
				continue;
			}
			GalleryItem item = new GalleryItem();
			item.setId(photoJsonObject.getString("id"));
			item.setCaption(photoJsonObject.getString("title"));
			item.setUrl(photoJsonObject.getString("url_s"));
			items.add(item);
		}
	}

	public byte[] getUrlBytes(String urlSpec) throws IOException {
		URL url = new URL(urlSpec);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream in = connection.getInputStream();
			if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
			}
			int bytesRead = 0;
			byte[] buffer = new byte[1024];
			while((bytesRead = in.read(buffer)) > 0) {
				out.write(buffer, 0, bytesRead);
			}
			out.close();
			return out.toByteArray();
		} finally {
			connection.disconnect();
		}

	}

	public String getUrlString(String urlSpec) throws IOException {
		return new String(getUrlBytes(urlSpec));
	}
}
