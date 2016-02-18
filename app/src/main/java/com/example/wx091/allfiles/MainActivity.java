package com.example.wx091.allfiles;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wx091.allfiles.Utils.FileUtil;
import com.example.wx091.allfiles.Utils.TimeUtil;
import com.example.wx091.allfiles.activity.PhotoActivity;
import com.example.wx091.allfiles.activity.VideoActivity;
import com.example.wx091.allfiles.activity.VideoView;
import com.example.wx091.allfiles.beans.File_Item;
import com.example.wx091.allfiles.beans.Fold_Group;
import com.example.wx091.allfiles.beans.IConstant;
import com.example.wx091.allfiles.database.DBManager;
import com.example.wx091.allfiles.dialog.CustomDialogWithEditText;
import com.example.wx091.allfiles.interfaces.ItemOptionInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements
        ExpandableListView.OnChildClickListener,
        ExpandableListView.OnGroupClickListener,
        ItemOptionInterface{
    private ExpandableListView expandableListView;
    private ArrayList<Fold_Group> groupList=new ArrayList<>();
    private ArrayList<List<File_Item>> childList=new ArrayList<>();
    private MyexpandableListAdapter adapter;
    int group,  child;
    File[] files;
    DBManager dbmgr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        expandableListView = (ExpandableListView) findViewById(R.id.expandablelist);
        dbmgr=new DBManager(this);
        initdata();
        adapter = new MyexpandableListAdapter(this);
        expandableListView.setAdapter(adapter);

        expandableListView.setOnChildClickListener(this);
        expandableListView.setOnGroupClickListener(this);
        registerForContextMenu(expandableListView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        dbmgr.closeDB();
    }
    //遍历两个文件夹，找出所有文件
    private void initdata() {
        for(int i=0; i< IConstant.FoldPath.length;i++){
            //get filesize
            Fold_Group g=new Fold_Group();
            g.name=IConstant.GroupName[i];
            File file = FileUtil.makeDir(IConstant.FoldPath[i]);
            files = file.listFiles();
            g.number=files.length;
            List<File_Item> list = new ArrayList<File_Item>();
            if(files != null && files.length != 0){
                for(int j=0;j<files.length;j++){
                    File_Item f=new File_Item();
                    f.name=files[j].getName();
                    f.space=FileUtil.getFileSpace(files[j]);
                    f.path=files[j].getAbsolutePath();
                    f.type=FileUtil.getFileType(files[j].getPath());
                    f.title=dbmgr.getTitle(f.name);
                    f.describe=dbmgr.getDescribe(f.name);
                    if(f.type.equals(getString(R.string.video))){
                        f.length=FileUtil.getVideoLength(files[j]);
                    }
                    g.space+=f.space;
                    list.add(f);
                }
            }
            g.space=(double)(Math.round( g.space*100)/100.0);
            groupList.add(g);
            childList.add(list);
            //get path name
        }
    }
    //右键菜单展示
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu_file_item, menu);
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
    }
    //右键功能实现
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        group = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        child = ExpandableListView.getPackedPositionChild(info.packedPosition);
        final File_Item f=childList.get(group).get(child);
        String s = item.getTitle().toString();
        if (s.equals(getString(R.string.item_menu_openwith))) {
            Intent openIntent = new Intent(Intent.ACTION_VIEW);
            if(f.type.equals(getString(R.string.video))){
                openIntent.setType("video/*");
            }else if(f.type.equals(getString(R.string.photo))){
                openIntent.setType("image/*");
            }
            Uri uri = Uri.fromFile(getFileStreamPath(f.name));
            openIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(openIntent, "选择打开方式"));
        } else if (s.equals(getString(R.string.item_menu_share))) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            if(f.type.equals(getString(R.string.video))){
                shareIntent.setType("video/*");
            }else if(f.type.equals(getString(R.string.photo))){
                shareIntent.setType("image/*");
            }
            Uri uri = Uri.fromFile(getFileStreamPath(f.name));
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(shareIntent, "请选择"));
        } else if (s.equals(getString(R.string.item_menu_nearby))) {
            Toast.makeText(MainActivity.this,"附近的文件",Toast.LENGTH_SHORT).show();
        } else if (s.equals(getString(R.string.item_menu_upload))) {
            Toast.makeText(MainActivity.this,"点击了上传按钮",Toast.LENGTH_SHORT).show();
        } else if (s.equals(getString(R.string.item_menu_bookmark))) {
            Toast.makeText(MainActivity.this,"书签",Toast.LENGTH_SHORT).show();
        } else if (s.equals(getString(R.string.item_menu_title))) {
            //Toast.makeText(MainActivity.this,"点击了标题",Toast.LENGTH_SHORT).show();
            new CustomDialogWithEditText.Builder(this).setType("title")
                    .setMessage(f.title)   //加入message，在dialog里可以判断是什么进来的，name代表是标题
                    .setInterface(this)
                    .create().show();
        } else if (s.equals(getString(R.string.item_menu_description))) {
            //Toast.makeText(MainActivity.this, "点击了描述", Toast.LENGTH_SHORT).show();
            new CustomDialogWithEditText.Builder(this).setType("describe")
                    .setMessage(f.describe)   //加入message，在dialog里可以判断是什么进来的,""代表是描述
                    .setInterface(this)
                    .create().show();
        } else if (s.equals(getString(R.string.item_menu_detail))) {
            //Toast.makeText(MainActivity.this,"点击了详情",Toast.LENGTH_SHORT).show();
            String[] details={" 文件名：","    类型：","    大小："," 分辨率：","    格式：","    上传：","    书签："};
            String videodetails="视频长度";
            String detailString="";
            detailString+=details[0]+f.name+"\n";
            detailString+=details[1]+f.type+"\n";
            detailString+=details[2]+f.space+"\n";
            detailString+=details[3]+"Unknow"+"\n";
            detailString+=details[4]+f.name.substring((f.name.lastIndexOf(".")) + 1).toLowerCase()+"\n";
            detailString+=details[5]+"false"+"\n";
            detailString+=details[6]+"false"+"\n";
            if(f.type.equals("video")){
                detailString+="    时长："+TimeUtil.intToTime(f.length)+"\n";
            }
            new AlertDialog.Builder(this).setTitle("详情").setMessage(detailString).create().show();
            //detailString = detailString+
        } else if (s.equals(getString(R.string.item_menu_delete))) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("确定要删除"+f.name+"吗？")
                    .setCancelable(false)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            //反射函数，更新界面
                            DeleteFile();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        return super.onContextItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //接口ItemOptionInterface的函数，对标题改变进行重写
    @Override
    public void TitleChange(String title) {
        File_Item f=childList.get(group).get(child);
        //Toast.makeText(this,"title changed"+title,Toast.LENGTH_LONG).show();
        if (!title.equals("") && !title.equals(f.title)) {// 修改后的文件名与已有的文件名冲突
            f.title=title;
            dbmgr.updateTitle(f);
            expandableListView.collapseGroup(group);
            expandableListView.expandGroup(group);
            Toast.makeText(this,"设置标题成功！",Toast.LENGTH_LONG).show();

        }
    }
    //接口ItemOptionInterface的函数，对增加描述进行重写
    @Override
    public void DescribeAdd(String des) {
        File_Item f=childList.get(group).get(child);
        //Toast.makeText(this,"title changed"+title,Toast.LENGTH_LONG).show();
        if (!des.equals("") && !des.equals(f.title)) {// 修改后的文件名与已有的文件名冲突
            f.describe=des;
            dbmgr.updateDescribe(f);
            expandableListView.collapseGroup(group);
            expandableListView.expandGroup(group);
            Toast.makeText(this,"添加描述成功！",Toast.LENGTH_LONG).show();
        }
    }
    //接口ItemOptionInterface的函数，对增加描述进行重写
    @Override
    public void DeleteFile() {
        File_Item f=childList.get(group).get(child);
        if(FileUtil.deleteFile(f.path)){
            dbmgr.deleteFile_Item(f);
            childList.get(group).remove(child);
            expandableListView.collapseGroup(group);
            expandableListView.expandGroup(group);
            Toast.makeText(this,"删除文件"+f.name+"成功",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,"删除文件出错",Toast.LENGTH_LONG).show();
        }
    }

    //创建一个二级列表的适配器
    class MyexpandableListAdapter extends BaseExpandableListAdapter {
        private Context context;
        private LayoutInflater inflater;

        public MyexpandableListAdapter(Context context) {
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        // ���ظ��б����
        @Override
        public int getGroupCount() {
            return groupList.size();
        }

        // �������б����
        @Override
        public int getChildrenCount(int groupPosition) {
            return childList.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {

            return groupList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childList.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {

            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            GroupHolder groupHolder = null;
            if (convertView == null) {
                groupHolder = new GroupHolder();
                convertView = inflater.inflate(R.layout.file_group, null);
                groupHolder.nametv = (TextView) convertView
                        .findViewById(R.id.group_nametv);
                groupHolder.detailtv = (TextView) convertView
                        .findViewById(R.id.group_detaitv);
                groupHolder.selectimg = (ImageView) convertView
                        .findViewById(R.id.group_selectimg);
                groupHolder.iconimg = (ImageView) convertView
                        .findViewById(R.id.group_iconimg);
                convertView.setTag(groupHolder);
            } else {
                groupHolder = (GroupHolder) convertView.getTag();
            }

            //groupHolder.textView.setText(getGroup(groupPosition).toString());
            groupHolder.nametv.setText(groupList.get(groupPosition).name);
            String detailstring="共"+groupList.get(groupPosition).number+"个文件，占用"+groupList.get(groupPosition).space+"M空间";
            groupHolder.detailtv.setText(detailstring);
            if (isExpanded)// ture is Expanded or false is not isExpanded
                groupHolder.selectimg.setImageResource(R.drawable.fold_open);
            else
                groupHolder.selectimg.setImageResource(R.drawable.fold_close);
            if(groupList.size()==0){
                groupHolder.iconimg.setImageResource(R.drawable.video_empty);
            }else{
                groupHolder.iconimg.setImageResource(R.drawable.video_normal);
            }
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            ItemHolder itemHolder=null;
            if (convertView == null || convertView.getTag()==null) {
                itemHolder=new ItemHolder();
                convertView = inflater.inflate(R.layout.file_item, null);
                itemHolder.nametv = (TextView) convertView
                        .findViewById(R.id.item_nametv);
                itemHolder.detailtv = (TextView) convertView
                        .findViewById(R.id.item_detailtv);
                itemHolder.lengthtv = (TextView) convertView
                        .findViewById(R.id.item_lengthtv);
                itemHolder.describeimg= (ImageView) convertView.findViewById(R.id.item_describe);
            }else{
                itemHolder=(ItemHolder)convertView.getTag();
            }
            if(childList.get(groupPosition).get(childPosition).title.equals("")){
                itemHolder.nametv.setText(childList.get(groupPosition).get(childPosition).name);
            }else{
                itemHolder.nametv.setText(childList.get(groupPosition).get(childPosition).title);
            }
            itemHolder.detailtv.setText(childList.get(groupPosition).get(childPosition).space+"M");
            itemHolder.lengthtv.setText(TimeUtil.intToTime(childList.get(groupPosition).get(childPosition).length));
            if(!childList.get(groupPosition).get(childPosition).describe.equals("")){
                itemHolder.describeimg.setVisibility(View.VISIBLE);
            }
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }
    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Toast.makeText(MainActivity.this,"hei" , Toast.LENGTH_SHORT).show();
        File_Item f=childList.get(groupPosition).get(childPosition);
        Intent i=new Intent();
        if(f.type.equals("video")){
            //jump to video
            i.setClass(this, VideoView.class);
            i.putExtra("childPosition", childPosition);
        }else if(f.type.equals("photo")){
            //jump to photo
            i.setClass(this, PhotoActivity.class);
            i.putExtra("childPosition", childPosition);
        }
        startActivity(i);
        //childList.get(groupPosition).get(childPosition)
        return false;
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        return false;
    }

    class GroupHolder {
        ImageView selectimg;
        ImageView iconimg;
        TextView nametv;
        TextView detailtv;
    }

    class ItemHolder{
        TextView nametv;
        TextView detailtv;
        TextView lengthtv;
        ImageView describeimg;
    }

}
