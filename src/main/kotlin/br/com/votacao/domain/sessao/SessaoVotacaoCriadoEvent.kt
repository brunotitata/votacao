package br.com.votacao.domain.sessao

import br.com.votacao.domain.DomainEvent
import java.util.*

data class SessaoVotacaoCriadoEvent(val sessaoId: UUID) : DomainEvent {

    private val eventType = "votacao-service.sessao.criado"

    override fun aggregateId(): String = sessaoId.toString()

    override fun eventType(): String = eventType

    override fun eventVersion(): Int = 1

}