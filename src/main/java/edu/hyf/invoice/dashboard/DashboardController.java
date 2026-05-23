package edu.hyf.invoice.dashboard;

import edu.hyf.invoice.security.UserPrincipal;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor

public class DashboardController {
    private final DashboardService dashboardService;


    @GetMapping("/summary")
    public ResponseEntity<@NonNull DashboardResponse> getSummary (@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam(required = false) UUID ownerUserId) {

        return ResponseEntity.ok(dashboardService.createSummary(userPrincipal, ownerUserId));

    }

}
