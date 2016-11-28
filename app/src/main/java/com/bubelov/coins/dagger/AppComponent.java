package com.bubelov.coins.dagger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bubelov.coins.PlacesCache;
import com.bubelov.coins.api.CoinsApi;
import com.bubelov.coins.util.MapMarkersCache;
import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author Igor Bubelov
 */

@Singleton
@Component(modules = {AppContextModule.class, DatabaseModule.class, CacheModule.class, ApiModule.class, ConverterModule.class})
public interface AppComponent {
    Context getContext();

    SQLiteDatabase database();

    PlacesCache getPlacesCache();

    MapMarkersCache getMarkersCache();

    CoinsApi provideApi();

    Gson provideGson();

    void inject(PlacesCache cache);
}