package com.vaultops.controllers;

import com.vaultops.model.Asset;
import com.vaultops.model.UpdateAssetCommand;
import com.vaultops.services.stats.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StatsController {

    private final GetAssetsInUseService getAssetsInUseService;
    private final GetAssetsInStorageService getAssetsInStorageService;
    private final GetAssetsInRepairsService getAssetsInRepairsService;
    private final GetExcellentConditionAssetsService getExcellentConditionAssetsService;
    private final GetGoodConditionAssetsService getGoodConditionAssetsService;
    private final GetFairConditionAssetsService getFairConditionAssetsService;
    private final GetBadConditionAssetsService getBadConditionAssetsService;
    private final GetDamagedConditionAssetsService getDamagedConditionAssetsService;
    private final GetTotalAssetsCountService getTotalAssetsCountService;
    private final GetNumberOfDayInRepairsService getNumberOfDayInRepairsService;

    @GetMapping("/stats/assets/in-use")
    public ResponseEntity<Long> getAssetsInUse() {
        return getAssetsInUseService.execute(null);
    }

    @GetMapping("/stats/assets/storage")
    public ResponseEntity<Long> getAssetsInStorage() {
        return getAssetsInStorageService.execute(null);
    }

    @GetMapping("/stats/assets/in-repairs")
    public ResponseEntity<Long> getAssetsInService() {
        return getAssetsInRepairsService.execute(null);
    }

    @GetMapping("/stats/assets/excellent")
    public ResponseEntity<Long> getAssetsInExcellentCondition() {
        return getExcellentConditionAssetsService.execute(null);
    }

    @GetMapping("/stats/assets/good")
    public ResponseEntity<Long> getAssetsInGoodCondition() {
        return getGoodConditionAssetsService.execute(null);
    }
    @GetMapping("/stats/assets/fair")
    public ResponseEntity<Long> getAssetsInFairCondition() {
        return getFairConditionAssetsService.execute(null);
    }

    @GetMapping("/stats/assets/bad")
    public ResponseEntity<Long> getAssetsInBadCondition() {
        return getBadConditionAssetsService.execute(null);
    }

    @GetMapping("/stats/assets/damaged")
    public ResponseEntity<Long> getAssetsInDamagedCondition() {
        return getDamagedConditionAssetsService.execute(null);
    }

    @GetMapping("/stats/assets/total")
    public ResponseEntity<Long> getAssetsTotalCount() {
        return getTotalAssetsCountService.execute(null);
    }

    @GetMapping("/stats/assets/day-in-service/{id}")
    public ResponseEntity<Long> getNumberOfDaysInService(@PathVariable Long id) {
        return getNumberOfDayInRepairsService.execute(new UpdateAssetCommand(id, null));
    }

}
