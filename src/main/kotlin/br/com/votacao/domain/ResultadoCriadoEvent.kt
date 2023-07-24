package br.com.votacao.domain

import java.util.*

data class ResultadoCriadoEvent(val payload: String) : DomainEvent {

    private val eventType = "votacao-service.resultado.criado"

    override fun aggregateId(): String = UUID.randomUUID().toString()

    override fun eventType(): String = eventType

    override fun eventVersion(): Int = 1

    override fun payload(): String = payload

}