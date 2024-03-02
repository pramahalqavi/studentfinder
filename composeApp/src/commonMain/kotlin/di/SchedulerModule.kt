package di

import org.koin.dsl.module
import utils.SchedulerProvider

val schedulerModule = module {
  single { SchedulerProvider() }
}