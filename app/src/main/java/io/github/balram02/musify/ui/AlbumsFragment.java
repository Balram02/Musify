package io.github.balram02.musify.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import io.github.balram02.musify.R;
import io.github.balram02.musify.ViewModels.AlbumsViewModel;

public class AlbumsFragment extends Fragment {

    private RecyclerView recyclerView;
    private AlbumsAdapter albumsAdapter;
    private SwipeRefreshLayout refreshLayout;
    private Context context;

    private AlbumsViewModel mViewModel;

    public static AlbumsFragment newInstance() {
        return new AlbumsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.albums_fragment, container, false);

        recyclerView = v.findViewById(R.id.recycler_view);
        refreshLayout = v.findViewById(R.id.refresh_layout);
        recyclerView.setHasFixedSize(true);
//        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        albumsAdapter = new AlbumsAdapter();
        recyclerView.setAdapter(albumsAdapter);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AlbumsViewModel.class);
        mViewModel.getAllSongsByAlbum().observe(getViewLifecycleOwner(), albumsModel -> {
            albumsAdapter.setSongs(albumsModel);
        });
    }

}
