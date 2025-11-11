package com.vaultops.controllers;

import com.vaultops.services.stats.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatsController {

    private final GetAssetsInUseService getAssetsInUseService;
    private final GetAssetsInStorageService getAssetsInStorageService;
    private final GetAssetsInRepairsService getAssetsInRepairsService;
    private final GetExcellentConditionAssetsService getExcellentConditionAssetsService;
    private final GetGoodConditionAssetsService getGoodConditionAssetsService;
    private final GetFairConditionAssetsService getFairConditionAssetsService;
    private final GetBadConditionAssetsService getBadConditionAssetsService;
    private final GetDamagedConditionAssetsService getDamagedConditionAssetsService;

    public StatsController(GetAssetsInUseService getAssetsInUseService,
                           GetAssetsInStorageService getAssetsInStorageService,
                           GetAssetsInRepairsService getAssetsInRepairsService,
                           GetExcellentConditionAssetsService getExcellentConditionAssetsService,
                           GetGoodConditionAssetsService getGoodConditionAssetsService,
                           GetFairConditionAssetsService getFairConditionAssetsService,
                           GetBadConditionAssetsService getBadConditionAssetsService,
                           GetDamagedConditionAssetsService getDamagedConditionAssetsService) {
        this.getAssetsInUseService = getAssetsInUseService;
        this.getAssetsInStorageService = getAssetsInStorageService;
        this.getAssetsInRepairsService = getAssetsInRepairsService;
        this.getExcellentConditionAssetsService = getExcellentConditionAssetsService;
        this.getGoodConditionAssetsService = getGoodConditionAssetsService;
        this.getFairConditionAssetsService = getFairConditionAssetsService;
        this.getBadConditionAssetsService = getBadConditionAssetsService;
        this.getDamagedConditionAssetsService = getDamagedConditionAssetsService;
    }

    @GetMapping("/stats/assets/in-use")
    public ResponseEntity<Integer> getAssetsInUse() {
        return getAssetsInUseService.execute(null);
    }

    @GetMapping("/stats/assets/storage")
    public ResponseEntity<Integer> getAssetsInStorage() {
        return getAssetsInStorageService.execute(null);
    }

    @GetMapping("/stats/assets/in-repairs")
    public ResponseEntity<Integer> getAssetsInService() {
        return getAssetsInRepairsService.execute(null);
    }

    @GetMapping("/stats/assets/excellent")
    public ResponseEntity<Integer> getAssetsInExcellentCondition() {
        return getExcellentConditionAssetsService.execute(null);
    }

    @GetMapping("/stats/assets/good")
    public ResponseEntity<Integer> getAssetsInGoodCondition() {
        return getGoodConditionAssetsService.execute(null);
    }
    @GetMapping("/stats/assets/fair")
    public ResponseEntity<Integer> getAssetsInFairCondition() {
        return getFairConditionAssetsService.execute(null);
    }

    @GetMapping("/stats/assets/bad")
    public ResponseEntity<Integer> getAssetsInBadCondition() {
        return getBadConditionAssetsService.execute(null);
    }


}
