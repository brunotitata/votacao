package br.com.votacao.domain.voto

import br.com.votacao.domain.DomainRegistry
import br.com.votacao.domain.associado.Associado
import br.com.votacao.domain.pauta.Pauta
import java.util.*
import javax.persistence.*

@Entity
data class Voto(
        @Id
        @Column(name = "voto_id")
        val votoId: UUID,
        @ManyToOne
        @JoinColumn(name = "associado_id")
        val associado: Associado,
        @ManyToOne
        @JoinColumn(name = "pauta_id")
        val pauta: Pauta,
        @Enumerated(EnumType.STRING)
        val voto: VotoEnum
) {

    companion object {
        fun realizarVoto(pauta: Pauta, associado: Associado, voto: VotoEnum): Voto {

            val novoVoto = Voto(
                    votoId = UUID.randomUUID(),
                    pauta = pauta,
                    associado = associado,
                    voto = voto
            )

            DomainRegistry.domainEventPublisher()
                    .publish(
                            VotoRealizadoEvent(
                                    votoId = novoVoto.votoId
                            )
                    )

            return novoVoto
        }

    }

    override fun toString(): String {
        return "Voto(votoId=$votoId, voto=$voto)"
    }

}

enum class VotoEnum {
    SIM, NAO
}