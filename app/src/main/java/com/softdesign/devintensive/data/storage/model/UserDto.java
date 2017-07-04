package com.softdesign.devintensive.data.storage.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miller52 on 15.07.16.
 */
public class UserDto implements Parcelable {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserDto> CREATOR = new Parcelable.Creator<UserDto>() {
        @Override
        public UserDto createFromParcel(Parcel in) {
            return new UserDto(in);
        }

        @Override
        public UserDto[] newArray(int size) {
            return new UserDto[size];
        }
    };
    private String mPhoto;
    private String mFullName;
    private String mRating;
    private String mCodeLines;
    private String mProjects;
    private String mBio;
    private List<String> mRepositories;

    public UserDto(User userData) {
        this.mPhoto = userData.getPhoto();
        this.mFullName = userData.getFullName();
        this.mRating = String.valueOf(userData.getRating());
        this.mCodeLines = String.valueOf(userData.getCodeLines());
        this.mProjects = String.valueOf(userData.getProjects());
        this.mBio = userData.getBio();

        mRepositories = new ArrayList<>();
        for (Repository repo : userData.getRepositories()) {
            mRepositories.add(repo.getRepositoryName());
        }
    }

    protected UserDto(Parcel in) {
        mPhoto = in.readString();
        mFullName = in.readString();
        mRating = in.readString();
        mCodeLines = in.readString();
        mProjects = in.readString();
        mBio = in.readString();
        if (in.readByte() == 0x01) {
            mRepositories = new ArrayList<String>();
            in.readList(mRepositories, String.class.getClassLoader());
        } else {
            mRepositories = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPhoto);
        dest.writeString(mFullName);
        dest.writeString(mRating);
        dest.writeString(mCodeLines);
        dest.writeString(mProjects);
        dest.writeString(mBio);
        if (mRepositories == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mRepositories);
        }
    }

    public String getmPhoto() {
        return mPhoto;
    }

    public String getmFullName() {
        return mFullName;
    }

    public String getmRating() {
        return mRating;
    }

    public String getmCodeLines() {
        return mCodeLines;
    }

    public String getmProjects() {
        return mProjects;
    }

    public String getmBio() {
        return mBio;
    }

    public List<String> getmRepositories() {
        return mRepositories;
    }
}
