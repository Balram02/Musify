package io.github.balram02.musify.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import io.github.balram02.musify.R;
import io.github.balram02.musify.ViewModels.FavoritesViewModel;
import io.github.balram02.musify.adapters.FavoritesAdapter;

public class FavoritesFragment extends Fragment {

    private FavoritesViewModel mViewModel;

    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }

    private RecyclerView recyclerView;
    private FavoritesAdapter favoritesAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.favorites_fragment, container, false);
        recyclerView = v.findViewById(R.id.favorites_recycler_view);
        favoritesAdapter = new FavoritesAdapter();
        recyclerView.setAdapter(favoritesAdapter);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FavoritesViewModel.class);
        mViewModel.getFavoriteSong().observe(getViewLifecycleOwner(), songsModels -> {
            favoritesAdapter.setFavorites(songsModels);
            favoritesAdapter.notifyDataSetChanged();
        });
    }
}
