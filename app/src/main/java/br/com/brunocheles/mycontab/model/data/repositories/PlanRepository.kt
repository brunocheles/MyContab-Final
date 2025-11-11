package br.com.brunocheles.mycontab.model.data.repositories

import br.com.brunocheles.mycontab.model.dao.PlanDao
import javax.inject.Inject

class PlanRepository @Inject constructor(
    private val planDao: PlanDao
) {

}
