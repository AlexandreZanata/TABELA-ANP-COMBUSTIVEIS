package com.anpfuel.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object AnpFuelDatabaseMigrations {

    val MIGRATION_1_2: Migration = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE VIRTUAL TABLE IF NOT EXISTS `municipality_fts` USING FTS4(
                    `municipality` TEXT NOT NULL,
                    `state` TEXT NOT NULL,
                    tokenize=unicode61 `remove_diacritics=2`,
                    content=`average_price`
                )
                """.trimIndent(),
            )
            db.execSQL(
                """
                CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_municipality_fts_BEFORE_UPDATE
                BEFORE UPDATE ON `average_price`
                BEGIN
                    DELETE FROM `municipality_fts` WHERE `docid`=OLD.`rowid`;
                END
                """.trimIndent(),
            )
            db.execSQL(
                """
                CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_municipality_fts_BEFORE_DELETE
                BEFORE DELETE ON `average_price`
                BEGIN
                    DELETE FROM `municipality_fts` WHERE `docid`=OLD.`rowid`;
                END
                """.trimIndent(),
            )
            db.execSQL(
                """
                CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_municipality_fts_AFTER_UPDATE
                AFTER UPDATE ON `average_price`
                BEGIN
                    INSERT INTO `municipality_fts`(`docid`, `municipality`, `state`)
                    VALUES (NEW.`rowid`, NEW.`municipality`, NEW.`state`);
                END
                """.trimIndent(),
            )
            db.execSQL(
                """
                CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_municipality_fts_AFTER_INSERT
                AFTER INSERT ON `average_price`
                BEGIN
                    INSERT INTO `municipality_fts`(`docid`, `municipality`, `state`)
                    VALUES (NEW.`rowid`, NEW.`municipality`, NEW.`state`);
                END
                """.trimIndent(),
            )
            db.execSQL("INSERT INTO municipality_fts(municipality_fts) VALUES('rebuild')")
        }
    }
}
