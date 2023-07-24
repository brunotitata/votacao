package br.com.votacao.port.adapters.repository

import br.com.votacao.domain.sessao.SessaoRepository
import br.com.votacao.domain.sessao.SessaoVotacao
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

interface SessaoRepositorySpringData : JpaRepository<SessaoVotacao, UUID> {

    fun findByPautaId(pautaId: UUID): SessaoVotacao?
    fun findBySessaoId(sessaoId: UUID): SessaoVotacao?

}

@Repository
class SessaoRepositoryJpa(
        private val repository: SessaoRepositorySpringData
) : SessaoRepository {

    override fun salvar(sessaoVotacao: SessaoVotacao) {
        repository.save(sessaoVotacao)
    }

    override fun buscarSessaoPauta(pautaId: UUID): SessaoVotacao? {
        return repository.findByPautaId(pautaId)
    }

    override fun buscarSessao(sessaoId: UUID): SessaoVotacao? {
        return repository.findBySessaoId(sessaoId)
    }

    override fun buscarTodasSessaoVotacao(): List<SessaoVotacao> {
        return repository.findAll()
    }

}