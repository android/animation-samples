package com.google.samples.gridtopager.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.samples.gridtopager.R;

import static com.google.samples.gridtopager.fragment.ImageData.IMAGE_DRAWABLES;

public class MainFragment extends Fragment {

    public MainFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        postponeEnterTransition();

        int adapterPosition = 3;
        ImageView imageView = view.findViewById(R.id.main_image);

        imageView.setOnClickListener(v -> {
            if (this.getFragmentManager() != null) {
                this.getFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .addSharedElement(v, v.getTransitionName())
                        .replace(R.id.fragment_container, ImageFragment.newInstance(IMAGE_DRAWABLES[adapterPosition]))
                        .addToBackStack(null)
                        .commit();
            }
        });

        setImage(adapterPosition, imageView);
        imageView.setTransitionName(String.valueOf(IMAGE_DRAWABLES[adapterPosition]));

        return view;
    }

    void setImage(final int adapterPosition, ImageView imageView) {
        Glide.with(this)
                .load(IMAGE_DRAWABLES[adapterPosition])
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        startPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable>
                            target, DataSource dataSource, boolean isFirstResource) {
                        startPostponedEnterTransition();
                        return false;
                    }
                })
                .into(imageView);
    }
}