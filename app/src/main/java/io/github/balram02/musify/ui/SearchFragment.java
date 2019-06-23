package io.github.balram02.musify.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.futuremind.recyclerviewfastscroll.FastScroller;

import io.github.balram02.musify.R;
import io.github.balram02.musify.adapters.SongsAdapter;
import io.github.balram02.musify.listeners.MusicPlayerServiceListener;
import io.github.balram02.musify.utils.Preferences;
import io.github.balram02.musify.viewModels.SharedViewModel;

import static io.github.balram02.musify.constants.Constants.TAG;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private SongsAdapter songsAdapter;
    private SharedViewModel mViewModel;
    private Context context;

    private MusicPlayerServiceListener musicPlayerServiceListener;
    private FastScroller fastScroller;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.search_fragment, container, false);
        recyclerView = v.findViewById(R.id.recent_recycler_view);
        fastScroller = v.findViewById(R.id.fast_scroller);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        songsAdapter = new SongsAdapter(getActivity());
        recyclerView.setAdapter(songsAdapter);

        songsAdapter.setOnItemClickListener(model -> {
            boolean state = Preferences.DefaultSettings.getShuffleState(context);
            if (state) {
                musicPlayerServiceListener.onUpdateService(mViewModel.getShuffleSongsQueue(), model, mViewModel);
            } else {
                musicPlayerServiceListener.onUpdateService(mViewModel.getAllSongsQueue(), model, mViewModel);
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(SharedViewModel.class);
        mViewModel.getRecentlyPlayedSongs().observe(getViewLifecycleOwner(), songsModels -> {
            songsAdapter.submitList(songsModels);
            songsAdapter.onAttachedToRecyclerView(recyclerView);
            fastScroller.setRecyclerView(recyclerView);
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        musicPlayerServiceListener = null;
        context = null;
    }

}
