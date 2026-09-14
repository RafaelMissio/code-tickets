package br.com.missio.codeticks.validators;

import br.com.missio.codeticks.entities.Importacao;
import br.com.missio.codeticks.exceptions.ImportacaoInvalidaException;

import java.time.LocalDate;
import java.time.Period;

public final class ImportacaoValidator {

    private static final int IDADE_MINIMA = 18;

    private ImportacaoValidator() {
    }

    public static void validar(Importacao importacao) {
        if (importacao.getCliente() == null || importacao.getCliente().isBlank()) {
            throw new ImportacaoInvalidaException("Campo 'cliente' nao pode ser em branco");
        }

        if (importacao.getNascimento() == null
                || Period.between(importacao.getNascimento(), LocalDate.now()).getYears() < IDADE_MINIMA) {
            throw new ImportacaoInvalidaException(
                    "Cliente '%s' nao e maior de idade".formatted(importacao.getCliente()));
        }

        if (importacao.getEvento() == null || importacao.getEvento().isBlank()) {
            throw new ImportacaoInvalidaException("Campo 'evento' nao pode ser em branco");
        }

        if (importacao.getTipoIngresso() == null || importacao.getTipoIngresso().isBlank()) {
            throw new ImportacaoInvalidaException("Campo 'tipoIngresso' nao pode ser vazio");
        }

        if (importacao.getValor() == null || importacao.getValor() < 0) {
            throw new ImportacaoInvalidaException("Campo 'valor' nao pode ser vazio nem negativo");
        }
    }
}
