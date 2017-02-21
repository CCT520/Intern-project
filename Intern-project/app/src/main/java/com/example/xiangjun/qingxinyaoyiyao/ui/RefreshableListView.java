package com.example.xiangjun.qingxinyaoyiyao.ui;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.xiangjun.qingxinyaoyiyao.R;
import com.example.xiangjun.qingxinyaoyiyao.function.ConnectRequest;
import com.example.xiangjun.qingxinyaoyiyao.function.QuicklyGetPageLinkItemAsyncTask;
import com.example.xiangjun.qingxinyaoyiyao.function.RequestData;
import com.example.xiangjun.qingxinyaoyiyao.function.RequestKind;

public class RefreshableListView extends ListView implements OnScrollListener {

    private final static int RELEASE_To_REFRESH = 0;// 下拉过程的状态值
    private final static int PULL_To_REFRESH = 1; // 从下拉返回到不刷新的状态值
    private final static int REFRESHING = 2;// 正在刷新的状态值
    private final static int DONE = 3;
    private final static int LOADING = 4;
    private final static int PULL_To_LOAD_MORE = 5;
    private final static int LOADINGMORE = 6;

    // 实际的padding的距离与界面上偏移距离的比例
    private final static int RATIO = 3;
    private LayoutInflater inflater;

    // ListView头部下拉刷新的布局
    private LinearLayout headerView;
    private TextView lvHeaderTipsTv;
    private TextView lvHeaderLastUpdatedTv;
    private ImageView lvHeaderArrowIv;
    private ProgressBar lvHeaderProgressBar;

    //ListView底部上拉加载的布局
    private LinearLayout footerView;
    private TextView lvfooterTipsTv;
    private ProgressBar lvFooterProgressBar;

    // 定义头部下拉刷新的布局的高度
    private int headerContentHeight;
    private int footerContentHeight;

    private RotateAnimation animation;
    private RotateAnimation reverseAnimation;

    private int startY;
    private int state;
    private boolean isBack;

    // 用于保证startY的值在一个完整的touch事件中只被记录一次
    private boolean isRecored;
    private boolean loadingIsRedcorded;

    private OnRefreshListener refreshListener;

    private boolean isRefreshable;
    private boolean isLoadable;

    private MydeviceListAdapter thisMydeviceAdapter;
    private FrameLibAdapter thisFrameLibAdapter;
    private DeployBatchOfDeviceSubListAdapter thisDeployBatchOfDeviceSubListAdapter;
    private AddReplyPageFrameAdapter thisAddReplyPageFrameAdapter;
    private QuicklyGetPageLinkListViewAdapter thisQuicklyGetPageLinkListViewAdapter;

    private String token;
    private RefreshableListView myDevicelv;
    private MainFrame thisFrame;

    private RefreshableListView frameLibLv;


    private RefreshableListView deployBatchOfDeviceSubList;
    private DeployBatchOfDeviceFrame thisDeployBatchOfDeviceFrame;
    private DeployBatchOfDeviceSegment deployBatchOfDeviceSegment;
    private Button chooseAllBtn;
    private Button startDeployBtn;
    private String frameId;

    private RefreshableListView addReplyPagelv;
    private AddReplyPageFrame thisAddReplyPageFrame;
    private String[] pagesId;
    private Button chooseBtn;

    private String quicklyGetPageLinkURL;
    private RefreshableListView quicklyGetPageLinkList;
    private GridView quicklyGetPageLinkGridView;
    private QuicklyGetPageLinkGridViewAdapter quicklyGetPageLinkGridViewAdapter;
    private Button finishPageLinkBtn;
    private QuicklyGetPageLinkFrame thisQuicklyGetPageLinkFrame;
    private String start_key;
    private String count;

    private Parcelable myDeviceStateBeforeRefreshing = null;
    private Parcelable framelibStateBeforeRefreshing = null;
    private Parcelable deployBatchOfDeviceSubListStateBeforeRefreshing = null;
    private Parcelable addReplyPageFrameStateBeforeRefreshing = null;
    private Parcelable quicklyGetPageLinkListViewStateBeforeRefreshing = null;

    public Parcelable getMyDeviceStateBeforeRefreshing() {
        return myDeviceStateBeforeRefreshing;
    }

    public void setMyDeviceStateBeforeRefreshing(Parcelable myDeviceStateBeforeRefreshing) {
        this.myDeviceStateBeforeRefreshing = myDeviceStateBeforeRefreshing;
    }

    public Parcelable getFramelibStateBeforeRefreshing() {
        return framelibStateBeforeRefreshing;
    }

