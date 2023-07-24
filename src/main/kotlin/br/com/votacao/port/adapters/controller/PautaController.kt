package br.com.votacao.port.adapters.controller

import br.com.votacao.application.PautaApplicationService
import br.com.votacao.port.adapters.controller.dto.PautaDTO
import br.com.votacao.port.adapters.controller.dto.VotoDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*

@RestController
@RequestMapping("/v1/pauta")
@Tag(name = "Endpoint - Pauta")
class PautaController(
        val pautaApplicationService: PautaApplicationService,
) {

    @PostMapping
    @Operation(summary = "Endpoint responsavel por cadastrar uma nova pauta")
    fun cadastrarPauta(@RequestBody pauta: PautaDTO): ResponseEntity<Void> {

        val pautaId = pautaApplicationService.cadastrarPauta(pauta)

        return ResponseEntity.created(
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{pautaId}")
                        .buildAndExpand(pautaId)
                        .toUri()
        ).build()
    }

    @PostMapping("/{pautaId}/associado/{associadoId}/votar")
    @Operation(summary = "Endpoint responsavel por realizar a votação por uma pauta especifica")
    fun votar(@PathVariable pautaId: UUID, @PathVariable associadoId: UUID, @RequestBody voto: VotoDTO): ResponseEntity<Void> {

        val votoId = pautaApplicationService.votar(
                pautaId = pautaId,
                associadoId = associadoId,
                voto = voto
        )

        return ResponseEntity.created(
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{votoId}")
                        .buildAndExpand(votoId)
                        .toUri()
        ).build()
    }


}