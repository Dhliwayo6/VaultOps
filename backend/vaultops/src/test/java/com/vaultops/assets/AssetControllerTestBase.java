package com.vaultops.assets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaultops.controllers.AssetController;
import com.vaultops.services.asset.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AssetController.class)
public abstract class AssetControllerTestBase {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected CreateAssetService createAssetService;
    @MockitoBean
    protected GetAssetService getAssetService;
    @MockitoBean
    protected GetAssetsService getAssetsService;
    @MockitoBean
    protected UpdateAssetService updateAssetService;
    @MockitoBean
    protected SearchAssetService searchAssetService;
    @MockitoBean
    protected DeleteAssetService deleteAssetService;
}