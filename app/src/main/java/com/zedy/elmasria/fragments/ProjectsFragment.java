package com.zedy.elmasria.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zedy.elmasria.R;

/**
 * Created by mostafa_anter on 12/2/16.
 */

public class ProjectsFragment extends Fragment {
    public ProjectsFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.projects_fragments, container, false);
        return view;
    }
}
