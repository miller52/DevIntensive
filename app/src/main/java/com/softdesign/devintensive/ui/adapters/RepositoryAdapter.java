package com.softdesign.devintensive.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

import com.softdesign.devintensive.R;

import java.util.List;

/**
 * Created by roman on 16.07.16.
 */
public class RepositoryAdapter extends BaseAdapter {

    private List<String> mRepoList;
    private LayoutInflater mInflater;

    public RepositoryAdapter(Context context, List<String> mRepoList) {
        this.mRepoList = mRepoList;
        mInflater = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
    }

    @Override
    public int getCount() {
        return mRepoList.size();
    }

    @Override
    public Object getItem(int i) {
        return mRepoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View itemView = view;
        if (itemView == null) {
            itemView = mInflater.inflate(R.layout.item_repo_list, viewGroup, false);
        }

        EditText repoName = (EditText) itemView.findViewById(R.id.item_repo_et);
        repoName.setText(mRepoList.get(i));
        return itemView;
    }
}
