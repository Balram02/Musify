package io.github.balram02.melody.UI;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.balram02.melody.R;
import io.github.balram02.melody.ViewModels.AllSongsViewModel;

public class AllSongsFragment extends Fragment {

    private AllSongsViewModel mViewModel;

    public static AllSongsFragment newInstance() {
        return new AllSongsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.all_songs_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AllSongsViewModel.class);
        // TODO: Use the ViewModel
    }

}
