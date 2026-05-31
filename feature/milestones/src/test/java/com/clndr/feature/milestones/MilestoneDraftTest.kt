package com.clndr.feature.milestones

import com.clndr.feature.milestones.model.MilestoneDraft
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDate

class MilestoneDraftTest {

    @Test
    fun `validate flags missing title`() {
        val draft = MilestoneDraft(title = "", targetDate = LocalDate.now())
        val errors = draft.validate()
        assertThat(errors).containsKey("title")
    }

    @Test
    fun `validate flags negative lead`() {
        val draft = MilestoneDraft(title = "Demo", reminderLeadMinutes = -1)
        assertThat(draft.validate()).containsKey("lead")
    }

    @Test
    fun `valid draft has no errors`() {
        val draft = MilestoneDraft(title = "Demo", targetDate = LocalDate.of(2026, 12, 31))
        assertThat(draft.validate()).isEmpty()
    }
}
