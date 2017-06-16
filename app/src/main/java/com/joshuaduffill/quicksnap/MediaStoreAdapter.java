package com.joshuaduffill.quicksnap;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by Joshua on 28/04/2017.
 */

public class MediaStoreAdapter extends RecyclerView.Adapter<MediaStoreAdapter.ViewHolder> {

    private Cursor mMediaStoreCursor;
    private final Activity mActivity;
    private OnClickThumbnailListener mOnclickThumbnailListener;

    public interface OnClickThumbnailListener{
        void OnClickImage(Uri imageUri);
        void OnClickVideo(Uri videoUri);

    }

    public MediaStoreAdapter(Activity activity) {
        this.mActivity = activity;
        this.mOnclickThumbnailListener = (OnClickThumbnailListener)activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.media_image_view, parent, false);
        return new ViewHolder(view);
    }



    //pass the bitmaps to the imageView
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Uri imageUri = getUriFromMediaStore(position);

        if (imageUri != null){

            Glide.with(mActivity)
                    .load(getUriFromMediaStore(position))
//                    .centerCrop()
                    .into(holder.getmImageView());
        }else{
            // clear when no image is shown, don't use holder.imageView.setImageDrawable(null) to do the same
            Glide.clear(holder.getmImageView());
            holder.getmImageView().setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);

        Glide.clear(holder.getmImageView());
    }

    @Override
    public int getItemCount() {
        return (mMediaStoreCursor == null) ? 0 : mMediaStoreCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        private final ImageView mImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            mImageView = (ImageView) itemView.findViewById(R.id.mediastoreImageView);
            mImageView.setOnClickListener(this);
        }

        public ImageView getmImageView(){
            return mImageView;
        }

        @Override
        public void onClick(View view) {
            getOnClickUri(getAdapterPosition());
        }


        @Override
        public boolean onLongClick(View v) {
            getOnClickUri(getAdapterPosition());
            return true;
        }
    }

    private Cursor swapCursor(Cursor cursor){
        if (mMediaStoreCursor == cursor){
            return null;
        }
        Cursor oldCursor = mMediaStoreCursor;
        this.mMediaStoreCursor = cursor;
        if(cursor != null){
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }
    public void changeCursor(Cursor cursor){
        Cursor oldCursor = swapCursor(cursor);
        if(oldCursor != null){
            oldCursor.close();
        }
    }

    //not in use, using Glide instead
    private Bitmap getBitmapFromMediaStore(int position){
        int idIndex = mMediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns._ID);
        int mediaTypeIndex = mMediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE);

        mMediaStoreCursor.moveToPosition(position);
        switch (mMediaStoreCursor.getInt(mediaTypeIndex)){
            case MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE:
                return MediaStore.Images.Thumbnails.getThumbnail(
                     mActivity.getContentResolver(),
                        mMediaStoreCursor.getLong(idIndex),
                        MediaStore.Images.Thumbnails.MICRO_KIND,
                        null
                );
            case MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO:
                return MediaStore.Video.Thumbnails.getThumbnail(
                        mActivity.getContentResolver(),
                        mMediaStoreCursor.getLong(idIndex),
                        MediaStore.Video.Thumbnails.MICRO_KIND,
                        null
                );
            default:
                return null;

        }
    }

    //gives a Uri for GLIDE to use with each image data
    private Uri getUriFromMediaStore(int position){
        int dataIndex = mMediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);

        mMediaStoreCursor.moveToPosition(position);

        String dataString = mMediaStoreCursor.getString(dataIndex);
        Uri mediaUri = Uri.parse("file://" + dataString);
        return mediaUri;
    }

    //gets uri of clicked image
    private void getOnClickUri(int position){

        int mediaTypeIndex = mMediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE);
        int dataIndex = mMediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);

        //checks if file is video or image
        mMediaStoreCursor.moveToPosition(position);
        String dataString = mMediaStoreCursor.getString(dataIndex);
        Uri mediaUri = Uri.parse("file://" + dataString);

        switch (mMediaStoreCursor.getInt(mediaTypeIndex)){
            case MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE:
                mOnclickThumbnailListener.OnClickImage(mediaUri);
                break;
            case MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO:
                mOnclickThumbnailListener.OnClickVideo(mediaUri);
                break;
            default:
        }
    }
}
