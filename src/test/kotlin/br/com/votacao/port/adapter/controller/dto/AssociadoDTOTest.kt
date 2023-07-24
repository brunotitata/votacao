package br.com.votacao.port.adapter.controller.dto

import br.com.votacao.domain.IsCPF
import br.com.votacao.port.adapters.controller.dto.AssociadoDTO
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import org.valiktor.constraints.NotBlank
import org.valiktor.test.shouldFailValidation

class AssociadoDTOTest : AnnotationSpec() {

    @Test
    fun deveCriarObjeto() {

        val associadoDTO = AssociadoDTO(
            nome = "Luiz Eduardo",
            cpf = "39624537097"
        )

        associadoDTO.nome.shouldBe("Luiz Eduardo")
        associadoDTO.cpf.shouldBe("39624537097")

    }

    @Test
    fun quandoCpfForInvalidoDeveLancarException() {

        shouldFailValidation<AssociadoDTO> {
            AssociadoDTO(
                nome = "Luiz Eduardo",
                cpf = ""
            )
        }.verify {
            expect(AssociadoDTO::cpf, "", IsCPF)
            expect(AssociadoDTO::cpf, "", NotBlank)
        }

        shouldFailValidation<AssociadoDTO> {
            AssociadoDTO(
                nome = "Luiz Eduardo",
                cpf = "1234567898"
            )
        }.verify {
            expect(AssociadoDTO::cpf, "1234567898", IsCPF)
        }

    }
}