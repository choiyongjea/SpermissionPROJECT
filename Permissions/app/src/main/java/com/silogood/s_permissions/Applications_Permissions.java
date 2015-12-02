package com.silogood.s_permissions;

import android.app.Activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 7217-182 on 2015-12-01.
 */
public class Applications_Permissions extends Activity {

    private Context context;
    private ImageButton manageButton;

    private PackageManager mPm;

    private static final String NAME = "Name";
    private static final String DESCRIPTION = "Description";
    private static final String PACKAGENAME = "PackageName";
    private static final String SECURITYLEVEL = "Securitylevel";
    private static final String PERMISSION = "permission";
    private static final String TAG = "Permissions";
    private List<Map<String, String>> mGroupData;
    private List<List<Map<String, String>>> mChildData;
    private ExpandableListView permissionList;


    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.applications_permissions);
        mPm = getPackageManager();
        Intent i = getIntent();
        String title = i.getStringExtra("PackageName");
        //final String PackageName = title.substring(8);
        final String PackageName = title;

        ExpandableListView list = (ExpandableListView) findViewById(R.id.permissionList);
        mGroupData = new ArrayList<Map<String, String>>();
        mChildData = new ArrayList<List<Map<String, String>>>();
        String permissionName;
        String applicationLabel;
        String packageName;
        PackageInfo pi = null;
        ApplicationInfo ai = null;
        applicationLabel = PackageName;
        int packageVersionCode;
        String packageVersionName;
        String AppName;
        Drawable icon;
        long lastUpdate;
        int system;
        int install;


        try {
            pi = mPm.getPackageInfo(PackageName, PackageManager.GET_META_DATA);

            try {
                icon = mPm.getApplicationIcon(PackageName);                             /////icon  빼기

            } catch (PackageManager.NameNotFoundException e) {
                icon = mPm.getDefaultActivityIcon();
            }

            ImageView imageView = (ImageView) findViewById(android.R.id.icon);
            imageView.setImageDrawable(icon);

            AppName = pi.packageName;
            packageVersionCode = pi.versionCode;
            packageVersionName = pi.versionName;
            Log.v("XXXX7", "name:     " + packageVersionName);

            ai = mPm.getApplicationInfo(PackageName, PackageManager.GET_META_DATA);     ////라벨 빼오기
            String labelName = mPm.getApplicationLabel(ai).toString();

            ((TextView) findViewById(R.id.curAppName)).setText(labelName); //Appname layout 연결
            ((TextView) findViewById(R.id.packagea)).setText(AppName); //Appname layout 연결
            ((TextView) findViewById(R.id.curAppversion)).setText(packageVersionCode + " / " + packageVersionName);


        } catch (Exception ex) {
            packageVersionCode = 0;
            packageVersionName = "n/a";
            //Log.e("PM", "Error fetching app version");
        }

///////////////////////////////////////////////////////////////////////////////////////////////////

        manageButton = (ImageButton)findViewById(R.id.application_detail_manage_button);
        manageButton.setImageResource(R.drawable.ic_menu_manage);
        manageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 9) {
                    try {
                        Intent i = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                        i.addCategory(Intent.CATEGORY_DEFAULT);
                        i.setData(Uri.parse("package:" + PackageName));
                        startActivity(i);
                    } catch (ActivityNotFoundException anfe) {
                        Intent i = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                        i.addCategory(Intent.CATEGORY_DEFAULT);
                        startActivity(i);
                    }
                } else {

                }

            }
        });










