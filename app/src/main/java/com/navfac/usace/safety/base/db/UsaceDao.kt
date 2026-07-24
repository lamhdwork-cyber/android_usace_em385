package com.navfac.usace.safety.base.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

@Dao
interface UsaceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSectionTitle(sectionTitleEntity: SectionsTitleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSectionSubtitle(sectionSubtitleEntity: SectionsSubtitleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppendices(appendicesEntity: AppendicesEntity)

    @Query("SELECT * FROM appendices_table ORDER BY ordinal ASC")
    fun getAllAppendices(): Flow<List<AppendicesEntity>>

    @Query("SELECT * FROM appendices_table ORDER BY ordinal ASC")
    fun getAllAppendicesRx(): Single<List<AppendicesEntity>>

    @Query("SELECT * FROM section_title_table ORDER BY ordinal ASC")
    fun getAllSectionTitle(): Flow<List<SectionsTitleEntity>>

    @Query("SELECT * FROM section_subtitle_table ORDER BY id ASC")
    fun getAllSectionSubtitle(): Flow<List<SectionsSubtitleEntity>>

    @Query("SELECT * FROM section_subtitle_table ORDER BY id ASC")
    fun getAllSectionSubtitleRx(): Single<List<SectionsSubtitleEntity>>

}