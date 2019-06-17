package io.github.balram02.musify.ui;

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
import io.github.balram02.musify.viewModels.SharedViewModel;

import static io.github.balram02.musify.constants.Constants.ALBUM_FRAGMENT_REQUEST;
import static io.github.balram02.musify.constants.Constants.ARTIST_FRAGMENT_REQUEST;

public class LibraryFragment extends Fragment {

    private Context context;

    private SharedViewModel mViewModel;
    private CardView albumsCardView, artistCardView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.library_fragment, container, false);
        artistCardView = v.findViewById(R.id.artist_card_view);
        albumsCardView = v.findViewById(R.id.albums_card_view);

        albumsCardView.setOnClickListener(v1 -> {
            Bundle bundle = new Bundle();
            bundle.putString("request_for", ALBUM_FRAGMENT_REQUEST);
            ((MainActivity) context).commonFragment.setArguments(bundle);
            ((MainActivity) context).setFragment(((MainActivity) context).commonFragment);
        });

        artistCardView.setOnClickListener(v1 -> {
            Bundle bundle = new Bundle();
            bundle.putString("request_for", ARTIST_FRAGMENT_REQUEST);
            ((MainActivity) context).commonFragment.setArguments(bundle);
            ((MainActivity) context).setFragment(((MainActivity) context).commonFragment);
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SharedViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }
}
