package com.example.foolish_guy.nds;

import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by foolish-guy on 17/7/17.
 */

public class ListViewAdapter extends ArrayAdapter <String> {
    List<String> list;
    public ListViewAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        list = objects;

    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
