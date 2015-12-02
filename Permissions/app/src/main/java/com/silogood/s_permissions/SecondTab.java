package com.silogood.s_permissions;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.List;
        import java.util.Map;

        import android.app.Dialog;
        import android.app.ExpandableListActivity;
        import android.app.ProgressDialog;
        import android.content.Context;
        import android.content.Intent;
        import android.content.pm.ApplicationInfo;
        import android.content.pm.PackageInfo;
        import android.content.pm.PackageManager;
        import android.content.pm.PermissionInfo;
        import android.content.pm.PackageManager.NameNotFoundException;
        import android.graphics.Color;
        import android.graphics.drawable.Drawable;
        import android.net.Uri;
        import android.os.Build;
        import android.os.Handler;
        import android.os.Message;
        import android.provider.Settings;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuInflater;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ExpandableListView;
        import android.widget.ImageView;
        import android.widget.SimpleExpandableListAdapter;
        import android.widget.TextView;

public class SecondTab extends ExpandableListActivity implements Runnable {
    private static final String TAG = "Permissions";

    private static final String NAME = "Name";
    private static final String DESCRIPTION = "Description";
    private static final String PACKAGENAME = "PackageName";
    private static final String SECURITYLEVEL = "Securitylevel";

    // Installed App Details
    private static final String SCHEME = "package";
    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    private static final String APP_PKG_NAME_22 = "pkg";
    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

    private int mDangerousColor;
    private int mDefaultTextColor;

    private static final int PROGRESS_DIALOG = 0;
    private ProgressDialog mProgressDialog;

    private List<Map<String, String>> mGroupData;
    private List<List<Map<String, String>>> mChildData;
    private List<Map<String, String>> mGroupData_S;
    private List<List<Map<String, String>>> mChildData_S;

    private List<Map<String, ?>> Group_Child;

    private PackageManager mPm;

    PermissionSingleton PS;

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {                                             // 1. 오픈
        super.onCreate(savedInstanceState);
        mPm = getPackageManager();
       // mDangerousColor = getResources().getColor(R.color.perms_dangerous_grp_color);
        mGroupData = new ArrayList<Map<String, String>>();
        mChildData = new ArrayList<List<Map<String, String>>>();
        mGroupData_S = new ArrayList<Map<String, String>>();
        mChildData_S = new ArrayList<List<Map<String, String>>>();
        mDefaultTextColor = Color.DKGRAY;
        PS = PermissionSingleton.getInstance();
        showDialog(PROGRESS_DIALOG);                                                                  // 2. 백그라운드 동작
    };

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                int childPosition, long id) {
//      showInstalledAppDetails(this, (String) v.getTag());                                                   // 임시 주석
        return super.onChildClick(parent, v, groupPosition, childPosition, id);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch(id) {
            case PROGRESS_DIALOG:
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
               // mProgressDialog.setMessage(getText(R.string.permission_settings_loading));
                mProgressDialog.setCancelable(false);
                Thread thread = new Thread(this);
                thread.start();
                return mProgressDialog;
            default:
                return super.onCreateDialog(id, null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

   // @Override
    //public boolean onOptionsItemSelected(MenuItem item) {
       // switch (item.getItemId()) {
        //    case R.id.refresh:
         //       showDialog(PROGRESS_DIALOG);
         //       return true;
        //    default:
        //        return super.onOptionsItemSelected(item);
     //   }
  //  }

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            removeDialog(PROGRESS_DIALOG);
            PermissionAdapter mAdapter = new PermissionAdapter(
                    SecondTab.this,
                    mGroupData_S,
                    R.layout.permissions_expandable_list_item_child,                                       // 자식 부모 순서 변경
                    new String[] { NAME, DESCRIPTION },
                    new int[] { android.R.id.text1, android.R.id.text2 },
                    mChildData_S,
                    R.layout.permissions_expandable_list_item,                                             // 자식 부모 순서 변경
                    new String[] { NAME, DESCRIPTION },
                    new int[] { android.R.id.text1, android.R.id.text2 }
            );
            setListAdapter(mAdapter);
        }
    };

    private class PermissionAdapter extends SimpleExpandableListAdapter {
        public PermissionAdapter(Context context, List<? extends Map<String, ?>> childData,
                                 int groupLayout, String[] groupFrom, int[] groupTo,
                                 List<? extends List<? extends Map<String, ?>>> groupData, int childLayout,
                                 String[] childFrom, int[] childTo) {
            super(context, childData, groupLayout, groupFrom, groupTo, groupData,
                    childLayout, childFrom, childTo);
        }

        @Override
        @SuppressWarnings("unchecked")
        public View getGroupView(int groupPosition, boolean isExpanded,                              // 부모 뷰 셋팅
                                 View convertView, ViewGroup parent) {                                          // 자식과 변경
            final View v = super.getGroupView(groupPosition, isExpanded,
                    convertView, parent);

            ImageView imageView = (ImageView) v.findViewById(android.R.id.icon);
            Map<String, String> group = (Map<String, String>) getGroup(groupPosition);
            Drawable icon;
            String packageName = (String)group.get(PACKAGENAME);
            try {
                icon = mPm.getApplicationIcon(packageName);
            } catch (NameNotFoundException e) {
                icon = mPm.getDefaultActivityIcon();
            }
            imageView.setImageDrawable(icon);
            v.setTag(packageName);
            return v;
        }

        @Override
        @SuppressWarnings("unchecked")
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,               // 자식 뷰 셋팅
                                 View convertView, ViewGroup parent) {                                          // 부모와 변경
            final View v = super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);

            Map<String, String> child =
                    (Map<String, String>)getChild(groupPosition, childPosition);

            int secLevel = Integer.parseInt(child.get(SECURITYLEVEL));
            TextView textView = (TextView) v.findViewById(android.R.id.text1);
            if (PermissionInfo.PROTECTION_DANGEROUS == secLevel) {
                textView.setTextColor(mDangerousColor);
            } else {
                textView.setTextColor(mDefaultTextColor);
            }
            return v;
        }
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
                : false;
    }

    public void run() {                                                               // 3. 동작

        mGroupData = PS.getmGroupData();
        mChildData = PS.getmChildData();

        for(int i=0; i<mGroupData.size();i++) {

            if(mGroupData.get(i).get("pack").equals("android.permission.INTERNET") ||
                    mGroupData.get(i).get("pack").equals("android.permission.CAMERA")) {
                Map<String, String> zzz;
                List<Map<String, String>> yyy;
                zzz = mGroupData.get(i);
                yyy = mChildData.get(i);
                mGroupData_S.add(zzz);
                mChildData_S.add(yyy);
            }
        }
        handler.sendEmptyMessage(0);
    }

    public static void showInstalledAppDetails(Context context, String packageName) {                  // 앱 정보 페이지 들어가기
        Intent intent = new Intent();
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) { // above 2.3
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(SCHEME, packageName, null);
            intent.setData(uri);
        } else { // below 2.3
            final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
                    : APP_PKG_NAME_21);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME,
                    APP_DETAILS_CLASS_NAME);
            intent.putExtra(appPkgName, packageName);
        }
        context.startActivity(intent);
    }
}