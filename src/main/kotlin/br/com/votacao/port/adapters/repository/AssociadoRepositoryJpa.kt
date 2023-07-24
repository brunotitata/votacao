package br.com.votacao.port.adapters.repository

import br.com.votacao.domain.associado.Associado
import br.com.votacao.domain.associado.AssociadoRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

interface AssociadoRepositorySpringData : JpaRepository<Associado, UUID> {

    fun findByCpf(cpf: String): Associado?
    fun findByAssociadoId(associadoId: UUID): Associado?
}

@Repository
class AssociadoRepositoryJpa(
        private val repository: AssociadoRepositorySpringData
) : AssociadoRepository {

    override fun salvar(associado: Associado) {
        repository.save(associado)
    }

    override fun buscarAssociadoPorCpf(cpf: String): Associado? {
        return repository.findByCpf(cpf)
    }

    override fun buscarAssociado(associadoId: UUID): Associado? {
        return repository.findByAssociadoId(associadoId)
    }

}