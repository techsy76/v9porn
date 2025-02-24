package com.u9porn.ui.porn9video.history;


import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;

import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u9porn.ui.about.AboutActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class HistoryActivityModule {
    @Provides
    AppCompatActivity provideAppCompatActivity(HistoryActivity historyActivity) {
        return historyActivity;
    }

    @Provides
    static LifecycleProvider<Lifecycle.Event> providerLifecycleProvider(AppCompatActivity mAppCompatActivity) {
        return AndroidLifecycle.createLifecycleProvider(mAppCompatActivity);
    }
}
