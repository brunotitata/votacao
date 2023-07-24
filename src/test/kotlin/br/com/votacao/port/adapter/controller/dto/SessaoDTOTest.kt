package br.com.votacao.port.adapter.controller.dto

import br.com.votacao.domain.DomainExceptions.SessaoException
import br.com.votacao.port.adapters.controller.dto.SessaoDTO
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import org.junit.jupiter.api.assertThrows
import java.util.*

class SessaoDTOTest : AnnotationSpec() {

    @Test
    fun `deve criar objeto SessaoDTO`() {

        val pautaId = UUID.randomUUID()

        val sessaoDTO = SessaoDTO(pautaId = pautaId, duracaoEmMinutos = 10)

        sessaoDTO.pautaId.shouldBe(pautaId)
        sessaoDTO.duracaoEmMinutos.shouldBe(10)
    }

    @Test
    fun `quando duracaoEmMinutos for menor ou igual a zero, deve retornar exception`() {

        assertThrows<SessaoException> {
            SessaoDTO(pautaId = UUID.randomUUID(), duracaoEmMinutos = 0)
        }.shouldHaveMessage("Duração da sessão deve ser maior que zero.")

    }

}