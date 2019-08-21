package io.github.balram02.musify.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    private LinearLayout nothing;
    private RecyclerView recyclerView;
    private FavoritesAdapter favoritesAdapter;
    private Context context;
    private MusicPlayerServiceListener musicPlayerServiceListener;
    private TextView nothingMsg;

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
        nothing = v.findViewById(R.id.nothing_layout);
        nothingMsg = v.findViewById(R.id.nothing_msg);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        favoritesAdapter = new FavoritesAdapter(context);
        recyclerView.setAdapter(favoritesAdapter);
        favoritesAdapter.setOnItemClickerListener(model -> {
            musicPlayerServiceListener.onPlayFromFavorites(model, false);
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SharedViewModel.class);
        mViewModel.getFavoriteSong().observe(getViewLifecycleOwner(), songsModels -> {
            favoritesAdapter.submitList(songsModels);
            int size = songsModels.size();
            if (size == 0 && nothing.getVisibility() == View.GONE) {
                nothingMsg.setText("You don't have any favorites");
                nothing.setVisibility(View.VISIBLE);
            } else if (size != 0 && nothing.getVisibility() == View.VISIBLE) {
                nothing.setVisibility(View.GONE);
            }

        });
    }
}
