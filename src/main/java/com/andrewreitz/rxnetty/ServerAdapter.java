package com.andrewreitz.rxnetty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ServerAdapter extends BindableAdapter<String> {
  private List<String> items = new ArrayList<>();

  public ServerAdapter(Context context) {
    super(context);
  }

  public void add(String message) {
    items.add(message);
    notifyDataSetChanged();
  }

  @Override public int getCount() {
    return items.size();
  }

  @Override public String getItem(int position) {
    return items.get(position);
  }

  @Override public long getItemId(int position) {
    return 0;
  }

  @Override public View newView(LayoutInflater inflater, int position, ViewGroup container) {
    return inflater.inflate(android.R.layout.simple_list_item_1, container, false);
  }

  @Override public void bindView(String item, int position, View view) {
    ((TextView)view).setText(item);
  }
}
