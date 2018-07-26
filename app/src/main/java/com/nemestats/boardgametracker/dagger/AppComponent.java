package com.nemestats.boardgametracker.dagger;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by mehegeo on 9/23/17.
 */

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent extends AppGraph {

    final class Initializer {

        public Initializer() {
        }

        public static AppComponent init(Context context) {
            return DaggerAppComponent.builder().appModule(new AppModule(context)).build();
        }

    }

}
