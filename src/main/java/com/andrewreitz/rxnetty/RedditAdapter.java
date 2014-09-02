package com.andrewreitz.rxnetty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public final class RedditAdapter extends BindableAdapter<String> {
  private List<String> items = Collections.emptyList();

  public RedditAdapter(Context context) {
    super(context);
  }

  public void replaceWith(List<String> items) {
    this.items = items;
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
    return new TextView(getContext());
  }

  @Override public void bindView(String item, int position, View view) {
    ((TextView)view).setText(item);
  }
}
