package com.anpfuel.data.di

import android.content.Context
import com.anpfuel.data.remote.AnpFileDownloader
import com.anpfuel.data.remote.AnpListingScraper
import com.anpfuel.data.remote.OkHttpClientFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClientFactory.create()

    @Provides
    @Singleton
    fun provideAnpListingScraper(okHttpClient: OkHttpClient): AnpListingScraper =
        AnpListingScraper(okHttpClient)

    @Provides
    @Singleton
    fun provideAnpFileDownloader(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient,
    ): AnpFileDownloader = AnpFileDownloader(context, okHttpClient)
}
