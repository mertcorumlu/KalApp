package com.kalom.kalapp.classes;

import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kalom.kalapp.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.List;



public class DuyuruAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Duyuru> duyuruList;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private final int VIEW_TYPE_NOTFOUND =2;

    private OnLoadMoreListener mOnLoadMoreListener;

    public boolean isLoading;
    private int lastVisibleItem, totalItemCount,visibleCount;

    public DuyuruAdapter(List<Duyuru> uyeler,RecyclerView mRecyclerView) {

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int newState , int b) {

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager
                        .findLastVisibleItemPosition();

                if (!isLoading && !recyclerView.canScrollVertically(1) ) {
                    if (mOnLoadMoreListener != null) {

                      mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;

                }
            }

        }

        );


        duyuruList = uyeler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.duyurulist_layout, parent, false);
            return new DuyuruHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_footer, parent, false);
            return new LoadingViewHolder(view);
        }else if (viewType == VIEW_TYPE_NOTFOUND) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_footer_error, parent, false);
            return new ErrorViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final ViewHolder out, int position) {

        if (out instanceof DuyuruHolder) {

            Duyuru duyuru = duyuruList.get(position);
            final DuyuruHolder holder=(DuyuruHolder) out;

            holder.title.setText(duyuru.getBaslik());
            holder.yazar.setText(duyuru.getYazar());
            holder.content.setText(duyuru.getIcerik());
            holder.date.setText(duyuru.getDate());

            holder.content_image.setImageDrawable(null);
            holder.content_image.setVisibility(View.GONE);

            if(!duyuru.getContentImage().equals("null") ){

                Ion.with(holder.content_image)
                        .error(R.drawable.danger)
                        .load(duyuru.getContentImage())
                        .setCallback(new FutureCallback<ImageView>() {
                    @Override
                    public void onCompleted(Exception e, ImageView result) {

                    }
                });

                holder.content_image.setVisibility(View.VISIBLE);

            }

            Ion.with(holder.yazar_image)
                    .error(R.drawable.danger)
                    .load(duyuru.getYazarImage())
                    .setCallback(new FutureCallback<ImageView>() {
                        @Override
                        public void onCompleted(Exception e, ImageView result) {
                            holder.yazar_image.setBackground(null);
                        }
                    })
            ;

        }else if (out instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) out;
            loadingViewHolder.progressBar.setBackgroundResource(R.drawable.loader);

            loadingViewHolder.draw = (AnimationDrawable) loadingViewHolder.progressBar.getBackground();
            loadingViewHolder.draw .start();
        }

        else if (out instanceof ErrorViewHolder) {
            Duyuru duyuru = duyuruList.get(position);
            ErrorViewHolder errorViewHolder = (ErrorViewHolder) out;
            errorViewHolder.text.setText(duyuru.getIcerik());
            isLoading=true;
        }



    }


    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        if(duyuruList.get(position) == null){
            return VIEW_TYPE_LOADING;
        }else if(duyuruList.get(position).getID() == 0){
            return VIEW_TYPE_NOTFOUND;
        }else{
            return VIEW_TYPE_ITEM;
        }

    }

    static class DuyuruHolder extends ViewHolder {

        public final TextView title;
        public final TextView yazar;
        public final TextView content;
        public final TextView date;
        public final ImageView content_image;
        public final ImageView yazar_image;




        public DuyuruHolder(final View view) {
            super(view);
            title = view.findViewById(R.id.title);
            yazar = view.findViewById(R.id.yazar);
            content = view.findViewById(R.id.content);
            date = view.findViewById(R.id.date);
            yazar_image = view.findViewById(R.id.list_image);
            content_image = view.findViewById(R.id.content_img);

        }
    }

    static class LoadingViewHolder extends ViewHolder {
        public ImageView progressBar;
        public AnimationDrawable draw;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar =  itemView.findViewById(R.id.login_progress);
        }
    }

    static class ErrorViewHolder extends ViewHolder {
        public TextView text;

        public ErrorViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.error_text);
        }
    }

    @Override
    public int getItemCount() {
        return duyuruList == null ? 0 : duyuruList.size();
    }

    public void setLoaded() {
        isLoading = false;
    }

    public void setLoading() {
        isLoading = true;
    }


}