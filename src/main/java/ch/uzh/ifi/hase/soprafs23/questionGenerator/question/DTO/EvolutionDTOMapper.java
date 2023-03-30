package ch.uzh.ifi.hase.soprafs23.questionGenerator.question.DTO;

import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.entity.Evolution;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.entity.HanziDrawing;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

public interface EvolutionDTOMapper {

    @Mapper
    public interface DTOMapper{
        DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

        @Mapping(source = "oracleURL", target = "oracleURL")
        @Mapping(source = "bronzeInscriptionURL", target = "bronzeInscriptionURL")
        @Mapping(source = "sealScriptURL", target = "sealScriptURL")
        @Mapping(source = "clerialScriptURL", target = "clerialScriptURL")
        @Mapping(source = "regularScriptURL", target = "regularScriptURL")
        Evolution convertDTOtoEntity(EvolutionDTO evolutionDTO);

        @Mapping(source = "oracleURL", target = "oracleURL")
        @Mapping(source = "bronzeInscriptionURL", target = "bronzeInscriptionURL")
        @Mapping(source = "sealScriptURL", target = "sealScriptURL")
        @Mapping(source = "clerialScriptURL", target = "clerialScriptURL")
        @Mapping(source = "regularScriptURL", target = "regularScriptURL")
        EvolutionDTO convertEntityToDTO(Evolution evolution);
    }
}
