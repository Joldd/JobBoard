package com.jobboard.controller;

import com.jobboard.dto.application.ApplicationResponse;
import com.jobboard.dto.application.CreateApplicationRequest;
import com.jobboard.dto.application.StatusChangeRequest;
import com.jobboard.dto.application.StatusHistoryResponse;
import com.jobboard.dto.application.UpdateApplicationRequest;
import com.jobboard.entity.enums.ApplicationStatus;
import com.jobboard.entity.enums.JobType;
import com.jobboard.security.SecurityUser;
import com.jobboard.service.ApplicationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "Candidatures")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationResponse create(
            @AuthenticationPrincipal SecurityUser user, @Valid @RequestBody CreateApplicationRequest request) {
        return applicationService.create(user.getId(), request);
    }

    @GetMapping
    public Page<ApplicationResponse> list(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false) JobType jobType,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @PageableDefault(size = 20, sort = "applicationDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return applicationService.list(user.getId(), status, jobType, company, dateFrom, dateTo, pageable);
    }

    @GetMapping("/{id}")
    public ApplicationResponse get(@AuthenticationPrincipal SecurityUser user, @PathVariable Long id) {
        return applicationService.get(user.getId(), id);
    }

    @PutMapping("/{id}")
    public ApplicationResponse update(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable Long id,
            @Valid @RequestBody UpdateApplicationRequest request) {
        return applicationService.update(user.getId(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal SecurityUser user, @PathVariable Long id) {
        applicationService.delete(user.getId(), id);
    }

    @PatchMapping("/{id}/status")
    public ApplicationResponse changeStatus(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable Long id,
            @Valid @RequestBody StatusChangeRequest request) {
        return applicationService.changeStatus(user.getId(), id, request);
    }

    @GetMapping("/{id}/history")
    public List<StatusHistoryResponse> history(@AuthenticationPrincipal SecurityUser user, @PathVariable Long id) {
        return applicationService.getHistory(user.getId(), id);
    }
}
