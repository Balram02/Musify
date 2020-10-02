package io.github.balram02.musify.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import io.github.balram02.musify.R;
import io.github.balram02.musify.listeners.FragmentListener;
import io.github.balram02.musify.viewModels.SharedViewModel;

public class LibraryFragment extends Fragment {

    private SharedViewModel mViewModel;
    private CardView albumsCardView, artistCardView;

    private FragmentListener fragmentListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (FragmentListener) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_library, container, false);
        artistCardView = v.findViewById(R.id.artist_card_view);
        albumsCardView = v.findViewById(R.id.albums_card_view);

        albumsCardView.setOnClickListener(v1 -> {
            fragmentListener.setCommonFragmentType(FragmentListener.ALBUM_FRAGMENT);
        });

        artistCardView.setOnClickListener(v1 -> {
            fragmentListener.setCommonFragmentType(FragmentListener.ARTIST_FRAGMENT);
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SharedViewModel.class);
    }

}
