package br.com.votacao.port.adapters.message

import br.com.votacao.domain.DomainEvent
import br.com.votacao.domain.DomainEventPublisher
import mu.KotlinLogging
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component

@Component
class EventMessagingAdapter(
        private val bridge: StreamBridge
) : DomainEventPublisher {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    override fun publish(domainEvent: DomainEvent) {

        logger.info("Enviando evento de dominio... {}", domainEvent)

        bridge.send("publisher-out-0",
                MessageBuilder
                        .withPayload(domainEvent)
                        .setHeader(DomainEvent::eventType.name, domainEvent.eventType())
                        .setHeader(DomainEvent::eventId.name, domainEvent.eventId().toString())
                        .setHeader(DomainEvent::aggregateId.name, domainEvent.aggregateId())
                        .setHeader(DomainEvent::eventVersion.name, domainEvent.eventVersion())
                        .setHeader(DomainEvent::occurredOn.name, domainEvent.occurredOn())
                        .build()
        )
    }
}