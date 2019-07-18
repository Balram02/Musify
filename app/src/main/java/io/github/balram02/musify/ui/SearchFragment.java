package io.github.balram02.musify.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.balram02.musify.R;
import io.github.balram02.musify.adapters.RecentAdapter;
import io.github.balram02.musify.listeners.MusicPlayerServiceListener;
import io.github.balram02.musify.models.SongsModel;
import io.github.balram02.musify.viewModels.SharedViewModel;

import static io.github.balram02.musify.constants.Constants.TAG;

public class SearchFragment extends Fragment {

    private SharedViewModel mViewModel;
    private Context context;

    private MusicPlayerServiceListener musicPlayerServiceListener;
    private ListView searchListView;

    private RecyclerView recentRecyclerView;

    private SearchView searchView;
    private ArrayAdapter<SongsModel> arrayAdapter;

    private RecentAdapter recentAdapter;
    private TextView noRecent;

    private View v;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            musicPlayerServiceListener = (MusicPlayerServiceListener) context;
        } catch (Exception e) {
            Log.d(TAG, "onAttach: " + e.toString());
            Toast.makeText(context, "Must Implement Service Listener", Toast.LENGTH_SHORT).show();
        }

        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        v = inflater.inflate(R.layout.search_fragment, container, false);
        searchListView = v.findViewById(R.id.search_list_view);
        recentRecyclerView = v.findViewById(R.id.recent_list_view);

        noRecent = v.findViewById(R.id.no_recent_text);

        recentRecyclerView.setHasFixedSize(true);

        recentAdapter = new RecentAdapter(context);
        recentRecyclerView.setAdapter(recentAdapter);

        arrayAdapter = new ArrayAdapter<SongsModel>(context, R.layout.recycler_view_song_item, R.id.song_name) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Holder holder;
                View v = convertView;

                if (v == null) {
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_song_item, parent, false);
                    holder = new Holder();
                    holder.title = v.findViewById(R.id.song_name);
                    holder.listLayout = v.findViewById(R.id.list_layout);
                    v.setTag(holder);
                } else {
                    holder = (Holder) v.getTag();
                }
                holder.setClickListeners(position);
                SongsModel model = getItem(position);
                holder.title.setText(model.getTitle());

                return v;
            }

            class Holder {
                private TextView title;
                private RelativeLayout listLayout;

                void setClickListeners(int position) {

                    SongsModel model = getItem(position);

                    listLayout.setOnClickListener(view -> {
                        musicPlayerServiceListener.onUpdateService(model);
                    });
                }
            }
        };

        searchListView.setAdapter(arrayAdapter);

        recentAdapter.setOnItemClickListener(model -> {
            musicPlayerServiceListener.onUpdateService(model);
        });

        searchListView.setOnItemClickListener((adapterView, view, i, l) -> {
            musicPlayerServiceListener.onUpdateService((SongsModel) adapterView.getItemAtPosition(i));
            Log.d(TAG, "onCreateView: " + i);
        });

        return v;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_fragment_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search_action);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        searchView.setOnCloseListener(() -> {
            arrayAdapter.clear();
            return true;
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: " + newText);
                newText = newText.trim();
                if (newText.isEmpty()) {
                    arrayAdapter.clear();
                    v.findViewById(R.id.search_text).setVisibility(View.GONE);
                } else {
                    v.findViewById(R.id.search_text).setVisibility(View.VISIBLE);
                    updateSearchList(mViewModel.getSearchQueryResults(newText));

                }
                return true;
            }
        });
    }

    private void updateSearchList(List<SongsModel> list) {
        arrayAdapter.clear();
        arrayAdapter.addAll(list);
        arrayAdapter.notifyDataSetChanged();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(SharedViewModel.class);
        mViewModel.getRecentlyPlayedSongs().observe(getViewLifecycleOwner(), songsModels -> {
            recentAdapter.submitList(songsModels);
            if (songsModels.size() != 0)
                noRecent.setVisibility(View.GONE);
            else
                noRecent.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        musicPlayerServiceListener = null;
        context = null;
    }

}
