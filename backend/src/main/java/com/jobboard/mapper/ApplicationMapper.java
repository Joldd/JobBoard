package com.jobboard.mapper;

import com.jobboard.dto.application.ApplicationResponse;
import com.jobboard.dto.application.StatusHistoryResponse;
import com.jobboard.dto.application.UpdateApplicationRequest;
import com.jobboard.entity.Application;
import com.jobboard.entity.StatusHistory;
import java.util.List;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    ApplicationResponse toResponse(Application application);

    StatusHistoryResponse toHistoryResponse(StatusHistory statusHistory);

    List<StatusHistoryResponse> toHistoryResponseList(List<StatusHistory> statusHistories);

    /**
     * Copie les champs modifiables sur l'entité déjà chargée (managée par le contexte de
     * persistance) plutôt que de reconstruire un nouvel objet : le dirty-checking JPA fait
     * le reste, pas besoin d'appeler save() explicitement dans le service. currentStatus
     * n'est délibérément pas dans UpdateApplicationRequest, donc jamais touché ici.
     */
    void updateEntityFromRequest(UpdateApplicationRequest request, @MappingTarget Application application);
}
