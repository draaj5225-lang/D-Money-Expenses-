package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Database(
  entities = [ExpenseEntity::class, CategoryEntity::class],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun expenseDao(): ExpenseDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context, coroutineScope: CoroutineScope): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "kharcha_tracker_database"
        )
        .addCallback(DatabaseSeedCallback(coroutineScope))
        .build()
        INSTANCE = instance
        instance
      }
    }
  }

  private class DatabaseSeedCallback(
    private val scope: CoroutineScope
  ) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
      super.onCreate(db)
      INSTANCE?.let { database ->
        scope.launch(Dispatchers.IO) {
          populateInitialData(database.expenseDao())
        }
      }
    }

    private suspend fun populateInitialData(dao: ExpenseDao) {
      // 1. Insert default categories
      val catEntities = Category.DEFAULT_CATEGORIES.map { CategoryEntity.fromDomain(it) }
      dao.insertCategories(catEntities)

      // 2. Pre-populate realistic seed expenses for Indian daily life
      val cal = Calendar.getInstance()
      val dayFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)
      val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)

      val seedList = mutableListOf<ExpenseEntity>()

      // Helper to compute day and month
      fun makeExpense(
        amount: Double,
        cat: Category,
        daysAgo: Int,
        notes: String,
        mode: String
      ): ExpenseEntity {
        val c = Calendar.getInstance()
        c.add(Calendar.DAY_OF_YEAR, -daysAgo)
        val time = c.timeInMillis
        return ExpenseEntity(
          amount = amount,
          categoryId = cat.id,
          categoryNameEn = cat.nameEn,
          categoryNameHi = cat.nameHi,
          iconName = cat.iconName,
          dateMillis = time,
          dayOfWeek = dayFormat.format(c.time),
          monthYear = monthFormat.format(c.time),
          notes = notes,
          paymentMode = mode
        )
      }

      val cats = Category.DEFAULT_CATEGORIES.associateBy { it.id }

      // Today
      cats["milk"]?.let {
        seedList.add(makeExpense(66.0, it, 0, "Amul Gold 1L Milk", "UPI"))
      }
      cats["grocery"]?.let {
        seedList.add(makeExpense(350.0, it, 0, "Fresh Vegetables, Onions & Coriander", "Cash"))
      }

      // Yesterday
      cats["petrol"]?.let {
        seedList.add(makeExpense(500.0, it, 1, "Bike Petrol full tank HP Pump", "UPI"))
      }
      cats["bills"]?.let {
        seedList.add(makeExpense(699.0, it, 2, "JioFiber Broadband Bill", "UPI"))
      }
      cats["other"]?.let {
        seedList.add(makeExpense(180.0, it, 3, "Haircut & Grooming Salon", "UPI"))
      }
      cats["grocery"]?.let {
        seedList.add(makeExpense(1250.0, it, 4, "Aashirvaad Atta, Fortune Mustard Oil & Basmati Rice", "Card"))
      }
      cats["milk"]?.let {
        seedList.add(makeExpense(66.0, it, 4, "Cow Milk 1L Pack", "UPI"))
      }
      cats["bills"]?.let {
        seedList.add(makeExpense(1420.0, it, 8, "Monthly Electricity Bill (BSES)", "UPI"))
      }
      cats["petrol"]?.let {
        seedList.add(makeExpense(400.0, it, 12, "Petrol refueling IndianOil", "UPI"))
      }
      cats["grocery"]?.let {
        seedList.add(makeExpense(890.0, it, 15, "Dry fruits, Masalas, Tea packet", "UPI"))
      }

      dao.insertExpenses(seedList)
    }
  }
}
