package com.renny.simplebrowser.view.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.renny.simplebrowser.R;
import com.renny.simplebrowser.view.bean.db.entity.History;
import com.renny.simplebrowser.business.helper.Folders;

import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;



public class DownloadAdapter extends BGARecyclerViewAdapter<History>  {

    public DownloadAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.item_download);
    }

    @Override
    public void setItemChildListener(final BGAViewHolderHelper helper, int viewType) {
        helper.setItemChildClickListener(R.id.word);

    }

    @Override
    public void fillData(BGAViewHolderHelper helper, int position, History data) {
        TextView textView = helper.getView(R.id.word);
        final ImageView icon = helper.getView(R.id.icon);
        textView.setText(TextUtils.isEmpty(data.getTitle()) ? data.getUrl() : data.getTitle());
        String[] strings = data.getUrl().split("/");
        if (strings.length >= 2) {
            String host = strings[2];
            String path = Folders.icon.getFolder().getAbsolutePath() + "/" + host.replace(".", "") + ".jpg";
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            if (bitmap != null) {
                icon.setImageBitmap(bitmap);
            }
        }
    }


}