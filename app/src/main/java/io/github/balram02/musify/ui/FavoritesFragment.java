package io.github.balram02.musify.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import io.github.balram02.musify.R;
import io.github.balram02.musify.adapters.FavoritesAdapter;
import io.github.balram02.musify.listeners.MusicPlayerServiceListener;
import io.github.balram02.musify.viewModels.SharedViewModel;

import static io.github.balram02.musify.constants.Constants.TAG;

public class FavoritesFragment extends Fragment {

    private SharedViewModel mViewModel;

    private RecyclerView recyclerView;
    private FavoritesAdapter favoritesAdapter;
    private Context context;
    private MusicPlayerServiceListener musicPlayerServiceListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        try {
            musicPlayerServiceListener = (MusicPlayerServiceListener) context;
        } catch (Exception e) {
            Log.e(TAG, "onAttach: " + e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        musicPlayerServiceListener = null;
        context = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.favorites_fragment, container, false);
        recyclerView = v.findViewById(R.id.favorites_recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        favoritesAdapter = new FavoritesAdapter(context);
        recyclerView.setAdapter(favoritesAdapter);
        favoritesAdapter.setOnItemClickerListener(model -> {
            musicPlayerServiceListener.onUpdateService(mViewModel.getShuffleSongsQueue(), model, mViewModel);
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SharedViewModel.class);
        mViewModel.getFavoriteSong().observe(getViewLifecycleOwner(), songsModels -> {
            favoritesAdapter.submitList(songsModels);
        });
    }
}
