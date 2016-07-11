package com.bizzy.projectalpha.speeddating;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * Created by comp3 on 10/4/2015.
 */
public class ConnectionDetect {
    private Context _context;

    public ConnectionDetect(Context context){
        this._context = context;
    }
    public boolean isConnectingToInternet(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
