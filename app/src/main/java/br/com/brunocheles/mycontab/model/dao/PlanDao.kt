package br.com.brunocheles.mycontab.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import br.com.brunocheles.mycontab.model.entities.PlansEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {
    @Query("SELECT * FROM plans_tb WHERE plan_user_id = (:userId)")
    fun getAllPlans(userId: String): Flow<List<PlansEntity>>

    @Insert
    fun insertPlan(plan: PlansEntity)

    @Delete
    fun deletePlan(plan: PlansEntity)
}