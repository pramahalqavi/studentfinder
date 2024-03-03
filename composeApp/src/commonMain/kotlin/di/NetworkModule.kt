package di

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
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
      install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.HEADERS
        filter { request ->
          request.url.host.contains(Constants.BASE_URL_HOST)
        }
        sanitizeHeader { header -> header == HttpHeaders.Authorization }
      }
    }
  }
}