///////////////////////////////////////////////////////////////////////////////////////////////////////////
        try {
            pi = mPm.getPackageInfo(PackageName, PackageManager.GET_PERMISSIONS);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        mChildData.clear();
        mGroupData.clear();


//        int count = 0;
//        try {
//            for (String key : pi.requestedPermissions) {
//                if (key.startsWith("android.permission.")) count++;
//            }
//            curGroupMap.put(NAME, applicationLabel + "(" + count + ")");
//            Log.v("XXXX7", "count :     " + count);
//        } catch (NullPointerException e) {
//            curGroupMap.put(NAME, applicationLabel + "(" + 0 + ")");
//            Log.v("XXXX7", "///count :     " + count);
//        }
//        curGroupMap.put(DESCRIPTION, packageVersionName);
//        Log.v("XXXX7", "Description  :     " + packageVersionName);
//        curGroupMap.put(PACKAGENAME, PackageName);
//        Log.v("XXXX7", "packageName  :     " + PackageName);
//
//        mGroupData.add(curGroupMap);

        Log.v("1", "" + mChildData);

        Log.v("2", "" + mChildData);
        try {
            Log.v("3", "" + mChildData);
            if (pi.requestedPermissions != null) {
                for (String key : pi.requestedPermissions) {
                    try {
                        if (!(key.startsWith("android.permission."))) continue;
                        Log.d("BBB", key);
                        PermissionInfo pinfo =
                                mPm.getPermissionInfo(key, PackageManager.GET_META_DATA);
                        Map<String, String> curGroupMap = new HashMap<String, String>();
                        Map<String, String> curChildMap = new HashMap<String, String>();
                        List<Map<String, String>> children = new ArrayList<Map<String, String>>();
                        CharSequence label = pinfo.loadLabel(mPm);
                        CharSequence desc = pinfo.loadDescription(mPm);

                        curGroupMap.put(NAME, (label == null) ? pinfo.name : label.toString());
                        Log.d("AAA", label.toString());
                        curGroupMap.put(SECURITYLEVEL, String.valueOf(pinfo.protectionLevel));
                        curChildMap.put(PERMISSION, key);
                        curChildMap.put(DESCRIPTION, (desc == null) ? "" : desc.toString());
                        curChildMap.put(SECURITYLEVEL, String.valueOf(pinfo.protectionLevel));
                        children.add(curChildMap);

                        mGroupData.add(curGroupMap);
                        Log.d("CCC", "" + mGroupData);
                        mChildData.add(children);
                        Log.d("DDD", "" + mChildData);


                    } catch (PackageManager.NameNotFoundException e) {
                        Log.i(TAG, "Ignoring unknown permission ");
                        continue;
                    }
                }
            } else {
                ((TextView) findViewById(R.id.iff)).setText(" App 의 요구 권한 이 없습니다 ^^  ");

//                mGroupData.add(curGroupMap);
//                mChildData.add(children);
            }
        } catch (NullPointerException e) {
            Log.i(TAG, "Ignoring unknown permission ");
        }


//        SimpleAdapter adapter = new SimpleAdapter(this, children, R.layout.permission_list_item, new String[]{"", "Name", "Description"}, new int[]{R.id.listviewpermissiontext, R.id.text1, R.id.text2});
//        if (pi.requestedPermissions != null) {
//            permissionList = (ListView) findViewById(R.id.permissionList);
//            permissionList.setAdapter(adapter);
//        } else {
//            ((TextView) findViewById(R.id.iff)).setText(" App 의 요구 권한 이 없습니다 ^^  ");
//        }

        PermissionAdapter mAdapter = new PermissionAdapter(
                Applications_Permissions.this, mGroupData,
                R.layout.marketplay_item,
                new String[]{NAME},
                new int[]{R.id.text1},
                mChildData,
                R.layout.marketplay_item_child,
                new String[]{DESCRIPTION,PERMISSION},
                new int[]{R.id.text1,R.id.text2}

        );
        list.setAdapter(mAdapter);

        Log.v("BBB", "" + mChildData);


        // permissionList = (ListView)findViewById(R.id.permissionL);
        // permissionList.setAdapter(adapter);    ///권한들 .. layout


    }


    private class PermissionAdapter extends SimpleExpandableListAdapter {
        public PermissionAdapter(Context context, List<? extends Map<String, ?>> groupData,
                                 int groupLayout, String[] groupFrom, int[] groupTo,
                                 List<? extends List<? extends Map<String, ?>>> childData, int childLayout,
                                 String[] childFrom, int[] childTo) {
            super(context, groupData, groupLayout, groupFrom, groupTo, childData,
                    childLayout, childFrom, childTo);
        }

        @Override
        @SuppressWarnings("unchecked")
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            final View v = super.getGroupView(groupPosition, isExpanded, convertView, parent);
            Map<String, String> group = (Map<String, String>) getGroup(groupPosition);
            int secLevel=0;
            if(!(group.get(SECURITYLEVEL)==null)) secLevel = Integer.parseInt(group.get(SECURITYLEVEL));
            TextView textView = (TextView) v.findViewById(R.id.text1);
            ImageView iv1 = (ImageView) v.findViewById(R.id.iv);
            if (PermissionInfo.PROTECTION_DANGEROUS == secLevel) {
                textView.setTextColor(Color.RED);
            } else {
                textView.setTextColor(Color.BLACK);
            }
          //  if(isExpanded){
             //   iv1.setImageDrawable(getDrawable(R.drawable.yes));
           // }else{
           //     iv1.setImageDrawable(getDrawable(R.drawable.no));
          //  }
            return v;
        }

        @Override
        @SuppressWarnings("unchecked")
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parent) {
            final View v = super.getChildView(groupPosition, childPosition, isLastChild,
                    convertView, parent);
            Map<String, String> child =
                    (Map<String, String>) getChild(groupPosition, childPosition);

            return v;
        }
    }





}
