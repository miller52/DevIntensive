package com.softdesign.devintensive.data.network.res;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roman on 15.07.16.
 */
public class UserListRes {

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("data")
    @Expose
    private List<UserData> data = new ArrayList<UserData>();

    public List<UserData> getData() {
        return data;
    }

    public class UserData {

        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("first_name")
        @Expose
        private String firstName;
        @SerializedName("second_name")
        @Expose
        private String secondName;
        @SerializedName("__v")
        @Expose
        private int v;
        @SerializedName("repositories")
        @Expose
        private Repositories repositories;
        @SerializedName("profileValues")
        @Expose
        private ProfileValues profileValues;
        @SerializedName("publicInfo")
        @Expose
        private PublicInfo publicInfo;
        @SerializedName("specialization")
        @Expose
        private String specialization;
        @SerializedName("updated")
        @Expose
        private String updated;

        public String getId() {
            return id;
        }

        public Repositories getRepositories() {
            return repositories;
        }

        public ProfileValues getProfileValues() {
            return profileValues;
        }

        public PublicInfo getPublicInfo() {
            return publicInfo;
        }

        public String getFullName() {
            return firstName + " " + secondName;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getSecondName() {
            return secondName;
        }
    }

    public class ProfileValues {

        @SerializedName("homeTask")
        @Expose
        private int homeTask;
        @SerializedName("projects")
        @Expose
        private int projects;
        @SerializedName("linesCode")
        @Expose
        private int linesCode;
        @SerializedName("rait")
        @Expose
        private int rait;
        @SerializedName("updated")
        @Expose
        private String updated;

        public int getLinesCode() {
            return linesCode;
        }

        public int getRait() {
            return rait;
        }

        public int getProjects() {
            return projects;
        }
    }

    public class PublicInfo {

        @SerializedName("bio")
        @Expose
        private String bio;
        @SerializedName("avatar")
        @Expose
        private String avatar;
        @SerializedName("photo")
        @Expose
        private String photo;
        @SerializedName("updated")
        @Expose
        private String updated;

        public String getPhoto() {
            return photo;
        }

        public String getBio() {
            return bio;
        }
    }

    public class Repo {

        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("git")
        @Expose
        private String git;
        @SerializedName("title")
        @Expose
        private String title;

        public String getGit() {
            return git;
        }

        public String getId() {
            return id;
        }
    }

    public class Repositories {

        @SerializedName("repo")
        @Expose
        private List<Repo> repo = new ArrayList<Repo>();
        @SerializedName("updated")
        @Expose
        private String updated;

        public List<Repo> getRepo() {
            return repo;
        }
    }
}
