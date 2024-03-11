package com.example

import com.example.model.ApiResponse
import com.example.plugins.configureRouting
import com.example.repository.HeroRepository
import com.example.repository.NEXT_PAGE_KEY
import com.example.repository.PREV_PAGE_KEY
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.inject
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    private val heroRepository: HeroRepository by inject(HeroRepository::class.java)

    @Test
    fun `access root endpoint assert correct information`() = testApplication {
        application {
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(expected = "Welcome to Boruto API!", bodyAsText())
        }
    }

    @Test
    fun `access all heroes endpoint assert correct information`() = testApplication {
        environment {
            developmentMode = false
        }
        application {
            configureRouting()
        }
        client.get("/boruto/heroes").apply {
            assertEquals(HttpStatusCode.OK, status)
            val expected = ApiResponse(
                success = true,
                message = "OK",
                prevPage = null,
                nextPage = 2,
                heroes = heroRepository.page1
            )

            val actual = Json.decodeFromString<ApiResponse>(bodyAsText())
            assertEquals(
                expected = expected,
                actual = actual
            )
        }
    }

    @Test
    fun `access all heroes endpoint, query 2nd page, assert correct information`() = testApplication {
        environment {
            developmentMode = false
        }
        application {
            configureRouting()
        }
        client.get("/boruto/heroes?page=2").apply {
            assertEquals(HttpStatusCode.OK, status)
            val expected = ApiResponse(
                success = true,
                message = "OK",
                prevPage = 1,
                nextPage = 3,
                heroes = heroRepository.page2
            )

            val actual = Json.decodeFromString<ApiResponse>(bodyAsText())
            assertEquals(
                expected = expected,
                actual = actual
            )
        }
    }

    @Test
    fun `access all heroes endpoint, query all page, assert correct information`() = testApplication {
        environment {
            developmentMode = false
        }
        application {
            configureRouting()
        }

        val page = 1..5
        val heroes = listOf(
            heroRepository.page1,
            heroRepository.page2,
            heroRepository.page3,
            heroRepository.page4,
            heroRepository.page5
        )
        page.forEach {
            client.get("/boruto/heroes?page=$it").apply {
                assertEquals(HttpStatusCode.OK, status)

                val expected = ApiResponse(
                    success = true,
                    message = "OK",
                    prevPage = calculatePage(it)["previousPage"],
                    nextPage = calculatePage(it)["nextPage"],
                    heroes = heroes[it - 1]
                )

                val actual = Json.decodeFromString<ApiResponse>(bodyAsText())
                assertEquals(expected, actual)
            }
        }

    }

    private fun calculatePage(page: Int): Map<String, Int?> {
        var prevPage: Int? = page
        var nextPage: Int? = page
        if (page in 1..4) {
            nextPage = nextPage?.plus(1)
        }
        if (page in 2..5) {
            prevPage = prevPage?.minus(1)
        }
        if (page == 1) {
            prevPage = null
        }

        if (page == 5) {
            nextPage = null
        }

        return mapOf(PREV_PAGE_KEY to prevPage, NEXT_PAGE_KEY to nextPage)
    }
}
