package com.example.android.unsplash.ui.pager;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.android.unsplash.R;
import com.example.android.unsplash.data.model.Photo;
import com.example.android.unsplash.databinding.DetailViewBinding;
import com.example.android.unsplash.ui.DetailSharedElementEnterCallback;
import com.example.android.unsplash.ui.ImageSize;

import java.util.List;

/**
 * Adapter for paging detail views.
 */

public class DetailViewPagerAdapter extends PagerAdapter {

    private final List<Photo> allPhotos;
    private final LayoutInflater layoutInflater;
    private final int photoWidth;
    private final Activity host;
    private DetailSharedElementEnterCallback sharedElementCallback;

    public DetailViewPagerAdapter(@NonNull Activity activity, @NonNull List<Photo> photos,
                                  @NonNull DetailSharedElementEnterCallback callback) {
        layoutInflater = LayoutInflater.from(activity);
        allPhotos = photos;
        photoWidth = activity.getResources().getDisplayMetrics().widthPixels;
        host = activity;
        sharedElementCallback = callback;
    }

    @Override
    public int getCount() {
        return allPhotos.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        DetailViewBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.detail_view, container, false);
        binding.setData(allPhotos.get(position));
        onViewBound(binding);
        binding.executePendingBindings();
        container.addView(binding.getRoot());
        return binding;
    }

    private void onViewBound(DetailViewBinding binding) {
        Glide.with(host)
                .load(binding.getData().getPhotoUrl(photoWidth))
                .placeholder(R.color.placeholder)
                .override(ImageSize.NORMAL[0], ImageSize.NORMAL[1])
                .into(binding.photo);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (object instanceof DetailViewBinding) {
            sharedElementCallback.setBinding((DetailViewBinding) object);
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object instanceof DetailViewBinding
                && view.equals(((DetailViewBinding) object).getRoot());
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(((DetailViewBinding) object).getRoot());
    }
}
