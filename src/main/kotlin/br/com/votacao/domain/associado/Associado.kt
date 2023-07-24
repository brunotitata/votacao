package br.com.votacao.domain.associado

import br.com.votacao.domain.DomainExceptions.AssociadoException
import br.com.votacao.domain.DomainRegistry
import br.com.votacao.domain.pauta.Pauta
import org.springframework.http.HttpStatus
import java.util.*
import javax.persistence.*

@Entity
data class Associado(
    @Id
    @Column(name = "associado_id")
    val associadoId: UUID,
    val nome: String,
    val cpf: String,
    @ManyToMany
    @JoinTable(
        name = "voto_associado",
        joinColumns = [JoinColumn(name = "associado_id")],
        inverseJoinColumns = [JoinColumn(name = "pauta_id")]
    )
    val pautas: List<Pauta> = emptyList()
) {
    companion object {
        fun novoAssociado(nome: String, cpf: String): Associado {

            if (nome.isBlank() || nome.isEmpty())
                throw AssociadoException(
                    msg = "Nome não pode ser nulo ou vazio",
                    httpStatus = HttpStatus.BAD_REQUEST
                )

            if (cpf.isBlank() || cpf.isEmpty())
                throw AssociadoException(
                    msg = "Cpf não pode ser nulo ou vazio",
                    httpStatus = HttpStatus.BAD_REQUEST
                )

            val novoAssociado = Associado(
                associadoId = UUID.randomUUID(),
                nome = nome,
                cpf = cpf
            )

            DomainRegistry.domainEventPublisher()
                .publish(
                    AssociadoCriadoEvent(
                        associadoId = novoAssociado.associadoId
                    )
                )

            return novoAssociado
        }
    }

    override fun toString(): String {
        return "Associado(associadoId=$associadoId, nome='$nome', cpf='$cpf', pautas=$pautas)"
    }


}
