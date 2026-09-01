package com.jobboard.service;

import com.jobboard.dto.application.ApplicationResponse;
import com.jobboard.dto.application.CreateApplicationRequest;
import com.jobboard.dto.application.StatusChangeRequest;
import com.jobboard.dto.application.StatusHistoryResponse;
import com.jobboard.dto.application.UpdateApplicationRequest;
import com.jobboard.entity.Application;
import com.jobboard.entity.StatusHistory;
import com.jobboard.entity.User;
import com.jobboard.entity.enums.ApplicationStatus;
import com.jobboard.entity.enums.JobType;
import com.jobboard.exception.ResourceNotFoundException;
import com.jobboard.mapper.ApplicationMapper;
import com.jobboard.repository.ApplicationRepository;
import com.jobboard.repository.ApplicationSpecifications;
import com.jobboard.repository.StatusHistoryRepository;
import com.jobboard.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final StatusHistoryRepository statusHistoryRepository;
    private final UserRepository userRepository;
    private final ApplicationMapper applicationMapper;

    @Transactional
    public ApplicationResponse create(Long userId, CreateApplicationRequest request) {
        // Référence sans SELECT : le JWT vient d'être validé sur cet userId dans le même
        // thread de requête, son existence est déjà garantie.
        User user = userRepository.getReferenceById(userId);
        ApplicationStatus initialStatus =
                request.initialStatus() != null ? request.initialStatus() : ApplicationStatus.TO_APPLY;

        Application application = Application.builder()
                .user(user)
                .company(request.company())
                .position(request.position())
                .jobType(request.jobType())
                .jobOfferUrl(request.jobOfferUrl())
                .applicationDate(request.applicationDate())
                .currentStatus(initialStatus)
                .estimatedSalary(request.estimatedSalary())
                .notes(request.notes())
                .build();
        applicationRepository.save(application);

        LocalDateTime initialChangedAt =
                request.initialStatusChangedAt() != null ? request.initialStatusChangedAt() : LocalDateTime.now();

        statusHistoryRepository.save(StatusHistory.builder()
                .application(application)
                .status(initialStatus)
                .changedAt(initialChangedAt)
                .comment("Création de la candidature")
                .build());

        return applicationMapper.toResponse(application);
    }

    @Transactional(readOnly = true)
    public Page<ApplicationResponse> list(
            Long userId,
            ApplicationStatus status,
            JobType jobType,
            String company,
            LocalDate dateFrom,
            LocalDate dateTo,
            Pageable pageable) {
        // Specification.allOf ne filtre PAS les éléments null de lui-même (il combine via
        // .and() en interne, qui lève IllegalArgumentException sur un null) : contrairement
        // à ce qu'on pourrait attendre, il faut retirer les filtres non renseignés soi-même
        // avant de le lui passer.
        List<Specification<Application>> specs = Stream.of(
                        ApplicationSpecifications.belongsToUser(userId),
                        ApplicationSpecifications.hasStatus(status),
                        ApplicationSpecifications.hasJobType(jobType),
                        ApplicationSpecifications.companyContains(company),
                        ApplicationSpecifications.applicationDateBetween(dateFrom, dateTo))
                .filter(Objects::nonNull)
                .toList();

        return applicationRepository.findAll(Specification.allOf(specs), pageable).map(applicationMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ApplicationResponse get(Long userId, Long id) {
        return applicationMapper.toResponse(findOwned(userId, id));
    }

    @Transactional
    public ApplicationResponse update(Long userId, Long id, UpdateApplicationRequest request) {
        Application application = findOwned(userId, id);
        applicationMapper.updateEntityFromRequest(request, application);
        // Pas de save() : application est une entité managée dans cette transaction,
        // Hibernate détecte les changements et les écrit au flush (dirty checking).
        // Le flush explicite est nécessaire ici : sans lui, le flush n'aurait lieu qu'au
        // commit (après le retour de cette méthode), et @UpdateTimestamp ne rafraîchirait
        // le champ updatedAt en mémoire qu'à ce moment-là — la réponse renverrait un
        // updatedAt périmé. Avec flush(), la valeur générée est déjà à jour ici.
        applicationRepository.flush();
        return applicationMapper.toResponse(application);
    }

    @Transactional
    public void delete(Long userId, Long id) {
        applicationRepository.delete(findOwned(userId, id));
    }

    @Transactional
    public ApplicationResponse changeStatus(Long userId, Long id, StatusChangeRequest request) {
        Application application = findOwned(userId, id);
        application.setCurrentStatus(request.status());

        statusHistoryRepository.save(StatusHistory.builder()
                .application(application)
                .status(request.status())
                .changedAt(request.changedAt() != null ? request.changedAt() : LocalDateTime.now())
                .comment(request.comment())
                .build());

        return applicationMapper.toResponse(application);
    }

    @Transactional(readOnly = true)
    public List<StatusHistoryResponse> getHistory(Long userId, Long id) {
        Application application = findOwned(userId, id);
        return applicationMapper.toHistoryResponseList(
                statusHistoryRepository.findByApplicationIdOrderByChangedAtDesc(application.getId()));
    }

    /**
     * Toujours filtrer par (id, userId) ensemble, jamais findById seul : une candidature
     * d'un autre utilisateur doit se comporter exactement comme un id inexistant (404),
     * pas comme un refus d'accès (403) qui confirmerait son existence à un tiers.
     */
    private Application findOwned(Long userId, Long id) {
        return applicationRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidature introuvable : " + id));
    }
}
