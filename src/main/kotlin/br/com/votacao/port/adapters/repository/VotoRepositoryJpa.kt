package br.com.votacao.port.adapters.repository

import br.com.votacao.domain.associado.Associado
import br.com.votacao.domain.pauta.Pauta
import br.com.votacao.domain.voto.Voto
import br.com.votacao.domain.voto.VotoRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

interface VotoRepositorySpringData : JpaRepository<Voto, UUID> {

    fun findByPautaAndAssociado(pauta: Pauta, associado: Associado, ): Voto?

}

@Repository
class VotoRepositoryJpa(
        private val repository: VotoRepositorySpringData
) : VotoRepository {
    override fun salvar(voto: Voto) {
        repository.save(voto)
    }

    override fun validar(associado: Associado, pauta: Pauta): Voto? {
        return repository.findByPautaAndAssociado(
                pauta = pauta,
                associado = associado
        )
    }

}