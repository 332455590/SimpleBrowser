package com.renny.simplebrowser.view.page.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.renny.simplebrowser.R;
import com.renny.simplebrowser.business.base.BaseDialogFragment;
import com.renny.simplebrowser.business.helper.DeviceHelper;
import com.renny.simplebrowser.business.helper.Folders;
import com.renny.simplebrowser.business.helper.ImgHelper;
import com.renny.simplebrowser.business.helper.SearchHelper;
import com.renny.simplebrowser.business.helper.UIHelper;
import com.renny.simplebrowser.business.helper.Validator;
import com.renny.simplebrowser.business.log.Logs;
import com.renny.simplebrowser.business.toast.ToastHelper;
import com.renny.simplebrowser.globe.helper.FileUtil;
import com.renny.simplebrowser.business.task.SimpleTask;
import com.renny.simplebrowser.business.task.TaskHelper;
import com.renny.zxing.Activity.CaptureActivity;
import com.tencent.smtt.sdk.WebView;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.baseadapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;

/**
 * Created by Renny on 2018/1/10.
 */

public class HandlePictureDialog extends BaseDialogFragment {
    private RecyclerView mRecyclerView;
    private int LocationX = 0;
    private int LocationY = 0;
    List<String> listData = new ArrayList<>();
    private listAdapter mListAdapter;
    WebView mWebView;
    String ImgUrl;
    String QrCodeInfo;

    public static HandlePictureDialog getInstance(int locationX, int locationY, String extra) {
        HandlePictureDialog listDialog = new HandlePictureDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("intX", locationX);
        bundle.putInt("intY", locationY);
        bundle.putString("ImgUrl", extra);
        listDialog.setArguments(bundle);
        return listDialog;
    }

    public void show(FragmentManager manager) {
        show(manager, HandlePictureDialog.class.getSimpleName());
    }


    @Override
    protected void initParams(Bundle bundle) {
        LocationX = bundle.getInt("intX");
        LocationY = bundle.getInt("intY");
        ImgUrl = bundle.getString("ImgUrl");
        listData.add("保存图片");
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
    }

    @Override
    protected void initDialogStyle(Dialog dialog, Window window) {
        super.initDialogStyle(dialog, window);
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        lp.x = LocationX;
        lp.y = LocationY;
        lp.width = UIHelper.dip2px(100);
        lp.dimAmount = 0.0f;
        lp.windowAnimations = R.style.dialogAnim;
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_list;
    }


    public void bindView(View rootView, Bundle savedInstanceState) {
        mRecyclerView = rootView.findViewById(R.id.popup_list);
    }

    public void setWebView(WebView webView) {
        mWebView = webView;
    }

    public void afterViewBind(View rootView, Bundle savedInstanceState) {
        mListAdapter = new listAdapter(mRecyclerView);
        mListAdapter.setOnItemChildClickListener(new BGAOnItemChildClickListener() {
            @Override
            public void onItemChildClick(ViewGroup parent, View childView, int position) {
                itemClick(position);
            }
        });
        mListAdapter.setData(listData);
        mRecyclerView.setAdapter(mListAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        TaskHelper.submitTask("qr code",new SimpleTask<File>() {
            @Override
            public File onBackground() throws Exception {
                File sourceFile = ImgHelper.syncLoadFile(ImgUrl);
                File file = Folders.temp.newTempFile(Validator.getNameFromUrl(ImgUrl), ".jpeg");
                FileUtil.copyFile(sourceFile, file);
                return file;
            }

            @Override
            public void onComplete(File file) {
                if (file != null && file.exists()) {
                    QrCodeInfo = CaptureActivity.handleResult(file.getPath());
                    if (!TextUtils.isEmpty(QrCodeInfo)) {
                        mListAdapter.addLastItem("识别图中二维码");
                    }
                }
            }
        });

    }

    private void itemClick(int position) {
        if (position == 0 && !TextUtils.isEmpty(ImgUrl)) {
            downLoad(ImgUrl);
        } else if (position == 1 && !TextUtils.isEmpty(QrCodeInfo)) {
            final String content = QrCodeInfo;
            TextView textView = (TextView) (UIHelper.inflaterLayout(getActivity(), R.layout.item_textview));
            SpannableStringBuilder ssb = new SpannableStringBuilder(content);
            ssb.setSpan(new UnderlineSpan(), 0, content.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            textView.setText(ssb);
            if (Validator.checkUrl(content)) {
                new AlertDialog.Builder(getContext())
                        .setView(textView)
                        .setNegativeButton("复制", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UIHelper.clipContent(content);
                                dismiss();
                            }
                        })
                        .setPositiveButton("直接访问", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mWebView.loadUrl(content);
                                dismiss();
                            }
                        })
                        .show();
            } else {
                new AlertDialog.Builder(getContext())
                        .setView(textView)
                        .setMessage(content)
                        .setNegativeButton("复制", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UIHelper.clipContent(content);
                                dismiss();
                            }
                        })
                        .setPositiveButton("搜索", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mWebView.loadUrl(SearchHelper.buildSearchUrl(content));
                                dismiss();
                            }
                        })
                        .show();

            }
        }
    }

    private void downLoad(final String imgUrl) {
        TaskHelper.submitTask("保存",new SimpleTask<File>() {
            @Override
            public File onBackground() throws Exception {
                File sourceFile = ImgHelper.syncLoadFile(imgUrl);
                File file = Folders.Camera.getPublicFile(Environment.DIRECTORY_DCIM, Validator.getNameFromUrl(imgUrl), ".jpg");
                FileUtil.copyFile(sourceFile, file);
                Logs.common.d("getPath:" + file.getAbsolutePath());
                return file;
            }

            @Override
            public void onComplete(final File file) {
                if (file != null && file.exists()) {
                    try {
                        MediaStore.Images.Media.insertImage(getContext().getContentResolver(), file.getAbsolutePath(), "title", "description");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    new AlertDialog.Builder(getContext())
                            .setMessage("保存成功")
                            .setPositiveButton("查看", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dismiss();
                                    DeviceHelper.openFile(getContext(), file);
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dismiss();
                                }
                            })
                            .show();
                } else {
                    dismiss();
                    ToastHelper.makeToast("保存失败");
                }
            }
        });

    }


    public static class listAdapter extends BGARecyclerViewAdapter<String> {
        listAdapter(RecyclerView recyclerView) {
            super(recyclerView, R.layout.item_popup_list);
        }

        @Override
        public void setItemChildListener(final BGAViewHolderHelper helper, int viewType) {
            helper.setItemChildClickListener(R.id.item_title);
        }

        @Override
        public void fillData(BGAViewHolderHelper helper, int position, String model) {
            TextView textView = helper.getView(R.id.item_title);
            textView.setText(model);
        }
    }

}

