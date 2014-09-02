package com.andrewreitz.rxnetty;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
  private static final Handler mainHandler = new Handler(Looper.getMainLooper());

  private ServerAdapter adapter;
  private RxServer<String, String> server;

  @InjectView(R.id.server_listview) ListView listView;

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
        Log.d(TAG, "Client Complete!");
      }

      @Override public void onError(Throwable throwable) {
        Log.e(TAG, "onError: " + throwable.getMessage());
      }

      @Override public void onNext(Object o) {
        final String message = o.toString();
        Log.d(TAG, "Client onNext: " + message);
        adapter.add(message);
      }
    });
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    server = RxNetty.createTcpServer(PORT, PipelineConfigurators.textOnlyConfigurator(),
        connection -> {
          mainHandler.post(() -> adapter.add("New client connection established."));
          connection.writeAndFlush("Welcome! \n\n");
          return connection.getInput().flatMap(msg -> {
            Log.d(TAG, "Server onNext: " + msg);
            msg = msg.trim();
            if (!msg.isEmpty()) {
              return connection.writeAndFlush("echo => " + msg + '\n');
            } else {
              return Observable.empty();
            }
          });
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

  @Override public void onStart() {
    super.onStart();
    adapter.add("TCP echo server started...");
    server.start();
  }

  @Override public void onStop() {
    super.onStop();
    adapter.add("TCP echo server shutting down...");
    try {
      server.shutdown();
    } catch (InterruptedException e) {
      Log.d(TAG, "Error shutting down server: " + e.getMessage());
    }
  }
}
