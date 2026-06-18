package com.anpfuel.domain.valueobject

enum class BrazilianState(
    val abbreviation: String,
    val region: BrazilianRegion,
) {
    ACRE("AC", BrazilianRegion.NORTH),
    ALAGOAS("AL", BrazilianRegion.NORTHEAST),
    AMAPA("AP", BrazilianRegion.NORTH),
    AMAZONAS("AM", BrazilianRegion.NORTH),
    BAHIA("BA", BrazilianRegion.NORTHEAST),
    CEARA("CE", BrazilianRegion.NORTHEAST),
    DISTRICT_FEDERAL("DF", BrazilianRegion.CENTRAL_WEST),
    ESPIRITO_SANTO("ES", BrazilianRegion.SOUTHEAST),
    GOIAS("GO", BrazilianRegion.CENTRAL_WEST),
    MARANHAO("MA", BrazilianRegion.NORTHEAST),
    MATO_GROSSO("MT", BrazilianRegion.CENTRAL_WEST),
    MATO_GROSSO_DO_SUL("MS", BrazilianRegion.CENTRAL_WEST),
    MINAS_GERAIS("MG", BrazilianRegion.SOUTHEAST),
    PARA("PA", BrazilianRegion.NORTH),
    PARAIBA("PB", BrazilianRegion.NORTHEAST),
    PARANA("PR", BrazilianRegion.SOUTH),
    PERNAMBUCO("PE", BrazilianRegion.NORTHEAST),
    PIAUI("PI", BrazilianRegion.NORTHEAST),
    RIO_DE_JANEIRO("RJ", BrazilianRegion.SOUTHEAST),
    RIO_GRANDE_DO_NORTE("RN", BrazilianRegion.NORTHEAST),
    RIO_GRANDE_DO_SUL("RS", BrazilianRegion.SOUTH),
    RONDONIA("RO", BrazilianRegion.NORTH),
    RORAIMA("RR", BrazilianRegion.NORTH),
    SANTA_CATARINA("SC", BrazilianRegion.SOUTH),
    SAO_PAULO("SP", BrazilianRegion.SOUTHEAST),
    SERGIPE("SE", BrazilianRegion.NORTHEAST),
    TOCANTINS("TO", BrazilianRegion.NORTH),
    ;

    companion object {
        fun fromAbbreviation(abbreviation: String): BrazilianState? =
            entries.firstOrNull { it.abbreviation.equals(abbreviation, ignoreCase = true) }
    }
}
