package br.com.votacao.domain.pauta

import br.com.votacao.domain.DomainEvent
import java.util.*

data class PautaCriadoEvent(val pautaId: UUID) : DomainEvent {

    private val eventType = "votacao-service.pauta.criado"

    override fun aggregateId(): String = pautaId.toString()

    override fun eventType(): String = eventType

    override fun eventVersion(): Int = 1

}