package br.com.votacao.domain.voto

import br.com.votacao.domain.associado.Associado
import br.com.votacao.domain.pauta.Pauta

interface VotoRepository {

    fun salvar(voto: Voto)
    fun validar(associado: Associado, pauta: Pauta): Voto?

}