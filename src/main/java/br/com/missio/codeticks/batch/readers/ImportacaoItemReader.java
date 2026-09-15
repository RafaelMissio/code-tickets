package br.com.missio.codeticks.batch.readers;

import br.com.missio.codeticks.entities.Importacao;
import br.com.missio.codeticks.mappers.ImportacaoMapper;
import org.springframework.batch.infrastructure.item.ItemStreamReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.core.io.FileSystemResource;

public final class ImportacaoItemReader {

    private ImportacaoItemReader() {
    }

    public static ItemStreamReader<Importacao> build(String importacaoCsvPath) {
        return new FlatFileItemReaderBuilder<Importacao>()
                .name("leitura-csv")
                .resource(new FileSystemResource(importacaoCsvPath))
                .comments("--")
                .delimited()
                .delimiter(";")
                .names("cpf", "cliente", "nascimento", "evento", "data", "tipoIngresso", "valor")
                .fieldSetMapper(new ImportacaoMapper())
                .build();
    }
}