    public void setFramelibStateBeforeRefreshing(Parcelable framelibStateBeforeRefreshing) {
        this.framelibStateBeforeRefreshing = framelibStateBeforeRefreshing;
    }

    public Parcelable getDeployBatchOfDeviceSubListStateBeforeRefreshing() {
        return deployBatchOfDeviceSubListStateBeforeRefreshing;
    }

    public void setDeployBatchOfDeviceSubListStateBeforeRefreshing(Parcelable deployBatchOfDeviceSubListStateBeforeRefreshing) {
        this.deployBatchOfDeviceSubListStateBeforeRefreshing = deployBatchOfDeviceSubListStateBeforeRefreshing;
    }

    public Parcelable getAddReplyPageFrameStateBeforeRefreshing() {
        return addReplyPageFrameStateBeforeRefreshing;
    }

    public void setAddReplyPageFrameStateBeforeRefreshing(Parcelable addReplyPageFrameStateBeforeRefreshing) {
        this.addReplyPageFrameStateBeforeRefreshing = addReplyPageFrameStateBeforeRefreshing;
    }

    public Parcelable getQuicklyGetPageLinkListViewStateBeforeRefreshing() {
        return quicklyGetPageLinkListViewStateBeforeRefreshing;
    }

    public void setQuicklyGetPageLinkListViewStateBeforeRefreshing(Parcelable quicklyGetPageLinkListViewStateBeforeRefreshing) {
        this.quicklyGetPageLinkListViewStateBeforeRefreshing = quicklyGetPageLinkListViewStateBeforeRefreshing;
    }

    public FrameLibAdapter getThisFrameLibAdapter() {
        return thisFrameLibAdapter;
    }

    public DeployBatchOfDeviceSubListAdapter getThisDeployBatchOfDeviceSubListAdapter() {
        return thisDeployBatchOfDeviceSubListAdapter;
    }

    public AddReplyPageFrameAdapter getThisAddReplyPageFrameAdapter() {
        return thisAddReplyPageFrameAdapter;
    }

    public QuicklyGetPageLinkListViewAdapter getThisQuicklyGetPageLinkListViewAdapter() {
        return thisQuicklyGetPageLinkListViewAdapter;
    }

    public QuicklyGetPageLinkGridViewAdapter getQuicklyGetPageLinkGridViewAdapter() {
        return quicklyGetPageLinkGridViewAdapter;
    }

    public RefreshableListView(Context context) {
        super(context);
        init(context);
    }

    public RefreshableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Button getChooseBtn() {
        return chooseBtn;
    }

    public MydeviceListAdapter getThisMydeviceAdapter() {
        return thisMydeviceAdapter;
    }

