package io.github.balram02.musify.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import io.github.balram02.musify.R;
import io.github.balram02.musify.ui.adapters.CommonAdapter;
import io.github.balram02.musify.listeners.FragmentListener;
import io.github.balram02.musify.viewModels.SharedViewModel;

import static io.github.balram02.musify.utils.Constants.TAG;

public class CommonFragment extends Fragment {

    private RecyclerView recyclerView;
    private CommonAdapter commonAdapter;
    //    private SwipeRefreshLayout refreshLayout;
    private AppCompatTextView nothing;

    private SharedViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_common, container, false);

        recyclerView = v.findViewById(R.id.recycler_view);
        nothing = v.findViewById(R.id.nothing_msg);
//        refreshLayout = v.findViewById(R.id.refresh_layout);
        recyclerView.setHasFixedSize(true);
        commonAdapter = new CommonAdapter(getActivity());
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
                int size = albumsModel.size();
                if (size == 0 && nothing.getVisibility() == View.GONE) {
                    nothing.setVisibility(View.VISIBLE);
                } else if (size != 0 && nothing.getVisibility() == View.VISIBLE) {
                    nothing.setVisibility(View.GONE);
                }
            });
        } else if (fragmentType.equals(FragmentListener.ARTIST_FRAGMENT)) {
            mViewModel.getArtist().observe(getViewLifecycleOwner(), artistModel -> {
                commonAdapter.setList(artistModel, false);
                int size = artistModel.size();
                if (size == 0 && nothing.getVisibility() == View.GONE) {
                    nothing.setVisibility(View.VISIBLE);
                } else if (size != 0 && nothing.getVisibility() == View.VISIBLE) {
                    nothing.setVisibility(View.GONE);
                }
            });
        }
        recyclerView.scrollTo(0, 0);
    }
}
