package br.com.votacao.port.adapters.controller.dto

data class ResultadoVotacaoDTO(
        val pauta: String,
        val sim: Int,
        val nao: Int,
        val total: Int
)
