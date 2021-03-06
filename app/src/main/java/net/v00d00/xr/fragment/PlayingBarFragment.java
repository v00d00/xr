/*

XR - Modern Android XBMC remote
Copyright (C) 2013 Ian Whyman

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/

package net.v00d00.xr.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import net.minidev.json.JSONObject;
import net.v00d00.xr.AsyncCallback;
import net.v00d00.xr.NullCallback;
import net.v00d00.xr.R;
import net.v00d00.xr.events.PlayEvent;
import net.v00d00.xr.events.ServiceAvailableEvent;
import net.v00d00.xr.events.StopEvent;
import net.v00d00.xr.model.EpisodeDetail;
import net.v00d00.xr.model.MovieDetail;
import net.v00d00.xr.model.SongDetail;
import net.v00d00.xr.view.ThumbnailView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayingBarFragment extends AbstractXRFragment implements OnClickListener, SlidingUpPanelLayout.PanelSlideListener {

	private RelativeLayout bar;
	private TextView title;
	private ThumbnailView image;
	private TextView subtitle;
	private ThumbnailView bigImage;

	private ImageButton prev;
	private ImageButton pause;
	private ImageButton next;

	private TextView underImageTitle;
	private TextView underImageSubtext;

	public interface Provider {
		public void setDragView(View view);
	}

	public void onEventMainThread(PlayEvent event) {
		switch (event.type) {
			case "song":
				showSongPlaying(event.id);
				break;
			case "episode":
				showTVPlaying(event.id);
				break;
			default:
				break;
		}
	}

	public void onEventMainThread(StopEvent event) {
		showNowPlaying("", "", null);
	}

	public void onEventMainThread(ServiceAvailableEvent event) {
		load();
	}

	@Override
	public void onResume() {
		super.onResume();
		load();
	}

	private void showNowPlaying(String title, String subtitle, String image) {
		this.title.setText(title);
		this.underImageTitle.setText(title);

		this.subtitle.setText(subtitle);
		this.underImageSubtext.setText(subtitle);

		this.image.setThumbnailPathNoFit(image);
		this.bigImage.setThumbnailPathNoFit(image);
	}

	private void showMoviePlaying(int id) {
        /*
		GetMovieDetails call = new GetMovieDetails(id, MovieDetail.TAGLINE, MovieDetail.THUMBNAIL, MovieDetail.TITLE, MovieDetail.YEAR, MovieDetail.GENRE);
		getConnectionManager().call(call, new ApiCallback<MovieDetail>() {
			@Override
			public void onResponse(AbstractCall<MovieDetail> call) {
				showMoviePlaying(call.getResult());
			}
			@Override
			public void onError(int code, String message, String hint) {
				Log.d("ON_NOW_PLAYING_ERROR", message);
			}
		});
		*/
	}

	private void showMoviePlaying(MovieDetail detail) {
        /*
		StringBuilder sb = new StringBuilder(detail.year);
		if (detail.tagline != null) {
			sb.append(" '");
			sb.append(detail.tagline);
			sb.append("'");
		}
		showNowPlaying(detail.title, sb.toString(), detail.thumbnail);
		*/
	}

	private void showSongPlaying(Long id) {

		Map<String, Object> params = new HashMap<>();
		params.put("songid", id);
		params.put("properties", Arrays.asList("displayartist", "thumbnail", "title"));

		requestData("AudioLibrary.GetSongDetails", params, new AsyncCallback() {
			@Override
			public void onSuccess(Object result) {
				Log.d("nowPlaying", result.toString());

				SongDetail songDetail = new SongDetail();

				Map<String, Object> outer = (Map<String, Object>) result;
				Map<String, Object> details = (Map<String, Object>) outer.get("songdetails");
				songDetail.displayartist = (String) details.get("displayartist");
				songDetail.title = (String) details.get("title");
				songDetail.thumbnail = (String) details.get("thumbnail");

				showSongPlaying(songDetail);
			}

			@Override
			public void onFailure(String error) {}
		});
	}

	private void showSongPlaying(SongDetail detail) {
		showNowPlaying(detail.title, detail.displayartist, detail.thumbnail);
	}

	private void showTVPlaying(long id) {

		Map<String, Object> params = new HashMap<>();
		params.put("episodeid", id);
		params.put("properties", Arrays.asList("thumbnail", "showtitle", "title", "season", "episode"));

		requestData("VideoLibrary.GetEpisodeDetails", params, new AsyncCallback() {
			@Override
			public void onSuccess(Object result) {
				Log.d("nowPlaying", result.toString());

				EpisodeDetail episodeDetail = new EpisodeDetail();

				Map<String, Object> outer = (Map<String, Object>) result;
				Map<String, Object> details = (Map<String, Object>) outer.get("episodedetails");
				episodeDetail.title = (String) details.get("title");
				episodeDetail.showtitle = (String) details.get("showtitle");
				episodeDetail.thumbnail = (String) details.get("thumbnail");
				episodeDetail.season = (Long) details.get("season");
				episodeDetail.episode = (Long) details.get("episode");

				showTVPlaying(episodeDetail);
			}

			@Override
			public void onFailure(String error) {
			}
		});
	}

	private void showTVPlaying(EpisodeDetail detail) {
		String subtext = String.format("%s S%02dE%02d", detail.showtitle, detail.season, detail.episode);
		showNowPlaying(detail.title, subtext, detail.thumbnail);
	}

	public PlayingBarFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.playing, container, false);

		if (bar == null)
			bar = (RelativeLayout) view.findViewById(R.id.now_playing_bar);
		if (image == null)
			image = (ThumbnailView) view.findViewById(R.id.now_playing_bar_img);
		if (title == null)
			title = (TextView) view.findViewById(R.id.now_playing_bar_title);
		if (subtitle == null)
			subtitle = (TextView) view.findViewById(R.id.now_playing_bar_subtitle);
		if (bigImage == null) {
			bigImage = (ThumbnailView) view.findViewById(R.id.now_playing_bar_img_big);
			bigImage.setOnClickListener(this);
		}
		if (prev == null) {
			prev = (ImageButton) view.findViewById(R.id.now_playing_bar_previous);
			prev.setOnClickListener(this);
		}
		if (pause == null) {
			pause = (ImageButton) view.findViewById(R.id.now_playing_bar_pause);
			pause.setOnClickListener(this);
		}
		if (next == null) {
			next = (ImageButton) view.findViewById(R.id.now_playing_bar_next);
			next.setOnClickListener(this);
		}

		if (underImageTitle == null)
			underImageTitle = (TextView) view.findViewById(R.id.under_image_title);

		if (underImageSubtext == null)
			underImageSubtext = (TextView) view.findViewById(R.id.under_image_subtext);

		// Make marquee mode work
		title.setSelected(true);
		subtitle.setSelected(true);
		underImageTitle.setSelected(true);

		Activity activity = getActivity();
		if (activity instanceof Provider)
			((Provider) activity).setDragView(bar);
		else
			throw new ClassCastException(activity.toString() + " does not implement ConnectionManagerProvider");

		return view;
	}

	@Override
	public boolean shouldLoad() {
		return true;
	}

	@Override
	protected void load() {
		Log.d("PlayingBarFragment", "called onLoad");

		Map<String, Object> params = new HashMap<>();
		params.put("properties", Arrays.asList("title", "displayartist", "thumbnail"));

		onActivePlayer("Player.GetItem", params, new AsyncCallback() {
			@Override
			public void onSuccess(Object result) {
				JSONObject obj = (JSONObject) result;
				Map<String, Object> item = (Map<String, Object>) obj.get("item");

				switch ((String) item.get("type")) {
					case "song":
						showNowPlaying((String) item.get("label"), (String) item.get("displayartist"), (String) item.get("thumbnail"));
						break;
					case "episode":
						showTVPlaying((Long) item.get("id"));
					case "unknown":
						showNowPlaying((String) item.get("label"), "", (String) item.get("thumbnail"));
						break;
					default:
						break;
				}
			}

			@Override
			public void onFailure(String error) {}
		});
	}

	@Override
	public CharSequence getTitle() {
		return "Now Playing";
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.now_playing_bar_pause:
				doPlayerPause();
				break;
			case R.id.now_playing_bar_next:
				doPlayerNext();
				break;
			case R.id.now_playing_bar_previous:
				doPlayerPrevious();
				break;
			case R.id.now_playing_bar_img_big:
				// TODO hide playing
				break;
		}
	}

	private void onActivePlayer(final String method) {
		onActivePlayer(method, new HashMap<String, Object>(), new NullCallback());
	}

	private void onActivePlayer(final String method, Map<String, Object> params) {
		onActivePlayer(method, params, new NullCallback());
	}

	private void onActivePlayer(final String method, final Map<String, Object> params, final AsyncCallback callback) {
		requestData("Player.GetActivePlayers", new AsyncCallback() {
			@Override
			public void onSuccess(Object result) {
				List<JSONObject> players = (List<JSONObject>) result;

				for (JSONObject player : players) {
					Long playerid = (Long) player.get("playerid");
					params.put("playerid", playerid);
					requestData(method, params, callback);
				}
			}

			@Override
			public void onFailure(String error) {

			}
		});
	}

	void doPlayerPause() {
		onActivePlayer("Player.PlayPause");
	}

	void doPlayerNext() {
		HashMap<String, Object> params = new HashMap<>();
		params.put("to", "next");
		onActivePlayer("Player.GoTo", params);
	}

	void doPlayerPrevious() {
		HashMap<String, Object> params = new HashMap<>();
		params.put("to", "next");
		onActivePlayer("Player.GoTo", params);
	}

	@Override
	public void onPanelSlide(View view, float slideOffset) {
		bigImage.setAlpha(slideOffset);
		ActionBar bar = getActivity().getActionBar();
		if (bar != null) {
			if (slideOffset > 0.7) {
				if (bar.isShowing()) {
					bar.hide();
				}
			} else {
				if (!bar.isShowing()) {
					bar.show();
				}
			}
		}
	}

	@Override
	public void onPanelCollapsed(View view) {
		Log.d("Panel", "Collapsed");
	}

	@Override
	public void onPanelExpanded(View view) {
		Log.d("Panel", "Expanded");
		if(getActivity().getActionBar().isShowing())
			getActivity().getActionBar().hide();
	}

	@Override
	public void onPanelAnchored(View view) {
		Log.d("Panel", "Anchored");

	}

	@Override
	public void onPanelHidden(View view) {
		Log.d("Panel", "Hidden");
	}


}
