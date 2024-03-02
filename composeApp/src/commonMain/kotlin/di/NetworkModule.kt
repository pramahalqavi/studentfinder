package di

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.EndpointConfig
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import utils.Constants

val networkModule = module {
  single {
    HttpClient(CIO) {
      install(ContentNegotiation) {
        json(json = Json { ignoreUnknownKeys = true }, contentType = ContentType.Any)
      }
    }
  }
}