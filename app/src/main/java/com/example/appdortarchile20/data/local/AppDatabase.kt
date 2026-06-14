package com.example.appdortarchile20.data.local

import android.content.Context
import androidx.room.*
import com.example.appdortarchile20.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // --- USUARIOS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registerUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    // --- MASCOTAS ---
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPet(pet: Pet)

    @Query("SELECT * FROM pets WHERE eliminado = 0")
    fun getAllPets(): Flow<List<Pet>>

    @Query("SELECT * FROM pets WHERE eliminado = 1")
    fun getPetsEliminadas(): Flow<List<Pet>>

    @Query("SELECT * FROM pets")
    fun getAllPetsHistorial(): Flow<List<Pet>>

    @Query("UPDATE pets SET eliminado = 1 WHERE id = :id")
    suspend fun marcarPetEliminada(id: Int)

    @Query("UPDATE pets SET eliminado = 0 WHERE id = :id")
    suspend fun restaurarPet(id: Int)

    @Delete
    suspend fun deletePet(pet: Pet)

    @Query("DELETE FROM pets")
    suspend fun deleteAllPets()

    // --- URGENCIAS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReporte(reporte: UrgenciaReporte)

    @Delete
    suspend fun deleteReporte(reporte: UrgenciaReporte)

    @Query("SELECT * FROM urgencias WHERE id = :id LIMIT 1")
    suspend fun getReporteById(id: Int): UrgenciaReporte?

    @Query("UPDATE urgencias SET resuelta = 1 WHERE id = :id")
    suspend fun marcarResuelta(id: Int)

    @Query("SELECT * FROM urgencias ORDER BY horaReporte DESC")
    fun getAllReportes(): Flow<List<UrgenciaReporte>>

    // --- MENSAJES ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMensaje(mensaje: Mensaje)

    @Query("""
        SELECT * FROM mensajes 
        WHERE petId = :petId AND (
            (remitenteEmail = :email1 AND destinatarioEmail = :email2) OR
            (remitenteEmail = :email2 AND destinatarioEmail = :email1)
        )
        ORDER BY timestamp ASC
    """)
    fun getMensajesChat(petId: Int, email1: String, email2: String): Flow<List<Mensaje>>

    @Query("""
        SELECT * FROM mensajes 
        WHERE remitenteEmail = :email OR destinatarioEmail = :email
        ORDER BY timestamp DESC
    """)
    fun getMensajesUsuario(email: String): Flow<List<Mensaje>>

    @Query("SELECT COUNT(*) FROM mensajes WHERE destinatarioEmail = :email AND leido = 0")
    fun getMensajesNoLeidos(email: String): Flow<Int>

    @Query("UPDATE mensajes SET leido = 1 WHERE petId = :petId AND destinatarioEmail = :email")
    suspend fun marcarComoLeidos(petId: Int, email: String)

    // --- EVALUACIONES ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvaluacion(evaluacion: Evaluacion)

    @Query("SELECT * FROM evaluaciones WHERE evaluadoEmail = :email")
    fun getEvaluacionesUsuario(email: String): Flow<List<Evaluacion>>

    @Query("SELECT AVG(estrellas) FROM evaluaciones WHERE evaluadoEmail = :email")
    fun getPromedioEstrellas(email: String): Flow<Float?>

    @Query("SELECT * FROM evaluaciones WHERE petId = :petId AND evaluadorEmail = :evaluadorEmail LIMIT 1")
    suspend fun getEvaluacionExistente(petId: Int, evaluadorEmail: String): Evaluacion?

    // --- TARJETAS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTarjeta(tarjeta: TarjetaGuardada)

    @Query("SELECT * FROM tarjetas WHERE userEmail = :email ORDER BY timestamp DESC LIMIT 1")
    suspend fun getTarjetaUsuario(email: String): TarjetaGuardada?

    @Query("DELETE FROM tarjetas WHERE userEmail = :email")
    suspend fun deleteTarjetasUsuario(email: String)
}

@Database(
    entities = [User::class, Pet::class, UrgenciaReporte::class, Mensaje::class, Evaluacion::class, TarjetaGuardada::class],
    version = 11,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "appdoptar_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}