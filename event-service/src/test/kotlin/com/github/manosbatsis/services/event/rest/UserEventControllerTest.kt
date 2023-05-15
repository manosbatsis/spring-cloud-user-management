package com.github.manosbatsis.services.event.rest

import com.github.manosbatsis.lib.test.MyLocalDateHandler
import com.github.manosbatsis.services.event.mapper.UserMapper
import com.github.manosbatsis.services.event.model.UserEvent
import com.github.manosbatsis.services.event.model.UserEventKey
import com.github.manosbatsis.services.event.service.UserEventService
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import java.util.Date

@WebMvcTest(UserEventController::class)
@Import(UserMapper::class)
@ActiveProfiles("test")
class UserEventControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var userEventService: UserEventService

    @Test
    @Throws(Exception::class)
    fun testGetUserEventsWhenThereIsNone() {
        BDDMockito.given(
            userEventService.findByUserId(ArgumentMatchers.anyLong()),
        ).willReturn(emptyList())
        val resultActions =
            mockMvc.perform(MockMvcRequestBuilders.get("/api/events?userId=1")).andDo(MockMvcResultHandlers.print())
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Any>(0)))
    }

    @Test
    @Throws(Exception::class)
    fun testGetUserEventsWhenThereIsOne() {
        val userEvent = UserEvent(UserEventKey(1L, Date()), "type", "data")
        BDDMockito.given(
            userEventService.findByUserId(ArgumentMatchers.anyLong()),
        )
            .willReturn(listOf(userEvent))
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/events?userId=" + 1))
            .andDo(MockMvcResultHandlers.print())
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath<Collection<*>>("$", Matchers.hasSize<Any>(1)))
            .andExpect(jsonPath("$[0].userId", Matchers.`is`(userEvent.key.userId.toInt())))
            .andExpect(jsonPath("$[0].data", Matchers.`is`(userEvent.data)))
            .andExpect(jsonPath("$[0].type", Matchers.`is`(userEvent.type)))
            .andExpect(
                jsonPath<String>(
                    "$[0].datetime",
                    Matchers.startsWith(
                        MyLocalDateHandler.fromDateToString(
                            userEvent.key.datetime,
                        ).substring(0, 26),
                    ),
                ),
            )
    }
}
