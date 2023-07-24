package br.com.votacao.application

import br.com.votacao.domain.DomainExceptions.AssociadoException
import br.com.votacao.domain.associado.Associado
import br.com.votacao.domain.associado.AssociadoRepository
import br.com.votacao.port.adapters.controller.dto.AssociadoDTO
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.util.*

@Component
class AssociadoApplicationService(
        private val associadoRepository: AssociadoRepository
) {
    fun criarAssociado(associado: AssociadoDTO): UUID {

        associadoRepository.buscarAssociadoPorCpf(cpf = associado.cpf)
                .takeIf { it != null }?.let {
                    throw AssociadoException(
                            msg = "Associado com cpf: ${associado.cpf} já cadastrado",
                            httpStatus = HttpStatus.CONFLICT
                    )
                }

        val novoAssociado = Associado.novoAssociado(
                nome = associado.nome,
                cpf = associado.cpf
        )

        associadoRepository.salvar(
                associado = novoAssociado
        )

        return novoAssociado.associadoId

    }

}