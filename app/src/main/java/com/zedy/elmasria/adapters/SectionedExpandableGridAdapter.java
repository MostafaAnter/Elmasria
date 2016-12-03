package com.zedy.elmasria.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zedy.elmasria.R;
import com.zedy.elmasria.models.ProjectItem;
import com.zedy.elmasria.utils.SquaredImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lenovo on 2/23/2016.
 */
public class SectionedExpandableGridAdapter extends RecyclerView.Adapter<SectionedExpandableGridAdapter.ViewHolder> {

    //data array
    private ArrayList<Object> mDataArrayList;

    //context
    private final Context mContext;

    //listeners
    private final ItemClickListener mItemClickListener;
    private final SectionStateChangeListener mSectionStateChangeListener;

    //view type
    private static final int VIEW_TYPE_SECTION = R.layout.layout_section;
    private static final int VIEW_TYPE_ITEM = R.layout.layout_item; //TODO : change this

    public SectionedExpandableGridAdapter(Context context, ArrayList<Object> dataArrayList,
                                          final GridLayoutManager gridLayoutManager, ItemClickListener itemClickListener,
                                          SectionStateChangeListener sectionStateChangeListener) {
        mContext = context;
        mItemClickListener = itemClickListener;
        mSectionStateChangeListener = sectionStateChangeListener;
        mDataArrayList = dataArrayList;

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return isSection(position)?gridLayoutManager.getSpanCount():1;
            }
        });
    }

    private boolean isSection(int position) {
        return mDataArrayList.get(position) instanceof Section;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(viewType, parent, false), viewType);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        switch (holder.viewType) {
            case VIEW_TYPE_ITEM :
                final ProjectItem item = (ProjectItem) mDataArrayList.get(position);
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.itemClicked(item);
                    }
                });

                Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/normal.ttf");
                Typeface fontBold = Typeface.createFromAsset(mContext.getAssets(), "fonts/bold.ttf");

                // Get element from your dataset at this position and replace the contents of the view
                // with that element
                holder.getMainTitel().setText(item.getTitle());
                holder.getMainTitel().setTypeface(fontBold);
                holder.getTimeStamp().setText(item.getTimeStamp());
                holder.getTimeStamp().setTypeface(font);

                // Chcek for empty status message
                if (!TextUtils.isEmpty(item.getContent())) {
                    holder.getTextStatusMsg().setText(item.getContent());
                    holder.getTextStatusMsg().setTypeface(font);

                } else {
                    // status is empty, remove from view
                    holder.getTextStatusMsg().setVisibility(View.GONE);
                }
                // populate mainImage
                Glide.with(mContext)
                        .load(item.getImageUrl())
                        .placeholder(R.color.dark_gray)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                holder.getmProgress().setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .crossFade()
                        .dontAnimate()
                        .thumbnail(0.2f)
                        .into(holder.getImageView());


                break;
            case VIEW_TYPE_SECTION :
                final Section section = (Section) mDataArrayList.get(position);
                holder.sectionTextView.setText(section.getName());
                holder.sectionTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.itemClicked(section);
                    }
                });
                holder.sectionToggleButton.setChecked(section.isExpanded);
                holder.sectionToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mSectionStateChangeListener.onSectionStateChanged(section, isChecked);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mDataArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isSection(position))
            return VIEW_TYPE_SECTION;
        else return VIEW_TYPE_ITEM;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        //common
        View view;
        int viewType;

        //for section
        TextView sectionTextView;
        ToggleButton sectionToggleButton;

        //for item
        @BindView(R.id.main_title) TextView mainTitel;
        @BindView(R.id.timestamp) TextView timeStamp;
        @BindView(R.id.txtStatusMsg) TextView textStatusMsg;
        @BindView(R.id.feedImage1) SquaredImageView imageView;
        @BindView(R.id.progressBar) ProgressBar mProgress;

        public ProgressBar getmProgress() {
            return mProgress;
        }

        public SquaredImageView getImageView() {
            return imageView;
        }

        public TextView getTextStatusMsg() {
            return textStatusMsg;
        }

        public TextView getTimeStamp() {
            return timeStamp;
        }

        public TextView getMainTitel() {
            return mainTitel;
        }

        public ViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;
            this.view = view;
            if (viewType == VIEW_TYPE_ITEM) {
                ButterKnife.bind(this, view);
            } else {
                sectionTextView = (TextView) view.findViewById(R.id.text_section);
                sectionToggleButton = (ToggleButton) view.findViewById(R.id.toggle_button_section);
            }
        }
    }
}
