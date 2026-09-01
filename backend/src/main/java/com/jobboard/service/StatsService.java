package com.jobboard.service;

import com.jobboard.dto.stats.CompanyCount;
import com.jobboard.dto.stats.ConversionStep;
import com.jobboard.dto.stats.DashboardStatsResponse;
import com.jobboard.dto.stats.JobTypeCount;
import com.jobboard.dto.stats.MonthlyCount;
import com.jobboard.dto.stats.StatusCount;
import com.jobboard.entity.StatusHistory;
import com.jobboard.entity.enums.ApplicationStatus;
import com.jobboard.repository.ApplicationRepository;
import com.jobboard.repository.StatusHistoryRepository;
import java.time.Duration;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Les regroupements simples (par statut, par type de poste, top entreprises) sont calculés
 * en base via des requêtes GROUP BY (voir ApplicationRepository) : c'est ce pour quoi une
 * base de données est faite, et ça reste correct même si le nombre de candidatures grossit.
 *
 * Le funnel de conversion et le délai moyen de réponse ont besoin de comparer des entrées
 * d'historique consécutives par candidature (ex: "quelle est l'entrée qui suit APPLIED ?") —
 * l'écrire en JPQL demanderait des fonctions fenêtrées (LAG/LEAD) peu portables et nettement
 * moins lisibles. Vu le volume de données d'un tracker personnel (des dizaines à quelques
 * centaines de candidatures, jamais plus), calculer ça en Java après une seule requête est
 * largement assez rapide et bien plus simple à comprendre et tester.
 */
@Service
@RequiredArgsConstructor
public class StatsService {

    /** Étapes du "vrai" entonnoir de recrutement : FOLLOW_UP est une relance, pas une étape. */
    private static final List<ApplicationStatus> FUNNEL_STAGES = List.of(
            ApplicationStatus.APPLIED,
            ApplicationStatus.HR_INTERVIEW,
            ApplicationStatus.TECHNICAL_INTERVIEW,
            ApplicationStatus.OFFER);

    private static final int TOP_COMPANIES_LIMIT = 5;

    private final ApplicationRepository applicationRepository;
    private final StatusHistoryRepository statusHistoryRepository;

    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats(Long userId) {
        List<StatusCount> byStatus = applicationRepository.countGroupedByStatus(userId).stream()
                .map(p -> new StatusCount(p.getStatus(), p.getCount()))
                .toList();

        List<JobTypeCount> byJobType = applicationRepository.countGroupedByJobType(userId).stream()
                .map(p -> new JobTypeCount(p.getJobType(), p.getCount()))
                .toList();

        List<CompanyCount> topCompanies = applicationRepository
                .countGroupedByCompany(userId, PageRequest.of(0, TOP_COMPANIES_LIMIT))
                .stream()
                .map(p -> new CompanyCount(p.getCompany(), p.getCount()))
                .toList();

        List<MonthlyCount> applicationsOverTime = computeMonthlyTrend(userId);

        Collection<List<StatusHistory>> historiesByApplication = statusHistoryRepository
                .findAllForUserOrderedByApplicationAndTime(userId)
                .stream()
                .collect(Collectors.groupingBy(
                        sh -> sh.getApplication().getId(), LinkedHashMap::new, Collectors.toList()))
                .values();

        List<ConversionStep> conversionFunnel = computeConversionFunnel(historiesByApplication);
        Double averageResponseDays = computeAverageResponseDays(historiesByApplication);

        return new DashboardStatsResponse(
                byStatus, byJobType, topCompanies, conversionFunnel, averageResponseDays, applicationsOverTime);
    }

    private List<MonthlyCount> computeMonthlyTrend(Long userId) {
        Map<YearMonth, Long> countsByMonth = applicationRepository.findAllApplicationDates(userId).stream()
                .collect(Collectors.groupingBy(YearMonth::from, TreeMap::new, Collectors.counting()));

        return countsByMonth.entrySet().stream()
                .map(entry -> new MonthlyCount(entry.getKey().toString(), entry.getValue()))
                .toList();
    }

    private List<ConversionStep> computeConversionFunnel(Collection<List<StatusHistory>> historiesByApplication) {
        Map<ApplicationStatus, Long> reachedCounts = new LinkedHashMap<>();
        FUNNEL_STAGES.forEach(stage -> reachedCounts.put(stage, 0L));

        for (List<StatusHistory> history : historiesByApplication) {
            Set<ApplicationStatus> statusesReached =
                    history.stream().map(StatusHistory::getStatus).collect(Collectors.toSet());
            for (ApplicationStatus stage : FUNNEL_STAGES) {
                if (statusesReached.contains(stage)) {
                    reachedCounts.merge(stage, 1L, Long::sum);
                }
            }
        }

        List<ConversionStep> steps = new ArrayList<>();
        for (int i = 0; i < FUNNEL_STAGES.size() - 1; i++) {
            ApplicationStatus from = FUNNEL_STAGES.get(i);
            ApplicationStatus to = FUNNEL_STAGES.get(i + 1);
            long fromCount = reachedCounts.get(from);
            long toCount = reachedCounts.get(to);
            double rate = fromCount == 0 ? 0.0 : (double) toCount / fromCount;
            steps.add(new ConversionStep(from, to, fromCount, toCount, rate));
        }
        return steps;
    }

    /**
     * Pour chaque candidature, cherche la première entrée APPLIED de son historique puis
     * l'entrée suivante chronologiquement (quelle qu'elle soit — relance, entretien, refus...
     * tout compte comme "une réponse est arrivée"). Ignore les candidatures où APPLIED n'a
     * jamais été suivi de rien (pas encore de réponse).
     */
    private Double computeAverageResponseDays(Collection<List<StatusHistory>> historiesByApplication) {
        List<Long> responseHours = new ArrayList<>();

        for (List<StatusHistory> history : historiesByApplication) {
            for (int i = 0; i < history.size(); i++) {
                if (history.get(i).getStatus() == ApplicationStatus.APPLIED && i + 1 < history.size()) {
                    Duration delay =
                            Duration.between(history.get(i).getChangedAt(), history.get(i + 1).getChangedAt());
                    responseHours.add(Math.max(delay.toHours(), 0));
                    break; // seulement la première occurrence d'APPLIED par candidature
                }
            }
        }

        if (responseHours.isEmpty()) {
            return null;
        }
        double averageHours =
                responseHours.stream().mapToLong(Long::longValue).average().orElse(0);
        return averageHours / 24.0;
    }
}
