package br.com.votacao.domain.sessao

import java.util.*

interface SessaoRepository {

    fun salvar(sessaoVotacao: SessaoVotacao)
    fun buscarSessaoPauta(pautaId: UUID) : SessaoVotacao?
    fun buscarSessao(sessaoId: UUID) : SessaoVotacao?
    fun buscarTodasSessaoVotacao() : List<SessaoVotacao>

}