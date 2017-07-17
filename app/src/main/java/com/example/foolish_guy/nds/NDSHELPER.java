package com.example.foolish_guy.nds;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by foolish-guy on 15/7/17.
 */

public class NDSHELPER {

    NsdManager nsdManager;
    NsdManager.DiscoveryListener discoveryListener;
    NsdManager.ResolveListener mResolveListener;
    public static Boolean isAlreadyDiscovered = false;
    static Boolean isDiscovering = false;
    List<NsdServiceInfo> serviceInfoList = new ArrayList<NsdServiceInfo>();

    private static final Boolean IS_LOG_ENABLED = true;

    public static final String SERVICE_TYPE = "_http._tcp";

    Context mContext;

    private static final String TAG = NDSHELPER.class.getSimpleName();

    public String mServiceName = "";

    NsdServiceInfo nsdServiceInfo;

    OnResolveListener listener;

    public NDSHELPER (Context context, OnResolveListener listener) {
        mContext = context;
        this.listener = listener;
        nsdManager =  (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    public void initializeNSD () {
        initializeResolveListener();
        initializeDiscoveryListener();
    }

    private void initializeDiscoveryListener() {

        discoveryListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onStartDiscoveryFailed(String s, int i) {
                LogIt("Discovery failed: Error code:" + i, 0);
                nsdManager.stopServiceDiscovery(this);
                isAlreadyDiscovered = false;
                isDiscovering = false;
                listener.onFailed(s, i);
            }

            @Override
            public void onStopDiscoveryFailed(String s, int i) {
                LogIt("Discovery failed: Error code:" + i, 0);
                nsdManager.stopServiceDiscovery(this);
                isAlreadyDiscovered = false;
                isDiscovering = false;
                listener.onFailed(s, i);
            }

            @Override
            public void onDiscoveryStarted(String s) {
                isDiscovering = true;
                LogIt("Service discovery started", 1);
            }

            @Override
            public void onDiscoveryStopped(String s) {
                isDiscovering = false;
                LogIt("Discovery stopped: " + s, 2);
                listener.onStopped(s);
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {

           LogIt("Service discovery success " + service, 1);
                if (!service.getServiceType().equals(SERVICE_TYPE)) {
                    LogIt("Unknown Service Type: " + service.getServiceType(), 1);
                }
                nsdManager.resolveService(service, mResolveListener);
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                LogIt("service lost" + service, 0);
                if (nsdServiceInfo == service) {
                    nsdServiceInfo = null;
                }
            }
        };
    }

    public void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                LogIt("Resolve failed" + errorCode, 0);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                LogIt("Resolve Succeeded. " + serviceInfo, 2);

                nsdServiceInfo = serviceInfo;
                isAlreadyDiscovered = true;

                serviceInfoList.add(serviceInfo);

                mServiceName = serviceInfo.getServiceName();
            }
        };
    }

    public void stopDiscovery() {
        try {
            if (isAlreadyDiscovered || isDiscovering) {
                nsdManager.stopServiceDiscovery(discoveryListener);
            }
        } catch (Exception e) {
            LogIt (e.toString(), 0);
        }
    }

    public void discoverServices() {
        if (isAlreadyDiscovered || isDiscovering)
            return;
        nsdManager.discoverServices(
                SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);
    }


    public NsdServiceInfo getChosenServiceInfo() {
        return nsdServiceInfo;
    }
    public List <NsdServiceInfo> getServiceInfoList () {
        return serviceInfoList;
    }
    public int getServiceListCount () {
        return serviceInfoList.size();
    }

    public interface OnResolveListener {
        public void onFailed (String errString, int code);
        public void onStopped (String s);
    }

    private void LogIt (String data, int type) {
        if (IS_LOG_ENABLED) {
            switch (type) {
                case 0:
                    Log.e(TAG, data);
                    break;

                case 1:
                    Log.d(TAG, data);
                    break;
                case 2:
                    Log.i(TAG, data);
                    break;
            }
        }
    }

}
