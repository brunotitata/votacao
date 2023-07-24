package br.com.votacao.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

typealias EventId = UUID

@Entity
@Table(name = "consumed_events")
data class ConsumedEvent(
        @Id
        val eventId: EventId,
        val occurredOn: OffsetDateTime
)

@JsonIgnoreProperties("eventId", "aggregateId", "eventType", "eventVersion", "occurredOn")
interface DomainEvent {

    fun eventId(): EventId {
        return UUID.randomUUID()
    }

    fun aggregateId(): String

    fun occurredOn(): OffsetDateTime {
        return OffsetDateTime.now(ZoneId.systemDefault())
    }

    fun eventType(): String

    fun eventVersion(): Int

    fun payload(): String? = null

}

interface DomainEventPublisher {
    fun publish(domainEvent: DomainEvent)
}