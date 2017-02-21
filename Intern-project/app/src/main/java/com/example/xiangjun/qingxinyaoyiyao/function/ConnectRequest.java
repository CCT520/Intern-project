package com.example.xiangjun.qingxinyaoyiyao.function;

import android.app.AlertDialog;
import android.os.Handler;
import android.widget.Button;
import android.widget.ListView;

import com.example.xiangjun.qingxinyaoyiyao.ui.AddOrEditPageFrame;
import com.example.xiangjun.qingxinyaoyiyao.ui.AddReplyPageFrame;
import com.example.xiangjun.qingxinyaoyiyao.ui.AddReplyPageFrameAdapter;
import com.example.xiangjun.qingxinyaoyiyao.ui.DeployBatchOfDeviceFrame;
import com.example.xiangjun.qingxinyaoyiyao.ui.DeployBatchOfDeviceSegment;
import com.example.xiangjun.qingxinyaoyiyao.ui.DeployBatchOfDeviceSubListAdapter;
import com.example.xiangjun.qingxinyaoyiyao.ui.EditDeviceFrame;
import com.example.xiangjun.qingxinyaoyiyao.ui.EditDeviceFrameListAdapter;
import com.example.xiangjun.qingxinyaoyiyao.ui.FrameLibAdapter;
import com.example.xiangjun.qingxinyaoyiyao.ui.MainFrame;
import com.example.xiangjun.qingxinyaoyiyao.ui.MydeviceListAdapter;
import com.example.xiangjun.qingxinyaoyiyao.ui.RefreshableListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConnectRequest {


    private String token;
    private RefreshableListView myDevicelv;
    private MydeviceListAdapter myDeviceAdapter;
    private int relationIndex;
    private MainFrame thisMainFrame;
    private EditDeviceFrame thisEditDeviceFrame;
    private EditDeviceFrameListAdapter editDeviceFrameListAdapter;
    private ListView frameOnThisDeviceList;
    private boolean bothEdit;

    private FrameLibAdapter frameLibAdapter;
    private RefreshableListView frameLibLv;
    private List<Map<String, Object>> frameLibList,myDeviceList;

    private DeployBatchOfDeviceSubListAdapter deployBatchOfDeviceSubListAdapter;
    private RefreshableListView deployBatchOfDeviceSubList;
    private ArrayList<Map<String, Object>> deployBatchOfDeviceTextList;
    private DeployBatchOfDeviceFrame thisDeployBatchOfDeviceFrame;
    private DeployBatchOfDeviceSegment deployBatchOfDeviceSegment;
    private Button chooseAllBtn;
    private Button startDeployBtn;
    private String frameId;

    private String searchDeviceRequestType="";
    private String searchPageNumberRequestType="";
    private String searchAllPageRequestType="";
    private String deployPageOnDeviceType="";
    private int deviceIndexInList;
    private int deploySuccessfullyNumber;

    private int deleteIndex;

    private AddReplyPageFrameAdapter addReplyPageFrameAdapter;
    private RefreshableListView addReplyPagelv;
    private List<Map<String, Object>> addReplayPageList;
    private AddReplyPageFrame thisAddReplyPageFrame;
    private String[] relationalPagesId;
    private Button chooseBtn;

    private int framesNumberOnThisDevice;

    private android.os.Handler handler;
    private byte[] edittedThumbnailBytes;
    private String pageLink;
    private AddOrEditPageFrame thisAddOrEditPageFrame;
    private int editFrameIndex;







    //mainframe使用的获取全部设备的构造函数
    public ConnectRequest(String token, RefreshableListView myDevicelv, MydeviceListAdapter myDeviceAdapter,
                          MainFrame thisMainFrame) {
        this.token = token;
        this.myDevicelv = myDevicelv;
        this.myDeviceAdapter = myDeviceAdapter;
        this.thisMainFrame = thisMainFrame;
        this.searchDeviceRequestType="mainFrame";
        if(myDeviceAdapter==null){
            myDeviceList = new ArrayList<Map<String, Object>>();
            this.myDeviceAdapter = new MydeviceListAdapter(myDeviceList, this.thisMainFrame,token);
        }

    }

    //mainFrame中用到获取全部页面的构造函数(从头获取)
    public ConnectRequest(String token, FrameLibAdapter frameLibAdapter, RefreshableListView frameLibLv,
                          MainFrame thisMainFrame) {
        this.token = token;
        this.frameLibAdapter = frameLibAdapter;
        this.frameLibLv = frameLibLv;
        this.thisMainFrame = thisMainFrame;
        this.searchAllPageRequestType="mainFrame";
        if(this.frameLibAdapter==null){
            this.frameLibList=new ArrayList<Map<String, Object>>();
            this.frameLibAdapter= new FrameLibAdapter(this.frameLibList, this.frameLibLv,this.thisMainFrame,token);
        }

    }

    //SearchDeviceRequest中用到的获取一个设备对应的页面数量的构造函数(还在search device中)
    public ConnectRequest(String token, MydeviceListAdapter myDeviceAdapter, RefreshableListView myDevicelv, int relationIndex,
                          MainFrame thisMainFrame) {
        this.token = token;
        this.myDeviceAdapter = myDeviceAdapter;
        this.relationIndex = relationIndex;
        this.myDevicelv=myDevicelv;
        this.thisMainFrame=thisMainFrame;
        this.searchPageNumberRequestType="mainFrame";
    }

    //SearchPageListRequest中用到的获取具体有什么页面构造函数(根据id获取)
    public ConnectRequest(String token, EditDeviceFrameListAdapter editDeviceFrameListAdapter,ListView frameOnThisDeviceList,EditDeviceFrame editDeviceFrame) {
        this.token = token;
        this.editDeviceFrameListAdapter = editDeviceFrameListAdapter;
        this.frameOnThisDeviceList=frameOnThisDeviceList;
        this.thisEditDeviceFrame=editDeviceFrame;
    }

    //DeployBatchOfDeviceAdapter中用到的获取设备列表（“部署设备”页面）的构造函数
    public ConnectRequest(String token, RefreshableListView deployBatchOfDeviceSubList, DeployBatchOfDeviceSubListAdapter deployBatchOfDeviceSubListAdapter,
                          DeployBatchOfDeviceFrame thisDeployBatchOfDeviceFrame,DeployBatchOfDeviceSegment deployBatchOfDeviceSegment,Button chooseAllBtn,
                          Button startDeployBtn,String frameId) {
        this.token = token;
        this.deployBatchOfDeviceSubList = deployBatchOfDeviceSubList;
        this.deployBatchOfDeviceSubListAdapter = deployBatchOfDeviceSubListAdapter;
        this.thisDeployBatchOfDeviceFrame = thisDeployBatchOfDeviceFrame;
        this.deployBatchOfDeviceSegment=deployBatchOfDeviceSegment;
        this.chooseAllBtn=chooseAllBtn;
        this.startDeployBtn=startDeployBtn;
        this.frameId=frameId;

        if(this.deployBatchOfDeviceSubListAdapter==null){
            this.deployBatchOfDeviceTextList = new ArrayList<Map<String, Object>>();
            this.deployBatchOfDeviceSubListAdapter = new DeployBatchOfDeviceSubListAdapter(
                    deployBatchOfDeviceTextList, thisDeployBatchOfDeviceFrame);
            this.searchDeviceRequestType="deployBatchOfDevices";
        }

    }

    //SearchDeviceRequest中用到的获取页面数量的构造函数
    public ConnectRequest(String token, DeployBatchOfDeviceFrame thisDeployBatchOfDeviceFrame,DeployBatchOfDeviceSubListAdapter deployBatchOfDeviceSubListAdapter,
                          RefreshableListView deployBatchOfDeviceSubList, int relationIndex) {
        this.token = token;
        this.thisDeployBatchOfDeviceFrame=thisDeployBatchOfDeviceFrame;
        this.deployBatchOfDeviceSubListAdapter = deployBatchOfDeviceSubListAdapter;
        this.relationIndex = relationIndex;
        this.deployBatchOfDeviceSubList=deployBatchOfDeviceSubList;

        this.searchPageNumberRequestType="deployBatchOfDevices";
    }

    //在EditDeviceFrame里的EditDeviceRequest所需的构造函数
    public ConnectRequest(String token,EditDeviceFrame thisEditDeviceFrame,int deviceIndexInList,boolean bothEdit,int framesNumberOnThisDevice) {
        this.token=token;
        this.thisEditDeviceFrame = thisEditDeviceFrame;
        this.deviceIndexInList=deviceIndexInList;
        this.bothEdit=bothEdit;
        this.framesNumberOnThisDevice=framesNumberOnThisDevice;
    }

    //在FrameLibAdapter中用到的PageDeleteRequest所需要用的构造函数
    public ConnectRequest(String token,MainFrame thisMainFrame,FrameLibAdapter thisFrameLibAdapter,RefreshableListView thisFrameLiblv,int position) {
        this.token = token;
        this.thisMainFrame=thisMainFrame;
        this.frameLibAdapter=thisFrameLibAdapter;
        this.frameLibLv=thisFrameLiblv;
        this.deleteIndex=position;
    }

    //AddReplyPage中用到获取全部页面的构造函数(从头获取)
    public ConnectRequest(String token, AddReplyPageFrameAdapter addReplyPageFrameAdapter, RefreshableListView addReplyPagelv,
                          AddReplyPageFrame thisAddReplyPageFrame,String[] relationalPagesId,Button chooseBtn) {
        this.token = token;
        this.addReplyPageFrameAdapter = addReplyPageFrameAdapter;
        this.addReplyPagelv = addReplyPagelv;
        this.thisAddReplyPageFrame = thisAddReplyPageFrame;
        this.relationalPagesId=relationalPagesId;
        this.chooseBtn=chooseBtn;
        this.searchAllPageRequestType="addReplyPage";

        if(this.addReplyPageFrameAdapter==null){
            this.addReplayPageList=new ArrayList<Map<String, Object>>();
            this.addReplyPageFrameAdapter= new AddReplyPageFrameAdapter(this.addReplayPageList,this.thisAddReplyPageFrame);
        }

    }

    //EditDeviceFrame中用到的部署多个页面到单个设备的构造函数
    public ConnectRequest(String token, EditDeviceFrame thisEditDeviceFrame, int deviceIndexInList, int framesNumberOnThisDevice,boolean bothEdit) {
        this.token = token;
        this.thisEditDeviceFrame = thisEditDeviceFrame;
        this.deviceIndexInList = deviceIndexInList;
        this.framesNumberOnThisDevice = framesNumberOnThisDevice;
        this.bothEdit=bothEdit;
        deployPageOnDeviceType="manyPagesToOneDevice";

    }

    //DeployBatchOfDevice页面用到的批量部署设备到单个页面的构造函数
    public ConnectRequest(String token,DeployBatchOfDeviceSubListAdapter deployBatchOfDeviceSubListAdapter,
                          RefreshableListView deployBatchOfDeviceSubList, DeployBatchOfDeviceFrame thisDeployBatchOfDeviceFrame,android.os.Handler handler,
                          int deviceIndexInList) {
        this.token=token;
        this.deployBatchOfDeviceSubListAdapter = deployBatchOfDeviceSubListAdapter;
        this.deployBatchOfDeviceSubList = deployBatchOfDeviceSubList;
        this.thisDeployBatchOfDeviceFrame = thisDeployBatchOfDeviceFrame;
        this.deviceIndexInList = deviceIndexInList;
        this.handler=handler;
        deployPageOnDeviceType="onePageToManyDevices";
    }

    //SearchDeviceRequest中用到的部署设备时候查询已有id的请求的构造函数
    public ConnectRequest(String token,DeployBatchOfDeviceFrame thisDeployBatchOfDeviceFrame, DeployBatchOfDeviceSubListAdapter deployBatchOfDeviceSubListAdapter,
                          RefreshableListView deployBatchOfDeviceSubList,android.os.Handler handler, int relationIndex,String frameId) {
        this.token = token;
        this.thisDeployBatchOfDeviceFrame=thisDeployBatchOfDeviceFrame;
        this.deployBatchOfDeviceSubListAdapter = deployBatchOfDeviceSubListAdapter;
        this.deployBatchOfDeviceSubList = deployBatchOfDeviceSubList;
        this.handler=handler;
        this.relationIndex = relationIndex;
        this.frameId=frameId;
        this.searchPageNumberRequestType="deployBatchOfDevicePageIds";
    }

    //这是添加页面和编辑页面request用到的构造函数
    public ConnectRequest(String token, Handler handler) {
        this.token = token;
        this.handler = handler;
    }


    public void RequestAPI(RequestKind request_kind, RequestData rd) {
        switch (request_kind) {
            case SearchDevice:
                if(searchDeviceRequestType.equals("mainFrame"))
                    new SearchDeviceRequest(this.token, rd,myDeviceAdapter,myDevicelv,thisMainFrame);
                else
                    new SearchDeviceRequest(this.token,rd,deployBatchOfDeviceSubListAdapter,
                            deployBatchOfDeviceSubList,thisDeployBatchOfDeviceFrame,this.deployBatchOfDeviceSegment,
                            this.chooseAllBtn,this.startDeployBtn,this.frameId);
                break;
            case SearchPageList:
                if(rd.getType().equals("1"))//这是根据id读取页面
                    new SearchPageListRequest(this.token, rd,this.editDeviceFrameListAdapter,this.frameOnThisDeviceList,this.thisEditDeviceFrame);
                else {//根据begin和count读取（type=2）
                    if(searchAllPageRequestType.equals("mainFrame"))
                        new SearchPageListRequest(this.token,rd,this.frameLibAdapter,this.frameLibLv,this.thisMainFrame);
                    else if(searchAllPageRequestType.equals("addReplyPage"))
                        new SearchPageListRequest(this.token,rd,this.addReplyPageFrameAdapter,this.addReplyPagelv,this.thisAddReplyPageFrame,this.relationalPagesId,
                                this.chooseBtn);
                }

                break;
            case EditDevice:
                new EditDeviceRequest(this.token, rd,this.thisEditDeviceFrame,this.deviceIndexInList,this.bothEdit,this.framesNumberOnThisDevice);
                break;
            case RelationSearch:
                if(searchPageNumberRequestType.equals("mainFrame"))
                    new RelationSearchRequest(this.token, rd,myDeviceAdapter,myDevicelv,relationIndex,this.thisMainFrame);
                else if(searchPageNumberRequestType.equals("deployBatchOfDevices"))
                    new RelationSearchRequest(this.token, rd,thisDeployBatchOfDeviceFrame,deployBatchOfDeviceSubListAdapter,
                            deployBatchOfDeviceSubList,relationIndex);
                else if(searchPageNumberRequestType.equals("deployBatchOfDevicePageIds"))
                    new RelationSearchRequest(this.token,rd,this.thisDeployBatchOfDeviceFrame,this.deployBatchOfDeviceSubListAdapter,
                            this.deployBatchOfDeviceSubList,this.handler, this.relationIndex,this.frameId);
                break;
            case PageAdd:
                new PageAddRequest(this.token, rd,this.handler);
                break;
            case PageEdit:
                new PageEditRequest(this.token, rd,this.handler);
                break;
            case PageDelete:
                new PageDeleteRequest(this.token, rd,this.thisMainFrame,this.frameLibAdapter,this.frameLibLv,this.deleteIndex);
                break;
            case DeployPages:
                if(deployPageOnDeviceType.equals("manyPagesToOneDevice"))
                    new DeployPagesRequest(this.token, rd,this.thisEditDeviceFrame,this.deviceIndexInList,this.framesNumberOnThisDevice,this.bothEdit);
                else
                    new DeployPagesRequest(this.token, rd,this.deployBatchOfDeviceSubListAdapter,this.deployBatchOfDeviceSubList,this.thisDeployBatchOfDeviceFrame,this.handler,this.deviceIndexInList);
                break;
        }

    }
}
