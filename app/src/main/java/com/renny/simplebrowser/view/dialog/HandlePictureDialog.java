package com.renny.simplebrowser.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.renny.simplebrowser.R;
import com.renny.simplebrowser.business.base.BaseDialogFragment;
import com.renny.simplebrowser.business.base.CommonAdapter;
import com.renny.simplebrowser.business.base.ViewHolder;
import com.renny.simplebrowser.business.helper.Folders;
import com.renny.simplebrowser.business.helper.ImgHelper;
import com.renny.simplebrowser.business.helper.UIHelper;
import com.renny.simplebrowser.business.helper.Validator;
import com.renny.simplebrowser.business.log.Logs;
import com.renny.simplebrowser.business.toast.ToastHelper;
import com.renny.simplebrowser.business.webview.X5WebView;
import com.renny.simplebrowser.globe.helper.BitmapUtils;
import com.renny.simplebrowser.globe.helper.FileUtil;
import com.renny.simplebrowser.globe.task.ITaskWithResult;
import com.renny.simplebrowser.globe.task.TaskHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Renny on 2018/1/10.
 */

public class HandlePictureDialog extends BaseDialogFragment {
    private X5WebView mWebView;
    private RecyclerView mRecyclerView;
    private int LocationX = 0;
    private int LocationY = 0;
    final List<String> list = new ArrayList<>();
    private CommonAdapter<String> commonAdapter;
    private String result;
    private String extra;

    public static HandlePictureDialog getInstance(int locationX, int locationY, String extra) {
        HandlePictureDialog listDialog = new HandlePictureDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("intX", locationX);
        bundle.putInt("intY", locationY);
        bundle.putString("extra", extra);
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
        extra = bundle.getString("extra");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.popup_list;
    }


    public void bindView(View rootView, Bundle savedInstanceState) {
        mRecyclerView = rootView.findViewById(R.id.popup_list);

    }

    public String getResult() {
        return result;
    }

    public void setWebView(X5WebView webView) {
        mWebView = webView;
    }

    public void setShowZxing(String result) {
        this.result = result;
        Logs.base.d("识别图中二维码" + result);
        commonAdapter.addData("识别图中二维码");
        commonAdapter.notifyDataSetChanged();
    }

    public void afterViewBind(View rootView, Bundle savedInstanceState) {
        list.add("保存图片");
        commonAdapter = new CommonAdapter<String>(R.layout.item_popup_list, list) {
            @Override
            protected void convert(ViewHolder holder, final int position) {
                TextView tv = holder.getView(R.id.item_title);
                tv.setText(list.get(position));
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mItemClickListener.onItemClicked(position, view);
                    }
                });
            }
        };
        commonAdapter.setItemClickListener(new CommonAdapter.ItemClickListener() {
            @Override
            public void onItemClicked(int position, View view) {
                if (position == 0) {
                    downLoad(extra);
                } else if (position == 1) {
                    final String content = getResult();
                    if (Validator.checkUrl(content)) {
                        new AlertDialog.Builder(getActivity())
                                .setMessage(content)
                                .setPositiveButton("复制", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        UIHelper.clipContent(content);
                                    }
                                })
                                .setNegativeButton("直接访问", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mWebView.loadUrl(content);
                                    }
                                })
                                .show();
                        ;
                    } else {
                        new AlertDialog.Builder(getActivity())
                                .setMessage(content)
                                .setPositiveButton("复制", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        UIHelper.clipContent(content);
                                    }
                                })
                                .setNegativeButton("搜索", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mWebView.loadUrl("http://wap.baidu.com/s?wd=" + content);
                                    }
                                })
                                .show();

                    }
                }
                dismiss();
            }
        });
        mRecyclerView.setAdapter(commonAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

    }


    private void downLoad(final String imgUrl) {
        ToastHelper.makeToast("开始保存");
        TaskHelper.submitResult(new ITaskWithResult<File>() {
            @Override
            public File onBackground() throws Exception {
                return ImgHelper.syncLoadFile(imgUrl);
            }

            @Override
            public void onComplete(File sourceFile) {
                if (sourceFile != null && sourceFile.exists()) {
                    File file = Folders.gallery.getFile(System.currentTimeMillis() + ".jpg");
                    FileUtil.copyFile(sourceFile, file);
                    BitmapUtils.displayToGallery(getActivity(), file);
                    ToastHelper.makeToast("保存成功");

                    new AlertDialog.Builder(getContext())
                            .setMessage("保存成功")
                            .setPositiveButton("打开相册", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Intent.ACTION_PICK);
                                    intent.setType("image/*");
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                } else {
                    ToastHelper.makeToast("保存失败");
                }
            }
        });
    }

    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
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
        }
    }

}

