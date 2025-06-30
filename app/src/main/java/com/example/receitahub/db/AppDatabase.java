package com.example.receitahub.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.receitahub.data.model.Receita;
import com.example.receitahub.db.dao.ReceitaDao;
import com.example.receitahub.data.model.User;
import com.example.receitahub.db.dao.UserDao;

// Imports necessários para o Executor
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {Receita.class, User.class},
        version = 8,
        exportSchema = true
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ReceitaDao receitaDao();
    public abstract UserDao userDao();

    private static volatile AppDatabase INSTANCE;

    // --- CÓDIGO ADICIONADO PARA CORRIGIR O ERRO 3 ---
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    // ----------------------------------------------------

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "receitas_app_db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}