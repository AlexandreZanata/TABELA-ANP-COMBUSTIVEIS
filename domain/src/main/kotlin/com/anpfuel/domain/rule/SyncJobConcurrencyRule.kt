package com.anpfuel.domain.rule

import com.anpfuel.domain.exception.DomainException
import com.anpfuel.domain.state.SyncJobState

/**
 * BR-015 — Only one active SyncJob at a time.
 */
object SyncJobConcurrencyRule {

    fun validateCanStartSync(currentState: SyncJobState) {
        if (currentState.isActive) {
            throw DomainException(
                "BR-015: Cannot start sync while another job is active in state ${currentState.name}",
            )
        }
    }

    fun canStartSync(currentState: SyncJobState): Boolean = !currentState.isActive
}
