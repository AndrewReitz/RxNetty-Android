package com.andrewreitz.rxnetty;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.channel.ObservableConnection;
import io.reactivex.netty.pipeline.PipelineConfigurators;
import io.reactivex.netty.server.RxServer;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

// Server client fragment
public final class ServerFragment extends Fragment {

  private static final String TAG = ServerFragment.class.getName();
  private static final int PORT = 2025;

  private ServerAdapter adapter;

  @InjectView(R.id.server_listview) ListView listView;

  @OnClick(R.id.server_button) void startServer() {
    RxServer<String, String> server = RxNetty.createTcpServer(PORT, PipelineConfigurators.textOnlyConfigurator(),
        connection -> {
          adapter.add("New client connection established.");
          connection.writeAndFlush("Welcome! \n\n");
          return connection.getInput().flatMap(msg -> {
            Log.d(TAG, "onNext: " + msg);
            msg = msg.trim();
            if (!msg.isEmpty()) {
              return connection.writeAndFlush("echo => " + msg + '\n');
            } else {
              return Observable.empty();
            }
          });
        });
    adapter.add("TCP echo server started...");
    server.start();
  }

  @OnClick(R.id.client_button) void startClient() {
    Observable<ObservableConnection<String, String>> connectionObservable =
        RxNetty.createTcpClient("localhost", PORT, PipelineConfigurators.textOnlyConfigurator()).connect();

    connectionObservable.flatMap(connection -> {
      Observable<String> helloMessage = connection.getInput()
          .take(1).map(String::trim);

      // output 10 values at intervals and receive the echo back
      Observable<String> intervalOutput =
          Observable.interval(500, TimeUnit.MILLISECONDS)
              .flatMap(aLong -> connection.writeAndFlush(String.valueOf(aLong + 1))
                  .map(aVoid -> ""));

      // capture the output from the server
      Observable<String> echo = connection.getInput().map(String::trim);

      // wait for the helloMessage then start the output and receive echo input
      return Observable.concat(helloMessage, Observable.merge(intervalOutput, echo));
    })
        .take(10)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<Object>() {
          @Override public void onCompleted() {
            Log.d(TAG, "COMPLETED!");
          }

          @Override public void onError(Throwable throwable) {
            Log.e(TAG, "onError: " + throwable.getMessage());
          }

          @Override public void onNext(Object o) {
            adapter.add(o.toString());
          }
        });
  }

  @Override public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_server, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    adapter = new ServerAdapter(getActivity());
    listView.setAdapter(adapter);
  }
}
