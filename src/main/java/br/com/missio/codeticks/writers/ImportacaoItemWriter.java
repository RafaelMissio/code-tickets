package br.com.missio.codeticks.writers;

import br.com.missio.codeticks.entities.Importacao;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;

import javax.sql.DataSource;

public final class ImportacaoItemWriter {

    private ImportacaoItemWriter() {
    }

    public static ItemWriter<Importacao> build(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Importacao>()
                .dataSource(dataSource)
                .sql("INSERT INTO importacao (cpf, cliente, nascimento, evento, data, tipo_ingresso, valor, hora_importacao, taxa_administrativa) " +
                        "VALUES ( :cpf, :cliente, :nascimento, :evento, :data, :tipoIngresso, :valor, :horaImportacao, :taxaAdministrativa)")
                .itemSqlParameterSourceProvider
                        (new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }
}
