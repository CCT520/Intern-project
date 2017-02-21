package com.example.xiangjun.qingxinyaoyiyao.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xiangjun.qingxinyaoyiyao.R;
import com.example.xiangjun.qingxinyaoyiyao.function.ConnectRequest;
import com.example.xiangjun.qingxinyaoyiyao.function.RequestData;
import com.example.xiangjun.qingxinyaoyiyao.function.RequestKind;
import com.example.xiangjun.qingxinyaoyiyao.function.UploadImageAsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddOrEditPageFrame extends Activity {

    private final static int EDIT_REQUEST_CODE = 4;
    private final static int EDIT_PAGENAME_RESULT_CODE = 5;
    private final static int EDIT_MAINTITLE_RESULT_CODE = 6;
    private final static int EDIT_SUBTITLE_RESULT_CODE = 7;
    private final static int EDIT_PAGELINK_RESULT_CODE = 8;
    private final static int THUMBNAIL_REQUEST_CODE = 9;

    private final static int EDITPAGE_RESULT_CODE = 14;
    private final static int UPLOAD_IMAGE_SUCCESS = 16;
    private final static int UPLOAD_IMAGE_FAILURE = 23;
    private final static int UPLOAD_IMAGE_NOTEXIST = 24;

    private final static int ADD_PAGE_SUCCESS = 17;
    private final static int ADD_PAGE_RESULT_CODE = 18;
    private final static int EDIT_PAGE_SUCCESS = 19;
    private final static int INVALID_FILE_SIZE = 20;
    private final static int EDIT_PAGE_FAILURE = 21;
    private final static int ADD_PAGE_FAILURE = 22;

    private final static int NETWORK_INNORMAL = 400;
    private final static int JSON_EXECPTION = 401;

    private List<Map<String, Object>> mData;

    private AddOrEditPageAdapter addOrEditPageAdapter;
    private ListView addOrEditPageList;

    private ActionBar bar;
    private Button saveBtn;

    private AddOrEditPageFrame thisAddOrEditPageFrame;


    private String newPageName;
    private String newMainTitle;
    private String newSubTitle;
    private String newPageLink;

    private byte[] originalThumbnailBytes;
    private Bitmap originalThumbnailBitmap;
    private String originalPageName;
    private String originalMainTitle;
    private String originalSubTitle;
    private String originalPageLink;
    private String originalIconUrl;
    private String pageId;
    private String token;
    private boolean thumbnailHasBeenEditted = false;
    private boolean thumbnailFromFrameLib = true;
    private int editIndex;

    private String operationType = "";

    private Handler listenUploadingImageHandler;
    private Handler listenAddingPageHandler;
    private Handler listenEditingPageHandler;

    private boolean addPageFramePageNameHasBeenEditted = false;
    private boolean addPageFrameMainTitleHasBeenEditted = false;
    private boolean addPageFrameSubTitleHasBeenEditted = false;
    private boolean addPageFramePageLinkHasBeenEditted = false;

    private AlertDialog isSavingDialog;


    public AddOrEditPageAdapter getAddOrEditPageAdapter() {
        return addOrEditPageAdapter;
    }

    public ListView getAddOrEditPageList() {
        return addOrEditPageList;
    }

    public String getOperationType() {
        return operationType;
    }

    public boolean isAddPageFramePageNameHasBeenEditted() {
        return addPageFramePageNameHasBeenEditted;
    }

    public boolean isAddPageFrameMainTitleHasBeenEditted() {
        return addPageFrameMainTitleHasBeenEditted;
    }

    public boolean isAddPageFrameSubTitleHasBeenEditted() {
        return addPageFrameSubTitleHasBeenEditted;
    }

    public boolean isAddPageFramePageLinkHasBeenEditted() {
        return addPageFramePageLinkHasBeenEditted;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_page_frame);

        thisAddOrEditPageFrame = this;

        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        operationType = intent.getStringExtra("operationType");
        pageId = intent.getStringExtra("pageId");
        editIndex = intent.getIntExtra("editIndex", 0);

        if (operationType.equals("editPage")) {
            originalThumbnailBytes = intent.getByteArrayExtra("originalThumbnailBytes");
            originalThumbnailBitmap = BitmapFactory.decodeByteArray(originalThumbnailBytes, 0,
                    originalThumbnailBytes.length);
            thumbnailHasBeenEditted = true;
            originalPageName = intent.getStringExtra("originalPageName");
            originalMainTitle = intent.getStringExtra("originalMainTitle");
            originalSubTitle = intent.getStringExtra("originalSubTitle");
            originalPageLink = intent.getStringExtra("originalPageLink");
            originalIconUrl = intent.getStringExtra("originalIconUrl");

        }


        //将自定义的列表内容放入list容器中
        mData = getData();
        //自定义的适配器
        addOrEditPageAdapter = new AddOrEditPageAdapter(this, mData);
        //定义一个listview并设置适配器
        addOrEditPageList = (ListView) findViewById(R.id.addOrEditPageList);
        addOrEditPageList.setAdapter(addOrEditPageAdapter);

        addOrEditPageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0://缩略图
                        Intent getThumbnailIntent = new Intent();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            getThumbnailIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                        } else {
                            getThumbnailIntent.setAction(Intent.ACTION_GET_CONTENT);
                        }
                        getThumbnailIntent.setType("image/*");
                        startActivityForResult(getThumbnailIntent, THUMBNAIL_REQUEST_CODE);
                        break;
                    case 1://页面名称
                        Intent editPageNameIntent = new Intent();
                        editPageNameIntent.putExtra("settingTitle", "页面名称");
                        if (getOperationType().equals("editPage")) {
                            if (!isAddPageFramePageNameHasBeenEditted()) {
                                editPageNameIntent.putExtra("settingEdit", originalPageName);
                                editPageNameIntent.putExtra("operationType", getOperationType());
                            } else {
                                editPageNameIntent.putExtra("settingEdit", newPageName);
                                editPageNameIntent.putExtra("operationType", getOperationType());
                            }
                        } else {
                            if (!isAddPageFramePageNameHasBeenEditted()) {
                                editPageNameIntent.putExtra("settingEdit", "未填写");
                                editPageNameIntent.putExtra("operationType", getOperationType());
                            } else {
                                editPageNameIntent.putExtra("settingEdit", newPageName);
                                editPageNameIntent.putExtra("operationType", getOperationType());
                            }

                        }
                        editPageNameIntent.setClass(thisAddOrEditPageFrame, AddOrEditPageInfoFrame.class);
                        thisAddOrEditPageFrame.startActivityForResult(editPageNameIntent, EDIT_REQUEST_CODE);
                        break;
                    case 2://主标题
                        Intent editMainTitleIntent = new Intent();
                        editMainTitleIntent.putExtra("settingTitle", "主标题");
                        if (getOperationType().equals("editPage")) {

                            if (!isAddPageFrameMainTitleHasBeenEditted()) {
                                editMainTitleIntent.putExtra("settingEdit", originalMainTitle);
                                editMainTitleIntent.putExtra("operationType", getOperationType());
                            } else {
                                editMainTitleIntent.putExtra("settingEdit", newMainTitle);
                                editMainTitleIntent.putExtra("operationType", getOperationType());
                            }
                        } else {
                            if (!isAddPageFrameMainTitleHasBeenEditted())
                                editMainTitleIntent.putExtra("settingEdit", "未填写");
                            else
                                editMainTitleIntent.putExtra("settingEdit", newMainTitle);
                        }
                        editMainTitleIntent.setClass(thisAddOrEditPageFrame, AddOrEditPageInfoFrame.class);
                        thisAddOrEditPageFrame.startActivityForResult(editMainTitleIntent, EDIT_REQUEST_CODE);
                        break;
                    case 3://副标题
                        Intent editSubTitleIntent = new Intent();
                        editSubTitleIntent.putExtra("settingTitle", "副标题");
                        if (getOperationType().equals("editPage")) {
                            if (!isAddPageFrameSubTitleHasBeenEditted()) {
                                editSubTitleIntent.putExtra("settingEdit", originalSubTitle);
                                editSubTitleIntent.putExtra("operationType", getOperationType());
                            } else {
                                editSubTitleIntent.putExtra("settingEdit", newSubTitle);
                                editSubTitleIntent.putExtra("operationType", getOperationType());
                            }
                        } else {
                            if (!isAddPageFrameSubTitleHasBeenEditted()) {
                                editSubTitleIntent.putExtra("settingEdit", "未填写");
                                editSubTitleIntent.putExtra("operationType", getOperationType());
                            } else {
                                editSubTitleIntent.putExtra("settingEdit", newSubTitle);
                                editSubTitleIntent.putExtra("operationType", getOperationType());
                            }
                        }
                        editSubTitleIntent.setClass(thisAddOrEditPageFrame, AddOrEditPageInfoFrame.class);
                        thisAddOrEditPageFrame.startActivityForResult(editSubTitleIntent, EDIT_REQUEST_CODE);
                        break;
                    case 4://页面链接
                        Intent editPageLinkIntent = new Intent();
                        editPageLinkIntent.putExtra("settingTitle", "页面链接");
                        if (getOperationType().equals("editPage")) {
                            if (!isAddPageFramePageLinkHasBeenEditted()) {
                                editPageLinkIntent.putExtra("settingEdit", originalPageLink);
                                editPageLinkIntent.putExtra("operationType", getOperationType());
                            } else {
                                editPageLinkIntent.putExtra("settingEdit", newPageLink);
                                editPageLinkIntent.putExtra("operationType", getOperationType());
                            }
                        } else {
                            if (!isAddPageFramePageLinkHasBeenEditted()) {
                                editPageLinkIntent.putExtra("settingEdit", "未填写");
                                editPageLinkIntent.putExtra("operationType", getOperationType());
                            } else {
                                editPageLinkIntent.putExtra("settingEdit", newPageLink);
                                editPageLinkIntent.putExtra("operationType", getOperationType());
                            }

                        }
                        editPageLinkIntent.putExtra("token", token);
                        editPageLinkIntent.setClass(thisAddOrEditPageFrame, AddOrEditPageLinkFrame.class);
                        thisAddOrEditPageFrame.startActivityForResult(editPageLinkIntent, EDIT_REQUEST_CODE);
                        break;

                }
            }
        });

        //自定义actionbar
        bar = getActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        bar.setCustomView(R.layout.actionbar_layout_add_or_edit_page);
        TextView addOrEditPageTitle = (TextView) bar.getCustomView().findViewById(R.id.addOrEditPageTitle);
        String title = intent.getStringExtra("title");
        addOrEditPageTitle.setText(title);
        saveBtn = (Button) bar.getCustomView().findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String pageLink = (String) getAddOrEditPageAdapter().getNamedItem(4, "pageLink");

                if (pageLink.equals("未填写")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(thisAddOrEditPageFrame);
                    builder.setMessage("您还未填写页面连接，请填写后再试！");
                    builder.setTitle("提示");
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    return;
                } else if ((!(pageLink.startsWith("http")) && !(pageLink.startsWith("https")))) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(thisAddOrEditPageFrame);
                    builder.setMessage("网络地址格式错误，请保证以http或https开头!");
                    builder.setTitle("提示");
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    return;
                } else if (addPageFramePageNameHasBeenEditted == false) {
                    if (!getOperationType().equals("editPage")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(thisAddOrEditPageFrame);
                        builder.setMessage("您还未填写页面名称，请填写后再试!");
                        builder.setTitle("提示");
                        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                        return;
                    } else {//如果是编辑页面且没有编辑过
                        if (originalPageName.length() > 15) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(thisAddOrEditPageFrame);
                            builder.setMessage("页面名称超过长度限制（15个字），请重新填写！");
                            builder.setTitle("提示");
                            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                            return;
                        }
                    }
                } else if (newPageName.length() > 15) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(thisAddOrEditPageFrame);
                    builder.setMessage("页面名称超过长度限制（15个字），请重新填写！");
                    builder.setTitle("提示");
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    return;
                } else if (addPageFrameMainTitleHasBeenEditted == false) {

                    if (!getOperationType().equals("editPage")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(thisAddOrEditPageFrame);
                        builder.setMessage("您还未填写主标题，请填写后再试!");
                        builder.setTitle("提示");
                        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                        return;
                    } else {
                        if (originalMainTitle.length() > 6) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(thisAddOrEditPageFrame);
                            builder.setMessage("主标题超过长度限制（6个字），请重新填写！");
                            builder.setTitle("提示");
                            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                            return;
                        }
                    }
                } else if (newMainTitle.length() > 6) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(thisAddOrEditPageFrame);
                    builder.setMessage("主标题超过长度限制（6个字），请重新填写！");
                    builder.setTitle("提示");
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    return;
                } else if (addPageFrameSubTitleHasBeenEditted == false) {

                    if (!getOperationType().equals("editPage")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(thisAddOrEditPageFrame);
                        builder.setMessage("您还未填写副标题，请填写后再试!");
                        builder.setTitle("提示");
                        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                        return;
                    } else {
                        if (originalSubTitle.length() > 7) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(thisAddOrEditPageFrame);
                            builder.setMessage("副标题超过长度限制（7个字），请重新填写！");
                            builder.setTitle("提示");
                            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                            return;
                        }
                    }
                } else if (newSubTitle.length() > 7) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(thisAddOrEditPageFrame);
                    builder.setMessage("副标题超过长度限制（7个字），请重新填写！");
                    builder.setTitle("提示");
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    return;
                } else if (thumbnailHasBeenEditted == false && !getOperationType().equals("editPage")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(thisAddOrEditPageFrame);
                    builder.setMessage("图片地址为空，请先选择图片!");
                    builder.setTitle("提示");
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    return;
                }

                saveBtn.setEnabled(false);

                AlertDialog.Builder builder = new AlertDialog.Builder(thisAddOrEditPageFrame);
                builder.setMessage("正在保存...");
                builder.setTitle("提示");
                builder.setCancelable(false);
                isSavingDialog = builder.create();
                isSavingDialog.show();

                Bitmap thumbnail = (Bitmap) getAddOrEditPageAdapter().getNamedItem(0, "thumbnail");//这是没有压缩过的
                int width = thumbnail.getWidth();
                int height = thumbnail.getHeight();

                if (width > 200 && height > 200) {
                    thumbnail = ThumbnailUtils.extractThumbnail(thumbnail, 200, 200);
                } else if (width > 200 && height <= 200) {
                    thumbnail = ThumbnailUtils.extractThumbnail(thumbnail, 200, height);
                } else if (width <= 200 && height > 200) {
                    thumbnail = ThumbnailUtils.extractThumbnail(thumbnail, width, 200);
                }

                //把获得的bitmap对象转换成byte数组以便传递
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.PNG, 100, baos);
                final byte[] thumbnailBytes = baos.toByteArray();


                final String pageName = (String) getAddOrEditPageAdapter().getNamedItem(1, "pageName");
                final String mainTitle = (String) getAddOrEditPageAdapter().getNamedItem(2, "pageMainTitleInfo");

                final String subTitle = (String) getAddOrEditPageAdapter().getNamedItem(3, "pageSubTitleInfo");

                listenAddingPageHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(thisAddOrEditPageFrame);
                        switch (msg.what) {
                            case ADD_PAGE_SUCCESS:
                                isSavingDialog.dismiss();
                                Bundle bundle = msg.getData();
                                long page_id = bundle.getLong("page_id");
                                String icon_url = bundle.getString("icon_url");
                                Intent intent = new Intent();
                                intent.putExtra("page_id", page_id);
                                intent.putExtra("icon_url", icon_url);
                                intent.putExtra("pageName", pageName);
                                intent.putExtra("mainTitle", mainTitle);
                                intent.putExtra("subTitle", subTitle);
                                intent.putExtra("pageLink", pageLink);
                                intent.putExtra("thumbnailBytes", thumbnailBytes);
                                thisAddOrEditPageFrame.setResult(ADD_PAGE_RESULT_CODE, intent);
                                thisAddOrEditPageFrame.finish();
                                break;
                            case INVALID_FILE_SIZE:
                                isSavingDialog.dismiss();
                                builder.setTitle("警告");
                                builder.setMessage("对不起，您上传的图片过大(不能超过200*200)，请重新上传!");
                                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                                break;
                            case ADD_PAGE_FAILURE:
                                isSavingDialog.dismiss();
                                builder.setTitle("警告");
                                builder.setMessage("哎呀，添加页面出错了，请重新检查后再操作!");
                                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                                break;
                            case NETWORK_INNORMAL:
                                isSavingDialog.dismiss();
                                builder.setTitle("警告");
                                builder.setMessage("网络异常，请检查网络!");
                                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                                break;
                            case JSON_EXECPTION:
                                isSavingDialog.dismiss();
                                builder.setTitle("警告");
                                builder.setMessage("JSON包解析出错，请联系开发人员！");
                                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                                break;
                        }

                    }

                };

                listenEditingPageHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(thisAddOrEditPageFrame);
                        switch (msg.what) {
                            case EDIT_PAGE_SUCCESS:
                                isSavingDialog.dismiss();
                                Bundle bundle = msg.getData();
                                String iconUrl = bundle.getString("icon_url");
                                if (iconUrl.equals(originalIconUrl)) {
                                    Intent intent = new Intent();
                                    intent.putExtra("newThumbnailBytesArray", originalThumbnailBytes);
                                    intent.putExtra("newIconUrl", originalIconUrl);
                                    intent.putExtra("newPageName", pageName);
                                    intent.putExtra("newMainTitle", mainTitle);
                                    intent.putExtra("newSubTitle", subTitle);
                                    intent.putExtra("newPageLink", pageLink);
                                    intent.putExtra("editIndex", editIndex);
                                    thisAddOrEditPageFrame.setResult(EDITPAGE_RESULT_CODE, intent);
                                    thisAddOrEditPageFrame.finish();
                                } else {
                                    Intent intent = new Intent();
                                    intent.putExtra("newThumbnailBytesArray", thumbnailBytes);
                                    intent.putExtra("newIconUrl", iconUrl);
                                    intent.putExtra("newPageName", pageName);
                                    intent.putExtra("newMainTitle", mainTitle);
                                    intent.putExtra("newSubTitle", subTitle);
                                    intent.putExtra("newPageLink", pageLink);
                                    intent.putExtra("editIndex", editIndex);
                                    thisAddOrEditPageFrame.setResult(EDITPAGE_RESULT_CODE, intent);
                                    thisAddOrEditPageFrame.finish();
                                }

                                break;
                            case INVALID_FILE_SIZE:
                                isSavingDialog.dismiss();
                                builder.setTitle("警告");
                                builder.setMessage("对不起，您上传的图片过大，请重新上传!");
                                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                                break;
                            case EDIT_PAGE_FAILURE:
                                isSavingDialog.dismiss();
                                builder.setTitle("警告");
                                builder.setMessage("哎呀，编辑页面出错了，请重新检查后再操作!");
                                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                                break;
                            case NETWORK_INNORMAL:
                                isSavingDialog.dismiss();
                                builder.setTitle("警告");
                                builder.setMessage("网络异常，请检查网络!");
                                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                                break;
                            case JSON_EXECPTION:
                                isSavingDialog.dismiss();
                                builder.setTitle("警告");
                                builder.setMessage("JSON包解析出错，请联系开发人员！");
                                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                                break;
                        }
                    }
                };

                listenUploadingImageHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {

                        AlertDialog.Builder builder=new AlertDialog.Builder(thisAddOrEditPageFrame);

                        switch (msg.what) {
                            case UPLOAD_IMAGE_SUCCESS:
                                if (operationType.equals("editPage")) {
                                    Bundle bundle = msg.getData();
                                    String icon_url = bundle.getString("icon_url");
                                    String[] editPageId = {pageId};
                                    RequestData editPageRequestData = new RequestData(null, null, null, null, editPageId, null, null, null, pageName, mainTitle, subTitle, pageLink, icon_url);
                                    ConnectRequest editPageConnectrequest = new ConnectRequest(token, listenEditingPageHandler);

                                    editPageConnectrequest.RequestAPI(RequestKind.PageEdit, editPageRequestData);


                                } else {
                                    Bundle bundle = msg.getData();
                                    String icon_url = bundle.getString("icon_url");

                                    RequestData addPageRequestData = new RequestData(null, null, null, null, null, null, null, null, pageName, mainTitle, subTitle, pageLink, icon_url);
                                    ConnectRequest addPageConnectrequest = new ConnectRequest(token, listenAddingPageHandler);

                                    addPageConnectrequest.RequestAPI(RequestKind.PageAdd, addPageRequestData);
                                }
                                break;

                            case UPLOAD_IMAGE_FAILURE:
                                isSavingDialog.dismiss();
                                builder.setTitle("提示");
                                builder.setMessage("图片上传失败");
                                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                                break;
                            case UPLOAD_IMAGE_NOTEXIST:
                                isSavingDialog.dismiss();
                                builder.setTitle("提示");
                                builder.setMessage("图片文件不存在，请重新选择！");
                                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                                break;
                        }

                    }
                };

                if (operationType.equals("editPage")) {
                    if (thumbnailFromFrameLib == false) {
                        String thumbnailPath = (String) getAddOrEditPageAdapter().getNamedItem(0, "thumbnailPath");
                        String URL = "http://mp.wxyaoyao.com/addon/storage?a=upload_image";

                        //需要先把图片上传到微信服务器
                        UploadImageAsyncTask uploadImageAsyncTask = new UploadImageAsyncTask(URL, thumbnailPath, listenUploadingImageHandler, thisAddOrEditPageFrame);
                        uploadImageAsyncTask.execute();
                    } else {//从主页面传过来且没有编辑过，就不用获取地址了

                        String[] editPageId = {pageId};
                        RequestData editPageRequestData = new RequestData(null, null, null, null, editPageId, null, null, null, pageName, mainTitle, subTitle, pageLink, originalIconUrl);
                        ConnectRequest editPageConnectrequest = new ConnectRequest(token, listenEditingPageHandler);

                        editPageConnectrequest.RequestAPI(RequestKind.PageEdit, editPageRequestData);
                    }
                } else {

                    String thumbnailPath = (String) getAddOrEditPageAdapter().getNamedItem(0, "thumbnailPath");
                    String URL = "http://mp.wxyaoyao.com/addon/storage?a=upload_image";

                    //需要先把图片上传到微信服务器
                    //UploadImageAsyncTask uploadImageAsyncTask = new UploadImageAsyncTask(URL, thumbnailPath,listenUploadingImageHandler);
                    UploadImageAsyncTask uploadImageAsyncTask = new UploadImageAsyncTask(URL, thumbnailPath, listenUploadingImageHandler, thisAddOrEditPageFrame);
                    uploadImageAsyncTask.execute();
                }


            }


        });


    }

    //自定义的list数据
    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("thumbnailTitle", "缩略图");
        if (getOperationType().equals("addPage")) {
            map1.put("thumbnailType", "drawable");
            map1.put("thumbnail", R.drawable.thumbnail);
        } else if (getOperationType().equals("editPage")) {
            map1.put("thumbnailType", "bitmap");
            map1.put("thumbnail", originalThumbnailBitmap);
        }
        map1.put("thumbnailArrow", R.drawable.right_arrow);
        list.add(map1);

        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("pageNameTitle", "页面名称");
        if (getOperationType().equals("addPage"))
            map2.put("pageName", "未填写");
        else if (getOperationType().equals("editPage"))
            map2.put("pageName", originalPageName);
        map2.put("pageNameArrow", R.drawable.right_arrow);
        list.add(map2);

        Map<String, Object> map3 = new HashMap<String, Object>();
        map3.put("pageMainTitle", "主标题");
        if (getOperationType().equals("addPage"))
            map3.put("pageMainTitleInfo", "未填写");
        else if (getOperationType().equals("editPage"))
            map3.put("pageMainTitleInfo", originalMainTitle);
        map3.put("pageMainTitleArrow", R.drawable.right_arrow);
        list.add(map3);

        Map<String, Object> map4 = new HashMap<String, Object>();
        map4.put("pageSubTitle", "副标题");
        if (getOperationType().equals("addPage"))
            map4.put("pageSubTitleInfo", "未填写");
        else if (getOperationType().equals("editPage"))
            map4.put("pageSubTitleInfo", originalSubTitle);
        map4.put("pageSubTitleArrow", R.drawable.right_arrow);
        list.add(map4);

        Map<String, Object> map5 = new HashMap<String, Object>();
        map5.put("pageLinkTitle", "页面链接");
        if (getOperationType().equals("addPage"))
            map5.put("pageLink", "未填写");
        else if (getOperationType().equals("editPage"))
            map5.put("pageLink", originalPageLink);
        map5.put("pageLinkArrow", R.drawable.right_arrow);
        list.add(map5);

        return list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_REQUEST_CODE) {
            switch (resultCode) {
                case EDIT_PAGENAME_RESULT_CODE:
                    Bundle newPageNameBundle = data.getExtras();
                    newPageName = newPageNameBundle.getString("edittedPageNameString");
                    getAddOrEditPageAdapter().setItem(1, "pageName", newPageName);
                    if (!newPageName.equals("未填写")) {//只有修改过后才重新设置
                        addPageFramePageNameHasBeenEditted = true;
                        getAddOrEditPageList().setAdapter(getAddOrEditPageAdapter());
                        saveBtn.setEnabled(true);
                    }
                    break;
                case EDIT_MAINTITLE_RESULT_CODE:
                    Bundle newMainTitleBundle = data.getExtras();
                    newMainTitle = newMainTitleBundle.getString("edittedMainTitleString");
                    getAddOrEditPageAdapter().setItem(2, "pageMainTitleInfo", newMainTitle);
                    if (!newMainTitle.equals("未填写")) {//只有修改过后才重新设置
                        addPageFrameMainTitleHasBeenEditted = true;
                        getAddOrEditPageList().setAdapter(getAddOrEditPageAdapter());
                        saveBtn.setEnabled(true);
                    }
                    break;
                case EDIT_SUBTITLE_RESULT_CODE:
                    Bundle newSubTitleBundle = data.getExtras();
                    newSubTitle = newSubTitleBundle.getString("edittedSubTitleString");
                    getAddOrEditPageAdapter().setItem(3, "pageSubTitleInfo", newSubTitle);
                    if (!newSubTitle.equals("未填写")) {//只有修改过后才重新设置
                        addPageFrameSubTitleHasBeenEditted = true;
                        getAddOrEditPageList().setAdapter(getAddOrEditPageAdapter());
                        saveBtn.setEnabled(true);
                    }
                    break;
                case EDIT_PAGELINK_RESULT_CODE:
                    Bundle newPageLinkBundle = data.getExtras();
                    newPageLink = newPageLinkBundle.getString("edittedPageLinkString");
                    getAddOrEditPageAdapter().setItem(4, "pageLink", newPageLink);
                    if (!newPageLink.equals("未填写")) {//只有修改过后才重新设置
                        addPageFramePageLinkHasBeenEditted = true;
                        getAddOrEditPageList().setAdapter(getAddOrEditPageAdapter());
                        saveBtn.setEnabled(true);
                    }
                    break;
            }
        } else if (requestCode == THUMBNAIL_REQUEST_CODE) {

            if (data != null) {
                Uri selectedContentURI = data.getData();

                if (selectedContentURI != null) {

                    //获取选择的图片地址
                    String[] pojo = {MediaStore.Images.Media.DATA};
                    Cursor cursor = managedQuery(selectedContentURI, pojo, null, null, null);
                    String selectedImgPath = "";
                    if (cursor != null) {
                        int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
                        cursor.moveToFirst();
                        selectedImgPath = cursor.getString(columnIndex);
                    }
                    ////////////////////////
                    // 读取uri所在的图片
                    try {
                        Bitmap thumbnail = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedContentURI);
                        getAddOrEditPageAdapter().setImage(thumbnail, selectedImgPath);
                        getAddOrEditPageList().setAdapter(getAddOrEditPageAdapter());
                        thumbnailHasBeenEditted = true;
                        thumbnailFromFrameLib = false;
                        saveBtn.setEnabled(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_page_frame, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