    private void init(Context context) {
        //setCacheColorHint(context.getResources().getColor(Color.TRANSPARENT));
        inflater = LayoutInflater.from(context);
        headerView = (LinearLayout) inflater.inflate(R.layout.lv_header, null);
        footerView = (LinearLayout) inflater.inflate(R.layout.lv_footer, null);
        lvHeaderTipsTv = (TextView) headerView
                .findViewById(R.id.lvHeaderTipsTv);
        lvHeaderLastUpdatedTv = (TextView) headerView
                .findViewById(R.id.lvHeaderLastUpdatedTv);

        lvHeaderArrowIv = (ImageView) headerView
                .findViewById(R.id.lvHeaderArrowIv);

        lvfooterTipsTv = (TextView) footerView
                .findViewById(R.id.lvFooterTipsTv);


        // 设置下拉刷新图标的最小高度和宽度
        lvHeaderArrowIv.setMinimumWidth(70);
        lvHeaderArrowIv.setMinimumHeight(50);

        lvHeaderProgressBar = (ProgressBar) headerView
                .findViewById(R.id.lvHeaderProgressBar);
        lvFooterProgressBar = (ProgressBar) footerView
                .findViewById(R.id.lvFooterProgressBar);
        measureView(headerView);
        headerContentHeight = headerView.getMeasuredHeight();
        footerContentHeight = footerView.getMeasuredHeight();
        // 设置内边距，正好距离顶部为一个负的整个布局的高度，正好把头部隐藏
        headerView.setPadding(0, -1 * headerContentHeight, 0, 0);
        footerView.setPadding(0, -1 * footerContentHeight, 0, 0);
        // 重绘一下
        headerView.invalidate();
        footerView.invalidate();
        // 将下拉刷新的布局加入ListView的顶部
        addHeaderView(headerView, null, false);
        addFooterView(footerView, null, true);
        // 设置滚动监听事件
        setOnScrollListener(this);

        // 设置旋转动画事件
        animation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(250);
        animation.setFillAfter(true);

        reverseAnimation = new RotateAnimation(-180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseAnimation.setInterpolator(new LinearInterpolator());
        reverseAnimation.setDuration(200);
        reverseAnimation.setFillAfter(true);

        // 一开始的状态就是下拉刷新完的状态，所以为DONE
        state = DONE;
        // 是否正在刷新
        isRefreshable = false;
        isLoadable = false;

        lvfooterTipsTv.setOnClickListener(new loadingMoreListener());
    }

    public class loadingMoreListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            lvFooterProgressBar.setVisibility(VISIBLE);
            lvfooterTipsTv.setText("加载中...");
            loadMore();
        }
    }

    public void loadMore() {


        if (getThisFrameLibAdapter() == null && getThisDeployBatchOfDeviceSubListAdapter() == null && getThisAddReplyPageFrameAdapter() == null && getThisQuicklyGetPageLinkListViewAdapter() == null) {//如果传进来的是mydeviceAdapter

            boolean hasFinishedLoading = getThisMydeviceAdapter().isHasFinishedLoading();
            if (hasFinishedLoading == true) {
                lvFooterProgressBar.setVisibility(GONE);
                lvfooterTipsTv.setText("已经全部加载完毕");
            } else {

                int lastLoadingMaxIndex = getThisMydeviceAdapter().getLastLoadingMaxIndex();
                RequestData myDeviceRequestData = new RequestData(String.valueOf(lastLoadingMaxIndex) + 1, "20", null, null, null, "2", null, null, null, null, null, null, null);
                ConnectRequest myDeviceConnectrequest = new ConnectRequest(token, myDevicelv, getThisMydeviceAdapter(), thisFrame);

                myDeviceConnectrequest.RequestAPI(RequestKind.SearchDevice, myDeviceRequestData);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lvFooterProgressBar.setVisibility(GONE);
                lvfooterTipsTv.setText("点击加载更多");
            }

        } else if (getThisMydeviceAdapter() == null && getThisDeployBatchOfDeviceSubListAdapter() == null && getThisAddReplyPageFrameAdapter() == null && getThisQuicklyGetPageLinkListViewAdapter() == null) {//如果传进来的是frameLibAdapter

            boolean hasFinishedLoading = getThisFrameLibAdapter().isHasFinishedLoading();
            if (hasFinishedLoading) {
                lvFooterProgressBar.setVisibility(GONE);
                lvfooterTipsTv.setText("已经全部加载完毕");
            } else {

                RequestData frameLibRequestData = new RequestData(String.valueOf(getThisFrameLibAdapter().getLastLoadingMaxIndex() + 1), "20", null, null, null, "2", null, null, null, null, null, null, null);
                ConnectRequest frameLibConnectrequest = new ConnectRequest(token, getThisFrameLibAdapter(), frameLibLv, thisFrame);

                frameLibConnectrequest.RequestAPI(RequestKind.SearchPageList, frameLibRequestData);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lvFooterProgressBar.setVisibility(GONE);
                lvfooterTipsTv.setText("点击加载更多");
            }
        } else if (getThisMydeviceAdapter() == null && getThisFrameLibAdapter() == null && getThisAddReplyPageFrameAdapter() == null && getThisQuicklyGetPageLinkListViewAdapter() == null) {

            boolean hasFinishedLoading = getThisDeployBatchOfDeviceSubListAdapter().isHasFinishedLoading();
            if (hasFinishedLoading) {
                lvFooterProgressBar.setVisibility(GONE);
                lvfooterTipsTv.setText("已经全部加载完毕");
            } else {
                RequestData deployOnDeviceRequestData = new RequestData(String.valueOf(getThisDeployBatchOfDeviceSubListAdapter().getLastLoadingMaxIndex() + 1), "20", null, null, null, "2", null, null, null, null, null, null, null);
                ConnectRequest deployOnDeviceConnectrequest = new ConnectRequest(token, deployBatchOfDeviceSubList, getThisDeployBatchOfDeviceSubListAdapter(), thisDeployBatchOfDeviceFrame, deployBatchOfDeviceSegment,
                        chooseAllBtn, startDeployBtn, frameId);

                deployOnDeviceConnectrequest.RequestAPI(RequestKind.SearchDevice, deployOnDeviceRequestData);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lvFooterProgressBar.setVisibility(GONE);
                lvfooterTipsTv.setText("点击加载更多");
            }
        } else if (getThisMydeviceAdapter() == null && getThisFrameLibAdapter() == null && getThisDeployBatchOfDeviceSubListAdapter() == null && getThisQuicklyGetPageLinkListViewAdapter() == null) {

            boolean hasFinishedLoading = getThisAddReplyPageFrameAdapter().isHasFinishedLoading();
            if (hasFinishedLoading) {
                lvFooterProgressBar.setVisibility(GONE);
                lvfooterTipsTv.setText("已经全部加载完毕");
            } else {
                RequestData addReplyPageRequestData = new RequestData(String.valueOf(getThisDeployBatchOfDeviceSubListAdapter().getLastLoadingMaxIndex() + 1), "20", null, null, null, "2", null, null, null, null, null, null, null);
                ConnectRequest addReplyPageConnectrequest = new ConnectRequest(token, thisAddReplyPageFrameAdapter, addReplyPagelv, thisAddReplyPageFrame, pagesId, getChooseBtn());

                addReplyPageConnectrequest.RequestAPI(RequestKind.SearchPageList, addReplyPageRequestData);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lvFooterProgressBar.setVisibility(GONE);
                lvfooterTipsTv.setText("点击加载更多");
            }
        } else if (getThisMydeviceAdapter() == null && getThisFrameLibAdapter() == null && getThisDeployBatchOfDeviceSubListAdapter() == null && getThisAddReplyPageFrameAdapter() == null) {
            boolean hasFinishedLoading = getQuicklyGetPageLinkGridViewAdapter().isHasFinishedLoading();
            if (hasFinishedLoading) {
                lvFooterProgressBar.setVisibility(GONE);
                lvfooterTipsTv.setText("已经全部加载完毕");
            } else {
                QuicklyGetPageLinkItemAsyncTask quicklyGetPageLinkItemAsyncTask = new QuicklyGetPageLinkItemAsyncTask(quicklyGetPageLinkURL, quicklyGetPageLinkList,
                        quicklyGetPageLinkGridView, quicklyGetPageLinkGridViewAdapter, finishPageLinkBtn, thisQuicklyGetPageLinkFrame, start_key, count);
                quicklyGetPageLinkItemAsyncTask.execute();

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lvFooterProgressBar.setVisibility(GONE);
                lvfooterTipsTv.setText("点击加载更多");
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        // 当不滚动时
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
            // 判断是否滚动到底部
            if (view.getLastVisiblePosition() == view.getCount() - 1) {
                //加载更多功能的代码'
                state = PULL_To_LOAD_MORE;
                isLoadable = true;
                changeHeaderViewByState();
            }else
                isLoadable = false;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem == 0) {
            isRefreshable = true;
        } else {
            isRefreshable = false;

        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isRefreshable) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!isRecored) {
                        isRecored = true;
                        startY = (int) ev.getY();// 手指按下时记录当前位置
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (state != REFRESHING && state != LOADING) {
                        if (state == PULL_To_REFRESH) {
                            state = DONE;
                            changeHeaderViewByState();
                        }
                        if (state == RELEASE_To_REFRESH) {
                            state = REFRESHING;
                            changeHeaderViewByState();
                            onLvRefresh();
                        }
                    }
                    isRecored = false;
                    isBack = false;

                    break;

                case MotionEvent.ACTION_MOVE:
                    int tempY = (int) ev.getY();
                    if (!isRecored) {
                        isRecored = true;
                        startY = tempY;
                    }
                    if (state != REFRESHING && isRecored && state != LOADING) {
                        // 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动
                        // 可以松手去刷新了
                        if (state == RELEASE_To_REFRESH) {
                            setSelection(0);
                            // 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
                            if (((tempY - startY) / RATIO < headerContentHeight)// 由松开刷新状态转变到下拉刷新状态
                                    && (tempY - startY) > 0) {
                                state = PULL_To_REFRESH;
                                changeHeaderViewByState();
                            }
                            // 一下子推到顶了
                            else if (tempY - startY <= 0) {// 由松开刷新状态转变到done状态
                                state = DONE;
                                changeHeaderViewByState();
                            }
                        }
                        // 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
                        if (state == PULL_To_REFRESH) {
                            setSelection(0);
                            // 下拉到可以进入RELEASE_TO_REFRESH的状态
                            if ((tempY - startY) / RATIO >= headerContentHeight) {// 由done或者下拉刷新状态转变到松开刷新
                                state = RELEASE_To_REFRESH;
                                isBack = true;
                                changeHeaderViewByState();
                            }
                            // 上推到顶了
                            else if (tempY - startY <= 0) {// 由DOne或者下拉刷新状态转变到done状态
                                state = DONE;
                                changeHeaderViewByState();
                            }
                        }
                        // done状态下
                        if (state == DONE) {
                            if (tempY - startY > 0) {
                                state = PULL_To_REFRESH;
                                changeHeaderViewByState();
                            }
                        }
                        // 更新headView的size
                        if (state == PULL_To_REFRESH) {
                            headerView.setPadding(0, -1 * headerContentHeight
                                    + (tempY - startY) / RATIO, 0, 0);

                        }
                        // 更新headView的paddingTop
                        if (state == RELEASE_To_REFRESH) {
                            headerView.setPadding(0, (tempY - startY) / RATIO
                                    - headerContentHeight, 0, 0);
                        }

                    }
                    break;

                default:
                    break;
            }
        } else if (isLoadable) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!isRecored) {
                        isRecored = true;
                        startY = (int) ev.getY();// 手指按下时记录当前位置
                    }
                    break;
                case MotionEvent.ACTION_UP:

                    isRecored = false;

                    break;
                case MotionEvent.ACTION_MOVE:
                    int tempY = (int) ev.getY();
                    if (!isRecored) {
                        isRecored = true;
                        startY = tempY;
                    }
                    if (state == PULL_To_LOAD_MORE) {
                        if (tempY - startY <= 0) {
                            state = LOADINGMORE;
                            changeHeaderViewByState();
                            loadMore();
                        }
                    }

            }

        }
        return super.onTouchEvent(ev);
    }

    // 当状态改变时候，调用该方法，以更新界面
    private void changeHeaderViewByState() {
        switch (state) {
            case RELEASE_To_REFRESH:
                lvHeaderArrowIv.setVisibility(View.VISIBLE);
                lvHeaderProgressBar.setVisibility(View.GONE);
                lvHeaderTipsTv.setVisibility(View.VISIBLE);
                lvHeaderLastUpdatedTv.setVisibility(View.VISIBLE);

                lvHeaderArrowIv.clearAnimation();// 清除动画
                lvHeaderArrowIv.startAnimation(animation);// 开始动画效果

                lvHeaderTipsTv.setText("松开刷新");
                break;
            case PULL_To_REFRESH:
                lvHeaderProgressBar.setVisibility(View.GONE);
                lvHeaderTipsTv.setVisibility(View.VISIBLE);
                lvHeaderLastUpdatedTv.setVisibility(View.VISIBLE);
                lvHeaderArrowIv.clearAnimation();
                lvHeaderArrowIv.setVisibility(View.VISIBLE);
                // 是由RELEASE_To_REFRESH状态转变来的
                if (isBack) {
                    isBack = false;
                    lvHeaderArrowIv.clearAnimation();
                    lvHeaderArrowIv.startAnimation(reverseAnimation);

                    lvHeaderTipsTv.setText("下拉刷新");
                } else {
                    lvHeaderTipsTv.setText("下拉刷新");
                }
                break;

            case REFRESHING:

                headerView.setPadding(0, 0, 0, 0);

                lvHeaderProgressBar.setVisibility(View.VISIBLE);
                lvHeaderArrowIv.clearAnimation();
                lvHeaderArrowIv.setVisibility(View.GONE);
                lvHeaderTipsTv.setText("正在刷新...");
                lvHeaderLastUpdatedTv.setVisibility(View.VISIBLE);
                break;
            case DONE:
                headerView.setPadding(0, -1 * headerContentHeight, 0, 0);

                lvHeaderProgressBar.setVisibility(View.GONE);
                lvHeaderArrowIv.clearAnimation();
                lvHeaderArrowIv.setImageResource(R.drawable.arrow);
                lvHeaderTipsTv.setText("下拉刷新");
                lvHeaderLastUpdatedTv.setVisibility(View.VISIBLE);
                break;
            case PULL_To_LOAD_MORE:
                footerView.setPadding(0, -1 * footerContentHeight, 0, 0);
                lvFooterProgressBar.setVisibility(GONE);
                lvfooterTipsTv.setVisibility(VISIBLE);
                if(!lvfooterTipsTv.getText().equals("已经全部加载完毕"))
                    lvfooterTipsTv.setText("点击加载更多");
                break;
            case LOADINGMORE:
                lvFooterProgressBar.setVisibility(VISIBLE);
                lvfooterTipsTv.setVisibility(VISIBLE);
                lvfooterTipsTv.setText("加载中...");
                break;

        }
    }

    // 此方法直接照搬自网络上的一个下拉刷新的demo，此处是“估计”headView的width以及height
    private void measureView(View child) {
        ViewGroup.LayoutParams params = child.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0,
                params.width);
        int lpHeight = params.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
                    MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    public void setonRefreshListener(OnRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
        isRefreshable = true;
    }

    public interface OnRefreshListener {
        public void onRefresh();
    }

    public void onRefreshComplete() {
        state = DONE;
        Calendar calendar = Calendar.getInstance();

        //如果日期里有小于10的数字，则在前边加个0
        String month_transfered = "";
        if ((calendar.get(Calendar.MONTH) + 1) < 10)
            month_transfered = "0" + (calendar.get(Calendar.MONTH) + 1);
        else
            month_transfered = "" + (calendar.get(Calendar.MONTH) + 1);

        String day_transfered = "";
        if (calendar.get(Calendar.DAY_OF_MONTH) < 10)
            day_transfered = "0" + calendar.get(Calendar.DAY_OF_MONTH);
        else
            day_transfered = "" + calendar.get(Calendar.DAY_OF_MONTH);

        String hour_transfered = "";
        if (calendar.get(Calendar.HOUR_OF_DAY) < 10)
            hour_transfered = "0" + calendar.get(Calendar.HOUR_OF_DAY);
        else
            hour_transfered = "" + calendar.get(Calendar.HOUR_OF_DAY);

        String minute_transfered = "";
        if (calendar.get(Calendar.MINUTE) < 10)
            minute_transfered = "0" + calendar.get(Calendar.MINUTE);
        else
            minute_transfered = "" + calendar.get(Calendar.MINUTE);

        String second_transfered = "";
        if (calendar.get(Calendar.SECOND) < 10)
            second_transfered = "0" + calendar.get(Calendar.SECOND);
        else
            second_transfered = "" + calendar.get(Calendar.SECOND);

        String time_Chinese = calendar.get(Calendar.YEAR) + "年"
                + month_transfered + "月"//从0计算
                + day_transfered + "日 "
                + hour_transfered + ":"
                + minute_transfered + ":"
                + second_transfered;
        lvHeaderLastUpdatedTv.setText("最近更新:" + time_Chinese);
        changeHeaderViewByState();

        if (this.thisFrameLibAdapter == null && this.thisDeployBatchOfDeviceSubListAdapter == null && this.thisAddReplyPageFrameAdapter == null && this.thisQuicklyGetPageLinkListViewAdapter == null) {//如果传进来的是mydeviceAdapter

            //刷新则把原来的缓存删掉重新获取
            SharedPreferences pageNumberCacheSharedPreferences = thisFrame.getSharedPreferences("pageNumberCache",
                    Activity.MODE_PRIVATE);
            Boolean pageNumberIsCached = pageNumberCacheSharedPreferences.getBoolean("pageNumberIsCached", false);
            if (pageNumberIsCached == true) {
                pageNumberCacheSharedPreferences.edit().clear().commit();
            }

            RequestData myDeviceRequestData = new RequestData("0", "20", null, null, null, "2", null, null, null, null, null, null, null);
            ConnectRequest myDeviceConnectrequest = new ConnectRequest(token, myDevicelv, null, thisFrame);

            myDeviceConnectrequest.RequestAPI(RequestKind.SearchDevice, myDeviceRequestData);

            lvFooterProgressBar.setVisibility(GONE);
            lvfooterTipsTv.setText("点击加载更多");

        } else if (this.thisMydeviceAdapter == null && this.thisDeployBatchOfDeviceSubListAdapter == null && this.thisAddReplyPageFrameAdapter == null && this.thisQuicklyGetPageLinkListViewAdapter == null) {//如果传进来的是frameLibAdapter


            RequestData frameLibRequestData = new RequestData("0", "20", null, null, null, "2", null, null, null, null, null, null, null);
            ConnectRequest frameLibConnectrequest = new ConnectRequest(token, null, frameLibLv, thisFrame);

            frameLibConnectrequest.RequestAPI(RequestKind.SearchPageList, frameLibRequestData);

            lvFooterProgressBar.setVisibility(GONE);
            lvfooterTipsTv.setText("点击加载更多");

        } else if (this.thisMydeviceAdapter == null && this.thisFrameLibAdapter == null && this.thisAddReplyPageFrameAdapter == null && this.thisQuicklyGetPageLinkListViewAdapter == null) {

            RequestData deployOnDeviceRequestData = new RequestData("0", "20", null, null, null, "2", null, null, null, null, null, null, null);
            ConnectRequest deployOnDeviceConnectrequest = new ConnectRequest(token, deployBatchOfDeviceSubList, null, thisDeployBatchOfDeviceFrame, deployBatchOfDeviceSegment,
                    chooseAllBtn, startDeployBtn, frameId);

            deployOnDeviceConnectrequest.RequestAPI(RequestKind.SearchDevice, deployOnDeviceRequestData);

            lvFooterProgressBar.setVisibility(GONE);
            lvfooterTipsTv.setText("点击加载更多");
        } else if (this.thisMydeviceAdapter == null && this.thisFrameLibAdapter == null && this.thisDeployBatchOfDeviceSubListAdapter == null && this.thisQuicklyGetPageLinkListViewAdapter == null) {

            RequestData addReplyPageRequestData = new RequestData("0", "20", null, null, null, "2", null, null, null, null, null, null, null);
            ConnectRequest addReplyPageConnectrequest = new ConnectRequest(token, null, addReplyPagelv, thisAddReplyPageFrame, pagesId, this.chooseBtn);

            addReplyPageConnectrequest.RequestAPI(RequestKind.SearchPageList, addReplyPageRequestData);

            lvFooterProgressBar.setVisibility(GONE);
            lvfooterTipsTv.setText("点击加载更多");
        } else if (this.thisMydeviceAdapter == null && this.thisFrameLibAdapter == null && this.thisDeployBatchOfDeviceSubListAdapter == null && this.thisAddReplyPageFrameAdapter == null) {

            QuicklyGetPageLinkItemAsyncTask quicklyGetPageLinkItemAsyncTask = new QuicklyGetPageLinkItemAsyncTask(quicklyGetPageLinkURL, quicklyGetPageLinkList,
                    quicklyGetPageLinkGridView, quicklyGetPageLinkGridViewAdapter, finishPageLinkBtn, thisQuicklyGetPageLinkFrame, start_key, count);
            quicklyGetPageLinkItemAsyncTask.execute();

            lvFooterProgressBar.setVisibility(GONE);
            lvfooterTipsTv.setText("点击加载更多");
        }


    }

    public void setOnRefreshMyDeviceListCompleteParams(String token, RefreshableListView myDevicelv,
                                                       MainFrame thisFrame) {
        this.token = token;
        this.myDevicelv = myDevicelv;
        this.thisFrame = thisFrame;
    }

    public void setOnRefreshFrameLibListCompleteParams(String token, RefreshableListView frameLibLv, MainFrame thisFrame) {
        this.token = token;
        this.frameLibLv = frameLibLv;
        this.thisFrame = thisFrame;
    }

    public void setOnRefreshDeployOnDeviceListCompleteParams(String token, RefreshableListView deployBatchOfDeviceSubList,
                                                             DeployBatchOfDeviceFrame thisDeployBatchOfDeviceFrame, DeployBatchOfDeviceSegment deployBatchOfDeviceSegment,
                                                             Button chooseAllBtn, Button startDeployBtn, String frameId) {
        this.token = token;
        this.deployBatchOfDeviceSubList = deployBatchOfDeviceSubList;
        this.thisDeployBatchOfDeviceFrame = thisDeployBatchOfDeviceFrame;
        this.deployBatchOfDeviceSegment = deployBatchOfDeviceSegment;
        this.chooseAllBtn = chooseAllBtn;
        this.startDeployBtn = startDeployBtn;
        this.frameId = frameId;
    }

    public void setOnRefreshAddReplyPageListCompleteParams(String token, RefreshableListView addReplyPagelv,
                                                           AddReplyPageFrame thisAddReplyPageFrame, String[] pagesId,
                                                           Button chooseBtn) {
        this.token = token;
        this.addReplyPagelv = addReplyPagelv;
        this.thisAddReplyPageFrame = thisAddReplyPageFrame;
        this.pagesId = pagesId;
        this.chooseBtn = chooseBtn;
    }


    public void setOnRefreshQuicklyGetPageLinkListCompleteParams(String quicklyGetPageLinkURL, RefreshableListView quicklyGetPageLinkList, GridView quicklyGetPageLinkGridView,
                                                                 QuicklyGetPageLinkGridViewAdapter quicklyGetPageLinkGridViewAdapter, Button finishPageLinkBtn,
                                                                 QuicklyGetPageLinkFrame thisQuicklyGetPageLinkFrame, String start_key, String count) {
        this.quicklyGetPageLinkURL = quicklyGetPageLinkURL;
        this.quicklyGetPageLinkList = quicklyGetPageLinkList;
        this.quicklyGetPageLinkGridView = quicklyGetPageLinkGridView;
        this.quicklyGetPageLinkGridViewAdapter = quicklyGetPageLinkGridViewAdapter;
        this.finishPageLinkBtn = finishPageLinkBtn;
        this.thisQuicklyGetPageLinkFrame = thisQuicklyGetPageLinkFrame;
        this.start_key = start_key;
        this.count = count;
    }


    private void onLvRefresh() {
        if (refreshListener != null) {
            refreshListener.onRefresh();
        }
    }


    public void setAdapter(MydeviceListAdapter mydeviceListAdapter, FrameLibAdapter frameLibAdapter,
                           DeployBatchOfDeviceSubListAdapter deployBatchOfDeviceSubListAdapter,
                           AddReplyPageFrameAdapter addReplyPageFrameAdapter, QuicklyGetPageLinkListViewAdapter quicklyGetPageLinkListViewAdapter) {
        Calendar calendar = Calendar.getInstance();

        //如果日期里有小于10的数字，则在前边加个0
        String month_transfered = "";
        if ((calendar.get(Calendar.MONTH) + 1) < 10)
            month_transfered = "0" + (calendar.get(Calendar.MONTH) + 1);
        else
            month_transfered = "" + (calendar.get(Calendar.MONTH) + 1);

        String day_transfered = "";
        if (calendar.get(Calendar.DAY_OF_MONTH) < 10)
            day_transfered = "0" + calendar.get(Calendar.DAY_OF_MONTH);
        else
            day_transfered = "" + calendar.get(Calendar.DAY_OF_MONTH);

        String hour_transfered = "";
        if (calendar.get(Calendar.HOUR_OF_DAY) < 10)
            hour_transfered = "0" + calendar.get(Calendar.HOUR_OF_DAY);
        else
            hour_transfered = "" + calendar.get(Calendar.HOUR_OF_DAY);

        String minute_transfered = "";
        if (calendar.get(Calendar.MINUTE) < 10)
            minute_transfered = "0" + calendar.get(Calendar.MINUTE);
        else
            minute_transfered = "" + calendar.get(Calendar.MINUTE);

        String second_transfered = "";
        if (calendar.get(Calendar.SECOND) < 10)
            second_transfered = "0" + calendar.get(Calendar.SECOND);
        else
            second_transfered = "" + calendar.get(Calendar.SECOND);

        String time_Chinese = calendar.get(Calendar.YEAR) + "年"
                + month_transfered + "月"//从0计算
                + day_transfered + "日 "
                + hour_transfered + ":"
                + minute_transfered + ":"
                + second_transfered;
        lvHeaderLastUpdatedTv.setText("最近更新:" + time_Chinese);


        this.thisMydeviceAdapter = mydeviceListAdapter;
        this.thisFrameLibAdapter = frameLibAdapter;
        this.thisDeployBatchOfDeviceSubListAdapter = deployBatchOfDeviceSubListAdapter;
        this.thisAddReplyPageFrameAdapter = addReplyPageFrameAdapter;
        this.thisQuicklyGetPageLinkListViewAdapter = quicklyGetPageLinkListViewAdapter;

        if (frameLibAdapter == null && deployBatchOfDeviceSubListAdapter == null && addReplyPageFrameAdapter == null && quicklyGetPageLinkListViewAdapter == null)//如果传进来的是mydeviceAdapter
            super.setAdapter(mydeviceListAdapter);
        else if (mydeviceListAdapter == null && deployBatchOfDeviceSubListAdapter == null && addReplyPageFrameAdapter == null && quicklyGetPageLinkListViewAdapter == null)//如果传进来的是frameLibAdapter
            super.setAdapter(frameLibAdapter);
        else if (mydeviceListAdapter == null && frameLibAdapter == null && addReplyPageFrameAdapter == null && quicklyGetPageLinkListViewAdapter == null)//如果传进来的是deployBatchOfDeviceSubListAdapter
            super.setAdapter(deployBatchOfDeviceSubListAdapter);
        else if (mydeviceListAdapter == null && frameLibAdapter == null && deployBatchOfDeviceSubListAdapter == null && quicklyGetPageLinkListViewAdapter == null)
            super.setAdapter(addReplyPageFrameAdapter);
        else if (mydeviceListAdapter == null && frameLibAdapter == null && deployBatchOfDeviceSubListAdapter == null && addReplyPageFrameAdapter == null)
            super.setAdapter(quicklyGetPageLinkListViewAdapter);

    }


}
