package com.bigniu.localimagesfinder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bigniu.localimagesfinder.finder.LocalImage;
import com.bigniu.localimagesfinder.finder.LocalImageFinder;
import com.bigniu.localimagesfinder.finder.OnImagesFindListener;
import com.bigniu.localimagesfinder.loader.LocalImageLoader;
import com.bigniu.localimagesfinder.loader.OnImageLoadListener;

import java.util.List;

/**
 * Created by bigniu on 16/9/17.
 */
public class ImgBrowsActivity extends Activity {

    private LocalImageLoader mLocalImageLoader;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_brows);

        int realHeight = getResources().getDimensionPixelSize(R.dimen.img_brows_real_height);
        LocalImageLoader.RealSize realSize = new LocalImageLoader.RealSize();
        realSize.setHeight(realHeight);

        mLocalImageLoader = new LocalImageLoader(getApplicationContext(), realSize);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_img_brows);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        LocalImageFinder finder = new LocalImageFinder(this);
        finder.execute(new OnImagesFindListener() {
            @Override
            public void onImagesFind(List<LocalImage> pLocalImageList) {
                if (pLocalImageList != null) {
                    mRecyclerView.setAdapter(new MyAdapter(pLocalImageList, getApplicationContext()));
                }
            }

        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()){
            mLocalImageLoader.stopLoader();
            mLocalImageLoader.clearCache();
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private List<LocalImage> mImageList;
        private LayoutInflater mLayoutInflater;

        MyAdapter(List<LocalImage> pImageList, Context pContext) {
            mImageList = pImageList;
            mLayoutInflater = LayoutInflater.from(pContext);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mLayoutInflater.inflate(R.layout.layout_image, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            holder.setImageView((ImageView) view.findViewById(R.id.iv_img_brows));
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            holder.setDatePosition(position);
            holder.getImageView().setImageBitmap(null);
            mLocalImageLoader.loadImage(mImageList.get(position), new OnImageLoadListener() {
                @Override
                public void onLoad(Bitmap pBitmap, Object pTag) {
                    if (holder.getDatePosition() == (int) pTag) {
                        holder.getImageView().setImageBitmap(pBitmap);
                    }
                }
            }, position);

        }

        @Override
        public int getItemCount() {
            return mImageList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            private ImageView mImageView;
            private int datePosition;

            public MyViewHolder(View itemView) {
                super(itemView);
            }

            public ImageView getImageView() {
                return mImageView;
            }

            public void setImageView(ImageView pImageView) {
                mImageView = pImageView;
            }

            public int getDatePosition() {
                return datePosition;
            }

            public void setDatePosition(int pDatePosition) {
                datePosition = pDatePosition;
            }
        }
    }
}
