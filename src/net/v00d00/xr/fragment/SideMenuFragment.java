package net.v00d00.xr.fragment;

import net.v00d00.xr.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SideMenuFragment extends ListFragment {

	public interface Provider {
		public void displayMusic();
		public void displayMovies();
		public void displayTVShows();
		public void displayPhotos();
		public void displayRemote();
	}

	private Provider provider;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.side_menu, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		SampleAdapter adapter = new SampleAdapter(getActivity());

		adapter.add(new MenuItem(R.string.menu_music, android.R.drawable.ic_menu_view));
		adapter.add(new MenuItem(R.string.menu_movies, android.R.drawable.ic_menu_view));
		adapter.add(new MenuItem(R.string.menu_tv, android.R.drawable.ic_menu_view));
		adapter.add(new MenuItem(R.string.menu_images, android.R.drawable.ic_menu_view));
		adapter.add(new MenuItem(R.string.menu_remote, android.R.drawable.ic_menu_compass));

		setListAdapter(adapter);
	}

	@Override
	public void onStart() {
		super.onStart();

		Activity activity = getActivity();
		if (activity instanceof Provider)
			provider = (Provider) activity;
		else
			throw new ClassCastException(activity.toString() + " does not implement SideMenuFragment.Provider");
	}

	private class MenuItem {
		public int tag;
		public int iconRes;
		public MenuItem(int tag, int iconRes) {
			this.tag = tag;
			this.iconRes = iconRes;
		}
	}

	public class SampleAdapter extends ArrayAdapter<MenuItem> {

		public SampleAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.side_menu_row, null);
			}
			ImageView icon = (ImageView) convertView.findViewById(R.id.side_menu_row_icon);
			icon.setImageResource(getItem(position).iconRes);
			TextView title = (TextView) convertView.findViewById(R.id.row_title);
			title.setText(getItem(position).tag);

			return convertView;
		}

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		switch (position) {
		case 0:
			provider.displayMusic();
			break;
		case 1:
			provider.displayMovies();
			break;
		case 2:
			provider.displayTVShows();
			break;
		case 3:
			provider.displayPhotos();
			break;
		case 4:
			provider.displayRemote();
			break;
		default:
			super.onListItemClick(l, v, position, id);
		}
	}
}