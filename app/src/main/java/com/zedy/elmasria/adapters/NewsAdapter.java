package com.zedy.elmasria.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zedy.elmasria.R;
import com.zedy.elmasria.activities.NewsDetailActivity;
import com.zedy.elmasria.models.NewsItem;
import com.zedy.elmasria.utils.OnLoadMoreListener;
import com.zedy.elmasria.utils.SquaredImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
/**
 * Created by mostafa on 02/08/16.
 */
public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    public OnLoadMoreListener mOnLoadMoreListener;
    public boolean isLoading;

    private List<NewsItem> mDataSet;
    private Context mContext;

    public NewsAdapter(Context mContext, List<NewsItem> dataSet) {
        this.mContext = mContext;
        mDataSet = dataSet;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.main_title) TextView mainTitel;
        @BindView(R.id.timestamp) TextView timeStamp;
        @BindView(R.id.txtStatusMsg) TextView textStatusMsg;
        @BindView(R.id.feedImage1) SquaredImageView imageView;
        @BindView(R.id.progressBar) ProgressBar mProgress;

        public TextView getMainTitel() {
            return mainTitel;
        }

        public TextView getTimeStamp() {
            return timeStamp;
        }

        public TextView getTextStatusMsg() {
            return textStatusMsg;
        }

        public SquaredImageView getImageView() {
            return imageView;
        }

        public ProgressBar getmProgress() {
            return mProgress;
        }

        public ItemViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                    Context context = v.getContext();
                    Intent intent = new Intent(context, NewsDetailActivity.class);
                    intent.putExtra("item", mDataSet.get(getPosition()));

                    context.startActivity(intent);
                }
            });

        }


    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }

    }

    @Override
    public int getItemViewType(int position) {
        return mDataSet.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_item, viewGroup, false);
            return new ItemViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_loading_item, viewGroup, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

            Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/normal.ttf");
            Typeface fontBold = Typeface.createFromAsset(mContext.getAssets(), "fonts/bold.ttf");

            // Get element from your dataset at this position and replace the contents of the view
            // with that element
            itemViewHolder.getMainTitel().setText(mDataSet.get(position).getTitle());
            itemViewHolder.getMainTitel().setTypeface(fontBold);
            itemViewHolder.getTimeStamp().setText(mDataSet.get(position).getTimeStamp());
            itemViewHolder.getTimeStamp().setTypeface(font);

            // Chcek for empty status message
            if (!TextUtils.isEmpty(mDataSet.get(position).getContent())) {
                itemViewHolder.getTextStatusMsg().setText(mDataSet.get(position).getContent());
                itemViewHolder.getTextStatusMsg().setTypeface(font);

            } else {
                // status is empty, remove from view
                itemViewHolder.getTextStatusMsg().setVisibility(View.GONE);
            }
            // populate mainImage
            Glide.with(mContext)
                    .load(mDataSet.get(position).getImageUrl())
                    .placeholder(R.color.dark_gray)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            itemViewHolder.getmProgress().setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .crossFade()
                    .dontAnimate()
                    .thumbnail(0.2f)
                    .into(itemViewHolder.getImageView());


        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet == null ? 0 : mDataSet.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    public void setLoaded() {
        isLoading = false;
    }

}


