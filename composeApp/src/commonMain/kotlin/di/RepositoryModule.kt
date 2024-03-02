package di

import org.koin.dsl.module
import repository.StudentRepository

val repositoryModule = module {
  single { StudentRepository(get()) }
}