package com.github.manosbatsis.services.event.service

import com.github.manosbatsis.services.event.model.UserEvent
import com.github.manosbatsis.services.event.model.UserEventKey
import com.github.manosbatsis.services.event.repository.UserEventRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.Date

@ExtendWith(SpringExtension::class)
@Import(UserEventServiceImpl::class)
class UserEventServiceImplTest {
    @Autowired
    private lateinit var userEventService: UserEventService

    @MockBean
    private lateinit var userEventRepository: UserEventRepository

    @Test
    fun testGetUserEventsWhenThereIsNone() {
        BDDMockito.given(
            userEventRepository.findByKeyUserId(ArgumentMatchers.anyLong()),
        ).willReturn(emptyList())
        val userEvents = userEventService.findByUserId(1L)
        Assertions.assertThat(userEvents).isNotNull()
        Assertions.assertThat(userEvents).isEmpty()
    }

    @Test
    fun testGetUserEventsWhenThereIsOne() {
        val userEvent = defaultUserEvent
        BDDMockito.given(
            userEventRepository.findByKeyUserId(ArgumentMatchers.anyLong()),
        )
            .willReturn(listOf(userEvent))
        val userEvents = userEventService.findByUserId(1L)
        Assertions.assertThat(userEvents).isNotNull()
        Assertions.assertThat(userEvents.size).isEqualTo(1)
        Assertions.assertThat(userEvents[0]).isEqualTo(userEvent)
    }

    @Test
    fun testSaveUserEvent() {
        val userEvent = defaultUserEvent
        BDDMockito.given(
            userEventRepository.save(
                ArgumentMatchers.any(
                    UserEvent::class.java,
                ),
            ),
        ).willReturn(userEvent)
        val userEventSaved = userEventService.save(userEvent)
        Assertions.assertThat(userEventSaved).isNotNull()
        Assertions.assertThat(userEventSaved).isEqualTo(userEvent)
    }

    private val defaultUserEvent: UserEvent
        private get() = UserEvent(UserEventKey(1L, Date()), "type", "data")
}
