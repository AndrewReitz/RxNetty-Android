package com.andrewreitz.rxnetty;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.andrewreitz.rxnetty.model.Child;
import com.andrewreitz.rxnetty.model.Data_;
import com.andrewreitz.rxnetty.model.Reddit;
import com.google.gson.Gson;

import java.nio.charset.Charset;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.reactivex.netty.RxNetty;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public final class GetFragment extends Fragment {

  private static final String TAG = GetFragment.class.getName();

  private Gson gson = new Gson();

  private RedditAdapter adapter;

  @InjectView(R.id.get_listview) ListView listView;

  @OnClick(R.id.get_button) void onClick() {
    // Must have www, will not follow redirects w/ out being told to
    RxNetty.createHttpGet("http://www.reddit.com/r/ReactiveProgramming.json")
        .flatMap(response -> response.getContent())
        .map(data -> data.toString(Charset.defaultCharset()))
        .flatMap(s -> Observable.from(
            gson.fromJson(s, Reddit.class)
                .getData()
                .getChildren())
        )
        .map(Child::getData)
        .map(Data_::getTitle)
        .toList()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<List<String>>() {
          @Override public void onCompleted() {
            Log.d(TAG, "GET onCompleted");
          }

          @Override public void onError(Throwable e) {
            Log.e(TAG, "GET onError: " + e.getMessage());
          }

          @Override public void onNext(List<String> titles) {
            Log.d(TAG, "GET onNext");
            adapter.replaceWith(titles);
          }
        });
  }

  @Override public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_get, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    adapter = new RedditAdapter(getActivity());
    listView.setAdapter(adapter);
  }
}
