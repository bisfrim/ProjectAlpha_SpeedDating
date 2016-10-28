package com.bizzy.projectalpha.speeddating;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.net.URL;
import java.net.URLConnection;

/**
 * Created by androiddev on 08.09.15.
 */
public class WaitForInternetConnectionView extends RelativeLayout implements View.OnClickListener{

    private Button mRefreshButton;
    private ProgressBar mProgressBar;
    private TextView mWaitMessageText;
    private Context mContext;
    private OnConnectionIsAvailableListener mOnConnectionIsAvailableListener;

    public WaitForInternetConnectionView(Context context, AttributeSet attrs){
        super(context, attrs);
        mContext = context;
        init();
    }

    public void init(){
        inflate(getContext(), R.layout.view_wait_for_internet_connection, this);
        mWaitMessageText = (TextView) findViewById(R.id.text_wait_message);
        mRefreshButton = (Button)findViewById(R.id.button_refresh);
        mRefreshButton.setVisibility(View.GONE);
        mRefreshButton.setOnClickListener(this);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_refresh:
                setWaitMessageNormal();
                if(mOnConnectionIsAvailableListener != null)
                    checkInternetConnection(mOnConnectionIsAvailableListener);
                break;
        }
    }

    private void setWaitMessageNegative(){
        mProgressBar.setVisibility(INVISIBLE);
        mWaitMessageText.setText("Internet connection is lost");
    }

    private void setWaitMessageNormal(){
        mProgressBar.setVisibility(VISIBLE);
        mWaitMessageText.setText("Loading information");
    }

    public void checkInternetConnection(OnConnectionIsAvailableListener listener){
        setVisibility(VISIBLE);
        mOnConnectionIsAvailableListener = listener;
        new AsyncCheckInternetConnection(listener).execute();
    }


    private class AsyncCheckInternetConnection extends AsyncTask<Void, Void, Boolean>{
        OnConnectionIsAvailableListener mListener;

        public AsyncCheckInternetConnection(OnConnectionIsAvailableListener listener){
            mListener = listener;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try{
                URL myUrl = new URL("https://www.back4app.com/");
                URLConnection connection = myUrl.openConnection();
                connection.setConnectTimeout(5000);
                connection.connect();
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isConnected) {
            if(isConnected){
                mListener.onConnectionIsAvailable();
            } else {
                setWaitMessageNegative();
                mRefreshButton.setVisibility(VISIBLE);
            }
        }
    }

    public void close(){
        setVisibility(GONE);
    }


    public interface OnConnectionIsAvailableListener{
        void onConnectionIsAvailable();
    }
}
