package br.com.missio.codeticks.batch.processors;

import br.com.missio.codeticks.entities.Importacao;
import br.com.missio.codeticks.exceptions.ImportacaoInvalidaException;
import br.com.missio.codeticks.validators.ImportacaoValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;

public class ImportacaoItemProcessor implements ItemProcessor<Importacao, Importacao> {

    private static final Logger log = LoggerFactory.getLogger(ImportacaoItemProcessor.class);

    @Override
    public Importacao process(Importacao item) {
        try {
            ImportacaoValidator.validar(item);
        } catch (ImportacaoInvalidaException e) {
            log.error("Erro de validacao no registro do cpf '{}': {}", item.getCpf(), e.getMessage());
            throw e;
        }

        double taxaAdministrativa = switch (item.getTipoIngresso().toLowerCase()) {
            case "vip" -> 130.0;
            case "camarote" -> 80.0;
            default -> 50.0;
        };
        item.setTaxaAdministrativa(taxaAdministrativa);

        return item;
    }

}
