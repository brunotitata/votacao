package br.com.votacao.port.adapters.controller

import br.com.votacao.domain.DomainExceptions.AssociadoException
import br.com.votacao.domain.DomainExceptions.PautaException
import br.com.votacao.domain.DomainExceptions.SessaoException
import br.com.votacao.domain.DomainExceptions.VotoException
import br.com.votacao.port.adapters.controller.dto.ApiErrorDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler
    fun pautaException(pautaException: PautaException): ResponseEntity<ApiErrorDTO> {
        return ResponseEntity
                .status(pautaException.httpStatus ?: HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorDTO(errorMessage = pautaException.msg))
    }

    @ExceptionHandler
    fun associadoException(associadoException: AssociadoException): ResponseEntity<ApiErrorDTO> {
        return ResponseEntity
                .status(associadoException.httpStatus ?: HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorDTO(errorMessage = associadoException.msg))
    }

    @ExceptionHandler
    fun votoException(votoException: VotoException): ResponseEntity<ApiErrorDTO> {
        return ResponseEntity
                .status(votoException.httpStatus ?: HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorDTO(errorMessage = votoException.msg))
    }

    @ExceptionHandler
    fun sessaoException(sessaoException: SessaoException): ResponseEntity<ApiErrorDTO> {
        return ResponseEntity
                .status(sessaoException.httpStatus ?: HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorDTO(errorMessage = sessaoException.msg))
    }

}