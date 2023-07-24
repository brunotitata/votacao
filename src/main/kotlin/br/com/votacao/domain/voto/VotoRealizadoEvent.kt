package br.com.votacao.domain.voto

import br.com.votacao.domain.DomainEvent
import java.util.*

data class VotoRealizadoEvent(val votoId: UUID) : DomainEvent {

    private val eventType = "votacao-service.voto.criado"

    override fun aggregateId(): String = votoId.toString()

    override fun eventType(): String = eventType

    override fun eventVersion(): Int = 1

}