package io.github.balram02.musify.ui;

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
import io.github.balram02.musify.viewModels.LibraryViewModel;

import static io.github.balram02.musify.ui.MainActivity.navigationView;

public class LibraryFragment extends Fragment {

    private LibraryViewModel mViewModel;
    private CardView songsCardView, albumsCardView;

    public static LibraryFragment newInstance() {
        return new LibraryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.library_fragment, container, false);
        songsCardView = v.findViewById(R.id.songs_card_view);
        albumsCardView = v.findViewById(R.id.albums_card_view);

        albumsCardView.setOnClickListener(v1 -> {
            ((MainActivity) getActivity()).setFragment(new AlbumsFragment());
        });

        songsCardView.setOnClickListener(v1 -> {
            navigationView.setSelectedItemId(R.id.music);
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(LibraryViewModel.class);
        // TODO: Use the ViewModel
    }

}
