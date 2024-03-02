package utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlin.coroutines.CoroutineContext

class SchedulerProvider {
  fun io(): CoroutineContext {
    return Dispatchers.IO
  }

  fun ui(): CoroutineContext {
    return Dispatchers.Main
  }

  fun default(): CoroutineContext {
    return Dispatchers.Default
  }

  fun unconfined(): CoroutineContext {
    return Dispatchers.Unconfined
  }
}