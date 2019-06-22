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
import androidx.recyclerview.widget.RecyclerView;

import io.github.balram02.musify.R;
import io.github.balram02.musify.adapters.CommonAdapter;
import io.github.balram02.musify.listeners.FragmentListener;
import io.github.balram02.musify.viewModels.SharedViewModel;

import static io.github.balram02.musify.constants.Constants.TAG;

public class CommonFragment extends Fragment {

    private RecyclerView recyclerView;
    private CommonAdapter commonAdapter;
    //    private SwipeRefreshLayout refreshLayout;
    private Context context;

    private SharedViewModel mViewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.common_fragment, container, false);

        recyclerView = v.findViewById(R.id.recycler_view);
//        refreshLayout = v.findViewById(R.id.refresh_layout);
        recyclerView.setHasFixedSize(true);
        commonAdapter = new CommonAdapter(getContext());
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setAdapter(commonAdapter);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SharedViewModel.class);
    }

    public void setRecyclerViewAdapterType(String fragmentType) {

        Log.d(TAG, "setRecyclerViewAdapterType: " + fragmentType);

        if (fragmentType.equals(FragmentListener.ALBUM_FRAGMENT)) {
            mViewModel.getAlbums().observe(getViewLifecycleOwner(), albumsModel -> {
                commonAdapter.setList(albumsModel, true);
            });
        } else if (fragmentType.equals(FragmentListener.ARTIST_FRAGMENT)) {
            mViewModel.getArtist().observe(getViewLifecycleOwner(), artistModel -> {
                commonAdapter.setList(artistModel, false);
            });
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }
}
