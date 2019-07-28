/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.ourstreets.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

public class Gallery implements Parcelable {

    private final String mTitle;
    private final String mDescription;
    private final String mGalleryId;
    private final List<Detail> mDetails;
    private final LatLng mLatLng;
    private LatLngBounds mLatLngBounds;

    public Gallery(String title, String description, String galleryId, LatLng latLng) {
        mTitle = title;
        mDescription = description;
        mGalleryId = galleryId;
        mLatLng = latLng;
        mDetails = new ArrayList<>();
    }

    public void replaceDetails(List<Detail> details) {
        mDetails.clear();
        if (details != null) {
            mDetails.addAll(details);
        }
    }

    public boolean hasDetails() {
        return mDetails.size() > 0;
    }

    public String getTitle() {
        return mTitle;
    }

    @NonNull
    public String getDescription() {
        return mDescription;
    }

    public String getGalleryId() {
        return mGalleryId;
    }

    public List<Detail> getDetails() {
        return mDetails;
    }

    public LatLng getPosition() {
        return mLatLng;
    }

    @NonNull
    public LatLngBounds getBounds() {
        if (null == mLatLngBounds) {
            mLatLngBounds = calculateBounds();
        }
        return mLatLngBounds;
    }

    @NonNull
    private LatLngBounds calculateBounds() {
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (Detail location : mDetails) {
            boundsBuilder.include(location.getPosition());
        }
        return boundsBuilder.build();
    }

    protected Gallery(Parcel in) {
        mTitle = in.readString();
        mDescription = in.readString();
        mGalleryId = in.readString();
        mLatLng = in.readParcelable(LatLng.class.getClassLoader());
        mDetails = new ArrayList<>();
        in.readTypedList(mDetails, Detail.CREATOR);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Gallery)) {
            return false;
        }

        Gallery gallery = (Gallery) o;

        if (!mTitle.equals(gallery.mTitle)) {
            return false;
        }
        if (!mDescription.equals(gallery.mDescription)) {
            return false;
        }
        if (!mGalleryId.equals(gallery.mGalleryId)) {
            return false;
        }
        if (mLatLngBounds != null ? !mLatLngBounds.equals(gallery.mLatLngBounds)
                : gallery.mLatLngBounds != null) {
            return false;
        }
        if (mDetails != null ? !mDetails.equals(gallery.mDetails)
                : gallery.mDetails != null) {
            return false;
        }
        return !(mLatLng != null ? !mLatLng.equals(gallery.mLatLng)
                : gallery.mLatLng != null);

    }

    @Override
    public int hashCode() {
        int result = mTitle.hashCode();
        result = 31 * result + mDescription.hashCode();
        result = 31 * result + mGalleryId.hashCode();
        result = 31 * result + (mLatLngBounds != null ? mLatLngBounds.hashCode() : 0);
        result = 31 * result + (mDetails != null ? mDetails.hashCode() : 0);
        result = 31 * result + (mLatLng != null ? mLatLng.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return mGalleryId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mDescription);
        dest.writeString(mGalleryId);
        dest.writeParcelable(mLatLng, flags);
        dest.writeTypedList(mDetails);
    }

    public static final Creator<Gallery> CREATOR = new Creator<Gallery>() {
        @Override
        public Gallery createFromParcel(Parcel in) {
            return new Gallery(in);
        }

        @Override
        public Gallery[] newArray(int size) {
            return new Gallery[size];
        }
    };
}
