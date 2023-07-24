package br.com.votacao.port.adapters.controller

import br.com.votacao.application.AssociadoApplicationService
import br.com.votacao.port.adapters.controller.dto.AssociadoDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@RestController
@RequestMapping("/v1/associado")
@Tag(name = "Endpoint - Associado")
class AssociadoController(
        private val associadoApplicationService: AssociadoApplicationService
) {

    @PostMapping
    @Operation(summary = "Endpoint responsavel por criar um novo associado")
    fun criarAssociado(@RequestBody associado: AssociadoDTO): ResponseEntity<Void> {

        val associadoId = associadoApplicationService.criarAssociado(associado)

        return ResponseEntity.created(
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{associadoId}")
                        .buildAndExpand(associadoId)
                        .toUri()
        ).build()
    }

}