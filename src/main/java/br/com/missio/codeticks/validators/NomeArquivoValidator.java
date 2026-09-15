package br.com.missio.codeticks.validators;

import br.com.missio.codeticks.exceptions.NomeArquivoInvalidoException;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class NomeArquivoValidator {

    private static final Pattern PADRAO_NOME = Pattern.compile("^tickets_(\\d{6})\\.csv$");

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("ddMMyy")
            .withResolverStyle(ResolverStyle.STRICT);

    private NomeArquivoValidator() {
    }

    public static void validar(String nomeArquivo) {
        Matcher matcher = PADRAO_NOME.matcher(nomeArquivo);
        if (!matcher.matches()) {
            throw new NomeArquivoInvalidoException(
                    "Nome de arquivo invalido: '%s'. Esperado o padrao tickets_ddMMyy.csv".formatted(nomeArquivo));
        }

        try {
            FORMATO_DATA.parse(matcher.group(1));
        } catch (DateTimeParseException e) {
            throw new NomeArquivoInvalidoException(
                    "Nome de arquivo invalido: '%s'. A data '%s' nao e valida".formatted(nomeArquivo, matcher.group(1)));
        }
    }

    public static boolean isValido(String nomeArquivo) {
        try {
            validar(nomeArquivo);
            return true;
        } catch (NomeArquivoInvalidoException e) {
            return false;
        }
    }
}
