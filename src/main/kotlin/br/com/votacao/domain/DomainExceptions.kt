package br.com.votacao.domain

import org.springframework.http.HttpStatus


object DomainExceptions {

    data class PautaException(val msg: String, val httpStatus: HttpStatus? = null) : RuntimeException(msg)
    data class AssociadoException(val msg: String, val httpStatus: HttpStatus? = null) : RuntimeException(msg)
    data class VotoException(val msg: String, val httpStatus: HttpStatus? = null) : RuntimeException(msg)
    data class SessaoException(val msg: String, val httpStatus: HttpStatus? = null) : RuntimeException(msg)

}