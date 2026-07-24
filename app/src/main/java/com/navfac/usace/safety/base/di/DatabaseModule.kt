package com.navfac.usace.safety.base.di

import android.content.Context
import androidx.room.Room
import com.navfac.usace.safety.base.db.UsaceDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)   //ApplicationComponent is Deprecated in Dagger Version 2.30, Removed 2.31
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context,
        UsaceDatabase::class.java,
        "usace_table"
    ).build()

    @Singleton
    @Provides
    fun provideDao(database: UsaceDatabase) = database.usaceDao()

}