package com.kalom.kalapp.classes;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.kalom.kalapp.AnketWebviewActivity;
import com.kalom.kalapp.R;

import java.util.List;


public class AnketAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Anket> anketlerList;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private final int VIEW_TYPE_NOTFOUND =2;

    private OnLoadMoreListener mOnLoadMoreListener;

    private boolean isLoading;
    private int lastVisibleItem, totalItemCount,visibleCount;

    public AnketAdapter(List<Anket> uyeler,RecyclerView mRecyclerView) {


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


        anketlerList = uyeler;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.anketlist_layout, parent, false);
            return new AnketHolder(view);
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
    public void onBindViewHolder(final RecyclerView.ViewHolder out, int position) {

        if (out instanceof AnketHolder) {

            final Anket ank = anketlerList.get(position);
            final AnketHolder holder=(AnketHolder) out;

            holder.title.setText(ank.getBaslik());
            holder.yazar.setText(ank.getYazar());

            if(ank.isVoted()){
                holder.list_image.setImageResource(R.drawable.survey_icon_black);
                holder.list_image.setColorFilter(Color.parseColor("#7F7F7F"));

                holder.title.setTextColor(Color.parseColor("#7F7F7F"));
                holder.yazar.setTextColor(Color.parseColor("#7F7F7F"));

                holder.info_text.setText("SONUÇLAR");



            }

            holder.rel.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent intent = new Intent(v.getContext(), AnketWebviewActivity.class);
                    intent.putExtra("anket_id",ank.getID());
                    v.getContext().startActivity(intent);
                }
            });

        }else if (out instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) out;
            loadingViewHolder.progressBar.setBackgroundResource(R.drawable.loader);

            loadingViewHolder.draw = (AnimationDrawable) loadingViewHolder.progressBar.getBackground();
            loadingViewHolder.draw.start();
        }
        else if (out instanceof ErrorViewHolder) {
            Anket anket = anketlerList.get(position);
            ErrorViewHolder errorViewHolder = (ErrorViewHolder) out;
            errorViewHolder.text.setText(anket.getBaslik());
            isLoading=true;
        }



    }


    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        if(anketlerList.get(position) == null){
            return VIEW_TYPE_LOADING;
        }else if(anketlerList.get(position).getID() == 0){
            return VIEW_TYPE_NOTFOUND;
        }else{
            return VIEW_TYPE_ITEM;
        }

    }

    static class AnketHolder extends ViewHolder {

        public final TextView title;
        public final TextView yazar;
        public final TextView info_text;
        public final RelativeLayout rel;
        public final ImageView list_image;
       // public final ImageView voted_image;

        AnimationDrawable animationDrawable;


        public AnketHolder(final View view) {
            super(view);

            title = view.findViewById(R.id.title);
            yazar = view.findViewById(R.id.yazar);
            info_text = view.findViewById(R.id.info_text);
            rel = view.findViewById(R.id.relativeLayout);
            list_image = view.findViewById(R.id.list_image);
            //voted_image=view.findViewById(R.id.voted_img);

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
        return anketlerList == null ? 0 : anketlerList.size();
    }

    public void setLoaded() {
        isLoading = false;
    }

    public void setLoading() {
        isLoading = true;
    }


}