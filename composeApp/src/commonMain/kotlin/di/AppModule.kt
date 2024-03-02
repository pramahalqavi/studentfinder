package di


fun appModule() = listOf(
  networkModule,
  viewModelModule,
  repositoryModule,
  schedulerModule
)