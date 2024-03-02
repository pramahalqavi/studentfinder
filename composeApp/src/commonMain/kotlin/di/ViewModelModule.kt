package di

import org.koin.dsl.module
import screenmodel.HomeScreenModel
import screenmodel.StudentDetailScreenModel

val viewModelModule = module {
  factory { HomeScreenModel(get(), get()) }
  factory { StudentDetailScreenModel(get(), get()) }
}