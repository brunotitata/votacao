package br.com.votacao.domain.associado

import br.com.votacao.domain.DomainEvent
import java.util.*

data class AssociadoCriadoEvent(val associadoId: UUID) : DomainEvent {

    private val eventType = "votacao-service.associado.criado"

    override fun aggregateId(): String = associadoId.toString()

    override fun eventType(): String = eventType

    override fun eventVersion(): Int = 1

}