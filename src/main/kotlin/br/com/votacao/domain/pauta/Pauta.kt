package br.com.votacao.domain.pauta

import br.com.votacao.domain.DomainExceptions.PautaException
import br.com.votacao.domain.DomainRegistry
import br.com.votacao.domain.voto.Voto
import org.springframework.http.HttpStatus
import java.util.*
import javax.persistence.*

@Entity
data class Pauta(
    @Id
    @Column(name = "pauta_id")
    val pautaId: UUID,
    val titulo: String,
    @OneToMany(cascade = [(CascadeType.ALL)], fetch = FetchType.EAGER, mappedBy = "pauta")
    val votos: List<Voto> = emptyList(),
) {

    companion object {
        fun novaPauta(titulo: String): Pauta {

            if (titulo.isBlank() || titulo.isEmpty())
                throw PautaException(
                    msg = "Titulo da pauta não pode ser nulo ou vazio",
                    httpStatus = HttpStatus.BAD_REQUEST
                )

            val pauta = Pauta(
                pautaId = UUID.randomUUID(),
                titulo = titulo,
                votos = listOf()
            )

            DomainRegistry.domainEventPublisher()
                .publish(
                    PautaCriadoEvent(
                        pautaId = pauta.pautaId
                    )
                )

            return pauta
        }
    }

    override fun toString(): String {
        return "Pauta(pautaId=$pautaId, titulo='$titulo', votos=$votos)"
    }


}
