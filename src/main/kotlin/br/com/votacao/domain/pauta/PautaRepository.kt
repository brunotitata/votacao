package br.com.votacao.domain.pauta

import java.util.*

interface PautaRepository {

    fun salvar(pauta: Pauta)
    fun buscarPautaPorId(pautaId: UUID) : Pauta?
    fun buscarPautaPorTitulo(titulo: String) : Pauta?

}