package com.navfac.usace.safety.base.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
        entities = [SectionsTitleEntity::class,
                    SectionsSubtitleEntity::class,
                    AppendicesEntity::class],
        version = 38,
        exportSchema = false
)
@TypeConverters(DataTypeConverter::class)
abstract class UsaceDatabase: RoomDatabase() {

    abstract fun usaceDao(): UsaceDao

}