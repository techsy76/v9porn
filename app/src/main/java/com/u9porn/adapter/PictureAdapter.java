package com.u9porn.adapter;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.orhanobut.logger.Logger;
import com.u9porn.R;
import com.u9porn.utils.GlideApp;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author flymegoc
 * @date 2018/1/25
 */

public class PictureAdapter extends PagerAdapter {

    private static final String TAG = PictureAdapter.class.getSimpleName();
    private List<String> imageList;
    private OnImageClickListener onImageClickListener;
    private Map<String, String> headers;

    public PictureAdapter(List<String> imageList, String referer) {
        // 用得多
        this(imageList, Collections.singletonMap("Referer", referer));
    }

    public PictureAdapter(List<String> imageList) {
        this.imageList = imageList;
    }

    public PictureAdapter(List<String> imageList, Map<String, String> headers) {
        this.imageList = imageList;
        this.headers = headers;
    }

    @Override
    public int getCount() {
        return imageList == null ? 0 : imageList.size();
    }

    @NonNull
    @Override
    public View instantiateItem(@NonNull ViewGroup container, final int position) {

        View contentView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_picture_adapter, container, false);

        PhotoView photoView = contentView.findViewById(R.id.photoView);
        final ProgressBar progressBar = contentView.findViewById(R.id.progressBar);
        //http://i.meizitu.net/2018/01/25c01.jpg
        String url = imageList.get(position);

        GlideApp.with(container)
                .load(buildGlideUrl(url))
                .transition(new DrawableTransitionOptions().crossFade(300)).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(photoView);

        // Now just add PhotoView to ViewPager and return it
        container.addView(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onImageClickListener != null) {
                    onImageClickListener.onImageClick(v, position);
                }
            }
        });
        photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onImageClickListener != null) {
                    onImageClickListener.onImageLongClick(v, position);
                }
                return true;
            }
        });
        Logger.t(TAG).d("instantiateItem");
        return contentView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        FrameLayout view = (FrameLayout) object;
        for (int i = 0; i < view.getChildCount(); i++) {
            View childView = view.getChildAt(i);
            if (childView instanceof PhotoView) {
                childView.setOnClickListener(null);
                childView.setOnLongClickListener(null);
                GlideApp.with(container).clear(childView);
                view.removeViewAt(i);
                Logger.t(TAG).d("clean photoView");
            }
        }
        container.removeView(view);
        Logger.t(TAG).d("destroyItem");
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public interface OnImageClickListener {
        void onImageClick(View view, int position);

        void onImageLongClick(View view, int position);
    }

    public void setOnImageClickListener(PictureAdapter.OnImageClickListener onImageClickListener) {
        this.onImageClickListener = onImageClickListener;
    }

    /**
     * build glide url 重写实现
     *
     * @param url
     * @return
     */
    protected GlideUrl buildGlideUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        } else {

            Uri parse = Uri.parse(url);
            LazyHeaders.Builder builder = new LazyHeaders.Builder()
                    .addHeader("Accept-Language", "zh-CN,zh;q=0.9,zh-TW;q=0.8")
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
            if (parse.getHost() != null) {
                builder.addHeader("Host", parse.getHost());
            }

            if (headers != null) {
                for (Map.Entry<String, String> h : headers.entrySet()) {
                    if (h.getKey() != null && h.getValue() != null) {
                        builder.addHeader(h.getKey(), h.getValue());
                    }
                }
            }

            return new GlideUrl(url, builder.build());
        }
    }
}
