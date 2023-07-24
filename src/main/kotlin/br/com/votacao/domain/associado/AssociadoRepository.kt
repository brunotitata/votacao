package br.com.votacao.domain.associado

import java.util.*

interface AssociadoRepository {

    fun salvar(associado: Associado)

    fun buscarAssociadoPorCpf(cpf: String) : Associado?

    fun buscarAssociado(associadoId: UUID) : Associado?

}