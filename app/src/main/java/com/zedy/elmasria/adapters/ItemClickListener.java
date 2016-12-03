package com.zedy.elmasria.adapters;


import com.zedy.elmasria.models.ProjectItem;

/**
 * Created by lenovo on 2/23/2016.
 */
public interface ItemClickListener {
    void itemClicked(ProjectItem item);
    void itemClicked(Section section);
}
