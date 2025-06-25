package com.example.receitahub.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.receitahub.data.model.Receita;
import com.example.receitahub.db.dao.ReceitaDao;
import com.example.receitahub.data.model.User;
import com.example.receitahub.db.dao.UserDao;

@Database(
        entities = {Receita.class, User.class},
        version = 6, // ALTERADO: Versão incrementada de 5 para 6
        exportSchema = true
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ReceitaDao receitaDao();
    public abstract UserDao userDao();

    private static volatile AppDatabase INSTANCE;

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