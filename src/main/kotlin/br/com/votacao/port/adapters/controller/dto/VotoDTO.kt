package br.com.votacao.port.adapters.controller.dto

import br.com.votacao.domain.voto.VotoEnum
import java.util.*

data class VotoDTO(
        val associadoId: UUID,
        val voto: VotoEnum
)
