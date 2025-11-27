package com.example.aplicativotranport.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.aplicativotranport.model.Aviso

class Database(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "transporte.db"
        private const val DATABASE_NAME_OLD = "transporte_old.db"
        private const val DATABASE_VERSION = 10

        // Tabelas
        private const val TABLE_USUARIOS = "usuarios"
        private const val TABLE_RESPONSAVEIS = "responsaveis"
        private const val TABLE_ESCOLAS = "escolas"
        private const val TABLE_TURMAS = "turmas"
        private const val TABLE_CONDUTORES = "condutores"
        private const val TABLE_ROTAS = "rotas"
        private const val TABLE_ALUNOS = "alunos"
        private const val TABLE_ALUNO_ROTA = "aluno_rota"
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        // Garante que FKs funcionem (ON DELETE CASCADE etc.)
        db.setForeignKeyConstraintsEnabled(true)
        db.execSQL("PRAGMA foreign_keys=ON;")
    }

    override fun onCreate(db: SQLiteDatabase) {
        // --- Usuários (login/cadastro) ---
        db.execSQL(
            """
            CREATE TABLE $TABLE_USUARIOS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT,
                email TEXT UNIQUE,
                senha TEXT
            )
            """.trimIndent()
        )

        // --- Entidades 1:N base ---
        db.execSQL(
            """
            CREATE TABLE $TABLE_RESPONSAVEIS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT,
                telefone TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_ESCOLAS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT,
                endereco TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_TURMAS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT,
                turno TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_CONDUTORES (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT,
                placa TEXT
            )
            """.trimIndent()
        )

        // --- Rotas (1:N com condutor) ---
        db.execSQL(
            """
            CREATE TABLE $TABLE_ROTAS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT,
                condutor_id INTEGER,
                FOREIGN KEY(condutor_id) REFERENCES $TABLE_CONDUTORES(id) ON DELETE SET NULL
            )
            """.trimIndent()
        )

        // --- Alunos (N:1 com responsável, turma e escola) + status embarcado ---
        db.execSQL(
            """
            CREATE TABLE $TABLE_ALUNOS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT,
                embarcado INTEGER DEFAULT 0,
                responsavel_id INTEGER,
                turma_id INTEGER,
                escola_id INTEGER,
                FOREIGN KEY(responsavel_id) REFERENCES $TABLE_RESPONSAVEIS(id) ON DELETE SET NULL,
                FOREIGN KEY(turma_id)       REFERENCES $TABLE_TURMAS(id)       ON DELETE SET NULL,
                FOREIGN KEY(escola_id)      REFERENCES $TABLE_ESCOLAS(id)      ON DELETE SET NULL
            )
            """.trimIndent()
        )

        // --- Avisos (simples) ---
        db.execSQL(
            """
            CREATE TABLE avisos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                titulo TEXT NOT NULL,
                descricao TEXT NOT NULL,
                data TEXT NOT NULL
            )
    """.trimIndent()
        )

        // --- Relação N:N Aluno⇄Rota ---
        db.execSQL(
            """
            CREATE TABLE $TABLE_ALUNO_ROTA (
                aluno_id INTEGER,
                rota_id  INTEGER,
                PRIMARY KEY (aluno_id, rota_id),
                FOREIGN KEY(aluno_id) REFERENCES $TABLE_ALUNOS(id) ON DELETE CASCADE,
                FOREIGN KEY(rota_id)  REFERENCES $TABLE_ROTAS(id)  ON DELETE CASCADE
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Descarta e recria (simples e seguro p/ este projeto acadêmico)
        db.execSQL("DROP TABLE IF EXISTS avisos")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ALUNO_ROTA")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ALUNOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ROTAS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CONDUTORES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TURMAS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ESCOLAS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RESPONSAVEIS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        onCreate(db)
    }


    // GENÉRICOS (CRUD simples)

    fun insert(table: String, values: ContentValues): Long {
        val db = writableDatabase
        val result = db.insert(table, null, values)
        db.close()
        return result
    }

    fun update(table: String, values: ContentValues, whereClause: String, whereArgs: Array<String>): Int {
        val db = writableDatabase
        val result = db.update(table, values, whereClause, whereArgs)
        db.close()
        return result
    }

    fun delete(table: String, id: Int) {
        val db = writableDatabase
        db.delete(table, "id=?", arrayOf(id.toString()))
        db.close()
    }

    fun getAll(table: String): List<Map<String, String>> {
        val db = readableDatabase
        val list = mutableListOf<Map<String, String>>()
        val cursor: Cursor = db.rawQuery("SELECT * FROM $table", null)
        if (cursor.moveToFirst()) {
            do {
                val row = mutableMapOf<String, String>()
                for (i in 0 until cursor.columnCount) {
                    row[cursor.getColumnName(i)] = cursor.getString(i) ?: ""
                }
                list.add(row)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

    fun getById(table: String, id: Int): Map<String, String>? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $table WHERE id = ?", arrayOf(id.toString()))
        val row: MutableMap<String, String>? =
            if (cursor.moveToFirst()) {
                mutableMapOf<String, String>().apply {
                    for (i in 0 until cursor.columnCount) {
                        this[cursor.getColumnName(i)] = cursor.getString(i) ?: ""
                    }
                }
            } else null
        cursor.close()
        db.close()
        return row
    }

    // AVISOS (CRUD)

    fun inserirAviso(titulo: String, descricao: String, data: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("titulo", titulo)
            put("descricao", descricao)
            put("data", data)
        }
        val result = db.insert("avisos", null, values)
        db.close()
        return result
    }

    fun listarAvisos(): List<Aviso> {
        val lista = mutableListOf<Aviso>()
        val db = readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery("SELECT * FROM avisos ORDER BY id DESC", null)
            if (cursor.moveToFirst()) {
                do {
                    val aviso = Aviso(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo")),
                        descricao = cursor.getString(cursor.getColumnIndexOrThrow("descricao")),
                        data = cursor.getString(cursor.getColumnIndexOrThrow("data"))
                    )
                    lista.add(aviso)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            android.util.Log.e("Database", "erro ao listar avisos", e)
        } finally {
            cursor?.close()
            db.close()
        }

        return lista
    }
    // ESPECÍFICOS (consultas)

    // Alunos por responsável
    fun getAlunosByResponsavel(responsavelId: Int): List<Map<String, String>> {
        val db = readableDatabase
        val list = mutableListOf<Map<String, String>>()
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_ALUNOS WHERE responsavel_id = ?",
            arrayOf(responsavelId.toString())
        )
        if (cursor.moveToFirst()) {
            do {
                val row = mutableMapOf<String, String>()
                for (i in 0 until cursor.columnCount) {
                    row[cursor.getColumnName(i)] = cursor.getString(i) ?: ""
                }
                list.add(row)
            } while (cursor.moveToNext())
        }
        cursor.close(); db.close()
        return list
    }

    // Rotas por condutor
    fun getRotasByCondutor(condutorId: Int): List<Map<String, String>> {
        val db = readableDatabase
        val list = mutableListOf<Map<String, String>>()
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_ROTAS WHERE condutor_id = ?",
            arrayOf(condutorId.toString())
        )
        if (cursor.moveToFirst()) {
            do {
                val row = mutableMapOf<String, String>()
                for (i in 0 until cursor.columnCount) {
                    row[cursor.getColumnName(i)] = cursor.getString(i) ?: ""
                }
                list.add(row)
            } while (cursor.moveToNext())
        }
        cursor.close(); db.close()
        return list
    }

    // Alunos vinculados a uma rota (N:N)
    fun getAlunosPorRota(rotaId: Int): List<Map<String, String>> {
        val db = readableDatabase
        val list = mutableListOf<Map<String, String>>()
        val cursor = db.rawQuery(
            """
            SELECT a.*
            FROM $TABLE_ALUNOS a
            JOIN $TABLE_ALUNO_ROTA ar ON ar.aluno_id = a.id
            WHERE ar.rota_id = ?
            """.trimIndent(),
            arrayOf(rotaId.toString())
        )
        if (cursor.moveToFirst()) {
            do {
                val row = mutableMapOf<String, String>()
                for (i in 0 until cursor.columnCount) {
                    row[cursor.getColumnName(i)] = cursor.getString(i) ?: ""
                }
                list.add(row)
            } while (cursor.moveToNext())
        }
        cursor.close(); db.close()
        return list
    }

    // Alunos NÃO vinculados a uma rota (útil para montar checklist)
    fun getAlunosForaDaRota(rotaId: Int): List<Map<String, String>> {
        val db = readableDatabase
        val list = mutableListOf<Map<String, String>>()
        val cursor = db.rawQuery(
            """
            SELECT a.*
            FROM $TABLE_ALUNOS a
            WHERE a.id NOT IN (
                SELECT aluno_id FROM $TABLE_ALUNO_ROTA WHERE rota_id = ?
            )
            """.trimIndent(),
            arrayOf(rotaId.toString())
        )
        if (cursor.moveToFirst()) {
            do {
                val row = mutableMapOf<String, String>()
                for (i in 0 until cursor.columnCount) {
                    row[cursor.getColumnName(i)] = cursor.getString(i) ?: ""
                }
                list.add(row)
            } while (cursor.moveToNext())
        }
        cursor.close(); db.close()
        return list
    }

    // Vincular aluno à rota (insere N:N)
    fun insertAlunoNaRota(alunoId: Int, rotaId: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("aluno_id", alunoId)
            put("rota_id", rotaId)
        }
        val result = db.insert(TABLE_ALUNO_ROTA, null, values)
        db.close()
        return result
    }

    // Remover vínculo aluno⇄rota
    fun removeAlunoDaRota(alunoId: Int, rotaId: Int): Int {
        val db = writableDatabase
        val result = db.delete(
            TABLE_ALUNO_ROTA,
            "aluno_id = ? AND rota_id = ?",
            arrayOf(alunoId.toString(), rotaId.toString())
        )
        db.close()
        return result
    }
}