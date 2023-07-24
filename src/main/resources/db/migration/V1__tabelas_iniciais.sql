create table IF NOT EXISTS pauta(
    pauta_id uuid not null,
    titulo varchar(255) not null,
    PRIMARY KEY (pauta_id)
);

create table IF NOT EXISTS associado(
    associado_id uuid not null,
    nome varchar(255) not null,
    cpf varchar(14) not null,
    PRIMARY KEY (associado_id)
);

create table IF NOT EXISTS sessao_votacao(
    sessao_id uuid not null,
    pauta_id uuid not null,
    duracao_pauta int not null,
    inicio_pauta timestamp with time zone not null,
    fim_pauta timestamp with time zone not null,
    encerrado bool not null,
    PRIMARY KEY (sessao_id)

);

create table IF NOT EXISTS voto(
    voto_id uuid not null,
    associado_id uuid not null,
    pauta_id uuid not null,
    voto varchar(4) not null,
    PRIMARY KEY (voto_id),
    FOREIGN KEY (pauta_id) REFERENCES pauta(pauta_id),
    FOREIGN KEY (associado_id) REFERENCES associado(associado_id)
);