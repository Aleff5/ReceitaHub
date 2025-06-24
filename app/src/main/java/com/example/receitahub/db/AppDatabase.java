package com.example.receitahub.db;


import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.receitahub.data.model.Receita;
import com.example.receitahub.db.dao.ReceitaDao;

@Database(entities = {Receita.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase{
    public  abstract ReceitaDao receitaDao();
    private static volatile AppDatabase INSTANCE;
    public static AppDatabase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (AppDatabase.class) {
                if  (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "receitas_app_db").build();
                }
            }
        }
        return  INSTANCE;
    }

}