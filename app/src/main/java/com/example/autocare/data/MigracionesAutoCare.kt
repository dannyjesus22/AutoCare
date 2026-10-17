
package com.example.autocare.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRACION_1_2 = object : Migration(1, 2) {

    override fun migrate(db: SupportSQLiteDatabase) {

        // Crear tabla de kilometraje
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `registros_kilometraje` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `vehiculoId` INTEGER NOT NULL,
                `fecha` INTEGER NOT NULL,
                `kilometrajeActual` INTEGER NOT NULL,
                FOREIGN KEY(`vehiculoId`)
                REFERENCES `vehiculos`(`id`)
                ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS
            `index_registros_kilometraje_vehiculoId`
            ON `registros_kilometraje` (`vehiculoId`)
            """.trimIndent()
        )

        // Crear tabla de mantenimiento
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `mantenimientos` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `vehiculoId` INTEGER NOT NULL,
                `titulo` TEXT NOT NULL,
                `tipo` TEXT NOT NULL,
                `kilometrajeRealizado` INTEGER NOT NULL,
                `proximoKilometraje` INTEGER,
                `fecha` TEXT NOT NULL,
                `proximaFecha` TEXT,
                `costo` REAL NOT NULL,
                `notas` TEXT NOT NULL,
                `completado` INTEGER NOT NULL,
                FOREIGN KEY(`vehiculoId`)
                REFERENCES `vehiculos`(`id`)
                ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS
            `index_mantenimientos_vehiculoId`
            ON `mantenimientos` (`vehiculoId`)
            """.trimIndent()
        )
    }
}



// Migración de versión 2 a 3.
// Agrega usuarios y propietarios sin borrar registros.
val MIGRACION_2_3 = object : Migration(2, 3) {

    override fun migrate(db: SupportSQLiteDatabase) {

        // Crear tabla de cuentas.
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `usuarios` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `nombre` TEXT NOT NULL,
                `correo` TEXT NOT NULL,
                `contrasenaHash` TEXT NOT NULL,
                `salt` TEXT NOT NULL
            )
            """.trimIndent()
        )

        // Impedir correos duplicados.
        db.execSQL(
            """
            CREATE UNIQUE INDEX IF NOT EXISTS
            `index_usuarios_correo`
            ON `usuarios` (`correo`)
            """.trimIndent()
        )

        // Agregar propietario a los vehículos.
        // Los vehículos antiguos quedarán temporalmente sin propietario.
        db.execSQL(
            """
            ALTER TABLE `vehiculos`
            ADD COLUMN `usuarioId` INTEGER
            """.trimIndent()
        )
    }
}
