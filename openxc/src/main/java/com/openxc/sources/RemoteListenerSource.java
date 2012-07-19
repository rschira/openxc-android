package com.openxc.sources;

import com.openxc.remote.RawMeasurement;
import com.openxc.remote.VehicleServiceInterface;
import com.openxc.remote.VehicleServiceListener;

import com.openxc.sources.BaseVehicleDataSource;

import android.os.RemoteException;

import android.util.Log;

/**
 * Pass measurements from a VehicleService to an in-process callback.
 *
 * This source is a bit of a special case - it's used by the
 * {@link com.openxc.VehicleManager} to inject measurement updates from a
 * {@link com.openxc.remote.VehicleService} into an in-process data
 * pipeline. By using the same workflow as on the remote process side, we can
 * share code between remote and in-process data sources and sinks. This makes
 * adding new sources and sinks possible for end users, since the
 * VehicleService doesn't need to have every possible implementation.
 */
public class RemoteListenerSource extends BaseVehicleDataSource {
    private final static String TAG = "RemoteListenerSource";
    private VehicleServiceInterface mService;

    /**
     * Registers a measurement listener with the remote service.
     */
    public RemoteListenerSource(VehicleServiceInterface service) {
        super();
        mService = service;

        try {
            mService.register(mRemoteListener);
        } catch(RemoteException e) {
            Log.w(TAG, "Unable to register to receive " +
                    "measurement callbacks", e);
        }
    }

    public void stop() {
        super.stop();
        try {
            mService.unregister(mRemoteListener);
        } catch(RemoteException e) {
            Log.w(TAG, "Unable to register to receive " +
                    "measurement callbacks", e);
        }
    }

    private VehicleServiceListener mRemoteListener =
        new VehicleServiceListener.Stub() {
            public void receive(RawMeasurement rawMeasurement) {
                handleMessage(rawMeasurement);
            }
        };
}