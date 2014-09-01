package com.andrewreitz.rxnetty;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.nio.charset.Charset;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.netty.RxNetty;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GetFragment extends Fragment {

  private static final String TAG = GetFragment.class.getName();

  @OnClick(R.id.get_button) void onClick() {
    RxNetty.createHttpGet("http://reddit.com/r/gratefuldead")
        .flatMap(response -> response.getContent())
        .map(data -> "Client => " + data.toString(Charset.defaultCharset()))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<String>() {
          @Override public void onCompleted() {
            Log.d(TAG, "GET onCompleted");
          }

          @Override public void onError(Throwable e) {
            Log.e(TAG, "GET onError: " + e.getMessage());
          }

          @Override public void onNext(String s) {
            Log.d(TAG, "GET onNext: " + s);
          }
        });
  }

  @Override public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragement_get, container, false);
    ButterKnife.inject(this, view);
    return view;
  }
}
