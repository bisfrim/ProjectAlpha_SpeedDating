package com.bizzy.projectalpha.speeddating;

import android.*;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;


/**
 * Created by nubxf5 on 8/3/2016.
 * Android Marshmallow requires user to have control of permissions
 * These permission has to be granted to use camera and gps
 */
final class MainActivityPermissions {
    protected static String LOG_TAG = "MainActivityPermissions";

    private static final int REQUEST_ONPICKPHOTO = 0;

    private static final String[] PERMISSION_ONPICKPHOTO = new String[] {"android.permission.WRITE_EXTERNAL_STORAGE"};
    private static final int REQUEST_ONPICKDOC = 1;

    private static final int REQUEST_GPSLOC = 2;
    private static final int REQUEST_CAMERA = 3;
    private static final String[] PERMISSION_ONCAMERAPIC = new String[] {"android.permission.READ_EXTERNAL_STORAGE"};

    private static final String[] PERMISSION_GPS_LOCAITON = new String[] {"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"};

    private MainActivityPermissions() {
    }


    static void onPhotCheckPermissions(MainActivity target){
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, "Permission Granted");
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                //Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, "Permission Denied" + deniedPermissions.toString());
            }
        };
        //TedPermission is a 3rd party library
        new TedPermission(target)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("we need permission for read camera and storage")
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setGotoSettingButtonText("allow permission")
                .setPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA) // Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                .check();
    }

}
