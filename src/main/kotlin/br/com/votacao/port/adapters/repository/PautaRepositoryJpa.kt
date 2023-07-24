package br.com.votacao.port.adapters.repository

import br.com.votacao.domain.pauta.Pauta
import br.com.votacao.domain.pauta.PautaRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

interface PautaRepositorySpringData : JpaRepository<Pauta, UUID> {

    fun findByPautaId(pautaId: UUID): Pauta?
    fun findByTitulo(titulo: String): Pauta?

}

@Repository
class PautaRepositoryJpa(
    private val repository: PautaRepositorySpringData
) : PautaRepository {

    override fun salvar(pauta: Pauta) {
        repository.save(pauta)
    }

    override fun buscarPautaPorId(pautaId: UUID): Pauta? {
        return repository.findByPautaId(pautaId)
    }

    override fun buscarPautaPorTitulo(titulo: String): Pauta? {
        return repository.findByTitulo(titulo)
    }

}